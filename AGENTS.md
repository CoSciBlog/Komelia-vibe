# Komelia-Vibe repository rules

## Repository boundaries

- This repository is the `CoSciBlog/Komelia-vibe` fork. Never push fork-specific work to `Snd-R/Komelia` or any upstream branch.
- `origin` must point to the fork; `upstream` is fetch-only for integrating original Komelia changes.
- Keep fork branding and distribution identifiers out of upstream-bound commits.

## Change workflow

- Start every change on a new `codex/<topic>` branch from an up-to-date local `main`.
- Create a feature request in the fork for each new feature and link it from the pull request.
- Test the change before opening a pull request. Open pull requests only against `CoSciBlog/Komelia-vibe:main`.
- Merge a tested pull request into the fork's `main`; never target the upstream repository.
- Update `CHANGELOG.md`, `README.md`, and version metadata for releases as appropriate.
- Disclose material AI/OpenAI Codex assistance in release notes and relevant documentation.

## Compatibility

- Preserve Komga protocol compatibility and the ability to integrate future upstream Komelia changes.
- Prefer changing distribution identity (Android application ID, packaging metadata, data directories, update source) over renaming internal Kotlin packages.
- Komelia-Vibe must remain independently installable and must not overwrite, update, or share mutable application data with upstream Komelia.
