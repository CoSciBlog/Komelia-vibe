# Android release signing

Starting with 0.20.21, Komelia-Vibe uses a replacement RSA-4096 signing key,
created at the owner's request after the key for published 0.20.20 could not be
located. This is a deliberate signing change, not an Android key-rotation lineage.

Certificate SHA-256:

`4f0735375fdf9ca3f7c6e75061e6a720f19f6b916b7145d87e0bdbdb7be19a41`

The public certificate is [release-signing/android-2026.pem](release-signing/android-2026.pem).
The application ID remains `io.github.cosciblog.komelia.vibe`.

## Installation consequences

Android cannot install 0.20.21 over a version signed with an earlier key. It must
be installed on a device without that earlier Komelia-Vibe installation. Before
uninstalling an older version, synchronize pending offline reading progress with
Komga and preserve any required local data. Uninstalling removes local app settings
and downloaded books. These are not automatically migrated to a fresh installation.

Later releases must reuse this 2026 key so updates from 0.20.21 retain their data.
Do not regenerate a key for each release or claim compatibility with the earlier
certificate without a verified Android signing lineage.

## Private release material

The canonical persistent location is outside every repository checkout and
managed worktree:

`C:/00data/codex/signing/komelia-vibe/`

- `komelia-vibe-release-2026.p12`, PKCS12 alias `komelia-vibe-2026`.
- `signing-password-2026.txt`, containing its generated password.
- `signing-identity.json`, a non-secret manifest recording paths, alias, fingerprint
  and the secondary copy's location.

Directory access is limited to the owner's Windows account, SYSTEM and local
administrators. A byte-identical secondary key/password copy remains under
`C:/00data/GIT/Komelia-vibe/release-artifacts/private/` with the same 2026 filenames.
`ACTIVE_SIGNING_IDENTITY.json` there points to the canonical location. The older
unversioned keystore has a different certificate and must not sign future releases.

`AGENTS.md` records the canonical path so future release work can find it directly.
Never remove the canonical directory when cleaning up worktrees. Keep an additional
secure backup outside this computer to protect against disk loss. Never commit or
upload the private files, print the password or silently replace this identity.
Public release assets may include only the public certificate.

Before publishing, verify the APK certificate against the fingerprint above, all
four required ABIs and the complete packaging checks documented in README. Signing
continuity for future releases means matching this certificate.

This signing transition, documentation and release preparation received material
assistance from OpenAI Codex.
