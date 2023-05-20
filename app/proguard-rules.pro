-dontobfuscate

-keep class com.livetl.** { *; }

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
-keep,includedescriptorclasses class com.livetl.**$$serializer { *; }
-keepclassmembers class com.livetl.data.** {
    *** Companion;
}
-keepclasseswithmembers class com.livetl.** {
    kotlinx.serialization.KSerializer serializer(...);
}

# https://youtrack.jetbrains.com/issue/KTOR-2708
-keep public class io.ktor.client.** {
    public <methods>;
    private <methods>;
}
# https://issuetracker.google.com/issues/188703877
-keep,allowobfuscation,allowoptimization class io.ktor.util.reflect.** { *; }

# These are generated automatically by the Android Gradle plugin.
-dontwarn org.bouncycastle.jsse.BCSSLParameters
-dontwarn org.bouncycastle.jsse.BCSSLSocket
-dontwarn org.bouncycastle.jsse.provider.BouncyCastleJsseProvider
-dontwarn org.conscrypt.Conscrypt$Version
-dontwarn org.conscrypt.Conscrypt
-dontwarn org.conscrypt.ConscryptHostnameVerifier
-dontwarn org.openjsse.javax.net.ssl.SSLParameters
-dontwarn org.openjsse.javax.net.ssl.SSLSocket
-dontwarn org.openjsse.net.ssl.OpenJSSE
-dontwarn org.slf4j.impl.StaticLoggerBinder