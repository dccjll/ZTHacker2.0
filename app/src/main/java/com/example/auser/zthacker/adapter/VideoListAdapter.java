package com.example.auser.zthacker.adapter;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.auser.zthacker.R;
import com.example.auser.zthacker.app.AppApplication;
import com.example.auser.zthacker.bean.UtilBean;
import com.example.auser.zthacker.bean.VideoInfoBean;
import com.example.auser.zthacker.utils.MyUtils;
import com.example.auser.zthacker.utils.TextUtil;
import java.util.List;
import fm.jiecao.jcvideoplayer_lib.JCUtils;
import fm.jiecao.jcvideoplayer_lib.JCVideoPlayer;
import fm.jiecao.jcvideoplayer_lib.JCVideoPlayerStandard;

/**
 * Created by zhengkq on 2017/8/16.
 */

public class VideoListAdapter extends RecyclerView.Adapter<VideoListAdapter.ViewHolder> {
    LayoutInflater inflater;
    private Activity context;
    private List<VideoInfoBean> list;
    private LruCache lruCache;

    public VideoListAdapter(Context context, List<VideoInfoBean> list) {
        this.context = (Activity) context;
        inflater = LayoutInflater.from(context);
        this.list = list;
        lruCache = AppApplication.getInstance().getCache();
    }

    @Override
    public VideoListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_video, parent, false);
        final ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final VideoInfoBean videoInfoBean = list.get(position);
        final String videoPath = MyUtils.toUtf8String(videoInfoBean.getFileUrl()).replaceAll(" ","%20");
        holder.tv_video_name.setText(videoInfoBean.getFileName());
        holder.videoplayer.setUp(videoPath
                , JCVideoPlayer.SCREEN_LAYOUT_LIST, "");
        holder.videoplayer.thumbImageView.setImageResource(R.mipmap.img_placeholder_320);
        Log.e("dafdsf",videoPath);
        Glide.with(context)
                .load(videoInfoBean.getFileUrlImg())
                .asBitmap()
                .diskCacheStrategy(DiskCacheStrategy.RESULT)
                .placeholder(R.mipmap.img_placeholder_320)
                .override(490,240)
                .thumbnail(0.2f)
                .into(holder.videoplayer.thumbImageView);
        Log.e("dadadadfg",System.currentTimeMillis()+"");
        final Handler mHandler = new Handler() {
            public void handleMessage(Message msg) {
                if (msg.what==position){
                    UtilBean utilBean = (UtilBean) msg.obj;
                    Log.e("dadada2",System.currentTimeMillis()+"");
                    if (utilBean!=null){
                        if (!TextUtil.isNull(utilBean.getaString())){
                            holder.videoplayer.tv_total_time.setText(JCUtils.stringForTime(Integer.parseInt(utilBean.getaString())));
                            lruCache.put(videoPath+"time",utilBean.getaString());
                        }
                       /* if (utilBean.getBitmap()!=null){
                            holder.videoplayer.thumbImageView.setImageBitmap(utilBean.getBitmap());
                            lruCache.put(videoPath,utilBean.getBitmap());
                        }*/
                    }
                }
            }
        };
        if(!TextUtil.isNull((String) lruCache.get(videoPath+"time"))){
            Log.e("dadada3",System.currentTimeMillis()+"/"+position);
            //holder.videoplayer.thumbImageView.setImageBitmap((Bitmap) lruCache.get(videoPath));
            holder.videoplayer.tv_total_time.setText(JCUtils.stringForTime(Integer.parseInt((String)lruCache.get(videoPath+"time"))));
        }else {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    final UtilBean bean = MyUtils.getVideoThumb(videoPath);
                    Log.e("dadada1",System.currentTimeMillis()+"/"+position);
                    Message message = Message.obtain();
                    message.what = position;
                    message.obj = bean;
                    mHandler.sendMessage(message);
                }
            }).start();
        }

    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_video_name;
        JCVideoPlayerStandard videoplayer;

        public ViewHolder(View view) {
            super(view);
            tv_video_name = (TextView) view.findViewById(R.id.tv_video_name);
            videoplayer = (JCVideoPlayerStandard) view.findViewById(R.id.videoplayer);
        }
    }
}
