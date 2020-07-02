package com.example.auser.zthacker.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.UnderlineSpan;
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
import com.example.auser.zthacker.bean.NormalObjData;
import com.example.auser.zthacker.bean.PublishInfoList;
import com.example.auser.zthacker.bean.UtilBean;
import com.example.auser.zthacker.dialog.LoadingDialog;
import com.example.auser.zthacker.dialog.MyPublishDelDialog;
import com.example.auser.zthacker.http.ApiRequestData;
import com.example.auser.zthacker.ui.activity.found.CommunityPicInfoActivity;
import com.example.auser.zthacker.ui.activity.found.CommunityVideoInfoActivity;
import com.example.auser.zthacker.ui.activity.found.ReportActivity;
import com.example.auser.zthacker.ui.activity.mine.MyCollectionActivity;
import com.example.auser.zthacker.ui.activity.mine.UserHomePageActivity;
import com.example.auser.zthacker.utils.DisplayUtil;
import com.example.auser.zthacker.utils.SPUtils;
import com.example.auser.zthacker.utils.TextUtil;
import com.example.auser.zthacker.utils.TimeUtil;
import com.example.auser.zthacker.utils.ToastUtil;
import com.example.auser.zthacker.utils.VideoFrameImageLoader;
import com.example.auser.zthacker.view.GlideCircleTransform;
import com.example.auser.zthacker.view.FullyGridLayoutManager;
import com.example.auser.zthacker.view.RecycleViewDividerLine;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import java.util.Iterator;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;
import fm.jiecao.jcvideoplayer_lib.JCUtils;
import fm.jiecao.jcvideoplayer_lib.JCVideoPlayerStandard;
import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by zhengkq on 2017-12-26.
 */

public class FoundCommunityAdapter extends RecyclerView.Adapter<FoundCommunityAdapter.ViewHolder> {
    LayoutInflater inflater;
    Activity context;
    private List<PublishInfoList.PublishInfo> list;
    private MyPublishDelDialog dialog;
    private GsonBuilder gsonBuilder;
    private int type;//标识哪个activity或者fragment new的adapter（默认：type=0；type=1：我发布的，没有个人头像、用户名、是否已关注等头部;type=2:我的收藏，没有点赞、评论和关注;type=3:首页展示，背景纯白，没有圆角）
    //private LruCache lruCache;
    private EventPostBean eventPostBean;
    private VideoFrameImageLoader mVideoFrameImageLoader;
    private boolean isFirst = true;
    private String[] videoUrls;
    private String userId;

    public FoundCommunityAdapter(Context context, List<PublishInfoList.PublishInfo> list, int type) {
        this.context = (Activity) context;
        inflater = LayoutInflater.from(context);
        this.list = list;
        this.type = type;
    }

    public String[] getVideoUrls() {
        return videoUrls;
    }

    public void setVideoUrls(VideoFrameImageLoader vfi) {
        this.videoUrls = vfi.getVideoUrls();
        this.mVideoFrameImageLoader = vfi;
    }

