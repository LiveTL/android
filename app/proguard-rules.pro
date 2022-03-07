-dontobfuscate

# Javascript interface
-keepclassmembers class * {
    @android.webkit.JavascriptInterface <methods>;
}

# Serializable models
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.SerializationKt
-keepclassmembers class kotlinx.serialization.json.** {
    *** Companion;
}
-keepclasseswithmembers class kotlinx.serialization.json.** {
    kotlinx.serialization.KSerializer serializer(...);
}
-keep,includedescriptorclasses class com.livetl.android.**$$serializer { *; }
-keepclassmembers class com.livetl.android.data.** {
    *** Companion;
}
-keepclasseswithmembers class com.livetl.android.** {
    kotlinx.serialization.KSerializer serializer(...);
}

# https://youtrack.jetbrains.com/issue/KTOR-2708
-keep public class io.ktor.client.** {
    public <methods>;
    private <methods>;
}