# Rodemap Air — Rebrand Manifest

Fork base: jqssun/android-airplay-server **v0.0.36** (commit `a46cca0`), submodules untouched.
Rebrand: applicationId `app.rodemap.air`, versionCode 1, versionName 1.0.0, app name "Rodemap Air".
Brand background color sampled from the Rodemap icon: **#070B16**.
License: GPL-3.0 — all LICENSE files (repo root and submodules) are **unchanged**.

## Modified files (6)

| Path | Change |
|---|---|
| `app/build.gradle.kts` | applicationId → `app.rodemap.air`; versionCode 36 → 1; versionName 0.0.36 → 1.0.0 (exactly 3 lines; namespace unchanged) |
| `app/src/main/res/values/strings.xml` | `app_name` → "Rodemap Air"; `notification_channel` → "Rodemap Air"; `notification_title` → "Rodemap Air Running"; `notification_text` → "Accepting connections"; `notification_pin_title` → "Rodemap Air PIN Required"; `dialog_pin_title` → "Rodemap Air PIN". All other strings (protocol descriptions etc.) unchanged |
| `app/src/main/kotlin/io/github/jqssun/airplay/Prefs.kt` | `SERVER_NAME` default "Android AirPlay" → "Rodemap Air" (1 line; nothing else in Kotlin) |
| `app/src/main/res/mipmap-anydpi-v26/ic_launcher.xml` | Adaptive icon: background → `@color/ic_launcher_background` (solid #070B16), foreground → `@drawable/ic_launcher_foreground_img`; monochrome still references the existing `@drawable/ic_launcher_monochrome` vector (unchanged) |
| `app/src/main/res/mipmap-anydpi-v26/ic_launcher_round.xml` | Same adaptive-icon update as ic_launcher.xml |
| `README.md` | Head only: title → "Rodemap Air", shield + store badge blocks removed, Rodemap Air intro line + upstream credit line added ("Fork of jqssun/android-airplay-server (GPL-3.0), built on UxPlay"). Everything below the head is byte-identical to upstream |

## Deleted files (1)

| Path | Reason |
|---|---|
| `app/src/main/res/drawable/banner.xml` | Replaced by the PNG banner below. `AndroidManifest.xml` is **not** edited: its `android:banner="@drawable/banner"` attribute now resolves to `drawable-nodpi/banner.png`, and `android:label` already references `@string/app_name` |

## Added files (13)

| Path | Spec |
|---|---|
| `app/src/main/res/values/colors.xml` | `<color name="ic_launcher_background">#070B16</color>` (sampled dominant brand-icon background) |
| `app/src/main/res/drawable-nodpi/ic_launcher_foreground_img.png` | 432×432 transparent canvas, brand icon at 72% (311×311) centered — adaptive-icon foreground |
| `app/src/main/res/drawable-nodpi/banner.png` | 320×180 TV banner: solid #070B16 background, brand icon 140×140 at left, no text |
| `app/src/main/res/mipmap-mdpi/ic_launcher.png` | 48×48 legacy icon (Fire OS 6 / API 25 has no adaptive icons) |
| `app/src/main/res/mipmap-mdpi/ic_launcher_round.png` | 48×48, circular-cropped |
| `app/src/main/res/mipmap-hdpi/ic_launcher.png` | 72×72 |
| `app/src/main/res/mipmap-hdpi/ic_launcher_round.png` | 72×72, circular-cropped |
| `app/src/main/res/mipmap-xhdpi/ic_launcher.png` | 96×96 |
| `app/src/main/res/mipmap-xhdpi/ic_launcher_round.png` | 96×96, circular-cropped |
| `app/src/main/res/mipmap-xxhdpi/ic_launcher.png` | 144×144 |
| `app/src/main/res/mipmap-xxhdpi/ic_launcher_round.png` | 144×144, circular-cropped |
| `app/src/main/res/mipmap-xxxhdpi/ic_launcher.png` | 192×192 |
| `app/src/main/res/mipmap-xxxhdpi/ic_launcher_round.png` | 192×192, circular-cropped |

Plus this manifest itself (`REBRAND_MANIFEST.md`, added — upload optional, it documents the change set).

**Total: 20 changed files (6 modified, 1 deleted, 13 added) + this manifest.**

## Notes for the fork build

- Signing: repo secrets `STORE` (base64 of the keystore, decoded by CI to `upload.jks` at repo root) and `LOCAL` (base64 of a local.properties with `storeFile`/`storePassword`/`keyAlias`/`keyPassword` — the exact keys `app/build.gradle.kts` reads). Signing material lives outside this tree in `~/workspace/rodemap-air/signing/`.
- ⚠️ Path check if CI signing fails: Gradle's `file(storeFile)` in the app module resolves relative to the **app/** directory, while CI writes `upload.jks` to the repo root. The prepared local.properties uses `storeFile=upload.jks` as specified; if the fork's first CI run reports the keystore missing, change that one value to `storeFile=../upload.jks` and re-encode the LOCAL secret.
- The upstream clone (`~/workspace/rodemap-air/android/`) is untouched — this tree is the only rebranded copy. Its sandbox-only `local.properties` (sdk.dir) was deliberately excluded.
