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
7. Distributions preserve upstream attribution and applicable component licenses
   and notices; fork and AI credits must supplement the original authors' credits.

## Owner-requested change inventory

### 1. Independent Komelia-Vibe distribution identity

Introduced in 0.20.1.

- Android application ID and Gradle group: `io.github.cosciblog.komelia.vibe`.
- Since 0.20.21, Android releases use the owner's replacement 2026 signing
  certificate, SHA-256 `4f0735375fdf9ca3f7c6e75061e6a720f19f6b916b7145d87e0bdbdb7be19a41`. Preserve this
  signing identity for future in-place updates. The earlier 0.20.20 key was
  unavailable; the owner requested this replacement, so 0.20.21 requires a fresh
  installation rather than an in-place update from older releases. Release notes
  must state the need to synchronize progress and preserve local data before
  uninstalling. Never silently regenerate or replace this new key.
- The persistent 2026 key/password live outside disposable checkouts at
  `C:/00data/codex/signing/komelia-vibe/`. `AGENTS.md` and `RELEASE_SIGNING.md`
  record the paths, alias and certificate fingerprint; retain these pointers.
  A secondary copy remains in the primary checkout's ignored private release folder.
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
- Android distributions include project credits, the Apache-2.0 license, and
  bundled source component license/notice files under `assets/legal/`. Keep the
  original projects' attribution and component-specific license terms intact.
- The reusable `cmake/verify-android-apk.py` release check inspects identity,
  version, every ABI's ELF architecture and dependency resolution, 16 KiB ELF
  alignment, EPUB reader assets, and legal material. Signature continuity and
  ZIP alignment also remain mandatory before publication.

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
  the application remains online. On Android, PDF pages are rendered from the
  downloaded file instead of being fetched again from Komga. A failed connection
  must not require the user to switch manually into offline mode before opening
  local content.
- Comic readers automatically prepare the next three pages (three spreads in paged
  layouts) and one preceding page/spread, including decoding, image processing,
  panel detection where applicable, and initial rasterization. This is separate
  from the opt-in next-book download policy. Prepare the visible page first and
  process offscreen pages sequentially, with a cache bounded to ten images.
- A page turn cancels only obsolete navigation/preparation waiters; cached source
  loads and processing are reused instead of being cancelled and restarted.
  Concurrent requests for a cached page share one source load. Evicted images,
  including pending loads that finish after eviction, must be closed.
- The page-data loading indicator says "Loading" because it can represent local
  archive extraction or disk-cache access as well as a network request.
- Downloaded books reconcile local and Komga read progress by modification time
  before the reader chooses its initial page. Successful online progress updates,
  EPUB progression changes, and progress deletion are written through to the local
  database, while synchronization pulls a newer Komga value back into that database.
  SQLite must preserve the true progress modification timestamp. Leaving the image
  reader after changing pages persists the final page independently of reader-scope
  cancellation, and progress writes remain serialized so an older request cannot
  finish last and move the reader backwards.
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
- Transient channel, socket, timeout, reset, and premature-stream failures retry a
  book transfer up to three times with bounded backoff. Every failed attempt removes
  its partial file before reopening the destination, and only the final failure is
  recorded as an error. Each offline log entry exposes a copy action for its complete
  message and stack trace.
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
- `komelia-domain/offline/src/*Main/kotlin/snd/komelia/offline/mediacontainer/`
- `komelia-domain/core/src/commonMain/kotlin/snd/komelia/api/RemoteBookApi.kt`
- `komelia-ui/src/commonMain/kotlin/snd/komelia/ui/reader/`
- `komelia-ui/src/commonMain/kotlin/snd/komelia/ui/settings/offline/`
- `komelia-ui/src/commonMain/kotlin/snd/komelia/ui/series/SeriesViewModel.kt`
- offline settings models, repositories, table, and migrations under
  `komelia-infra/database/`

Regression test:

