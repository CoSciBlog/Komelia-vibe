#!/bin/bash
set -euo pipefail

# Mount the checkout read-only at /source and an empty output directory at /export.
# Native dependency patch steps run only in disposable container-local clones.
platform=${1:?Provide windows-x86_64 or linux-x86_64}
case "$platform" in windows-x86_64|linux-x86_64) ;; *) exit 2 ;; esac
test ! -e /build/.git
git config --global --add safe.directory /source
git config --global --add safe.directory '/source/*'
git config --global --add safe.directory /build
git config --global core.autocrlf false
git config --global advice.detachedHead false

clone_sources() {
    local source=$1 destination=$2 path
    git clone --quiet --shared "$source" "$destination"
    while IFS= read -r path; do
        if test -e "$source/$path/.git"; then
            clone_sources "$source/$path" "$destination/$path"
        fi
    done < <(git -C "$source" ls-files --stage | awk '$1 == "160000" {sub(/^[^\t]*\t/, ""); print}')
}

clone_sources /source /build
cd /build
# Include local build-system changes while keeping dependency checkouts clean.
while IFS= read -r -d '' path; do
    cp "/source/$path" "$path"
done < <(git ls-files -z CMakeLists.txt cmake)

mkdir -p /export
case "$platform" in
    windows-x86_64)
        sed -i 's/\r$//' cmake/windows-x86_64-build.sh
        bash cmake/windows-x86_64-build.sh
        mkdir -p /export/bin
        cp -L cmake/build-w64/sysroot/bin/*.dll /export/bin/
        ;;
    linux-x86_64)
        sed -i 's/\r$//' cmake/linux-x86_64-build.sh
        bash cmake/linux-x86_64-build.sh
        mkdir -p /export/lib
        cp -L cmake/build/sysroot/lib/*.so /export/lib/
        ;;
esac
