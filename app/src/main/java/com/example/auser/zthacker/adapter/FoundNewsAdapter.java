package com.example.auser.zthacker.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.auser.zthacker.R;
import com.example.auser.zthacker.base.Config;
import com.example.auser.zthacker.bean.EventPostBean;
import com.example.auser.zthacker.bean.NewsInfoBean;
import com.example.auser.zthacker.bean.NormalObjData;
import com.example.auser.zthacker.bean.UtilBean;
import com.example.auser.zthacker.dialog.LoadingDialog;
import com.example.auser.zthacker.http.ApiRequestData;
import com.example.auser.zthacker.ui.activity.found.NewsInfoActivity;
import com.example.auser.zthacker.utils.DisplayUtil;
import com.example.auser.zthacker.utils.SPUtils;
import com.example.auser.zthacker.utils.TextUtil;
import com.example.auser.zthacker.utils.TimeUtil;
import com.example.auser.zthacker.utils.ToastUtil;
import com.example.auser.zthacker.utils.VideoFrameImageLoader;
import com.example.auser.zthacker.view.FullyGridLayoutManager;
import com.example.auser.zthacker.view.RecycleViewDividerLine;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;
import fm.jiecao.jcvideoplayer_lib.JCUtils;
import fm.jiecao.jcvideoplayer_lib.JCVideoPlayerStandard;
import okhttp3.Call;
import okhttp3.Response;

import static android.view.View.GONE;

/**
 * Created by zhengkq on 2018-1-30.
 */

public class FoundNewsAdapter extends RecyclerView.Adapter<FoundNewsAdapter.ViewHolder> {
    Activity context;
    LayoutInflater mInflater;
    private List<NewsInfoBean> list;
    private int type;//1:首页资讯 2:发现中的咨询
    private OnRecyclerViewItemClickListener mOnItemClickListener;
    private GsonBuilder gsonBuilder;
    private EventPostBean eventPostBean;
    private VideoFrameImageLoader mVideoFrameImageLoader;
    private boolean isFirst = true;
    private String[] videoUrls;

    public FoundNewsAdapter(Context context, List<NewsInfoBean> list, int type) {
        this.context = (Activity) context;
        mInflater = LayoutInflater.from(context);
        this.list = list;
        this.type = type;

    }

    public String[] getVideoUrls() {
        return videoUrls;
    }

    public void setVideoUrls( VideoFrameImageLoader vfi) {
        this.videoUrls = vfi.getVideoUrls();
        this.mVideoFrameImageLoader = vfi;
    }

