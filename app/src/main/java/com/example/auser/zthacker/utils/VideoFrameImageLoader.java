package com.example.auser.zthacker.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v4.util.LruCache;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ImageView;

import com.example.auser.zthacker.bean.UtilBean;

import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import fm.jiecao.jcvideoplayer_lib.JCUtils;
import fm.jiecao.jcvideoplayer_lib.JCVideoPlayerStandard;

/**
 * Created by jiana on 16-4-3.
 */
public class VideoFrameImageLoader {
    private static final String TAG = "VideoFrameImageLoader";
    /**
     * 缓存image的类，当存储image的大小大于LruCache设定的值，系统自动释放内存
     */
    private LruCache<String, Bitmap> mBitmapCache = null;
    private LruCache<String, String> mStringCache = null;
    private FileManager fileManager;
    /**
     * 获取视频流一帧图片的线程池
     */
    private ExecutorService mImageThreadPool = null;

    /**
     * 记录是否刚打开程序，用于解决进入程序不滚动不会获取图片的问题
     */
    private boolean isFirstEnter = true;

    /**
     * 一屏中第一个item的位置
     */
    private int mFirstVisibleItem;

    /**
     * 一屏中所有的item的个数
     */
    private int mVisibleItemCount;

    /**
     * 展示列表
     */
    private View listView;

    private String[] videoUrls;


    public VideoFrameImageLoader(Context context, View listView) {
        //获取系统分配给每个应用的最大内存，每个应用系统分配32M
        int maxMemory = (int) Runtime.getRuntime().maxMemory();
        Log.e(TAG, "maxMemory = " + maxMemory);
        //给lruCache分配1/8 4M
        int mBitmapSize = 4*1024*1024;
        int mStringSize = 2*1024*1024;
        mBitmapCache = new LruCache<String, Bitmap>(mBitmapSize) {
            //必须重写此方法，来测量Bitmap的大小
            @Override
            protected int sizeOf(String key, Bitmap value) {
                return value.getRowBytes() * value.getHeight();
            }
        };
        mStringCache = new LruCache<String,String>(mStringSize){
        };
        fileManager = new FileManager(context);
        this.listView = listView;
    }

    public void setVideoUrls(String[] videoUrls) {
        this.videoUrls = videoUrls;
    }

