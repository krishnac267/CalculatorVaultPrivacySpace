-keepattributes *Annotation*,Signature,InnerClasses,EnclosingMethod
-keep class dagger.hilt.** { *; }
-keep class javax.inject.** { *; }
-keep @androidx.room.Entity class *
-keep class * extends androidx.room.RoomDatabase
-keep class com.calculator.vault.privacy.domain.model.** { *; }
-keep class com.calculator.vault.privacy.data.db.** { *; }
-keepclassmembers class * {
    @com.google.gson.annotations.SerializedName <fields>;
}
-dontwarn net.sqlcipher.**
-keep class net.sqlcipher.** { *; }
-keep class net.sqlcipher.database.** { *; }
