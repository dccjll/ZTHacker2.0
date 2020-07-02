package com.example.auser.zthacker.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ForegroundColorSpan;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.auser.zthacker.R;
import com.example.auser.zthacker.bean.CommunityCommentBean;
import com.example.auser.zthacker.bean.MsgAdapterListBean;
import com.example.auser.zthacker.bean.MsgZanListBean;
import com.example.auser.zthacker.bean.PublishInfoList;
import com.example.auser.zthacker.ui.activity.found.CommunityPicInfoActivity;
import com.example.auser.zthacker.ui.activity.message.MyCommentMesActivity;
import com.example.auser.zthacker.utils.DisplayUtil;
import com.example.auser.zthacker.utils.TextUtil;
import com.example.auser.zthacker.utils.TimeUtil;
import com.example.auser.zthacker.utils.VideoFrameImageLoader;
import com.example.auser.zthacker.view.RecycleViewDividerLine;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;
import fm.jiecao.jcvideoplayer_lib.JCUtils;
import fm.jiecao.jcvideoplayer_lib.JCVideoPlayerStandard;

/**
 * Created by Dell on 2018-2-3.
 */

public class MyMsgCommentAdapter extends RecyclerView.Adapter<MyMsgCommentAdapter.ViewHolder> {
    LayoutInflater inflater;
    private Context context;
    private List<MsgAdapterListBean> msgCommentList;
    private int type;//type=0:消息点赞;type=1:消息评论
    private OnRecyclerViewItemClickListener mOnItemClickListener;
    private VideoFrameImageLoader mVideoFrameImageLoader;
    private boolean isFirst = true;
    private String[] videoUrls;
    public MyMsgCommentAdapter(Context context, List<MsgAdapterListBean> msgCommentList, int type) {
        this.context = context;
        this.msgCommentList = msgCommentList;
        this.type = type;
        inflater = LayoutInflater.from(context);
    }

    public String[] getVideoUrls() {
        return videoUrls;
    }

    public void setVideoUrls(VideoFrameImageLoader vfi) {
        this.videoUrls = vfi.getVideoUrls();
        this.mVideoFrameImageLoader = vfi;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_found_community_msg_one_picture, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyMsgCommentAdapter.ViewHolder viewHolder, final int position) {
        if (isFirst) {
            mVideoFrameImageLoader.initList();
            isFirst = false;
        }
        MsgAdapterListBean msgAdapterListBean = msgCommentList.get(position);
        final List<CommunityCommentBean> commetList = msgAdapterListBean.getCommetList();
        final PublishInfoList.PublishInfo publishInfo = msgAdapterListBean.getArticles();
        MsgZanListBean zanBean = msgAdapterListBean.getZanBean();
        if (commetList!=null){
            viewHolder.tv_comment_count.setText(commetList.size() + "");
        }
        if (type==1){
            viewHolder.tv_time.setText(TimeUtil.formatDateStr2Desc(commetList.get(0).getCreateDate(), "yyyy-MM-dd HH:mm:ss"));
        }else{
            viewHolder.tv_time.setText(TimeUtil.formatDateStr2Desc(publishInfo.getCreateDate(),"yyyy-MM-dd HH:mm:ss"));
        }
        if (zanBean!=null){
            List<String> likeList = zanBean.getLikeList();
            viewHolder.tv_zan_count.setText(likeList.size()+"");
            StringBuilder stringBuilder = new StringBuilder();
            for (int i = 0;i<likeList.size();i++){
                if (i==likeList.size()-1){
                    stringBuilder.append(likeList.get(i));
                }else {
                    stringBuilder.append(likeList.get(i)+",");
                }
            }
            SpannableString styledText = new SpannableString(stringBuilder.toString());
            int t = 0;
            for (int i = 0;i<likeList.size();i++){
                styledText.setSpan(new ForegroundColorSpan(context.getResources().getColor(R.color.comment_username_bg))
                        , t, zanBean.getLikeList().get(i).length()+t, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                if (i==likeList.size()-1){
                    t =t+ likeList.get(i).length();
                }else {
                    t =t+ likeList.get(i).length()+1;
                }
            }
            viewHolder.tv_zan_user.setText(styledText);
        }
        if (TextUtil.isNull(msgAdapterListBean.getArticles().getContent())) {
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
        }
        viewHolder.tv_title.setVisibility(!TextUtil.isNull(publishInfo.getTitle())
                ? View.VISIBLE : View.GONE);
        viewHolder.tv_title.setText(publishInfo.getTitle());

        String imgUrls = publishInfo.getImgIds();
        final List<String> imagesList = publishInfo.getImagesList(imgUrls);
        String classify = publishInfo.getType();
        if (classify.equals("0")) {
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

        //评论
        FirstClassCommentAdapter adapter = new FirstClassCommentAdapter(context, commetList, 3);
        viewHolder.rl_reply.setAdapter(adapter);
        viewHolder.rl_reply.setFocusable(false);
        adapter.setOnItemClickListener(new FirstClassCommentAdapter.OnRecyclerViewClickListener() {
            @Override
            public void onClick(View view, int position) {
                switch (view.getId()) {
                    case R.id.tv_reply:
                        ((MyCommentMesActivity) context).showSoftKeyboard(publishInfo, commetList.get(position));
                        break;
                }
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
    }

    private void bindVideoViewHolder(ViewHolder viewHolder, PublishInfoList.PublishInfo publishInfo, int position) {
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
    }

    private void bindMoreViewHolder(ViewHolder viewHolder, final PublishInfoList.PublishInfo publishInfo, List<String> imagesList) {
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

    private void bindOneViewHolder(ViewHolder viewHolder, PublishInfoList.PublishInfo publishInfo, List<String> imagesList) {
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

    @Override
    public int getItemCount() {
        return msgCommentList == null ? 0 : msgCommentList.size();
    }

    public interface OnRecyclerViewItemClickListener {
        void onItemClick(View view , int position);
    }
    public void setOnItemClickListener(OnRecyclerViewItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_title)
        TextView tv_title;
        @BindView(R.id.tv_content)
        TextView tv_content;
        @BindView(R.id.iv_one_picture)
        ImageView iv_one_picture;
        @BindView(R.id.rv_pictures)
        RecyclerView rv_pictures;
        @BindView(R.id.videoplayer)
        JCVideoPlayerStandard videoplayer;
        @BindView(R.id.tv_time)
        TextView tv_time;
        @BindView(R.id.ll_zan)
        LinearLayout ll_zan;
        @BindView(R.id.iv_zan)
        ImageView iv_zan;
        @BindView(R.id.tv_zan_count)
        TextView tv_zan_count;
        @BindView(R.id.tv_zan_user)
        TextView tv_zan_user;
        @BindView(R.id.ll_comment)
        LinearLayout ll_comment;
        @BindView(R.id.tv_comment_count)
        TextView tv_comment_count;
        @BindView(R.id.rl_reply)
        RecyclerView rl_reply;
        private RecycleViewDividerLine recycleViewDividerLine;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            if (type == 0) {
                ll_zan.setVisibility(View.VISIBLE);
                ll_comment.setVisibility(View.GONE);
            } else if (type == 1) {
                ll_zan.setVisibility(View.GONE);
                ll_comment.setVisibility(View.VISIBLE);
            }
            rl_reply.setLayoutManager(new LinearLayoutManager(context,LinearLayoutManager.VERTICAL,false));

            rv_pictures.setLayoutManager(new GridLayoutManager(context,3));
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
