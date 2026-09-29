#!/bin/bash
set -euo pipefail

# Mount the checkout read-only at /source and an empty output directory at /export.
# Run as root in a disposable container; native dependency patch steps must never
# reset or clean the user's checkout. Git object stores are only read through the
# read-only mount; each clone has its own index and writable working tree.
arch=${1:?Provide aarch64, armv7a, x86_64 or x86}
case "$arch" in aarch64|armv7a|x86_64|x86) ;; *) exit 2 ;; esac
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
sed -i 's/\r$//' cmake/android-build.sh
bash cmake/android-build.sh "$arch"
mkdir -p /export/lib
cp -L "cmake/build-android-$arch/sysroot/lib/"*.so /export/lib/
