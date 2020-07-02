# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in C:\Users\Auser\AppData\Local\Android\Sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
#############################################
#
# 对于一些基本指令的添加
#
#############################################
#代码混淆压缩比，在0~7之间，默认为5，一般不做修改
-optimizationpasses 5
#混合时不使用大小写混合，混合后的类名为小写
-dontusemixedcaseclassnames
#指定不去忽略非公共库的类
-dontskipnonpubliclibraryclasses
#这句话能够使我们的项目混淆后产生映射文件
#包含有类名->混淆后类名的映射关系
-verbose
#指定不去忽略非公共库的类
-dontskipnonpubliclibraryclassmembers
#不做预校验，preverify是proguard的四个步骤之一，Android不需要preverify，去掉这一步能够加快混淆速度。
-dontpreverify
#保留Annotation不混淆
-keepattributes *Annotation*,InnerClasses
#避免混淆泛型
-keepattributes Signature
#抛出异常时保留代码行号
-keepattributes SourceFile,LineNumberTable
#指定混淆是采用的算法，后面的参数是一个过滤器
#这个过滤器是谷歌推荐的算法，一般不做更改
-optimizations !code/simplification/cast,!field/*,!class/merging/*

#############################################
#
# Android开发中一些需要保留的公共部分
#
#############################################
#保留我们使用的四大组件，自定义的Application等等这些类不被混淆
#因为这些子类都有可能被外部调用
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Appliction
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
-keep public class * extends android.view.View
-keep public class com.android.vending.licensing.ILicensingService
#保留support下的所有类及其内部类
-keep class android.support.** {*;}
#保留R下面的资源
-keep class **.R$* {*;}
#保留本地native方法不被混淆
-keepclasseswithmembernames class * {
    native <methods>;
}
# 保持自定义控件类不被混淆
-keepclasseswithmembers class * {
 public <init>(android.content.Context);
}
# 保持自定义控件类不被混淆
-keepclasseswithmembers class * {
 public <init>(android.content.Context, android.util.AttributeSet);
}
# 保持自定义控件类不被混淆
-keepclasseswithmembers class * {
 public <init>(android.content.Context, android.util.AttributeSet, int);
}

#保留在Activity中的方法参数是view的方法，
#这样以来我们在layout中写的onClick就不会被影响
-keepclassmembers class * extends android.app.Activity{
    public void *(android.view.View);
    #private *;
}
#保留FragmentStatePagerAdapter类不被混淆(根据项目不同，选择是否混淆该类)
-keep public class com.example.auser.zthacker.adapter.MyFragmentPagerAdapter{
    public *;
}

#保留枚举类不被混淆
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}
#保留我们自定义控件（继承自View）不被混淆
-keep public class * extends android.view.View{
    *** get*();
    void set*(***);
    public <init>(android.content.Context);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
}
#保留Parcelable序列化类不被混淆
-keep class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator *;
}
#保留Serializable序列化的类不被混淆
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}
#对于带有回调函数的onXXEvent的，不能被混淆
-keepclassmembers class * {
    void *(**On*Event);
}
#对于实力类我们不能进行混淆，我们需要保留他们的set和get方法。对于boolean类型的get方法为isXXX,不能够
#遗漏。在开发的时候我们可以将所有的实体类放在一个包内，这样我们写一次混淆就行了。
-keep public class com.example.auser.zthacker.bean.** {
    public void set*(***);
    public *** get*();
    public *** is*();
}

#webView处理，项目中没有使用到webView忽略即可
-keepclassmembers class fqcn.of.javascript.interface.for.webview {
   public *;
}
-keepclassmembers class * extends android.webkit.webViewClient {
    public void *(android.webkit.WebView, java.lang.String, android.graphics.Bitmap);
    public boolean *(android.webkit.WebView, java.lang.String);
}
-keepclassmembers class * extends android.webkit.webViewClient {
    public void *(android.webkit.webView, jav.lang.String);
}
#我们需要确保这些js要调用的原生方法不能够被混淆，于是我们需要做如下处理：
-keepclassmembers class com.ljd.example.JSInterface {
    <methods>;
}

#############################################
#
#-----------处理第三方依赖库---------
#
#############################################

#如果引用了v4或者v7包，可以忽略警告，因为用不到android.support
-dontwarn android.support.**

#EventBus
-keep class de.greenrobot.** {*;}
-keepclassmembers class ** {
    public void onEvent*(**);
}
-keepattributes *Annotation*
-keepclassmembers class ** {
    @org.greenrobot.eventbus.Subscribe <methods>;
}
-keep enum org.greenrobot.eventbus.ThreadMode { *; }
-keepclassmembers class * extends org.greenrobot.eventbus.util.ThrowableFailureEvent {# Only required if you use AsyncExecutor
    <init>(java.lang.Throwable);
}

#ButterKnife
-keep class butterknife.** { *; }
-dontwarn butterknife.internal.**
-keep class **$$ViewBinder { *; }
-keepclasseswithmembernames class * {
    @butterknife.* <fields>;
}
-keepclasseswithmembernames class * {
    @butterknife.* <methods>;
}

#Fresco
-keep class com.facebook.fresco.** {*;}
-keep interface com.facebook.fresco.** {*;}
-keep enum com.facebook.fresco.** {*;}

#极光推送
-dontoptimize
-dontpreverify
-dontwarn cn.jpush.**
-keep class cn.jpush.** { *; }

#Facebook
-dontwarn com.facebook.**
-keep enum com.facebook.**

#gson
-keep class sun.misc.Unsafe { *; }
-keep class com.example.auser.zthacker.bean.**{*;}#根据bean包名配置
-keep class com.android.test.bean.**{*;}

#picasso
-dontwarn com.squareup.picasso.**

#okgo
      -dontwarn com.lzy.okgo.**
      -keep class com.lzy.okgo.**{*;}

      #okrx
      -dontwarn com.lzy.okrx.**
      -keep class com.lzy.okrx.**{*;}

      #okserver
      -dontwarn com.lzy.okserver.**
      -keep class com.lzy.okserver.**{*;}

      #okhttp
      -dontwarn okhttp3.**
      -keep class okhttp3.**{*;}

      #okio
      -dontwarn okio.**
      -keep class okio.**{*;}

#bgabanner
-keep class cn.bingoogolapple.bgabanner.BGAViewPager { *; }

#glide
-keep class com.bumptech.glide.Glide { *; }
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public class * extends com.bumptech.glide.AppGlideModule
-keep public enum com.bumptech.glide.load.resource.bitmap.ImageHeaderParser$** {
       **[] $VALUES;
       public *;
  }
-dontwarn com.bumptech.glide.**
#-keepnames class com.example.auser.zthacker.utils.GlideCache
# or more generally:
#-keep public class * implements com.bumptech.glide.module.GlideModule

#zxing
-dontwarn com.google.zxing.**
-keep  class com.google.zxing.**{*;}

#galleryfinal
-keep class cn.finalteam.galleryfinal.widget.*{*;}
-keep class cn.finalteam.galleryfinal.widget.crop.*{*;}
-keep class cn.finalteam.galleryfinal.widget.zoonview.*{*;}

#jcvideoplayer
-keepclassmembers class ** {
    public void onEvent*(***);
}
# Only required if you use AsyncExecutor
-keepclassmembers class * extends de.greenrobot.event.util.ThrowableFailureEvent {
    public <init>(java.lang.Throwable);
}
# Don't warn for missing support classes
-dontwarn de.greenrobot.event.util.*$Support
-dontwarn de.greenrobot.event.util.*$SupportManagerFragment

#PictureSelector 2.0(一直到# for DexGuard only)
-keep class com.luck.picture.lib.** { *; }
-dontwarn com.yalantis.ucrop**
-keep class com.yalantis.ucrop** { *; }
-keep interface com.yalantis.ucrop** { *; }

 #rxjava
-dontwarn sun.misc.**
-keepclassmembers class rx.internal.util.unsafe.*ArrayQueue*Field* {
 long producerIndex;
 long consumerIndex;
}
-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueProducerNodeRef {
 rx.internal.util.atomic.LinkedQueueNode producerNode;
}
-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueConsumerNodeRef {
 rx.internal.util.atomic.LinkedQueueNode consumerNode;
}
#rxandroid
-dontwarn sun.misc.**
-keepclassmembers class rx.internal.util.unsafe.*ArrayQueue*Field* {
   long producerIndex;
   long consumerIndex;
}
-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueProducerNodeRef {
    rx.internal.util.atomic.LinkedQueueNode producerNode;
}
-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueConsumerNodeRef {
    rx.internal.util.atomic.LinkedQueueNode consumerNode;
}
# for DexGuard only
#-keepresourcexmlelements manifest/application/meta-data@value=GlideModule
# Qiniu
-keep class com.qiniu.**{*;}
-keep class com.qiniu.**{public <init>();}
#-ignorewarning
#shareSDK
-keep class cn.sharesdk.**{*;}
-keep class com.sina.**{*;}
-keep class **.R$* {*;}
-keep class **.R{*;}
-keep class com.mob.**{*;}

#retrofit2
# Platform calls Class.forName on types which do not exist on Android to determine platform.
-dontnote retrofit2.Platform
# Platform used when running on RoboVM on iOS. Will not be used at runtime.
-dontnote retrofit2.Platform$IOS$MainThreadExecutor
# Platform used when running on Java 8 VMs. Will not be used at runtime.
-dontwarn retrofit2.Platform$Java8
# Retain generic type information for use by reflection by converters and adapters.
-keepattributes Signature
# Retain declared checked exceptions for use by a Proxy instance.
-keepattributes Exceptions

#keep wps
-dontwarn com.tencent.**
-keep class com.tencent.**{*;}




