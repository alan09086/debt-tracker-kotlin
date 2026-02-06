# Production Hardening & Play Store Release Plan

**Goal:** Fix critical bugs, harden for production, and publish to Google Play Store.

**Architecture:** MVVM + Repository pattern. Room for persistence, Gson for JSON backup serialization, Jetpack Compose UI.

**Tech Stack:** Kotlin 2.0.21, Android SDK 35, Jetpack Compose (BOM 2024.02.00), Room 2.6.1, Gson 2.10.1

**App ID:** `com.agsoftware.debttracker` (AG Software)

**Build:** `./gradlew bundleRelease` (JDK 21 pinned in gradle.properties)

---

## Phase 1: Critical Fixes - COMPLETE

### Task 1: Fix ProGuard package paths - DONE
- Fixed `com.debttracker.app.data.*` → `com.debttracker.app.data.model.*`
- Added TransactionType enum keep rule
- Removed overly broad Compose blanket keep

### Task 2: Remove broken getBackupJson() and dead methods - DONE
- Removed race-condition `getBackupJson()` from ViewModel
- Removed dead `getBackupData()` and `getAllPersonsSnapshot()` from repository

### Task 3: Add database transactions - DONE
- Wrapped 6 repository methods in `database.withTransaction{}`
- Added database parameter to DebtRepository constructor

## Phase 2: Data Integrity - COMPLETE

### Task 4: Fix currency formatting locale - DONE
- Added `Locale.US` to all `String.format("%.2f")` calls (8 occurrences, 5 files)

### Task 5: Fix PersonCard Flow recomposition - DONE
- Wrapped Flow in `remember(person.id)` in PersonCard

### Task 6: Enable Room schema export - DONE
- Set `exportSchema = true`, configured KSP schema output directory

## Phase 3: Cleanup - COMPLETE

### Task 7: Remove unused dependencies - DONE
- Removed navigation-compose, datastore-preferences, ui-graphics
- Added explicit lifecycle-viewmodel-compose

### Task 8: Remove unused code - DONE
- Removed unused semantic colors (PositiveBalance, NegativeBalance, ZeroBalance)
- Removed unused imports in Theme.kt and Type.kt

### Task 9: Fix deprecated API calls - DONE
- ArrowBack → AutoMirrored.Filled.ArrowBack
- Divider → HorizontalDivider

## Phase 4: Verification - COMPLETE

### Task 10: End-to-end verification - DONE
- Release build successful
- Backup/restore code path verified
- Version bumped to 1.1.0

## Phase 4.5: Bugfixes - COMPLETE

### Task 10.1: Fix confirmation dialog button cutoff - DONE
- Stacked Cancel/Confirm buttons vertically in `ConfirmDialog` (full-width each)
- Horizontal layout caused "[ Confirm ]" text to clip on narrow screens
- Fixes all 4 confirmation dialogs (delete person, purge data, delete recurring charge, delete transaction)
- Version bumped to 1.1.2 (versionCode 5)

### Task 10.2: Fix app launcher icon - DONE
- Replaced default Android Studio placeholder icons with Debt Tracker branding
- Generated all mipmap densities (mdpi through xxxhdpi) from 512x512 Play Store icon
- Generated adaptive icon foreground with proper safe zone padding
- Generated circular `ic_launcher_round.png` variants
- Changed adaptive icon background from white (#FFFFFF) to black (#000000)

## Phase 5: Play Store Release - IN PROGRESS

### Task 11: Prepare store assets - DONE
- App icon (512x512): `release/app-icon-512.png`
- Feature graphic (1024x500): `release/feature-graphic-1024x500.png`
- Screenshots (4): `release/photo_*.jpg`
- Store listing text: `release/store-listing.txt`
- Release notes: `release/release-notes.txt`

### Task 12: Privacy policy - DONE
- Created `docs/privacy.html`
- Hosted via GitHub Pages: https://alan09086.github.io/debt-tracker-kotlin/privacy.html

### Task 13: System back button - DONE
- Added BackHandler to navigate within app instead of exiting

### Task 14: Play Store submission - IN PROGRESS
- [x] Changed applicationId to `com.agsoftware.debttracker` (original was taken)
- [x] Bumped compileSdk/targetSdk to 35 (Play Store requirement)
- [x] Installed SDK platform 35
- [x] Rebuilt AAB with versionCode 3, uploaded to Play Console closed testing
- [x] v1.1.1 bugfix (versionCode 4): dialog button cutoff (partial fix)
- [x] v1.1.2 bugfix (versionCode 5): dialog buttons stacked vertically, app icon fixed
- [ ] Waiting for Google review of closed testing release
- [x] Reddit posts drafted for r/TestMyApp and r/playmytesting (`release/reddit-posts.txt`)
- [ ] Post to Reddit and get 12 testers to opt in
- [ ] 14-day closed testing period
- [ ] Apply for production access

---

## Release Assets

Location: `release/` (gitignored — binaries not tracked)

| File | Description |
|------|-------------|
| `debt-tracker-v1.1.2.aab` | Signed release bundle |
| `debt-tracker-v1.1.2-debug.apk` | Debug APK for testing |
| `app-icon-512.png` | Play Store app icon |
| `feature-graphic-1024x500.png` | Play Store feature graphic |
| `photo_1` through `photo_4` | Screenshots |
| `store-listing.txt` | Short + full description |
| `release-notes.txt` | v1.1.2 changelog |
| `reddit-posts.txt` | r/TestMyApp and r/playmytesting posts |

## Key Notes

- **Keystore**: `~/projects/debt-tracker-kotlin/release.keystore` (on disk, NOT in git)
- **JDK**: Java 21 pinned in `gradle.properties` — JDK 25 incompatible with AGP/Kotlin
- **GitHub**: https://github.com/alan09086/debt-tracker-kotlin (public — for GitHub Pages)