    @Override
    public FoundNewsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_found_news_one, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final FoundNewsAdapter.ViewHolder viewHolder, @SuppressLint("RecyclerView") final int position) {
        if (list != null) {
            if (isFirst) {
                mVideoFrameImageLoader.initList();
                isFirst = false;
            }

            final NewsInfoBean newsInfoBean = list.get(position);
            if (TextUtil.isNull(newsInfoBean.getShowType())||newsInfoBean.getShowType().equals("3")) {
                bindOneViewHolder(viewHolder, newsInfoBean);
            } else if (newsInfoBean.getShowType().equals("2")) {
                bindMoreViewHolder(viewHolder, newsInfoBean);
            } else if (newsInfoBean.getShowType().equals("1")) {
                bindVideoViewHolder(viewHolder, newsInfoBean, position);
            } else {
                bindOneViewHolder(viewHolder, newsInfoBean);
            }
            viewHolder.tv_content.setText(newsInfoBean.getArticleTitle());
            if (newsInfoBean.getAttachList() != null && !TextUtil.isNull(newsInfoBean.getAttachList().get(0).getFileUrl())) {
                Glide.with(context)
                        .load(newsInfoBean.getAttachList().get(0).getFileUrl())
                        .asBitmap()
                        .diskCacheStrategy(DiskCacheStrategy.RESULT)
                        .centerCrop()
                        .placeholder(R.mipmap.img_placeholder_152)
                        .override(80, 60)
                        .thumbnail(0.1f)
                        .into(viewHolder.iv_one_picture);
            }
            viewHolder.tv_time.setText(TimeUtil.formatDateStr2Desc(newsInfoBean.getCreateDate(), "yyyy-MM-dd HH:mm:ss"));
            viewHolder.iv_zan.setImageResource(newsInfoBean.getIsLike().equals("0")
                    ? R.mipmap.icon_zan : R.mipmap.bottombar_icon_zan_hl);
            viewHolder.tv_zan_count.setText(newsInfoBean.getLikeCount() + "");
            /*viewHolder.tv_comment_count.setText(newsInfoBean.getCommentCount() + "");*/

            viewHolder.ll_zan.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setZanRequest(newsInfoBean, viewHolder);
                }
            });
            //判断是否设置了监听器
            if (mOnItemClickListener != null) {
                //为ItemView设置监听器
                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mOnItemClickListener.onItemClick(viewHolder.itemView, position); // 2
                    }
                });
            }


               /* //更新主界面，显示缩略图
                final Handler myHandler = new Handler() {
                    public void handleMessage(Message msg) {
                        UtilBean utilBean = (UtilBean) msg.obj;
                        if (msg.what == position) {
                            if (utilBean.getBitmap() != null) {
                                viewHolder.videoplayer.thumbImageView.setImageBitmap(utilBean.getBitmap());
                            }
                            if (!TextUtil.isNull(utilBean.getaString())) {
                                viewHolder.videoplayer.tv_total_time.setText(JCUtils.stringForTime(Integer.parseInt(utilBean.getaString())));
                            }
                            if (!TextUtil.isNull(utilBean.getUpToken()) && utilBean.getBitmap() != null) {
                                lruCache.put(utilBean.getUpToken(), utilBean.getBitmap());
                                lruCache.put(utilBean.getUpToken() + "time", utilBean.getaString());
                                //MyUtils.releaseBitmap(utilBean.getBitmap());
                            }
                           *//* if (position==list.size()-1&&utilBean.getBitmap()!=null&&!utilBean.getBitmap().isRecycled()){
                                utilBean.getBitmap().recycle();
                                MyUtils.releaseBitmap();
                            }
                            utilBean = null;*//*
                        }
                    }
                };
                if (newsInfoBean.getAttachList() != null && newsInfoBean.getAttachList().size() > 0
                        && !TextUtil.isNull(newsInfoBean.getAttachList().get(0).getFileUrl())) {
                    viewHolder.videoplayer.setUp(newsInfoBean.getAttachList().get(0).getFileUrl(), JCVideoPlayerStandard.SCREEN_LAYOUT_LIST);
                    if (lruCache.get(newsInfoBean.getAttachList().get(0).getFileUrl()) != null) {
                        viewHolder.videoplayer.thumbImageView.setImageBitmap((Bitmap) lruCache.get(newsInfoBean.getAttachList().get(0).getFileUrl()));
                        viewHolder.videoplayer.tv_total_time.setText(JCUtils.stringForTime(Integer.parseInt((String) lruCache.get(newsInfoBean.getAttachList().get(0).getFileUrl() + "time"))));
                    } else {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                final UtilBean utilBean = MyUtils.getVideoThumb(newsInfoBean.getAttachList().get(0).getFileUrl());
                                Message msg = Message.obtain();
                                msg.obj = utilBean;
                                msg.what = position;
                                myHandler.sendMessage(msg);
                            }
                        }).start();
                    }
                }*/

        }
    }

    private void bindOneViewHolder(ViewHolder viewHolder, NewsInfoBean newsInfoBean) {
        viewHolder.iv_one_picture.setVisibility(View.VISIBLE);
        viewHolder.rv_pictures.setVisibility(GONE);
        viewHolder.videoplayer.setVisibility(GONE);
    }

    private void bindMoreViewHolder(ViewHolder viewHolder, final NewsInfoBean newsInfoBean) {
        viewHolder.iv_one_picture.setVisibility(GONE);
        viewHolder.rv_pictures.setVisibility(View.VISIBLE);
        viewHolder.videoplayer.setVisibility(GONE);
        if (TextUtil.isNull(newsInfoBean.getArticleTitle())) {
            viewHolder.tv_content.setVisibility(GONE);
        } else {
            viewHolder.tv_content.setVisibility(View.VISIBLE);
            viewHolder.tv_content.setText(newsInfoBean.getArticleTitle());
        }
        List<String> stringList = new ArrayList<>();
        if (newsInfoBean.getAttachList() != null && newsInfoBean.getAttachList().size() > 0) {
            for (int i = 0; i < newsInfoBean.getAttachList().size(); i++) {
                stringList.add(newsInfoBean.getAttachList().get(i).getFileUrl());
            }
        }
        FoundCommunityPicturesAdapter adapter = new FoundCommunityPicturesAdapter(context, stringList, 0);
        viewHolder.rv_pictures.setAdapter(adapter);
        adapter.setOnItemClickListener(new FoundCommunityPicturesAdapter.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                NewsInfoActivity.startActivity(context, newsInfoBean);
            }
        });
    }

    private void bindVideoViewHolder(ViewHolder viewHolder, NewsInfoBean newsInfoBean, int position) {
        viewHolder.iv_one_picture.setVisibility(GONE);
        viewHolder.rv_pictures.setVisibility(GONE);
        viewHolder.videoplayer.setVisibility(View.VISIBLE);
        /*viewHolder.ll_comment.setVisibility(GONE);*/
        if (TextUtil.isNull(newsInfoBean.getArticleTitle())) {
            viewHolder.tv_content.setVisibility(GONE);
        } else {
            viewHolder.tv_content.setVisibility(View.VISIBLE);
            viewHolder.tv_content.setText(newsInfoBean.getArticleTitle());
        }
        String mImageUrl = videoUrls[position];
        viewHolder.videoplayer.setTag(mImageUrl);
        //从缓存中获取图片
        Bitmap bitmap = mVideoFrameImageLoader.showCacheBitmap(VideoFrameImageLoader.formatVideoUrl(videoUrls[position]));
        if (bitmap != null) {
            viewHolder.videoplayer.thumbImageView.setImageBitmap(bitmap);
        } else {
            //没有从缓存中加载到时，先设置一张默认图
            viewHolder.videoplayer.thumbImageView.setImageResource(R.mipmap.img_placeholder_152);
        }
        String time = mVideoFrameImageLoader.getStringFormMemCache(videoUrls[position]);
        if (!TextUtil.isNull(time)) {
            viewHolder.videoplayer.tv_total_time.setText(JCUtils.stringForTime(Integer.parseInt(time)));
        }
        viewHolder.videoplayer.setUp(mImageUrl, JCVideoPlayerStandard.SCREEN_LAYOUT_LIST);
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    private void setZanRequest(final NewsInfoBean newsInfoBean, final ViewHolder viewHolder) {
        String userId = SPUtils.getSharedStringData(context, "userId");
        if (TextUtil.isNull(userId)) {
            LoadingDialog.showDialogForLogin(context);
            return;
        }
        ApiRequestData.getInstance(context).ShowDialog(null);
        OkGo.get(ApiRequestData.getInstance(context).PublishTextLike)//点赞
                .tag(context)
                .params("objectId", newsInfoBean.getId())
                .params("sysAppUser", userId)
                .params("likeType", "2")
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        if (gsonBuilder == null) {
                            gsonBuilder = new GsonBuilder();
                        }
                        ApiRequestData.getInstance(context).getDialogDismiss();
                        NormalObjData<UtilBean> mData = gsonBuilder
                                .setPrettyPrinting()
                                .disableHtmlEscaping()
                                .create().fromJson(s, new TypeToken<NormalObjData<UtilBean>>() {
                                }.getType());
                        String code = mData.getCode();
                        if (!TextUtil.isNull(code) && code.equals("0")&&!TextUtil.isNull(mData.getMsg())) {
                            ToastUtil.show(context, mData.getMsg());
                            return;
                        }
                        if (mData.getData() != null) {
                            if (mData.getData().getState() == 0) {
                                newsInfoBean.setLikeCount(newsInfoBean.getLikeCount() - 1);
                            } else {
                                newsInfoBean.setLikeCount(newsInfoBean.getLikeCount() + 1);
                            }
                            newsInfoBean.setIsLike(mData.getData().getState()+"");
                            viewHolder.tv_zan_count.setText(newsInfoBean.getLikeCount() + "");
                            viewHolder.iv_zan.setImageResource(mData.getData().getState() == 0
                                    ? R.mipmap.icon_zan : R.mipmap.bottombar_icon_zan_hl);
                            eventPostBean = new EventPostBean();
                            eventPostBean.setType(Config.NEWS_ZAN);
                            eventPostBean.setNewsInfoBean(newsInfoBean);
                            EventBus.getDefault().post(eventPostBean);
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        ToastUtil.show(context, e.getMessage());
                    }
                });
    }

    public interface OnRecyclerViewItemClickListener {
        void onItemClick(View view, int position);
    }

    public void setOnItemClickListener(OnRecyclerViewItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_content)
        TextView tv_content;
        @BindView(R.id.iv_one_picture)
        ImageView iv_one_picture;
        @BindView(R.id.rv_pictures)
        RecyclerView rv_pictures;
        @BindView(R.id.videoplayer)
        JCVideoPlayerStandard videoplayer;
        /*@BindView(R.id.ll_comment)
        RelativeLayout ll_comment;
        @BindView(R.id.iv_comment)
        ImageView iv_comment;
        @BindView(R.id.tv_comment_count)
        TextView tv_comment_count;*/
        @BindView(R.id.ll_zan)
        RelativeLayout ll_zan;
        @BindView(R.id.iv_zan)
        ImageView iv_zan;
        @BindView(R.id.tv_zan_count)
        TextView tv_zan_count;
        @BindView(R.id.iv_more)
        ImageView iv_more;
        @BindView(R.id.tv_time)
        TextView tv_time;
        @BindView(R.id.ll_item)
        LinearLayout ll_item;
        private RecycleViewDividerLine recycleViewDividerLine;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            iv_more.setVisibility(GONE);

            if (type == 1) {
                ll_item.setBackgroundColor(context.getResources().getColor(R.color.white));
            }
            FullyGridLayoutManager fullyGridLayoutManager = new FullyGridLayoutManager(context, 3) {
                @Override
                public boolean canScrollHorizontally() {
                    return true;
                }

                @Override
                public boolean canScrollVertically() {
                    return false;
                }
            };
            rv_pictures.setLayoutManager(fullyGridLayoutManager);
            if (recycleViewDividerLine == null) {
                recycleViewDividerLine = new RecycleViewDividerLine(context, RecycleViewDividerLine.BOTH_SET,
                        DisplayUtil.dip2px(4), context.getResources().getColor(R.color.white));
                rv_pictures.addItemDecoration(recycleViewDividerLine);
            }
            videoplayer.backButton.setVisibility(View.INVISIBLE);
            videoplayer.batteryTimeLayout.setVisibility(View.INVISIBLE);
        }
    }
}
