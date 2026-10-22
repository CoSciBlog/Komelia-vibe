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

The maintainer's persistent files are kept outside Git under
`release-artifacts/private/` in the primary checkout:

- `komelia-vibe-release-2026.p12`, PKCS12 alias `komelia-vibe-2026`.
- `signing-password-2026.txt`, containing its generated password.

Keep a secure backup of both files. Never commit or upload them, print the password,
or replace them silently. Worktrees must use this same private key rather than
creating their own. Public release assets may include only the public certificate.

Before publishing, verify the APK certificate against the fingerprint above, all
four required ABIs and the complete packaging checks documented in README. Signing
continuity for future releases means matching this certificate.

This signing transition, documentation and release preparation received material
assistance from OpenAI Codex.