    /**
     * 初始化Adapter后初始化数据
     * 这个需要在adapter中调用，做初始化
     * 监听是否为滚动状态，判断截取显示的子布局对应的哪几个视频流
     */
    public void initList() {
        //判断为ListView或GridView时
        if (listView instanceof AbsListView) {
            AbsListView absListView = (AbsListView) listView;
            absListView.setOnScrollListener(new AbsListView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(AbsListView view, int scrollState) {
                    //仅当ListView禁止时才去下载，ListView滑动时取消所有正在下载的任务
                    if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
                        showImage(mFirstVisibleItem, mVisibleItemCount);
                    } else {
                        cancelTask();
                    }
                }

                @Override
                public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                    mFirstVisibleItem = firstVisibleItem;
                    mVisibleItemCount = visibleItemCount;
                    if (isFirstEnter && mVisibleItemCount > 0) {
                        showImage(mFirstVisibleItem, mVisibleItemCount);
                        isFirstEnter = false;
                    }
                }
            });
            return;
        }

        //判断为RecyclerView时
        if (listView instanceof RecyclerView) {
            RecyclerView recyclerView = (RecyclerView) listView;
            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);
                    //仅当ListView禁止时才去下载，ListView滑动时取消所有正在下载的任务
                    if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                        showImage(mFirstVisibleItem, mVisibleItemCount);
                    } else {
                        cancelTask();
                    }
                }

                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    //网格布局列表或线性列表
                    LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                    mVisibleItemCount = layoutManager.getChildCount();
                    mFirstVisibleItem = layoutManager.findFirstVisibleItemPosition();
                    if (isFirstEnter && mVisibleItemCount > 0) {
                        showImage(mFirstVisibleItem, mVisibleItemCount);
                        isFirstEnter = false;
                    }
                }
            });
        }
    }

    /**
     * 显示当前屏幕的图片，先会去查找LruCache，LruCache没有就去手机目录查找，在没有就开启线程去下载截取帧图片
     * @param firstVisibleItem
     * @param visibleItemCount
     */
    private void showImage(int firstVisibleItem, int visibleItemCount) {
        for (int i = firstVisibleItem; i < firstVisibleItem + visibleItemCount; i++) {
            if (videoUrls!=null&&videoUrls.length>0&&i<videoUrls.length){
                String videoUrl = videoUrls[i];
                if (!TextUtil.isNull(videoUrl)){
                    final JCVideoPlayerStandard jCVideoPlayerStandard = (JCVideoPlayerStandard) listView.findViewWithTag(videoUrl);
                    cutVideoFrameImage(videoUrl, new VideoFrameImageLoader.OnFrameImageLoaderListener() {
                        @Override
                        public void onImageLoader(UtilBean utilBean, String url) {
                            if (jCVideoPlayerStandard != null && utilBean != null) {
                                jCVideoPlayerStandard.thumbImageView.setImageBitmap(utilBean.getBitmap());
                                jCVideoPlayerStandard.tv_total_time.setText(JCUtils.stringForTime(Integer.parseInt(utilBean.getaString())));
                            }
                        }
                    });
                }
            }
        }
    }

    /**
     * 获取线程池的方法，因为涉及到并发的问题，我们加上同步锁
     *
     * @return
     */
    public ExecutorService getThreadPool() {
        if (mImageThreadPool == null) {
            synchronized (ExecutorService.class) {
                if (mImageThreadPool == null) {
                    mImageThreadPool = Executors.newFixedThreadPool(5);
                }
            }
        }
        return mImageThreadPool;
    }

    /**
     * 添加Bitmap到内存缓存
     */
    public void addBitmapToMemoryCache(String key, Bitmap bitmap) {
        Log.e(TAG, "key = " + key);
        if (getBitmapFormMemCache(key) == null && bitmap != null) {
            mBitmapCache.put(key, bitmap);
        }
    }

    /**
     * 从内存缓存中获取一个bitmap
     *
     * @param key
     * @return
     */
    public Bitmap getBitmapFormMemCache(String key) {

        return mBitmapCache.get(key);
    }
    /**
     * 添加String到内存缓存
     */
    public void addStringToMemoryCache(String key, String value) {
        Log.e(TAG, "key = " + key);
        if (TextUtil.isNull(getStringFormMemCache(key)) && !TextUtil.isNull(value)) {
            mStringCache.put(key, value);
        }
    }
    /**
     * 从内存缓存中获取一个String
     *
     * @param key
     * @return
     */
    public String getStringFormMemCache(String key) {
        return mStringCache.get(key);
    }

    /**
     * 先从内存中获取Bitmap，如果没有就从本地获取
     *
     * @param url
     * @param listener
     * @return
     */
    public Bitmap cutVideoFrameImage(final String url, final OnFrameImageLoaderListener listener) {

        final String subUrl = VideoFrameImageLoader.formatVideoUrl(url);
        Bitmap bitmap = showCacheBitmap(url);
        if (bitmap != null) {
            return bitmap;
        } else {
            final Handler handler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    listener.onImageLoader((UtilBean) msg.obj, url);
                }
            };
            getThreadPool().execute(new Runnable() {
                @Override
                public void run() {
                    UtilBean utilBean = getBitmapFormUrl(url);
                    if (utilBean==null||utilBean.getBitmap() == null) {
                        return;
                    }
                    Message msg = handler.obtainMessage();
                    msg.obj = utilBean;
                    handler.sendMessage(msg);
                    try {
                        //保存在手机本地存储
                        fileManager.saveBitmap(subUrl, utilBean.getBitmap());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    //将bitmap加如内存缓存
                    addBitmapToMemoryCache(subUrl, utilBean.getBitmap());
                    //String
                    addStringToMemoryCache(subUrl,utilBean.getaString());
                }
            });
        }
        return null;
    }

    /**
     * 获取Bitmap内存中没有就去本地存储获取
     *
     * @param url
     * @return
     */
    public Bitmap showCacheBitmap(String url) {
        if (getBitmapFormMemCache(url) != null) {
            return getBitmapFormMemCache(url);
        } else if (fileManager.isFileExists(url) && fileManager.getFileSize(url) != 0) {
            //从本地获取bitmap
            Bitmap bitmap = fileManager.getBitmap(url);
            //将bitmap加入内存缓存
            addBitmapToMemoryCache(url, bitmap);
            return bitmap;
        }
        return null;
    }

    /**
     * 替换Url中非字母和非数字的字符，这里比较重要，因为我们用Url作为文件名，比如我们的Url
     * 是Http://xiaanming/abc.jpg;用这个作为图片名称，系统会认为xiaanming为一个目录，
     * 我们没有创建此目录保存文件就会报错
     *
     * @param videoUrl
     * @return
     */
    public static String formatVideoUrl(String videoUrl) {
        return videoUrl.replaceAll("[\\^\\W]", "") + ".jpeg";
    }

    /**
     * 从.mp4的url视频中获取第一帧
     *
     * @param url
     * @return
     */
    private UtilBean getBitmapFormUrl(String url) {
        Bitmap bitmap = null;
        String duration = null;
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        try {
            if (Build.VERSION.SDK_INT >= 14) {
                retriever.setDataSource(url, new HashMap<String, String>());
            } else {
                retriever.setDataSource(url);
            }
            bitmap = retriever.getFrameAtTime();
            // 播放时长单位为毫秒
            duration = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        } catch (RuntimeException ex) {
            // Assume this is a corrupt video file.
        } finally {
            try {
                retriever.release();
            } catch (RuntimeException ex) {
                // Ignore failures while cleaning up.
            }
        }
        UtilBean utilBean = new UtilBean();
        utilBean.setBitmap(bitmap);
        utilBean.setaString(duration);
        return utilBean;
    }

    /**
     * 取消正在下载的任务
     */
    public synchronized void cancelTask() {
        if (mImageThreadPool != null) {
            mImageThreadPool.shutdownNow();
            mImageThreadPool = null;
        }
    }

    public String[] getVideoUrls() {
        return videoUrls;
    }

    public FileManager getFileManager() {
        return fileManager;
    }

    public interface OnFrameImageLoaderListener {
        void onImageLoader(UtilBean bitmap, String url);
    }
}