- `komelia-domain/offline/src/commonTest/kotlin/snd/komelia/offline/sync/DownloadRetentionPolicyTest.kt`
- `komelia-ui/src/commonTest/kotlin/snd/komelia/ui/reader/image/ReaderPageCacheTest.kt`
- `komelia-ui/src/commonTest/kotlin/snd/komelia/ui/reader/image/ReaderImagePreparationTest.kt`
- `komelia-domain/core/src/commonTest/kotlin/snd/komelia/api/RemoteBookApiTest.kt`

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

### 7. Direct read-status actions on comic details

Introduced in 0.20.14.

- Book and oneshot detail pages expose localized Mark read and Mark unread
  buttons beside Read and Download without requiring the overflow menu.
- Unread books offer Mark read, completed books offer Mark unread, and books
  with incomplete progress offer both actions. Visibility follows current
  progress after updates, including in the overflow menus.
- Action rows wrap on narrow displays. The buttons reuse `BookMenuActions` so
  Komga synchronization, offline progress, and download cleanup retain the
  behavior of the existing menu commands.

Primary areas:

- `komelia-ui/src/commonMain/kotlin/snd/komelia/ui/common/BookReadStatusButtons.kt`
- book and oneshot detail content under `komelia-ui/src/commonMain/`
- `BookActionsMenu.kt` and `OneshotActionsMenu.kt`

### 8. Direct series download beside publication status

Introduced in 0.20.15.

- The standard series detail page exposes a localized Download button immediately
  after its publication-status chip (including Ongoing), with wrapping and vertical
  alignment on narrow displays. Other metadata filters remain usable.
- The button has the same online/offline-platform availability as the series
  overflow-menu download command and does not require administrator privileges.
- It uses the existing notification permission and series-download confirmation,
  then calls `SeriesMenuActions.download`. Series queuing, unread-only policy,
  already-local skips, and download provenance remain unchanged.

Primary areas:

- `komelia-ui/src/commonMain/kotlin/snd/komelia/ui/series/view/SeriesContent.kt`
- `SeriesDescriptionRow.kt` and `SeriesDownloadButton.kt` in the same directory

### 9. Downloaded-series Home filter

Introduced in 0.20.16.

- Home can display a persisted Downloaded series filter, added directly from
  Add filter or selected as a series filter type. Label, order and display limit
  remain editable; existing saved filters are not overwritten. Fresh/reset
  defaults include the new filter.
- Query the local offline database even while online. A series qualifies when
  at least one complete book download is present; exclude metadata-only series
  and remove a series when its last local book is deleted. Partially downloaded
  series are included, and multiple local books must not duplicate a series.
- Apply download membership before sorting/pagination, with title-ascending
  ordering and a positive display limit. Existing book events refresh Home
  after local downloads and deletions. No Komga protocol payload changes or new
  remote download-status conditions are introduced.
- Since 0.20.17, a per-filter **Only series not fully read** checkbox hides
  fully read series while retaining unread and in-progress series. It defaults
  to false, including when decoding old saved filters. Persist the selection,
  update the editor preview when toggled, and filter the active user's local
  progress before the display limit. Progress belonging to another user must
  not affect membership.
- Preserve serialized type names of existing filters. The additive Downloaded
  type uses existing JSON storage without changing database migrations. Platforms
  without offline downloads hide its add/type option and safely load no results.

Primary areas and regression tests:

- `komelia-domain/core/src/commonMain/kotlin/snd/komelia/homefilters/HomeFilters.kt`
- `OfflineSeriesApi` / `OfflineSeriesDtoRepository` and SQLite downloaded-series query
- Home and filter editor code under `komelia-ui/src/commonMain/`
- `DownloadedSeriesFilterTest.kt`, `SeriesDownloadedFilterStateTest.kt`, and
  `DownloadedSeriesQueryTest.kt`

### 10. Manual series download deletion

Introduced in 0.20.18.

