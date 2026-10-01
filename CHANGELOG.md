# Changelog

All notable changes to the Komelia-Vibe fork are documented here.

## Unreleased

- Render downloaded PDF pages locally on Android instead of hitting the
  `NotImplementedError` fallback and loading them again from Komga.
- This fix and its tests were prepared with assistance from OpenAI Codex.

## 0.20.10 - 2026-10-01

- Fix an `IndexOutOfBoundsException` when the reader switches to a preloaded next
  book while still exposing the completed book's final page, and safely handle
  missing or empty page selections in paged and panel modes.
- This fix and its tests were prepared with assistance from OpenAI Codex.

## 0.20.9 - 2026-10-01

- Extend the offline log with per-book download history for manual downloads,
  complete-series downloads, and reader preloads.
- Record whether each requested book was freshly downloaded or skipped because an
  identical local download was already available.
- Record whether opening a book loads its existing local copy or reads it from the
  Komga server, including book title and ID for diagnosis.
- Keep older persisted download tasks compatible by defaulting missing origin data
  to a manual download.
- This change and its tests were prepared with assistance from OpenAI Codex.

## 0.20.8 - 2026-10-01

- Replace the unreliable next-book preload number field with an explicit enable
  checkbox and a discrete 1–20 page slider.
- Preserve previously enabled preload settings through offline database migration
  V4 while keeping preloading disabled for users whose old threshold was `0`.
- Fix reader failures after a preload/local-download transition by falling back to
  Komga when a locally stored page cannot be decoded, and record preload/page-load
  failures with book, page, and stack-trace details in the offline log.
- Add a configurable comic-reader overlay for the current time, Android battery
  level, and page progress such as `1/17` in paged, continuous, and panel modes.
- Allow each overlay value to be enabled independently and configure its top/bottom
  and left/center/right position, text color, and font size.
- Persist reader-overlay preferences through app database migration V14 while
  retaining serialization defaults for web and future upstream compatibility.
- These changes were prepared with assistance from OpenAI Codex.

## 0.20.7 - 2026-09-30

- Ship an Android-only release; Windows and Linux packages are intentionally not rebuilt.
- Make every Android debug and release export a verified universal APK containing
  ARM64, ARMv7, x86_64, and x86 native libraries; incomplete ABI sets now fail
  packaging instead of producing an architecture-specific artifact.
- Derive the installed version shown by the update screen from the same Gradle
  version metadata used by the APK instead of a stale hard-coded value.
- Prefer a release asset ending in `-android-universal.apk` during Android
  self-updates, with the existing generic APK fallback retained for older releases.
- Prefer downloaded image and EPUB book content automatically, regardless of the
  current online/offline mode, to reduce mobile-data use and reader latency.
- Add a configurable next-book preload threshold from 0 to 20 remaining pages;
  `0` disables preloading and an optional notification announces the download.
- Require a validated network connection before preloading and skip books that are
  already downloaded or already queued.
- Mark the current image book complete when advancing so the existing opt-in
  post-reading cleanup can safely remove it after Komga confirms the progress.
- Document and enforce that branches, issues, pull requests, releases, and other
  project work belong only to the `CoSciBlog/Komelia-vibe` fork.
- These changes and their tests were prepared with assistance from OpenAI Codex.

## 0.20.6 - 2026-09-30

- Add an opt-in series-download filter that skips books already marked as read while retaining unread and in-progress books.
- Add an opt-in storage policy that removes downloaded books after their completed reading progress has been confirmed by Komga.
- Clean up older downloaded books that were completed online or on another device when their series is opened online.
- Preserve offline reading progress until it has synchronized successfully, preventing automatic cleanup from losing unsent progress.
- Keep both policies disabled by default and implement them entirely in the fork's offline layer for Komga and future upstream Komelia compatibility.
- This feature, its tests, Android build, and release verification were prepared with assistance from OpenAI Codex.

## 0.20.5 - 2026-09-30

- Ship the restricted-library offline-download correction across the Windows, Linux, and Android distributions.
- Offline users remain unrestricted because the offline database contains only explicitly downloaded books; Komga continues to enforce access while online.
- Retain upstream-compatible internal packages and model boundaries so the eventual upstream implementation can replace the fork patch cleanly.
- Include the Android SQLite native-loading and visible application-name fixes introduced in 0.20.3 and 0.20.4.
- This release, its cross-platform builds, and verification were prepared with assistance from OpenAI Codex.

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
