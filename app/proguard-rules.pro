# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Retrofit için API ve modelleri koru
-keep class com.fatih.prayertime.data.remote.** { *; }
-keep class com.fatih.prayertime.domain.model.** { *; }
-keep class com.fatih.prayertime.data.remote.dto.** { *; }

# Room entity'lerini koru
-keep class com.fatih.prayertime.data.local.entity.** { *; }

# JSON ve model sınıflarını koru
-keep class com.fatih.prayertime.data.remote.dto.duadto.** { *; }
-keep class com.fatih.prayertime.data.remote.dto.duadto.Dua { *; }
-keep class com.fatih.prayertime.data.remote.dto.duadto.DuaCategoryData { *; }
-keep class com.fatih.prayertime.data.remote.dto.duadto.DuaCategoryDetail { *; }
-keep class com.fatih.prayertime.domain.model.EsmaulHusna { *; }

# Asset ve JSON işleme sınıflarını koru
-keep class com.fatih.prayertime.data.repository.LocalDataRepositoryImpl { *; }
-keep class com.fatih.prayertime.util.utils.AssetUtils { *; }
-keep class com.fatih.prayertime.util.utils.** { *; }

# TypeToken ve reflection için özel koruma
-keep class * extends com.google.gson.reflect.TypeToken
-keep class * extends com.google.gson.TypeAdapter
-keepattributes RuntimeVisibleAnnotations,RuntimeVisibleParameterAnnotations

# Gson kullanımı için
-keepattributes Signature
-keepattributes *Annotation*
-dontwarn sun.misc.**
-keep class com.google.gson.** { *; }
-keep class * implements com.google.gson.TypeAdapter
-keep class * implements com.google.gson.TypeAdapterFactory
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer
-keepclassmembers,allowobfuscation class * {
  @com.google.gson.annotations.SerializedName <fields>;
}

# Hata ayıklama için source dosya bilgilerini koru
-keepattributes SourceFile,LineNumberTable

# Kotlin serialization
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt
-keepclassmembers class kotlinx.serialization.json.** { *** Companion; }
-keepclasseswithmembers class kotlinx.serialization.json.** { kotlinx.serialization.KSerializer serializer(...); }
-keepclassmembers class kotlinx.** {
    volatile <fields>;
}

# Coroutines
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}

# Alarm manager için sınıfları koru
-keep class com.fatih.prayertime.data.alarm.** { *; }

# Lottie için gerekli kurallar
-keep class com.airbnb.lottie.** { *; }

# OkHttp için eksik sınıf uyarılarını yoksay
-dontwarn org.bouncycastle.jsse.BCSSLParameters
-dontwarn org.bouncycastle.jsse.BCSSLSocket
-dontwarn org.bouncycastle.jsse.provider.BouncyCastleJsseProvider
-dontwarn org.conscrypt.Conscrypt$Version
-dontwarn org.conscrypt.Conscrypt
-dontwarn org.conscrypt.ConscryptHostnameVerifier
-dontwarn org.openjsse.javax.net.ssl.SSLParameters
-dontwarn org.openjsse.javax.net.ssl.SSLSocket
-dontwarn org.openjsse.net.ssl.OpenJSSE
-dontwarn okhttp3.internal.platform.ConscryptPlatform
-dontwarn okhttp3.internal.platform.BouncyCastlePlatform
-dontwarn okhttp3.internal.platform.OpenJSSEPlatform

# JSON işleme için veri modellerini koru
-keepclassmembers class * {
  public static ** CREATOR;
}

# Generic Type kullanımı için
-keepattributes Signature

# R sınıfları için
-keep class **.R
-keep class **.R$* {
    <fields>;
}

# JSON içinde kullanılan enum'ları koru
-keepclassmembers enum * { *; }

# Reflection için
-keepattributes InnerClasses
-keep class **.R$* { *; }

# Ekran kesintileri dosya adı kaynaklarını gizle
-renamesourcefileattribute SourceFile