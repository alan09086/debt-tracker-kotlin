# Debt Tracker ProGuard Rules

# Room Database
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class *
-keep @androidx.room.Dao class *
-dontwarn androidx.room.paging.**

# Keep Room entities and their fields
-keep class com.debttracker.app.data.Person { *; }
-keep class com.debttracker.app.data.Transaction { *; }
-keep class com.debttracker.app.data.RecurringCharge { *; }
-keep class com.debttracker.app.data.BackupData { *; }

# Gson serialization
-keepattributes Signature
-keepattributes *Annotation*
-dontwarn sun.misc.**
-keep class com.google.gson.** { *; }
-keep class * implements com.google.gson.TypeAdapterFactory
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer

# Keep data classes for Gson reflection
-keepclassmembers class com.debttracker.app.data.** {
    <fields>;
    <init>(...);
}

# Kotlin serialization
-keepattributes InnerClasses
-keep class kotlin.Metadata { *; }

# Coroutines
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-keepclassmembers class kotlinx.coroutines.** {
    volatile <fields>;
}

# Compose
-dontwarn androidx.compose.**
-keep class androidx.compose.** { *; }
