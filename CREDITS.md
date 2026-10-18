# Komelia-Vibe credits

Updated for Komelia-Vibe 0.20.17, 2026-10-18.

Komelia-Vibe is an independently distributed fork of
[Komelia by Snd-R and its contributors](https://github.com/Snd-R/Komelia).
The original application's authors, translators, and contributors retain their
credit and copyright. The application source is distributed under the
[Apache License 2.0](LICENSE); dependency licenses remain separate.

[CoSciBlog](https://github.com/CoSciBlog/Komelia-vibe) maintains the fork's
distribution identity, offline-reading changes, reader status overlay, and page
preparation improvements. Fork modifications are documented in
[FORK_CHANGES.md](FORK_CHANGES.md) and [CHANGELOG.md](CHANGELOG.md), with authorship
and file-level changes recorded in Git history.

The 0.20.17 unread-only downloaded-series Home filter, regression tests, release preparation, and
documentation were prepared with material assistance from OpenAI Codex. This
acknowledgment supplements the original project credits.

## Bundled readers and components

- [Komga](https://github.com/gotson/komga), including the EPUB web reader:
  Gauthier Roebroeck and contributors; the bundled reader's MIT license is retained.
- [ッツ Reader](https://github.com/ttu-ttu/ebook-reader): ッツ Reader Authors and
  contributors; the bundled reader's BSD 3-Clause license is retained.
- Readium reader styles retain their accompanying license text.
- Kotlin, Jetpack Compose, Compose Multiplatform, AndroidX, Ktor, Coil, Voyager,
  and the other JVM/Android libraries retain their respective authors and licenses.
- libvips and its image-codec dependencies, ONNX Runtime, SQLite JDBC, and the
  native libraries retain their respective authors, copyright, and licenses.
  Dependency source revisions are recorded by Git submodules, the Gradle version
  catalog, and the repository's native build definitions.

The Android APK includes project and bundled source component license/notice
files in `assets/legal/`. These acknowledgments do not replace or relicense any
dependency. Consult each component's complete license and notices for its terms.

## Source for released binaries

The release tag in [CoSciBlog/Komelia-vibe](https://github.com/CoSciBlog/Komelia-vibe)
identifies the application source used for that release. Clone the tag with its
submodules to obtain the pinned dependency revisions. Native build definitions
and patches are included under `cmake/` and `komelia-infra/`; Android build and
repackaging instructions are in [README.md](README.md). The universal APK uses
shared native libraries. Fork changes do not claim ownership of upstream code
or remove the rights granted by the component licenses.
