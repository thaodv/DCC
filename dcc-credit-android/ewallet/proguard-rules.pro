# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile
-keep,includedescriptorclasses class io.wexchain.digitalwallet.api.domain.** {*;}
-keep,includedescriptorclasses class io.wexchain.digitalwallet.api.domain.*$* {*;}

# Platform calls Class.forName on types which do not exist on Android to determine platform.
-dontnote retrofit2.Platform
# Platform used when running on Java 8 VMs. Will not be used at runtime.
-dontwarn retrofit2.Platform$Java8
# Retain generic type information for use by reflection by converters and adapters.
-keepattributes Signature
# Retain declared checked exceptions for use by a Proxy instance.
-keepattributes Exceptions


# spongy castle
-keep,includedescriptorclasses class org.spongycastle.crypto.* {*;}
-keep,includedescriptorclasses class org.spongycastle.crypto.agreement.** {*;}
-keep,includedescriptorclasses class org.spongycastle.crypto.digests.* {*;}
-keep,includedescriptorclasses class org.spongycastle.crypto.ec.* {*;}
-keep,includedescriptorclasses class org.spongycastle.crypto.encodings.* {*;}
-keep,includedescriptorclasses class org.spongycastle.crypto.engines.* {*;}
-keep,includedescriptorclasses class org.spongycastle.crypto.macs.* {*;}
-keep,includedescriptorclasses class org.spongycastle.crypto.modes.* {*;}
-keep,includedescriptorclasses class org.spongycastle.crypto.paddings.* {*;}
-keep,includedescriptorclasses class org.spongycastle.crypto.params.* {*;}
-keep,includedescriptorclasses class org.spongycastle.crypto.prng.* {*;}
-keep,includedescriptorclasses class org.spongycastle.crypto.signers.* {*;}

-keep,includedescriptorclasses class org.spongycastle.jcajce.provider.asymmetric.* {*;}
-keep,includedescriptorclasses class org.spongycastle.jcajce.provider.asymmetric.util.* {*;}
-keep,includedescriptorclasses class org.spongycastle.jcajce.provider.asymmetric.dh.* {*;}
-keep,includedescriptorclasses class org.spongycastle.jcajce.provider.asymmetric.ec.* {*;}

-keep,includedescriptorclasses class org.spongycastle.jcajce.provider.digest.** {*;}
-keep,includedescriptorclasses class org.spongycastle.jcajce.provider.keystore.** {*;}
-keep,includedescriptorclasses class org.spongycastle.jcajce.provider.symmetric.** {*;}
-keep,includedescriptorclasses class org.spongycastle.jcajce.spec.* {*;}
-keep,includedescriptorclasses class org.spongycastle.jce.** {*;}

-dontwarn javax.naming.**

## Retrofit 2
# Platform calls Class.forName on types which do not exist on Android to determine platform.
-dontnote retrofit2.Platform
# Platform used when running on Java 8 VMs. Will not be used at runtime.
-dontwarn retrofit2.Platform$Java8
# Retain generic type information for use by reflection by converters and adapters.
-keepattributes Signature
# Retain declared checked exceptions for use by a Proxy instance.
-keepattributes Exceptions

-keepattributes RuntimeVisibleAnnotations
-keepattributes RuntimeInvisibleAnnotations
-keepattributes RuntimeVisibleParameterAnnotations
-keepattributes RuntimeInvisibleParameterAnnotations

-keepattributes EnclosingMethod

-keepclassmembernames,allowobfuscation interface * {
    @retrofit2.http.* <methods>;
}

## Juzix
-dontwarn web3j.service.**
-dontwarn web3j.ukey.**
-dontwarn web3j.module.privacyprotect.FileManager
-dontwarn org.web3j.**