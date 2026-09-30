# Changelog

All notable changes to the Komelia-Vibe fork are documented here.

## 0.20.4 - 2026-09-30

- Fix legacy Android package installers displaying the internal `snd.komelia.App` class name instead of **Komelia-Vibe** after installation.
- Store the Android application label directly in every distribution manifest so installers do not need to resolve a string resource for the visible name.
- This fix and its Android package verification were developed with assistance from OpenAI Codex.

## 0.20.3 - 2026-09-30

- Fix Android startup failures where `System.loadLibrary("sqlitejdbc")` could not find the packaged `libsqlitejdbc.so`.
- Extract packaged JNI libraries into Android's native library directory during installation for reliable runtime loading.
- This fix and its Android package verification were developed with assistance from OpenAI Codex.

## 0.20.2 - 2026-09-30

- Fix offline book downloads for Komga users with access to only selected libraries. Offline users are now unrestricted because the offline database contains only explicitly downloaded data.
- Fix Android Docker builds from Windows checkouts by enforcing LF shell-script line endings and trusting Git repositories only within the container's `/build` mount.
- Correct the Android JNI-copy task names and document Windows SDK setup and release APK signing/verification.
- Provide an isolated Android native build that keeps dependency cleanup inside disposable container checkouts.
- Include `libpng16.so`, the actual PNG dependency required by Android's `libvips.so`.
- Reject Android exports when required native libraries (including SQLite) or EPUB reader assets are missing.
- Default APKs to ARM64 and allow selecting complete ABI sets with `komelia.android.abis`, avoiding incomplete ABIs pulled in by AndroidX dependencies.
- Order Android JNI copying and SQLite extraction before native-library merging when preparing and packaging in one Gradle invocation.
- Android build fixes developed with assistance from OpenAI Codex.
- The offline-download fix and release work were developed with assistance from OpenAI Codex.

## 0.20.1 - 2026-09-28

- Renamed the distributable applications to **Komelia-Vibe**.
- Added the independent Android application ID `io.github.cosciblog.komelia.vibe`, so Komelia-Vibe can be installed next to upstream Komelia.
- Added independent desktop package metadata, application data/cache paths, and Windows upgrade identity.
- Redirected in-app update discovery to releases from the Komelia-Vibe fork.
- Documented the fork-only development and upstream-integration workflow.
- Changes in this release were developed with the assistance of AI and OpenAI Codex and reviewed through the normal build/test workflow.

The application remains compatible with Komga and intentionally retains upstream internal source packages to reduce merge conflicts.
