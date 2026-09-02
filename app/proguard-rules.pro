# ==============================================================================
# ProGuard / R8 Rules for KheloJito Android Application
# ==============================================================================

# Obfuscation Dictionaries
-obfuscationdictionary proguard-dictionary.txt
-classobfuscationdictionary proguard-dictionary.txt
-packageobfuscationdictionary proguard-dictionary.txt

# Preserve line numbers and source file for crash debugging
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

# Preserve Annotations & JavaScript Interfaces
-keepattributes *Annotation*
-keepattributes JavascriptInterface
-keepattributes InnerClasses
-keepattributes EnclosingMethod
-keepattributes Signature
-keepattributes Exceptions

# Keep KheloJito Main Entry Point
-keep class com.app.khelojito.MainActivity {
    *;
}

# Keep Android WebView & JavaScript Interface Methods
-keepclassmembers class * {
    @android.webkit.JavascriptInterface <methods>;
}

-keepclassmembers class * extends android.webkit.WebChromeClient {
    *;
}

-keepclassmembers class * extends android.webkit.WebViewClient {
    *;
}

-keepclassmembers class * extends android.webkit.ValueCallback {
    *;
}

# Keep Android View Constructors for XML Inflation
-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet);
}
-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet, int);
}

# Keep AndroidX & Material Components
-keep class androidx.appcompat.** { *; }
-keep class com.google.android.material.** { *; }
-dontwarn androidx.**
-dontwarn com.google.android.material.**

# Optimization parameters
-repackageclasses ''
-allowaccessmodification