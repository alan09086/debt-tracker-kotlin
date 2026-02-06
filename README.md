# Debt Tracker

A native Android app for tracking shared expenses and debts between people. Built with Kotlin and Jetpack Compose with a Matrix-inspired terminal aesthetic.

Fully offline, no accounts, no ads, no tracking.

## Features

- **Track People** — Add anyone you share expenses with
- **Log Transactions** — Record who owes whom, with descriptions and dates
- **Automatic Balances** — See at a glance who owes you and who you owe
- **Recurring Charges** — Set up automatic monthly expenses (rent, subscriptions, etc.) with 12-month backfill limit
- **Transaction History** — View, edit, or delete past transactions
- **Backup & Restore** — Export/import data as JSON files
- **Share Backups** — Send backup files via email, cloud storage, or messaging apps

## Privacy

All data stays on your device. No internet connection needed, no accounts required, no analytics or tracking. Your financial information is never uploaded anywhere.

[Privacy Policy](https://alan09086.github.io/debt-tracker-kotlin/privacy.html)

## Tech Stack

- **Language**: Kotlin
- **UI**: Jetpack Compose with Material3
- **Architecture**: MVVM with ViewModel + Repository
- **Database**: Room (SQLite) with atomic transactions
- **Async**: Kotlin Coroutines & Flow
- **Serialization**: Gson
- **Min SDK**: 26 (Android 8.0)
- **Target SDK**: 35 (Android 15)

## Building

### Requirements

- JDK 17 or 21
- Android SDK 35

### Debug Build

```bash
./gradlew assembleDebug
```

APK output: `app/build/outputs/apk/debug/app-debug.apk`

### Release Build

Requires `keystore.properties` in the project root with signing config.

```bash
./gradlew bundleRelease
```

AAB output: `app/build/outputs/bundle/release/app-release.aab`

## Project Structure

```
app/src/main/java/com/debttracker/app/
├── MainActivity.kt
├── data/
│   ├── database/
│   │   ├── DebtTrackerDao.kt      # Room DAOs
│   │   └── DebtTrackerDatabase.kt # Room database
│   ├── model/
│   │   ├── Person.kt              # Person entity
│   │   ├── Transaction.kt         # Transaction entity
│   │   ├── RecurringCharge.kt     # Recurring charge entity
│   │   └── BackupData.kt          # Backup/restore data class
│   └── repository/
│       └── DebtRepository.kt      # Repository layer
└── ui/
    ├── DebtTrackerViewModel.kt
    ├── components/
    │   └── MatrixComponents.kt    # Reusable UI components
    ├── screens/
    │   ├── Dialogs.kt
    │   ├── HomeScreen.kt
    │   ├── TransactionHistoryScreen.kt
    │   ├── RecurringChargesScreen.kt
    │   └── SettingsScreen.kt
    └── theme/
        ├── Color.kt
        ├── Theme.kt
        └── Type.kt
```

## License

[GPL-3.0](LICENSE)
