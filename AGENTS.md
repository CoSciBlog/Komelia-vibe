# Komelia-Vibe repository rules

## Repository boundaries

- This repository is the `CoSciBlog/Komelia-vibe` fork. Never push fork-specific work to `Snd-R/Komelia` or any upstream branch.
- `origin` must point to the fork; `upstream` is fetch-only for integrating original Komelia changes.
- Keep fork branding and distribution identifiers out of upstream-bound commits.
- Create and manage all branches, issues, pull requests, releases, and other project work only in `CoSciBlog/Komelia-vibe`, never in `Snd-R/Komelia`.
- When upstream context must be linked from fork issues, pull requests, or comments, use a `redirect.github.com/Snd-R/Komelia/...` URL instead of a normal GitHub issue/PR URL or `Snd-R/Komelia#...` reference so GitHub does not create an upstream backlink.

## Change workflow

- Start every change on a new `codex/<topic>` branch from an up-to-date local `main`.
- Create a feature request in `CoSciBlog/Komelia-vibe` for each new feature and link it from the pull request.
- Test the change before opening a pull request. Open pull requests only against `CoSciBlog/Komelia-vibe:main`.
- Merge a tested pull request into the fork's `main`; never target the upstream repository.
- Validate changes using the focused validation policy below. Fix failures before merging or publishing; record the commands, results, and any checks that could not be performed. Do not describe an untested build as verified.
- Treat delivery of completed application changes as a release workflow: increment `app-version` and the Android version code monotonically, and update `README.md`, `CHANGELOG.md`, `CREDITS.md`, and release notes for the final shipped behavior. Documentation-only and instruction-only changes follow the exception below.
- Preserve the original authors' attribution, copyright, patent, trademark, license, and applicable NOTICE texts. Follow the repository's Apache-2.0 license and each bundled dependency's own license; identify fork modifications and include required license/notice material and corresponding-source information with distributions. Do not replace upstream credits with fork or AI credits.
- Build the Android release APK from the tested release commit, using the existing Komelia-Vibe signing identity. Validate package identity, version name/code, signature/certificate continuity, all four ABIs and required native libraries, native dependency resolution, 16 KiB alignment, EPUB assets, and bundled license/credit material before export.
- After merging the tested release pull request into `CoSciBlog/Komelia-vibe:main`, publish a new version tag and GitHub release in that fork with the verified signed universal APK, SHA-256 checksum, release notes, and required license/credit material. Confirm the published tag resolves to the shipped source and verify uploaded asset hashes. Unless the owner explicitly narrows or postpones delivery, do not stop after a source-only change or an unpublished APK.
- Never publish a release when tests, signing, packaging, licensing requirements, or required release verification fail. Retain the work and explain the concrete blocker instead of silently skipping a required step.
- Disclose material AI/OpenAI Codex assistance in release notes and relevant documentation.

## Focused validation

- Minimize execution time and credit usage by running only checks needed to validate the changed areas and functions, their directly affected integrations, and relevant protected invariants. Select the smallest sufficient scope before running commands.
- Prefer existing tests for the changed behavior, filtered to the relevant test classes or methods where supported. Do not run unrelated modules or the full test suite by default. Add tests only when needed to cover meaningful changed behavior or a regression.
- Compile only the platforms affected by the change. Shared-code changes may require checks on multiple affected targets; do not automatically compile Android, desktop, and WebAssembly for every edit.
- Broaden validation only when the change has wider impact, a failure points outside the initial scope, or a concrete unresolved risk requires it. Broad dependency, build-system, database-migration, or upstream-integration changes can justify broader checks. Briefly record why extra checks were necessary.
- Reuse successful checks for unchanged code and configuration. Do not repeat passing checks solely because a pull request is opened, merged, tagged, or published, or because only documentation/version metadata changed. Rerun checks when subsequent changes invalidate their results; release artifacts must still be built from the final release commit.
- For documentation-only and instruction-only changes, including `AGENTS.md`, review the content and run `git diff --check`. Do not run application tests, compile the app, bump app versions, or build/publish binaries solely for these changes unless the owner explicitly requests it.
- For actual releases, retain the required artifact-specific checks: package identity/version, signing continuity, universal ABIs and native dependencies, alignment, EPUB assets, licenses/credits, source/tag correspondence, and uploaded hashes. These checks do not require rerunning unrelated application tests. Never skip a necessary safety check merely to save time or credits.

## Compatibility

- Preserve Komga protocol compatibility and the ability to integrate future upstream Komelia changes.
- Prefer changing distribution identity (Android application ID, packaging metadata, data directories, update source) over renaming internal Kotlin packages.
- Komelia-Vibe must remain independently installable and must not overwrite, update, or share mutable application data with upstream Komelia.
- Export and publish Android builds only as verified universal APKs containing `arm64-v8a`, `armeabi-v7a`, `x86_64`, and `x86`; never publish an ABI-specific APK or relabel one as universal.

## Android release signing key

- Reuse the existing 2026 release key; never generate a replacement for a new version.
- Canonical persistent key: `C:/00data/codex/signing/komelia-vibe/komelia-vibe-release-2026.p12`.
- Password file: `C:/00data/codex/signing/komelia-vibe/signing-password-2026.txt`.
- PKCS12 alias: `komelia-vibe-2026`. The non-secret `signing-identity.json` in that
  directory records this identity and its backup location.
- Required certificate SHA-256: `4f0735375fdf9ca3f7c6e75061e6a720f19f6b916b7145d87e0bdbdb7be19a41`.
  Verify this fingerprint before publishing so updates from 0.20.21 retain data.
- A secondary copy remains in `C:/00data/GIT/Komelia-vibe/release-artifacts/private/`
  under the same 2026 filenames. The older unversioned key is a different identity;
  do not use it. Consult `RELEASE_SIGNING.md` before signing.
- Never commit or upload private key/password files or print password contents.
  The canonical directory is outside Git/worktrees and must survive their cleanup.

## Fork change preservation

- `FORK_CHANGES.md` is the authoritative register of owner-requested fork behavior and protected invariants. Read it completely before integrating, merging, or rebasing changes from `upstream`.
- Preserve documented behavior across upstream refactors; do not rely only on old file paths or symbol names when checking whether a customization still exists.
- Never resolve conflicts in a protected area by blindly accepting the upstream side or by dropping fork database migrations, distribution identifiers, storage separation, tests, or safety checks.
- An upstream implementation may replace fork-specific code only after its behavior, defaults, persistence, migrations, compatibility, and edge cases have been compared and verified by relevant tests.
- Update `FORK_CHANGES.md` in the same pull request whenever an owner-requested fork behavior is added, changed, replaced by an upstream implementation, or deliberately removed.
- Every upstream-integration pull request must state which registered changes were reviewed, how conflicts were resolved, and which tests or package inspections confirm that the protected behavior remains intact.
