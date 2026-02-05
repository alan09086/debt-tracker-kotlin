# Debt Tracker - Play Store Publishing Plan

## App Details

- **Package ID**: `com.debttracker.app`
- **Version**: 1.0.0
- **AAB File**: `~/Desktop/debt-tracker-v1.0.0.aab`
- **Min Android**: 8.0 (API 26)

---

## Store Listing Content

### App Name
```
Debt Tracker
```

### Short Description (80 chars max)
```
Track shared expenses and debts with friends. Simple, private, offline-first.
```
(76 characters)

### Full Description
```
Debt Tracker is a simple, privacy-focused app for tracking shared expenses and IOUs between friends, roommates, or family members.

KEY FEATURES

- Track People: Add anyone you share expenses with
- Log Transactions: Record who owes whom, with descriptions and dates
- Automatic Balances: See at a glance who owes you and who you owe
- Recurring Charges: Set up automatic monthly expenses (rent, subscriptions, etc.)
- Transaction History: View, edit, or delete past transactions
- Backup & Restore: Export your data as JSON files
- Share Backups: Send backups via email, cloud storage, or messaging apps

PRIVACY FIRST

- All data stays on your device
- No account required
- No internet connection needed
- No ads or tracking
- Your financial information is never uploaded anywhere

CLEAN DESIGN

Minimalist terminal-inspired interface that's easy to use and distraction-free. No clutter, no unnecessary features - just simple debt tracking that works.

PERFECT FOR

- Splitting rent with roommates
- Tracking group trip expenses
- Managing IOUs with friends
- Keeping tabs on shared subscriptions
- Any situation where money changes hands between people

Open source and free forever. No premium version, no in-app purchases.
```
(1,198 characters)

---

## Graphics Requirements

### App Icon (512x512 PNG)
- Current icon: Green terminal-style design on dark background
- Location: `app/src/main/res/mipmap-xxxhdpi/ic_launcher.png` (scale up or recreate)

### Feature Graphic (1024x500 PNG)
Create a banner showing:
- App name "Debt Tracker"
- Matrix green color scheme (#00FF00 on #0D0D0D)
- Tagline: "Track debts. Stay balanced."
- Maybe show simplified UI mockup

### Screenshots (minimum 2, recommended 4-6)
Capture these screens at 1080x1920 or similar:

1. **Home Screen** - Show people list with balances
2. **Add Transaction** - Dialog for adding new debt/payment
3. **Transaction History** - List of past transactions
4. **Settings/Backup** - Show export/import options
5. **Recurring Charges** - Monthly charges setup (optional)
6. **Person Detail** - Individual balance view (optional)

Screenshot tips:
- Use demo data (not real names)
- Show variety of positive/negative balances
- Keep Matrix green theme visible

---

## Content Rating Questionnaire

Expected answers for IARC rating:

- Violence: None
- Sexual content: None
- Language: None
- Controlled substances: None
- User interaction: None (offline app)
- Data collection: None (all local)
- Ads: None

**Expected rating**: Everyone / PEGI 3

---

## Privacy Policy

Since the app stores financial data (even locally), a privacy policy is recommended.

### Privacy Policy Content
```
Privacy Policy for Debt Tracker

Last updated: [DATE]

Debt Tracker is committed to protecting your privacy.

DATA COLLECTION
This app does not collect, transmit, or share any personal data. All information you enter (names, transactions, amounts) is stored locally on your device only.

INTERNET ACCESS
This app does not require or use internet access. All functionality works completely offline.

THIRD-PARTY SERVICES
This app does not integrate with any third-party analytics, advertising, or tracking services.

DATA STORAGE
Your data is stored in a local database on your device. You can export your data at any time using the backup feature, and you can delete all data through the app settings.

CONTACT
For questions about this privacy policy, contact: [EMAIL]
```

Host this on:
- GitHub Pages (free): `https://[username].github.io/debt-tracker-kotlin/privacy`
- Or any static hosting

---

## Publishing Checklist

### Before Submitting
- [ ] Create Google Play Developer account ($25)
- [ ] Complete identity verification
- [ ] Prepare app icon (512x512)
- [ ] Prepare feature graphic (1024x500)
- [ ] Take 4-6 screenshots
- [ ] Host privacy policy URL
- [ ] Test AAB installs correctly

### In Play Console
- [ ] Create new app
- [ ] Upload AAB to Production track
- [ ] Fill store listing (descriptions, graphics)
- [ ] Complete content rating questionnaire
- [ ] Set up pricing (Free)
- [ ] Select countries for distribution
- [ ] Submit for review

### After Approval
- [ ] Monitor reviews and crash reports
- [ ] Plan v1.1 improvements based on feedback

---

## Future Updates

When releasing updates:
1. Increment `versionCode` in `build.gradle.kts`
2. Update `versionName` (e.g., "1.1.0")
3. Build new AAB: `./gradlew bundleRelease`
4. Upload to Play Console with release notes

---

## Quick Reference

```bash
# Build release AAB
cd ~/projects/debt-tracker-kotlin
JAVA_HOME=/usr/lib/jvm/java-21-openjdk ./gradlew bundleRelease

# Output location
app/build/outputs/bundle/release/app-release.aab

# Keystore info
cat ~/projects/debt-tracker-kotlin/KEYSTORE-INFO.txt
```
