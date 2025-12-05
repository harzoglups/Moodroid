# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Keep WebView JavaScript interface
-keepclassmembers class * {
    @android.webkit.JavascriptInterface <methods>;
}

# Keep attributes for debugging
-keepattributes SourceFile,LineNumberTable
-keepattributes Signature, Exception, *Annotation*

# Keep Compose and ViewModel classes
-keep class androidx.lifecycle.ViewModel { *; }
-keep class androidx.compose.** { *; }

# Keep DataStore
-keep class androidx.datastore.** { *; }
-dontwarn androidx.datastore.**

# OkHttp
-dontwarn okhttp3.**
-dontwarn okio.**
-keep class okhttp3.** { *; }
-keep interface okhttp3.** { *; }

# OkHttp platform used only on JVM and when Conscrypt dependency is available.
-dontwarn org.conscrypt.**
-dontwarn org.bouncycastle.**
-dontwarn org.openjsse.**

# Keep coroutines
-keepclassmembernames class kotlinx.** {
    volatile <fields>;
}

# Keep enums
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# Keep Parcelable implementations
-keepclassmembers class * implements android.os.Parcelable {
    public static final ** CREATOR;
}