- Series overflow menus expose Delete downloaded and Delete read downloads on
  offline-capable platforms, online and offline, with localized confirmations
  and removed/retained counts in English, German, Spanish, French, Japanese,
  Russian and Simplified Chinese. Existing read/unread, Edit and Download menu
  labels use string resources; read-status visibility follows current series data.
- Operate on every local book of the requested series, independent of display
  pagination and automatic cleanup settings. Read-only deletion keeps unread and
  in-progress books. Never call Komga media deletion endpoints.
- Confirm local reading progress with Komga before deleting its download.
  Keep downloads when offline progress cannot be confirmed, another account has
  stored progress, synchronization fails, or the progress/account changes before
  deletion. Books without stored progress can be removed offline in all-downloads
  mode. Reconnect and retry for protected downloads.
- Check all accounts' progress and recheck the confirmed persisted snapshot in the
  same transaction as deletion. Do not overwrite newer local progress when pulling
  remote progress. Cancellation must stop deletion. Reuse existing local book/file
  deletion and events; preserve existing Home filters and database migrations.

Primary areas and regression tests:

- `SeriesDownloadDeletion.kt`, `SeriesDownloadDeletionTest.kt`, `OfflineModule.kt`
- `SyncReadProgressAction.kt` and `OfflineReadProgressRepository`
- `SeriesActionsMenu.kt`, `ViewModelFactory.kt`, and localized Compose resources

### 11. Recoverable image reads and update checks

Introduced in 0.20.19.

- Update versions accept both numeric versions and GitHub's `v`-prefixed tags.
  Persisted versions keep their numeric format and updates target this fork.
- Update checks leave the loading state on errors or an empty release list and
  ignore duplicate checks while one is active.
- Coil image fetches, reader page fetches and update metadata reads retry the
  complete read at most twice on I/O errors, including interrupted response bodies.
  Successful local-first behavior remains unchanged, cancellation stops retries,
  and application/HTTP errors are not retried.

Primary areas: `http/RetryNetworkRead.kt`, `image/coil/NetworkReadRetryInterceptor.kt`,
`image/BookImageLoader.kt`, `updates/AppVersion.kt`, `updates/UpdateClient.kt`, and
`ui/settings/updates/AppUpdatesViewModel.kt`, plus their regression tests.

### 12. Enlarged cover preview on detail pages

Introduced in 0.20.20.

- Clicking the cover on series, book and oneshot details opens a window-sized
  preview, fitting the complete image without cropping or distorting it.
- A visible, localized Close button remains available above the image. Normal
  Back/Escape dismissal returns to the same detail page without starting a reader.
- Book/series thumbnail loading and offline support use the existing image pipeline.
  Covers in library grids retain their existing navigation behavior.
- Preview state resets when the displayed book or series changes.

Primary areas:

- `komelia-ui/src/commonMain/kotlin/snd/komelia/ui/common/images/ExpandableCover.kt`
- series, book and oneshot detail content and localized cover strings

### 13. Configurable simultaneous book downloads

- Offline Mode → Downloads provides a discrete 1–10 slider with default 2.
- Persist the limit across restarts and upgrades, including existing installs via
  offline migration `V5__concurrent_downloads.sql`. Keep earlier migrations intact.
- One shared limiter in `BookDownloadService` covers manual, series and preload
  downloads on Android and desktop. No fixed four-download cap may override it.
- Raising the limit admits waiting books immediately. Lowering it never cancels
  active transfers; new books wait until the active count falls below the limit.
- Cancelled waiting downloads never consume a slot. Completed, failed and cancelled
  active downloads always release their slot. Values are constrained to 1–10.
- Labels and explanations are localized in all seven supported resource locales.

Primary areas: offline settings models/repositories/migration, `BookDownloadService`,
`DownloadConcurrencyLimiter`, Android/desktop download execution and offline settings UI.
Regression tests cover concurrency, live changes, cancellation and settings persistence.

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
