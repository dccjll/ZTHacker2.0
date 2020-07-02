package com.example.auser.zthacker.app;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.graphics.Color;
import android.support.multidex.MultiDex;
import android.util.LruCache;

import com.example.auser.zthacker.utils.localImages.PicassoImageLoader;
import com.example.auser.zthacker.view.wps.ExceptionHandler;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.cache.CacheEntity;
import com.lzy.okgo.cache.CacheMode;
import com.lzy.okgo.cookie.store.PersistentCookieStore;
import com.lzy.okgo.model.HttpHeaders;
import com.lzy.okgo.model.HttpParams;
import com.squareup.leakcanary.LeakCanary;
import com.tencent.smtt.sdk.QbSdk;

import org.apache.http.protocol.HTTP;

import java.util.LinkedList;
import java.util.List;

import cn.finalteam.galleryfinal.CoreConfig;
import cn.finalteam.galleryfinal.FunctionConfig;
import cn.finalteam.galleryfinal.GalleryFinal;
import cn.finalteam.galleryfinal.ImageLoader;
import cn.finalteam.galleryfinal.ThemeConfig;
/**
 * APPLICATION
 */
public class AppApplication extends Application {
    private List<Activity> mList = new LinkedList<Activity>();
    private static AppApplication instance;
    private String currentArticleId;
    private static LruCache lruCache;

    public String getCurrentArticleId() {
        return currentArticleId;
    }

    public void setCurrentArticleId(String currentArticleId) {
        this.currentArticleId = currentArticleId;
    }

    public AppApplication() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        LeakCanary.install(this);
        //增加这句话(展示WPS文档)
        QbSdk.initX5Environment(this,null);
        ExceptionHandler.getInstance().initConfig(this);

        /*MobSDK.init(this);*/
        //Fresco.initialize(this);
           /*GallerFinal初始化*/
        //设置主题
        ThemeConfig theme = new ThemeConfig.Builder()
                .setTitleBarBgColor(Color.parseColor("#212629"))
                .build();

        final FunctionConfig functionConfig = new FunctionConfig.Builder()
       /* setMutiSelect(boolean)//配置是否多选
        setMutiSelectMaxSize(int maxSize)//配置多选数量
        setEnableEdit(boolean)//开启编辑功能
        setEnableCrop(boolean)//开启裁剪功能
        setEnableRotate(boolean)//开启选择功能
        setEnableCamera(boolean)//开启相机功能
        setCropWidth(int width)//裁剪宽度
        setCropHeight(int height)//裁剪高度
        setCropSquare(boolean)//裁剪正方形
        setSelected(List)//添加已选列表,只是在列表中默认呗选中不会过滤图片
        setFilter(List list)//添加图片过滤，也就是不在GalleryFinal中显示
        takePhotoFolter(File file)//配置拍照保存目录，不做配置的话默认是/sdcard/DCIM/GalleryFinal/
        setRotateReplaceSource(boolean)//配置选择图片时是否替换原始图片，默认不替换
        setCropReplaceSource(boolean)//配置裁剪图片时是否替换原始图片，默认不替换
        setForceCrop(boolean)//启动强制裁剪功能,一进入编辑页面就开启图片裁剪，不需要用户手动点击裁剪，此功能只针对单选操作
        setForceCropEdit(boolean)//在开启强制裁剪功能时是否可以对图片进行编辑（也就是是否显示旋转图标和拍照图标）
        setEnablePreview(boolean)//是否开启预览功能*/
                .setEnableEdit(true)//开启编辑功能
                .setEnableCrop(true)//开启裁剪功能
                .setForceCrop(true)//启动强制裁剪功能,一进入编辑页面就开启图片裁剪，不需要用户手动点击裁剪，此功能只针对单选操作
                .setCropSquare(true)//裁剪正方形
                .build();
        //配置imageloader
        ImageLoader imageloader = new PicassoImageLoader();
        CoreConfig coreConfig = new CoreConfig.Builder(this, imageloader, theme)
                // .setDebug(BuildConfig.DEBUG)
                .setFunctionConfig(functionConfig)
                .setNoAnimcation(true)
                .build();
        GalleryFinal.init(coreConfig);

