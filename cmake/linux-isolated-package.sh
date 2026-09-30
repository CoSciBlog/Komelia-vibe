#!/bin/bash
set -euo pipefail

# Mount the checkout read-only at /source, the native Linux export at /native,
# and an empty release directory at /export. Everything else is disposable.
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
mkdir -p cmake/build/sysroot/lib /export
cp -L /native/*.so cmake/build/sysroot/lib/

./gradlew linux-x86_64_copyJniLibs buildEpubReaders --no-configuration-cache --console=plain
./gradlew desktopDeb --no-configuration-cache --console=plain

package=$(find komelia-app/desktopApp/build/compose/binaries -type f -name '*.deb' -print -quit)
test -n "$package"
version=$(sed -n 's/^app-version = "\([^"]*\)"/\1/p' gradle/libs.versions.toml)
test -n "$version"
cp "$package" "/export/Komelia-Vibe-${version}-linux-x64.deb"
