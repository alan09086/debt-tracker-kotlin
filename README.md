# Debt Tracker

A native Android app for tracking shared expenses and debts between people. Built with Kotlin and Jetpack Compose with a Matrix-inspired terminal aesthetic.

## Features

- **Track People**: Add people you share expenses with
- **Log Transactions**: Record who owes whom and for what
- **Balance Tracking**: Automatic calculation of net balances
- **Recurring Charges**: Set up automatic recurring expenses (with 12-month backfill limit)
- **Transaction History**: View and manage all past transactions
- **Edit & Delete**: Full CRUD operations on people and transactions
- **Backup & Restore**: Export/import data as JSON files
- **Share Backups**: Send backup files via email, cloud storage, etc.

## Tech Stack

- **Language**: Kotlin
- **UI**: Jetpack Compose with Material3
- **Architecture**: MVVM with ViewModel
- **Database**: Room (SQLite)
- **Async**: Kotlin Coroutines & Flow
- **Serialization**: Gson
- **Min SDK**: 26 (Android 8.0)
- **Target SDK**: 34 (Android 14)

## Building

### Requirements

- Android Studio Hedgehog or newer
- JDK 17+
- Android SDK 34

### Debug Build

```bash
./gradlew assembleDebug
```

APK output: `app/build/outputs/apk/debug/app-debug.apk`

### Release Build

```bash
./gradlew assembleRelease
```

APK output: `app/build/outputs/apk/release/app-release.apk`

## Project Structure

```
app/src/main/java/com/debttracker/app/
├── data/
│   ├── Person.kt              # Person entity
│   ├── Transaction.kt         # Transaction entity
│   ├── RecurringCharge.kt     # Recurring charge entity
│   ├── BackupData.kt          # Backup/restore data class
│   ├── DebtTrackerDao.kt      # Room DAO
│   ├── DebtTrackerDatabase.kt # Room database
│   └── DebtRepository.kt      # Repository layer
├── ui/
│   ├── DebtTrackerViewModel.kt
│   ├── components/
│   │   ├── MatrixComponents.kt # Reusable UI components
│   │   └── Dialogs.kt          # Dialog components
│   ├── screens/
│   │   ├── HomeScreen.kt
│   │   ├── TransactionHistoryScreen.kt
│   │   ├── RecurringChargesScreen.kt
│   │   └── SettingsScreen.kt
│   └── theme/
│       ├── Color.kt
│       ├── Theme.kt
│       └── Type.kt
└── MainActivity.kt
```

## License

MIT License
