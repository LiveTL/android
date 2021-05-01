# Javascript interface
-keepclassmembers class com.livetl.android.data.chat.ChatService {
   public *;
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
-keep,includedescriptorclasses class com.yourcompany.yourpackage.**$$serializer { *; }
-keepclassmembers class com.livetl.android.data.** {
    *** Companion;
}
-keepclasseswithmembers class com.livetl.android.data.** {
    kotlinx.serialization.KSerializer serializer(...);
}