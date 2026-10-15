"""Verify a Komelia-Vibe universal APK using the Python standard library and aapt."""

import argparse
import hashlib
import json
import pathlib
import re
import struct
import subprocess
import zipfile


ABIS = {
    "arm64-v8a": (2, 183, "aarch64-linux-android"),
    "armeabi-v7a": (1, 40, "arm-linux-androideabi"),
    "x86_64": (2, 62, "x86_64-linux-android"),
    "x86": (1, 3, "i686-linux-android"),
}
REQUIRED_LIBRARIES = {
    "libkomelia_android_bitmap.so", "libkomelia_vips.so",
    "libkomelia_onnxruntime.so", "libvips.so", "libpng16.so",
    "libonnxruntime.so", "libsqlitejdbc.so",
}


def elf_dependencies(data, elf_class, machine, name):
    assert data[:4] == b"\x7fELF" and data[4:6] == bytes((elf_class, 1)), f"Invalid ELF: {name}"
    assert struct.unpack_from("<H", data, 18)[0] == machine, f"Wrong ELF machine: {name}"
    if elf_class == 2:
        offset = struct.unpack_from("<Q", data, 32)[0]
        entry_size, count = struct.unpack_from("<HH", data, 54)
        headers = [struct.unpack_from("<IIQQQQQQ", data, offset + i * entry_size) for i in range(count)]
        segments = [(h[0], h[2], h[3], h[5], h[7]) for h in headers]
        dynamic_format = "<qQ"
    else:
        offset = struct.unpack_from("<I", data, 28)[0]
        entry_size, count = struct.unpack_from("<HH", data, 42)
        headers = [struct.unpack_from("<IIIIIIII", data, offset + i * entry_size) for i in range(count)]
        segments = [(h[0], h[1], h[2], h[4], h[7]) for h in headers]
        dynamic_format = "<iI"
    loads = [s for s in segments if s[0] == 1]
    assert loads, f"No load segments: {name}"
    # Android's 16 KiB requirement applies to arm64-v8a and x86_64;
    # the 32-bit ABIs retain 4 KiB pages. See Android NDK's
    # https://android.googlesource.com/platform/ndk/+/master/docs/BuildSystemMaintainers.md
    alignment = 16384 if elf_class == 2 else 4096
    assert all(s[4] >= alignment and s[4] & (s[4] - 1) == 0 for s in loads), f"Not {alignment}-byte aligned: {name}"
    assert all((s[1] - s[2]) % alignment == 0 for s in loads), f"Misaligned load segment: {name}"
    dynamic = next((s for s in segments if s[0] == 2), None)
    if dynamic is None:
        return []
    records = []
    size = struct.calcsize(dynamic_format)
    for position in range(dynamic[1], dynamic[1] + dynamic[3], size):
        tag, value = struct.unpack_from(dynamic_format, data, position)
        if tag == 0:
            break
        records.append((tag, value))
    needed = [value for tag, value in records if tag == 1]
    if not needed:
        return []
    strings_address = next(value for tag, value in records if tag == 5)
    load = next(s for s in loads if s[2] <= strings_address < s[2] + s[3])
    strings_offset = load[1] + strings_address - load[2]
    return [data[strings_offset + value:data.index(b"\0", strings_offset + value)].decode("utf-8") for value in needed]


def verify(args):
    badging = subprocess.check_output([str(args.aapt), "dump", "badging", str(args.apk)], text=True)
    package = re.search(r"package: name='([^']+)' versionCode='([^']+)' versionName='([^']+)'", badging)
    assert package, "Missing package metadata"
    assert package.groups() == ("io.github.cosciblog.komelia.vibe", args.version_code, args.version), package.groups()
    assert "sdkVersion:'26'" in badging, "Unexpected minimum SDK"
    assert "targetSdkVersion:'37'" in badging, "Unexpected target SDK"
    sysroots = list((args.ndk / "toolchains/llvm/prebuilt").glob("*/sysroot/usr/lib"))
    assert len(sysroots) == 1, "Specify an NDK with one host toolchain"
    counts = {}
    with zipfile.ZipFile(args.apk) as archive:
        assert archive.testzip() is None, "ZIP CRC validation failed"
        names = archive.namelist()
        assert len(names) == len(set(names)), "Duplicate ZIP entries"
        for required in ("AndroidManifest.xml", "resources.arsc", "classes.dex", "assets/legal/LICENSE", "assets/legal/NOTICE", "assets/legal/CREDITS.md"):
            assert required in names and archive.getinfo(required).file_size > 0, f"Missing {required}"
        for reader in ("komga.html", "ttsu.html"):
            matches = [n for n in names if n.startswith("assets/") and n.endswith("/" + reader)]
            assert len(matches) == 1 and archive.getinfo(matches[0]).file_size > 1000, f"Missing EPUB reader: {reader}"
        for notice in ("komga-webui/LICENSE", "komga-webui/src/styles/readium/LICENSE", "ttu-ebook-reader/LICENSE"):
            assert "assets/legal/readers/" + notice in names, f"Missing reader license: {notice}"
        assert any(n.startswith("assets/legal/third_party/") for n in names), "Missing native component notices"
        libraries = [n for n in names if n.startswith("lib/") and n.endswith(".so")]
        assert {n.split("/")[1] for n in libraries} == set(ABIS), "APK must contain exactly all four ABIs"
        for abi, (elf_class, machine, triplet) in ABIS.items():
            abi_libraries = [n for n in libraries if n.split("/")[1] == abi]
            packaged = {pathlib.PurePosixPath(n).name for n in abi_libraries}
            assert REQUIRED_LIBRARIES <= packaged, f"Missing {abi} libraries: {REQUIRED_LIBRARIES - packaged}"
            platform_directory = sysroots[0] / triplet / "26"
            assert platform_directory.is_dir(), f"Missing NDK platform libraries: {platform_directory}"
            platform = {p.name for p in platform_directory.glob("*.so")}
            for name in abi_libraries:
                needed = elf_dependencies(archive.read(name), elf_class, machine, name)
                missing = set(needed) - packaged - platform
                assert not missing, f"{name}: unresolved native dependencies {missing}"
            counts[abi] = len(abi_libraries)
        legal_count = len([n for n in names if n.startswith("assets/legal/") and not n.endswith("/")])
    return {
        "apk": args.apk.name,
        "sha256": hashlib.file_digest(args.apk.open("rb"), "sha256").hexdigest(),
        "package": package[1], "version": package[3], "versionCode": int(package[2]),
        "nativeLibraries": counts, "legalFiles": legal_count,
        "zipCrc": "PASS", "epubAssets": "PASS", "nativeDependencies": "PASS",
        "elf16KiBAlignment": {"arm64-v8a": "PASS", "x86_64": "PASS"},
        "elf4KiBAlignment32Bit": "PASS", "licensesAndCredits": "PASS",
    }


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("apk", type=pathlib.Path)
    parser.add_argument("--ndk", type=pathlib.Path, required=True)
    parser.add_argument("--aapt", type=pathlib.Path, required=True)
    parser.add_argument("--version", required=True)
    parser.add_argument("--version-code", required=True)
    parser.add_argument("--report", type=pathlib.Path)
    args = parser.parse_args()
    result = verify(args)
    rendered = json.dumps(result, indent=2)
    if args.report:
        args.report.write_text(rendered + "\n", encoding="utf-8")
    print(rendered)


if __name__ == "__main__":
    main()
