#OkHttp、OkGo混淆配置 start
#https://github.com/jeasonlzy/okhttp-OkGo
#okhttp
-dontwarn okhttp3.**
-keep class okhttp3.**{*;}
#okio
-dontwarn okio.**
-keep class okio.**{*;}
#OkHttp、OkGo混淆配置 end

#Gson混淆配置 start
-keepattributes Signature
-keepattributes *Annotation*
-dontwarn sun.misc.**
-keep class sun.misc.Unsafe { *; }
-keep class com.google.gson.stream.** { *; }

-keep class * implements com.google.gson.TypeAdapterFactory
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer

#-keepattributes EnclosingMethod
#Gson混淆配置 end

#Serializable防止混淆配置 start
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}
#Serializable防止混淆配置 end