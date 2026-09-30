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
- Update `CHANGELOG.md`, `README.md`, and version metadata for releases as appropriate.
- Disclose material AI/OpenAI Codex assistance in release notes and relevant documentation.

## Compatibility

- Preserve Komga protocol compatibility and the ability to integrate future upstream Komelia changes.
- Prefer changing distribution identity (Android application ID, packaging metadata, data directories, update source) over renaming internal Kotlin packages.
- Komelia-Vibe must remain independently installable and must not overwrite, update, or share mutable application data with upstream Komelia.
- Export and publish Android builds only as verified universal APKs containing `arm64-v8a`, `armeabi-v7a`, `x86_64`, and `x86`; never publish an ABI-specific APK or relabel one as universal.

## Fork change preservation

- `FORK_CHANGES.md` is the authoritative register of owner-requested fork behavior and protected invariants. Read it completely before integrating, merging, or rebasing changes from `upstream`.
- Preserve documented behavior across upstream refactors; do not rely only on old file paths or symbol names when checking whether a customization still exists.
- Never resolve conflicts in a protected area by blindly accepting the upstream side or by dropping fork database migrations, distribution identifiers, storage separation, tests, or safety checks.
- An upstream implementation may replace fork-specific code only after its behavior, defaults, persistence, migrations, compatibility, and edge cases have been compared and verified by relevant tests.
- Update `FORK_CHANGES.md` in the same pull request whenever an owner-requested fork behavior is added, changed, replaced by an upstream implementation, or deliberately removed.
- Every upstream-integration pull request must state which registered changes were reviewed, how conflicts were resolved, and which tests or package inspections confirm that the protected behavior remains intact.
