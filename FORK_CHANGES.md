# Komelia-Vibe fork change register

This file is the durable inventory of owner-requested behavior that differs from
upstream [Snd-R/Komelia](https://github.com/Snd-R/Komelia). Its purpose is to keep
those changes intact when upstream commits are merged into this fork.

Treat the behavior and invariants below as protected. File paths are navigation
hints, not the definition of the feature: upstream refactors may move the code.
If upstream adds an equivalent implementation, compare behavior, persisted data,
package identity, and tests before replacing the fork implementation. Update this
register in the same pull request whenever an owner-requested fork behavior is
added, changed, superseded, or deliberately removed.

## Protected invariants

These requirements must remain true after every upstream integration:

1. Komelia-Vibe is independently installable and must not overwrite, upgrade, or
   share mutable application data with upstream Komelia.
2. Komga protocol models and the internal `snd.komelia` Kotlin packages stay
   upstream-compatible. Fork branding belongs at distribution boundaries.
3. Restricted Komga users can download books for offline use. Offline access is
   limited by the locally downloaded data rather than copied remote library and
   label restrictions.
4. Android release APKs are universal and contain every required native library
   for `arm64-v8a`, `armeabi-v7a`, `x86_64`, and `x86`, plus EPUB reader assets.
   This includes `libsqlitejdbc.so` for every ABI.
5. User-created offline reading progress is never deleted before successful
   synchronization with Komga.
6. Fork-specific settings retain their stored values and database migrations
   across upgrades. Existing migrations must never be renumbered or silently
   dropped.

## Owner-requested change inventory

### 1. Independent Komelia-Vibe distribution identity

Introduced in 0.20.1.

- Android application ID and Gradle group: `io.github.cosciblog.komelia.vibe`.
- Android preferences namespace: `io.github.cosciblog.komelia.vibe.preferences`.
- Visible application and window name: `Komelia-Vibe`, including all Android
  distribution manifests so legacy installers show the correct name.
- Desktop package name/vendor: `Komelia-Vibe` / `CoSciBlog`.
- Windows upgrade UUID: `ED3F8A54-02CD-47E5-8E1F-E65DC1057E43`.
- Desktop data, cache, native-library, ONNX Runtime, and FileKit locations use
  fork-specific `Komelia-Vibe` or `komelia-vibe` identities.
- In-app update discovery targets `CoSciBlog/Komelia-vibe`, never the upstream
  release feed.
- The in-app current-version value is generated from `app-version` in the Gradle
  version catalog; do not replace it with a hard-coded Kotlin version. Android
  updates prefer the fork's `-android-universal.apk` release asset.
- The root project and release metadata use Komelia-Vibe branding, while internal
  Kotlin packages intentionally remain `snd.komelia`.

Primary areas:

- `komelia-app/androidApp/build.gradle.kts`
- `komelia-app/androidApp/src/main/`
- `komelia-app/desktopApp/`
- `komelia-domain/core/src/commonMain/kotlin/snd/komelia/updates/UpdateClient.kt`
- `komelia-domain/core/src/jvmMain/kotlin/snd/komelia/AppDirectories.kt`
- `komelia-infra/jni/` and `komelia-infra/onnxruntime/`
- `settings.gradle.kts`, Fastlane metadata, README, and release metadata

Do not resolve identity conflicts by accepting upstream values. An upstream
rename may be adopted internally, but the fork's external identifiers and storage
separation must remain independent.

### 2. Offline downloads for restricted Komga users

Introduced in 0.20.2 and released across all platforms in 0.20.5.

- `KomgaUser.toOfflineUser` deliberately does not copy remote library-sharing,
  label, or age restrictions into the offline user.
- The offline user is treated as sharing all locally present libraries because
  the offline database contains only content the user explicitly downloaded.
- Komga remains responsible for authorization while online.
- This prevents foreign-key failures when a restricted account downloads a book
  without having every remotely referenced library available offline.

Primary implementation and regression test:

- `komelia-domain/offline/src/commonMain/kotlin/snd/komelia/offline/user/model/OfflineUser.kt`
- `komelia-domain/offline/src/commonTest/kotlin/snd/komelia/offline/user/model/OfflineUserTest.kt`

Upstream replacement is acceptable only if restricted users can still download
and open authorized books offline without importing inaccessible library records.

### 3. Reliable Android native build and APK packaging

Introduced in 0.20.2, with SQLite loading fixed in 0.20.3 and the visible Android
name fixed in 0.20.4.

- Shell build scripts are kept LF-only through `.gitattributes`.
- Docker builds trust the mounted repository and provide the tools needed by the
  native and packaging stages.
- `cmake/android-isolated-build.sh` builds from disposable container-local copies
  so native dependency cleanup cannot damage the host checkout or submodules.
- Android JNI copy task names match the architecture tasks documented in README.
- Packaging validation fails before export if native libraries, SQLite JNI, or the
  `komga.html` / `ttsu.html` EPUB readers are missing.
- `libpng16.so` is included because it is the SONAME required by Android `libvips`.
- APK ABI filters always include `arm64-v8a`, `armeabi-v7a`, `x86_64`, and `x86`.
  Packaging must fail if any ABI lacks the complete native dependency set;
  AndroidX-provided libraries must not make an incomplete ABI look supported.
- SQLite extraction and JNI preparation occur before Android native-library merge.
- Android uses extracted JNI packaging so `System.loadLibrary("sqlitejdbc")` can
  resolve the packaged `libsqlitejdbc.so` from `nativeLibraryDir`.
- Release APKs remain unsigned build outputs until aligned and signed with the
  private Komelia-Vibe release key; verification must cover signature, identity,
  ABI, required libraries/assets, dependency resolution, and 16 KiB alignment.

Primary areas:

- `.gitattributes`, `.gitignore`, and `build.gradle.kts`
- `cmake/android-build.sh`, `cmake/android-isolated-build.sh`, and
  `cmake/android.Dockerfile`
- `komelia-app/androidApp/build.gradle.kts`
- `komelia-infra/database/sqlite/build.gradle.kts`
- `komelia-infra/jni/build.gradle.kts`
- Android build and verification instructions in `README.md`

Never remove a packaging check merely because compilation succeeds. A valid
release must also install and load its native libraries at runtime. Never publish
an ABI-specific APK or rename one to look like a universal artifact.

### 4. Isolated Windows and Linux release builds

Introduced for the 0.20.5 release.

- Desktop native builds can run against read-only source mounts and export through
  disposable container-local checkouts.
- Windows and Linux build images include the packaging tools used by the release
  workflow, including `fakeroot` for Debian packages.
- The libffi/native dependency adjustments and LF handling prevent Windows-hosted
  checkouts from corrupting or rewriting dependency source trees.
- `cmake/desktop-isolated-build.sh` and `cmake/linux-isolated-package.sh` are the
  preferred safe paths when the checkout or submodules contain local work.

Primary areas:

- `cmake/desktop-isolated-build.sh`
- `cmake/linux-isolated-package.sh`
- platform Dockerfiles and platform build scripts under `cmake/`
- desktop build instructions in `README.md`

### 5. Read-aware downloads, local-first reading, preloading, and cleanup

Introduced in 0.20.6. Both user-facing policies are opt-in and default to `false`.

- **Download only unread series books** skips completed books when a full series
  is queued, but continues to download unread and in-progress books.
- **Delete downloaded books after reading** queues local deletion only after Komga
  confirms completed progress.
- Opening a series while online checks its remote books and removes local downloads
  completed online or on another device. This handles cases where a user has moved
  ahead in a series elsewhere.
- Progress created while offline is retained until synchronization succeeds; an
  error or unavailable server must not cause local deletion.
- The implementation uses existing Komga models and the fork's offline task layer;
  it must not introduce incompatible Komga protocol payloads.
- Offline settings migration `V2__download_cleanup_settings.sql` and its persisted
  columns are part of the upgrade contract.
- Image and EPUB readers prefer an already downloaded book automatically, even when
  the application remains online. A failed connection must not require the user to
  switch manually into offline mode before opening local content.
- Next-book preloading has an explicit opt-in checkbox and a discrete threshold
  slider accepting `1` through `20` remaining pages. Preloading requires a validated
  Wi-Fi or mobile-data connection and skips a book already downloaded or requested.
- Switching to a preloaded next book resets the reader page before publishing the
  new book state. Paged and panel readers must treat missing or empty page selections
  as recoverable state and never use `indexOfFirst`'s `-1` result as a list index.
- A separate opt-in notification reports when a next-book preload starts.
- Local-first page loading falls back to Komga when local extraction fails and a
  connection is available. Local page-load and preload failures must retain book ID,
  page/threshold context, and a full stack trace in the offline log.
- The offline log retains per-book download provenance for manual downloads,
  complete-series downloads, and reader preloads. It distinguishes newly downloaded
  files, already-local skips, local/offline reader loads, and Komga server loads.
  Persisted download tasks without provenance must continue to decode as manual
  downloads so upgrades do not lose queued work.
- Advancing from an image book records completed progress, allowing the existing
  delete-after-reading policy to remove it only after Komga confirms that progress.
- Offline settings migration `V3__reader_preload_settings.sql` and its persisted
  preload threshold and notification columns are part of the upgrade contract.
- Offline settings migration `V4__preload_enabled_setting.sql` separates activation
  from the threshold. It keeps old positive thresholds enabled, maps the former
  disabled value `0` to an unchecked checkbox, and retains a usable slider value.

Primary areas:

- `komelia-domain/offline/src/commonMain/kotlin/snd/komelia/offline/settings/`
- `komelia-domain/offline/src/commonMain/kotlin/snd/komelia/offline/sync/`
- `komelia-domain/offline/src/commonMain/kotlin/snd/komelia/offline/tasks/`
- `komelia-domain/core/src/commonMain/kotlin/snd/komelia/api/RemoteBookApi.kt`
- `komelia-ui/src/commonMain/kotlin/snd/komelia/ui/reader/`
- `komelia-ui/src/commonMain/kotlin/snd/komelia/ui/settings/offline/`
- `komelia-ui/src/commonMain/kotlin/snd/komelia/ui/series/SeriesViewModel.kt`
- offline settings models, repositories, table, and migrations under
  `komelia-infra/database/`

Regression test:

- `komelia-domain/offline/src/commonTest/kotlin/snd/komelia/offline/sync/DownloadRetentionPolicyTest.kt`

An upstream implementation may replace this code only after confirming equivalent
defaults, cross-device cleanup, sync-before-delete safety, and migration continuity.

### 6. Configurable comic-reader status overlay

Introduced after 0.20.7.

- The image reader can display the current time, Android battery percentage, and
  current/total page count in paged, continuous, and panel modes.
- Clock, battery, and page count can be enabled independently. Position supports
  top or bottom combined with left, center, or right alignment.
- Text color and font size are user-configurable. Preferences apply consistently
  across books and survive application restarts and upgrades.
- App migration `V14__reader_status_overlay.sql` and the serializable setting
  defaults are part of the persistence and cross-target compatibility contract.
- Platforms without a supported battery source omit only the battery value; time
  and page progress remain available.

Primary areas:

- `komelia-domain/core/src/commonMain/kotlin/snd/komelia/settings/`
- `komelia-infra/database/shared/` and `komelia-infra/database/sqlite/`
- `komelia-ui/src/commonMain/kotlin/snd/komelia/ui/reader/image/`
- Android battery integration under `komelia-ui/src/androidMain/`

An upstream replacement is acceptable only if it preserves all display choices,
stored values, migration continuity, all image-reader modes, and graceful behavior
on platforms where battery information is unavailable.

## Required upstream-integration procedure

Before merging or rebasing upstream changes:

1. Read this file and `AGENTS.md` completely.
2. Fetch without pushing: `git fetch upstream main`.
3. Record the current fork delta with:
   - `git log --oneline --no-merges upstream/main..main`
   - `git diff --name-status upstream/main...main`
4. Integrate upstream on a dedicated `codex/<topic>` branch. Never resolve a
   conflict by wholesale checkout of either side in a protected area.
5. For every protected item above, verify behavior rather than just checking that
   the old file or symbol still exists. Follow upstream refactors to the new code.
6. If upstream now provides equivalent behavior, compare defaults, persistence,
   migrations, package identity, and edge cases. Remove duplicate fork code only
   when equivalence is demonstrated by tests and document the replacement here.
7. Run the relevant regression tests plus platform compilation/package checks for
   every affected target. For Android packaging changes, inspect the final APK.
8. Review `git diff upstream/main...HEAD` once more for accidentally restored
   upstream branding, identifiers, paths, update URLs, or removed migrations.
9. Update this register and `CHANGELOG.md` in the same pull request when the fork
   delta or its preservation requirements change.

## Maintenance notes

- Release version numbers, commit IDs, and file paths are useful breadcrumbs, but
  the documented behavior and invariants take precedence after refactors.
- Historical release details remain in `CHANGELOG.md`; this register focuses on
  what future integrations must preserve.
- Material AI/OpenAI Codex assistance must continue to be disclosed according to
  `AGENTS.md`.