    @Override
    public FoundCommunityAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_found_community_one_picture, parent, false));
    }

    @Override
    public void onBindViewHolder(final FoundCommunityAdapter.ViewHolder viewHolder, final int position) {
        if (list != null && list.size() > 0) {
            if (isFirst) {
                mVideoFrameImageLoader.initList();
                isFirst = false;
            }
            final PublishInfoList.PublishInfo publishInfo = list.get(position);
            String imgUrls = publishInfo.getImgIds();
            List<String> imagesList = publishInfo.getImagesList(imgUrls);
            String classify = publishInfo.getType();
            //0代表文章 1代表图片 2代表视频
            if (TextUtil.isNull(classify)||classify.equals("0")) {
                bindOneViewHolder(viewHolder, publishInfo, imagesList);
            } else if (classify.equals("1")) {
                if (imagesList != null && imagesList.size() > 1) {
                    bindMoreViewHolder(viewHolder, publishInfo, imagesList);
                } else {
                    bindOneViewHolder(viewHolder, publishInfo, imagesList);
                }
            } else if (classify.equals("2")) {
                bindVideoViewHolder(viewHolder, publishInfo, position);
            }

            viewHolder.tv_name.setText(publishInfo.getSysAppUserName());
            if (TextUtil.isNull(publishInfo.getTitle())) {
                viewHolder.tv_title.setVisibility(View.GONE);
            } else {
                viewHolder.tv_title.setVisibility(View.VISIBLE);
                viewHolder.tv_title.setText(publishInfo.getTitle());
            }
            Glide.with(context)
                    .load(publishInfo.getSysAppUerImg())
                    .asBitmap()
                    .diskCacheStrategy(DiskCacheStrategy.RESULT)
                    .transform(new GlideCircleTransform(context))
                    .placeholder(R.mipmap.icon_userphote_hl)
                    .override(54,54)
                    .thumbnail(0.2f)
                    .dontAnimate()
                    .into(viewHolder.simpleDraweeView_head_icon);
            viewHolder.tv_is_attention.setTextColor(publishInfo.getIsFollow() == 1
                    ? context.getResources().getColor(R.color.text_64) : context.getResources().getColor(R.color.text_32));
            viewHolder.tv_is_attention.setText(publishInfo.getIsFollow() == 1 ? "已关注" : "+关注");
            if (TextUtil.isNull(publishInfo.getContent())) {
                viewHolder.tv_content.setVisibility(View.GONE);
            } else {
                viewHolder.tv_content.setVisibility(View.VISIBLE);
                if (!TextUtil.isNull(publishInfo.getContent()) && publishInfo.getContent().length() > 140) {
                    String more_content = publishInfo.getContent().substring(0, 140) + "...全文";
                    SpannableString ss = new SpannableString(more_content);
                    UnderlineSpan underlineSpan = new UnderlineSpan();
                    ss.setSpan(underlineSpan, more_content.length() - 2, more_content.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
                    viewHolder.tv_content.setMovementMethod(LinkMovementMethod.getInstance());
                    viewHolder.tv_content.setText(ss);
                } else {
                    viewHolder.tv_content.setText(publishInfo.getContent());
                }
                viewHolder.tv_content.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(publishInfo);
                    }
                });
            }

            viewHolder.tv_time.setText(TimeUtil.formatDateStr2Desc(publishInfo.getCreateDate(), "yyyy-MM-dd HH:mm:ss"));
            viewHolder.iv_zan.setImageResource(publishInfo.getIsLike() == 0
                    ? R.mipmap.icon_zan : R.mipmap.bottombar_icon_zan_hl);
            viewHolder.tv_zan_count.setText(publishInfo.getLikeCount() + "");
            /*viewHolder.tv_comment_count.setText(publishInfo.getCommentCount() + "");*/
            viewHolder.iv_more.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (type == 2) {
                        if (!TextUtil.isNull(publishInfo.getSysAppUser())
                                && publishInfo.getSysAppUser().equals(SPUtils.getSharedStringData(context, "userId"))) {
                            removeDate(publishInfo, "取消收藏", "");
                        } else {
                            removeDate(publishInfo, "举报", "取消收藏");
                        }
                    } else {
                        if (!TextUtil.isNull(publishInfo.getSysAppUser())
                                && publishInfo.getSysAppUser().equals(SPUtils.getSharedStringData(context, "userId"))) {
                            removeDate(publishInfo, "删除", "");
                        } else {
                            removeDate(publishInfo, "举报", "");
                        }
                    }
                }
            });

            if (!TextUtil.isNull(publishInfo.getSysAppUser()) && publishInfo.getSysAppUser().equals(SPUtils.getSharedStringData(context, "userId"))) {
                viewHolder.tv_is_attention.setVisibility(View.INVISIBLE);
            } else {
                viewHolder.tv_is_attention.setVisibility(View.VISIBLE);
            }
            viewHolder.tv_name.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    UserHomePageActivity.startActivity(context, publishInfo.getSysAppUser());
                }
            });
            viewHolder.simpleDraweeView_head_icon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    UserHomePageActivity.startActivity(context, publishInfo.getSysAppUser());
                }
            });
            viewHolder.ll_zan.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setZanRequest(publishInfo, viewHolder);
                }
            });
            viewHolder.tv_is_attention.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setAttentionRequest(publishInfo);
                }
            });
            //为ItemView设置监听器
            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(publishInfo);
                }
            });
        }
    }

    private void bindOneViewHolder(final ViewHolder viewHolder, final PublishInfoList.PublishInfo publishInfo
            , final List<String> imagesList) {
        viewHolder.tv_title.setVisibility(View.VISIBLE);
        viewHolder.iv_one_picture.setVisibility(View.VISIBLE);
        viewHolder.rv_pictures.setVisibility(View.GONE);
        viewHolder.videoplayer.setVisibility(View.GONE);
        if (imagesList != null && imagesList.size() > 0 && !TextUtil.isNull(imagesList.get(0))) {
            viewHolder.iv_one_picture.setVisibility(View.VISIBLE);
            Glide.with(context)
                    .load(imagesList.get(0))
                    .asBitmap()
                    .diskCacheStrategy(DiskCacheStrategy.RESULT)
                    .placeholder(R.mipmap.img_placeholder_152)
                    .override(620, 262)
                    .centerCrop()
                    .skipMemoryCache(false)
                    .thumbnail(0.2f)
                    .dontAnimate()
                    .into(viewHolder.iv_one_picture);
        } else {
            viewHolder.iv_one_picture.setVisibility(View.GONE);
        }
    }

    private void bindMoreViewHolder(final ViewHolder viewHolder, final PublishInfoList.PublishInfo publishInfo
            , List<String> imagesList) {
        viewHolder.tv_title.setVisibility(View.GONE);
        viewHolder.iv_one_picture.setVisibility(View.GONE);
        viewHolder.rv_pictures.setVisibility(View.VISIBLE);
        viewHolder.videoplayer.setVisibility(View.GONE);
        if (imagesList != null && imagesList.size() > 0) {
            FoundCommunityPicturesAdapter adapter = new FoundCommunityPicturesAdapter(context, imagesList, 1);
            viewHolder.rv_pictures.setFocusable(false);
            viewHolder.rv_pictures.setAdapter(adapter);
            adapter.setOnItemClickListener(new FoundCommunityPicturesAdapter.OnRecyclerViewItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    CommunityPicInfoActivity.startActivity(context, publishInfo, position);
                }
            });
        }
    }

    private void bindVideoViewHolder(final ViewHolder viewHolder, final PublishInfoList.PublishInfo publishInfo, final int position) {
        viewHolder.tv_title.setVisibility(View.GONE);
        viewHolder.iv_one_picture.setVisibility(View.GONE);
        viewHolder.rv_pictures.setVisibility(View.GONE);
        viewHolder.videoplayer.setVisibility(View.VISIBLE);
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
        if (!TextUtil.isNull(time)){
            viewHolder.videoplayer.tv_total_time.setText(JCUtils.stringForTime(Integer.parseInt(time)));
        }
        viewHolder.videoplayer.setUp(mImageUrl, JCVideoPlayerStandard.SCREEN_LAYOUT_LIST);
        /*//更新主界面，显示缩略图
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
                }
            }
        };
        if (!TextUtil.isNull(publishInfo.getVideoIds())) {
            viewHolder.videoplayer.setUp(publishInfo.getVideoIds(), JCVideoPlayerStandard.SCREEN_LAYOUT_LIST);
            if (lruCache.get(publishInfo.getVideoIds()) != null) {
                viewHolder.videoplayer.thumbImageView.setImageBitmap((Bitmap) lruCache.get(publishInfo.getVideoIds()));
                viewHolder.videoplayer.tv_total_time.setText(JCUtils.stringForTime(Integer.parseInt((String) lruCache.get(publishInfo.getVideoIds() + "time"))));
            } else {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        final UtilBean utilBean = MyUtils.getVideoThumb(publishInfo.getVideoIds());
                        Message msg = Message.obtain();
                        msg.obj = utilBean;
                        msg.what = position;
                        myHandler.sendMessage(msg);
                    }
                }).start();
            }
        }*/
    }

    private void startActivity(PublishInfoList.PublishInfo publishInfo) {
        if (publishInfo.getType().equals("2")) {
            CommunityVideoInfoActivity.startActivity(context, publishInfo);
        } else {
            CommunityPicInfoActivity.startActivity(context, publishInfo, 0);
        }
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    private void setAttentionRequest(final PublishInfoList.PublishInfo publishInfo) {
        userId = SPUtils.getSharedStringData(context, "userId");
        if (TextUtil.isNull(userId)) {
            LoadingDialog.showDialogForLogin(context);
            return;
        }
        ApiRequestData.getInstance(context).ShowDialog(null);
        OkGo.get(ApiRequestData.getInstance(context).PublishTextAttention)//关注
                .tag(context)
                .params("sysAppUser", userId)
                .params("sysAppUser2", publishInfo.getSysAppUser())
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        if (gsonBuilder == null) {
                            gsonBuilder = new GsonBuilder();
                        }
                        NormalObjData<UtilBean> mData = gsonBuilder
                                .setPrettyPrinting()
                                .disableHtmlEscaping()
                                .create().fromJson(s, new TypeToken<NormalObjData<UtilBean>>() {
                                }.getType());
                        String code = mData.getCode();
                        ApiRequestData.getInstance(context).getDialogDismiss();
                        if (!TextUtil.isNull(code) && code.equals("0")&&!TextUtil.isNull(mData.getMsg())) {
                            ToastUtil.show(context, mData.getMsg());
                            return;
                        }
                        //String sysAppUser = publishInfo.getSysAppUser();
                        if (mData.getData() != null) {
                            publishInfo.setIsFollow(mData.getData().getState());
                            for (int i = 0; i < list.size(); i++) {
                                if (list.get(i).getSysAppUser().equals(publishInfo.getSysAppUser())) {
                                    list.get(i).setIsFollow(mData.getData().getState());
                                    notifyItemChanged(i);
                                }
                            }
                            eventPostBean = new EventPostBean();
                            eventPostBean.setType(Config.PERSONAL_ATTENTION);
                            eventPostBean.setCount(publishInfo.getIsFollow());
                            eventPostBean.setId(publishInfo.getSysAppUser());
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

    private void setZanRequest(final PublishInfoList.PublishInfo publishInfo, final ViewHolder viewHolder) {
        userId = SPUtils.getSharedStringData(context, "userId");
        if (TextUtil.isNull(userId)) {
            LoadingDialog.showDialogForLogin(context);
            return;
        }
        ApiRequestData.getInstance(context).ShowDialog(null);
        OkGo.get(ApiRequestData.getInstance(context).PublishTextLike)//点赞
                .tag(context)
                .params("objectId", publishInfo.getId())
                .params("sysAppUser", userId)
                .params("likeType", "1")
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        if (gsonBuilder == null) {
                            gsonBuilder = new GsonBuilder();
                        }
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
                                publishInfo.setLikeCount(publishInfo.getLikeCount() - 1);
                            } else {
                                publishInfo.setLikeCount(publishInfo.getLikeCount() + 1);
                            }
                            publishInfo.setIsLike(mData.getData().getState());

                            viewHolder.tv_zan_count.setText(publishInfo.getLikeCount() + "");
                            viewHolder.iv_zan.setImageResource(mData.getData().getState() == 0
                                    ? R.mipmap.icon_zan : R.mipmap.bottombar_icon_zan_hl);
                            eventPostBean = new EventPostBean();
                            eventPostBean.setType(Config.PUBLISH_ZAN);
                            eventPostBean.setPublishInfo(publishInfo);
                            EventBus.getDefault().post(eventPostBean);
                            ApiRequestData.getInstance(context).getDialogDismiss();
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        ToastUtil.show(context, e.getMessage());
                    }
                });
    }


    private void removeDate(final PublishInfoList.PublishInfo publishInfo, final String del, final String report) {
        userId = SPUtils.getSharedStringData(context, "userId");
        dialog = (MyPublishDelDialog) LoadingDialog.showDialogForDelAndReport(context, del, report);
        this.dialog.setOnClickChoose(new MyPublishDelDialog.OnClickChoose() {
            @Override
            public void onClick(int id) {
                if (id == 1) {
                    if (TextUtil.isNull(userId)) {
                        LoadingDialog.showDialogForLogin(context);
                        return;
                    }
                    if (del.equals("删除")) {
                        ApiRequestData.getInstance(context).ShowDialog(null);
                        OkGo.get(ApiRequestData.getInstance(context).PublishTextDel)//删除文章
                                .tag(context)
                                .params("articleId", publishInfo.getId())
                                .params("sysAppUser", userId)
                                .execute(new StringCallback() {
                                    @Override
                                    public void onSuccess(String s, Call call, Response response) {
                                        if (gsonBuilder == null) {
                                            gsonBuilder = new GsonBuilder();
                                        }
                                        ApiRequestData.getInstance(context).getDialogDismiss();
                                        NormalObjData mData = gsonBuilder
                                                .setPrettyPrinting()
                                                .disableHtmlEscaping()
                                                .create().fromJson(s, new TypeToken<NormalObjData>() {
                                                }.getType());
                                        String code = mData.getCode();
                                        if (!TextUtil.isNull(code) && code.equals("0")&&!TextUtil.isNull(mData.getMsg())) {
                                            ToastUtil.show(context, mData.getMsg());
                                            return;
                                        }
                                        Iterator<PublishInfoList.PublishInfo> iterator = list.iterator();
                                        while (iterator.hasNext()) {
                                            PublishInfoList.PublishInfo next = iterator.next();
                                            if (next.equals(publishInfo)) {
                                                iterator.remove();
                                            }
                                        }
                                        notifyDataSetChanged();
                                        eventPostBean = new EventPostBean();
                                        eventPostBean.setType(Config.PUBLISH_TEXT_DEL);
                                        eventPostBean.setMessage(publishInfo.getId());
                                        EventBus.getDefault().post(eventPostBean);
                                    }

                                    @Override
                                    public void onError(Call call, Response response, Exception e) {
                                        super.onError(call, response, e);
                                        ToastUtil.show(context, e.getMessage());
                                    }
                                });

                    } else if (del.equals("举报")) {
                        ReportActivity.startActivity(context, publishInfo.getSysAppUser(), publishInfo.getId(), 1);
                    } else if (del.equals("取消收藏")) {
                        setCollectionRequest(publishInfo);
                    }
                } else if (id == 2) {
                    if (report.equals("取消收藏")) {
                        setCollectionRequest(publishInfo);
                    }
                }
            }
        });
    }

    private void setCollectionRequest(final PublishInfoList.PublishInfo publishInfo) {
        userId = SPUtils.getSharedStringData(context, "userId");
        ApiRequestData.getInstance(context).ShowDialog(null);
        OkGo.get(ApiRequestData.getInstance(context).PublishTextCollection)//收藏
                .tag(this)
                .params("communityArticleId", publishInfo.getId())
                .params("sysAppUser", userId)
                .params("likeType", "1")
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
                                publishInfo.setCollectCount(publishInfo.getCollectCount() - 1);
                            } else {
                                publishInfo.setCollectCount(publishInfo.getCollectCount() + 1);
                            }
                            publishInfo.setIsCollect(mData.getData().getState());
                            Iterator<PublishInfoList.PublishInfo> iterator = list.iterator();
                            while (iterator.hasNext()) {
                                PublishInfoList.PublishInfo next = iterator.next();
                                if (next.equals(publishInfo)) {
                                    iterator.remove();
                                }
                            }
                            notifyDataSetChanged();
                            if (type == 2) {
                                ((MyCollectionActivity) context).initAdapter(list);
                                EventPostBean eventPostBean = new EventPostBean();
                                eventPostBean.setType(Config.PUBLISH_COLLECTION);
                                eventPostBean.setPublishInfo(publishInfo);
                                EventBus.getDefault().post(eventPostBean);
                            }
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        ToastUtil.show(context, e.getMessage());
                    }
                });
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.simpleDraweeView_head_icon)
        ImageView simpleDraweeView_head_icon;
        @BindView(R.id.tv_name)
        TextView tv_name;
        @BindView(R.id.tv_is_attention)
        TextView tv_is_attention;
        @BindView(R.id.tv_content)
        TextView tv_content;
        @BindView(R.id.tv_time)
        TextView tv_time;
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
        @BindView(R.id.ll_top)
        RelativeLayout ll_top;
        @BindView(R.id.ll_item)
        LinearLayout ll_item;
        @BindView(R.id.tv_title)
        TextView tv_title;
        @BindView(R.id.iv_one_picture)
        ImageView iv_one_picture;
        @BindView(R.id.rv_pictures)
        RecyclerView rv_pictures;
        @BindView(R.id.videoplayer)
        JCVideoPlayerStandard videoplayer;
        private RecycleViewDividerLine recycleViewDividerLine;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            if (type == 3) {
                ll_item.setBackgroundColor(context.getResources().getColor(R.color.white));
            }
            if (type == 2) {
                ll_zan.setVisibility(View.GONE);
                /*ll_comment.setVisibility(View.GONE);*/
                tv_is_attention.setVisibility(View.INVISIBLE);
            } else {
                ll_zan.setVisibility(View.VISIBLE);
                /*ll_comment.setVisibility(View.VISIBLE);*/
                tv_is_attention.setVisibility(View.VISIBLE);
            }
            if (type == 1) {
                ll_top.setVisibility(View.GONE);
            } else {
                ll_top.setVisibility(View.VISIBLE);
            }
            FullyGridLayoutManager fullyGridLayoutManager = new FullyGridLayoutManager(context, 3) {
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
            videoplayer.batteryTimeLayout.setVisibility(View.INVISIBLE);
            videoplayer.backButton.setVisibility(View.INVISIBLE);
        }
    }
}