        //---------这里给出的是示例代码,告诉你可以这么传,实际使用的时候,根据需要传,不需要就不传-------------//
        HttpHeaders headers = new HttpHeaders();
        headers.put("Content-Type", "text/html");    //这行很重要
        headers.put("charset", HTTP.UTF_8);
        //headers.put("commonHeaderKey1", "commonHeaderValue1");    //header不支持中文
        //headers.put("commonHeaderKey2", "commonHeaderValue2");
        HttpParams params = new HttpParams();
        params.put("commonParamsKey1", "commonParamsValue1");     //param支持中文,直接传,不要自己编码
        params.put("commonParamsKey2", "这里支持中文参数");
        //-----------------------------------------------------------------------------------//
        //必须调用初始化
        OkGo.init(this);
        //以下设置的所有参数是全局参数,同样的参数可以在请求的时候再设置一遍,那么对于该请求来讲,请求中的参数会覆盖全局参数
        //好处是全局参数统一,特定请求可以特别定制参数
        try {
            //以下都不是必须的，根据需要自行选择,一般来说只需要 debug,缓存相关,cookie相关的 就可以了
            OkGo.getInstance()
                    //打开该调试开关,控制台会使用 红色error 级别打印log,并不是错误,是为了显眼,不需要就不要加入该行
                    .debug("OkGo")
                    //如果使用默认的 60秒,以下三行也不需要传
                    .setConnectTimeout(30*1000)  //全局的连接超时时间
                    .setReadTimeOut(30*1000)     //全局的读取超时时间
                    .setWriteTimeOut(30*1000)    //全局的写入超时时间
                    //可以全局统一设置缓存模式,默认是不使用缓存,可以不传,具体其他模式看 github 介绍 https://github.com/jeasonlzy/
                    .setCacheMode(CacheMode.NO_CACHE)
                    //可以全局统一设置缓存时间,默认永不过期,具体使用方法看 github 介绍
                    .setCacheTime(CacheEntity.CACHE_NEVER_EXPIRE)
                    //如果不想让框架管理cookie,以下不需要
//                .setCookieStore(new MemoryCookieStore())                //cookie使用内存缓存（app退出后，cookie消失）
                    .setCookieStore(new PersistentCookieStore())          //cookie持久化存储，如果cookie不过期，则一直有效
                    //可以设置https的证书,以下几种方案根据需要自己设置,不需要不用设置
                    .setCertificates()                                  //方法一：信任所有证书
//                    .setCertificates(getAssets().open("srca.cer"))      //方法二：也可以自己设置https证书
//                    .setCertificates(getAssets().open("aaaa.bks"), "123456", getAssets().open("srca.cer"))//方法三：传入bks证书,密码,和cer证书,支持双向加密
                    //可以添加全局拦截器,不会用的千万不要传,错误写法直接导致任何回调不执行
//                .addInterceptor(new Interceptor() {
//                    @Override
//                    public Response intercept(Chain chain) throws IOException {
//                        return chain.proceed(chain.request());
//                    }
//                })
                    //这两行同上,不需要就不要传
                    .addCommonHeaders(headers)                                         //设置全局公共头
                    .addCommonParams(params);                                          //设置全局公共参数
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    public  LruCache getCache(){
        if (lruCache==null) {
            lruCache = new LruCache((int) Runtime.getRuntime().maxMemory() / 10);
        }
        return lruCache;
    }

    public synchronized static AppApplication getInstance(){
        if (null == instance) {
            instance = new AppApplication();
        }
        return instance;
    }
    // add Activity
    public void addActivity(Activity activity) {
        mList.add(activity);
    }
    //关闭每一个list内的activity
    public void exit() {
        try {
            for (Activity activity:mList) {
                if (activity != null)
                    activity.finish();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            System.exit(0);
        }
    }
}
