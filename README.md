# Komelia-Vibe - Komga media client

Komelia-Vibe is an independently installable fork of [Komelia](https://github.com/Snd-R/Komelia). It remains compatible with Komga while using its own Android application ID, desktop package identity, application data directories, and fork-specific update channel. It can therefore be installed alongside the original Komelia app without overwriting or updating it.

Fork releases and development take place only in [CoSciBlog/Komelia-vibe](https://github.com/CoSciBlog/Komelia-vibe). Upstream changes can still be integrated because the internal source packages are intentionally kept compatible.

Offline downloads work for restricted Komga accounts as well. Access restrictions
are enforced by Komga while online; the offline database exposes only books that
were explicitly downloaded, so it does not copy remote library or label restrictions.
This follows the behavior planned for upstream Komelia and keeps the implementation
easy to replace when the upstream fix is integrated.

The offline download settings also provide two opt-in storage controls:

- **Download only unread series books** skips completed books when a complete series
  is queued, while retaining unread and in-progress books.
- **Delete downloaded books after reading** removes local book data only after the
  completed progress is confirmed by Komga. When a series is opened online, books
  completed on another device are cleaned up as well. Progress created offline is
  retained until synchronization succeeds, so cleanup never discards unsent progress.

Both controls are disabled by default. They use the existing Komga models and the
fork's offline task layer without changing protocol payloads or internal Kotlin package
names, keeping future upstream integration straightforward.

Development and maintenance changes in this fork may be made with the assistance of AI and OpenAI Codex. Such changes are still subject to the project's build and test workflow.

### Downloads:

- Latest Komelia-Vibe release: https://github.com/CoSciBlog/Komelia-vibe/releases
- Release assets vary by version. Version 0.20.6 is an Android-only feature release;
  earlier releases also provide Windows x64 and Linux x64 packages. Every platform
  keeps the independent Komelia-Vibe identity.
- Original Komelia distribution channels: https://github.com/Snd-R/Komelia#downloads

## Screenshots

<details>
  <summary>Mobile</summary>
   <img src="/fastlane/metadata/android/en-US/images/phoneScreenshots/1.png" alt="Komelia" width="270">  
   <img src="/fastlane/metadata/android/en-US/images/phoneScreenshots/2.png" alt="Komelia" width="270">  
   <img src="/fastlane/metadata/android/en-US/images/phoneScreenshots/3.png" alt="Komelia" width="270">  
   <img src="/fastlane/metadata/android/en-US/images/phoneScreenshots/4.png" alt="Komelia" width="270">  
   <img src="/fastlane/metadata/android/en-US/images/phoneScreenshots/5.png" alt="Komelia" width="270">  
   <img src="/fastlane/metadata/android/en-US/images/phoneScreenshots/6.png" alt="Komelia" width="270">  
</details>

<details>
  <summary>Tablet</summary>
   <img src="/fastlane/metadata/android/en-US/images/tenInchScreenshots/1.jpg" alt="Komelia" width="400" height="640">  
   <img src="/fastlane/metadata/android/en-US/images/tenInchScreenshots/2.jpg" alt="Komelia" width="400" height="640">  
   <img src="/fastlane/metadata/android/en-US/images/tenInchScreenshots/3.jpg" alt="Komelia" width="400" height="640">  
   <img src="/fastlane/metadata/android/en-US/images/tenInchScreenshots/4.jpg" alt="Komelia" width="400" height="640">  
   <img src="/fastlane/metadata/android/en-US/images/tenInchScreenshots/5.jpg" alt="Komelia" width="400" height="640">  
   <img src="/fastlane/metadata/android/en-US/images/tenInchScreenshots/6.jpg" alt="Komelia" width="400" height="640">  
</details>

<details>
  <summary>Desktop</summary>
   <img src="/screenshots/1.jpg" alt="Komelia" width="1280">  
   <img src="/screenshots/2.jpg" alt="Komelia" width="1280">  
   <img src="/screenshots/3.jpg" alt="Komelia" width="1280">  
   <img src="/screenshots/4.jpg" alt="Komelia" width="1280">  
   <img src="/screenshots/5.jpg" alt="Komelia" width="1280">  
</details>

## Translations
You can help translate this project to your language by using service provided by [Weblate](https://hosted.weblate.org/engage/komelia/)

[![Translation status](https://hosted.weblate.org/widget/komelia/horizontal-auto.svg)](https://hosted.weblate.org/engage/komelia/)

## Build instructions
Make sure you download all git submodules\
`git clone --recurse-submodules https://github.com/CoSciBlog/Komelia-vibe` \
if you already cloned repository without recurse command run\
`git submodule update --init --recursive`

Requires jdk 17 or higher\
Android and JVM targets require C and C++ compiler for native libraries and Node.js for epub readers build.\
Recommended way to build is by using docker images that contain all required build dependencies.\
If you want to build with system toolchain and dependencies try running:\
`./gradlew komeliaBuildNonJvmDependencies` (Linux Only)

## Desktop App
Replace <*platform*> placeholder with your target platform. \
Available platforms include: `linux-x86_64`, `windows-x86_64`

- `docker build -t komelia-build-<platfrom> . -f ./cmake/<paltform>.Dockerfile `
- `docker run -v .:/build komelia-build-<paltform>`
- `./gradlew <platform>_copyJniLibs`
- `./gradlew buildEpubReaders`

Then choose your packaging option:
- `./gradlew :desktopRun` to launch desktop app
- `./gradlew :desktopJar` output in `./komelia-app/desktopApp/build/compose/jars`
- `./gradlew :desktopDeb` output in `./komelia-app/desktopApp/build/compose/binaries`
- `./gradlew :desktopMsi` output in `./komelia-app/desktopApp/build/compose/binaries`

On Windows checkouts, use the isolated desktop wrapper so native dependency
cleanup and line-ending conversion happen only in disposable container-local clones:

```powershell
New-Item -ItemType Directory -Force ./cmake/build-isolated-output/private-mask, ./cmake/build-isolated-output/windows-x86_64, ./cmake/build-isolated-output/linux-x86_64 | Out-Null
docker run --rm --user 0 --mount "type=bind,source=$($PWD.Path),target=/source,readonly" --mount "type=bind,source=$($PWD.Path)/cmake/build-isolated-output/private-mask,target=/source/release-artifacts/private,readonly" --mount "type=bind,source=$($PWD.Path)/cmake/build-isolated-output/windows-x86_64,target=/export" --entrypoint bash komelia-build-windows-x86_64 /source/cmake/desktop-isolated-build.sh windows-x86_64
docker run --rm --user 0 --mount "type=bind,source=$($PWD.Path),target=/source,readonly" --mount "type=bind,source=$($PWD.Path)/cmake/build-isolated-output/private-mask,target=/source/release-artifacts/private,readonly" --mount "type=bind,source=$($PWD.Path)/cmake/build-isolated-output/linux-x86_64,target=/export" --entrypoint bash komelia-build-linux-x86_64 /source/cmake/desktop-isolated-build.sh linux-x86_64
```

The Linux Debian package can also be produced without modifying the checkout:

```powershell
New-Item -ItemType Directory -Force ./release-artifacts/linux-package | Out-Null
docker run --rm --user 0 --mount "type=bind,source=$($PWD.Path),target=/source,readonly" --mount "type=bind,source=$($PWD.Path)/cmake/build-isolated-output/private-mask,target=/source/release-artifacts/private,readonly" --mount "type=bind,source=$($PWD.Path)/cmake/build-isolated-output/linux-x86_64/lib,target=/native,readonly" --mount "type=bind,source=$($PWD.Path)/release-artifacts/linux-package,target=/export" --entrypoint bash komelia-build-linux-x86_64 /source/cmake/linux-isolated-package.sh
```

## Android App
Replace <*arch*> placeholder with your target architecture.\
Available architectures include:  `aarch64`, `armv7a`, `x86_64`, `x86`

- `docker build -t komelia-build-android . -f ./cmake/android.Dockerfile `
- `docker run -v .:/build komelia-build-android <arch>`
- `./gradlew android-<arch>_copyJniLibs`
- `./gradlew buildEpubReaders`

Then choose app build option:

- `./gradlew :androidDebug` output in `./komelia-app/androidApp/build/outputs/apk/debug`
- `./gradlew :androidRelease` output in `./komelia-app/androidApp/build/outputs/apk/release`

On Windows, use `./gradlew.bat` and set `JAVA_HOME` to JDK 17 or newer and
`ANDROID_HOME` to your Android SDK directory. Docker Desktop must be running
with Linux containers (`docker version` must show a Server section).
Shell scripts must use LF line endings; `.gitattributes` enforces this for new
checkouts. For an existing checkout, convert `cmake/android-build.sh` to LF if
Docker reports `exec ./cmake/android-build.sh: no such file or directory`.

For ARM64 devices, use `aarch64` for the Docker argument and
`android-aarch64_copyJniLibs` for the Gradle task. APKs default to ARM64.
For other devices, pass `"-Pkomelia.android.abis=armeabi-v7a"` (or use `x86_64` or `x86` as the value)
to the APK build. For multiple ABIs, use a comma-separated list (for example
`"-Pkomelia.android.abis=arm64-v8a,armeabi-v7a"`) after building/copying each ABI.
Keep the whole `-P...` argument quoted in PowerShell.
Packaging checks the native libraries and reader assets before building the APK.
The visible Android application label is stored directly as `Komelia-Vibe` in all
distribution manifests for compatibility with package installers that cannot resolve
resource-backed labels before installation.
Android JNI libraries are extracted during installation so libraries loaded through
`System.loadLibrary`, including `libsqlitejdbc.so`, are available from the app's
native library directory on all supported devices.
Release APKs are unsigned by default: align them with Android SDK `zipalign`,
then sign them with `apksigner` using your private release key before installing.
Verify the exported APK with `apksigner verify --verbose --print-certs` and
`aapt dump badging`. Keep signing keys and passwords private and reuse the same
key for future updates.

For Windows checkouts, or whenever submodules contain local work, use an isolated
native build. The existing CMake dependency steps reset/clean their source trees;
this wrapper runs those steps on fresh container-local clones. Run from the
repository root in PowerShell (replace `aarch64` consistently for another ABI):

```powershell
docker build -t komelia-build-android . -f ./cmake/android.Dockerfile
New-Item -ItemType Directory -Force ./cmake/build-android-aarch64/sysroot | Out-Null
docker run --rm --user 0 --mount "type=bind,source=$($PWD.Path),target=/source,readonly" --mount "type=bind,source=$($PWD.Path)/cmake/build-android-aarch64/sysroot,target=/export" --entrypoint bash komelia-build-android /source/cmake/android-isolated-build.sh aarch64
./gradlew.bat android-aarch64_copyJniLibs
./gradlew.bat buildEpubReaders
./gradlew.bat :androidRelease
```

The wrapper uses the currently checked-out submodule commits and includes local
tracked CMake build-system edits. Commit any native dependency source changes you
want to build before running it. Its source mount is read-only, so dependency
cleanup cannot remove files from the host checkout.


## Komf Wasm WebUI
run `./gradlew :komfWebUI` output will be in `./build/komf-webui`

## Komf Wasm Extension
for chrome `./gradlew :komfExtensionChrome` \
for firefox `./gradlew :komfExtensionFirefox` \
output archive will be in `./komelia-komf-extension/app/build/distributions`
