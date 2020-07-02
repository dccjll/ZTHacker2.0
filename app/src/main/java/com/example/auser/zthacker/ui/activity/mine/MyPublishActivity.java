package com.example.auser.zthacker.ui.activity.mine;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.aspsine.swipetoloadlayout.OnLoadMoreListener;
import com.aspsine.swipetoloadlayout.OnRefreshListener;
import com.aspsine.swipetoloadlayout.SwipeToLoadLayout;
import com.example.auser.zthacker.R;
import com.example.auser.zthacker.adapter.FoundCommunityAdapter;
import com.example.auser.zthacker.base.BaseActivity;
import com.example.auser.zthacker.base.Config;
import com.example.auser.zthacker.bean.EventPostBean;
import com.example.auser.zthacker.bean.MyCollectionArticlesBean;
import com.example.auser.zthacker.bean.NormalObjData;
import com.example.auser.zthacker.bean.PublishInfoList;
import com.example.auser.zthacker.dialog.LoadingDialog;
import com.example.auser.zthacker.dialog.PublishSelectDialog;
import com.example.auser.zthacker.http.ApiRequestData;
import com.example.auser.zthacker.ui.activity.found.PublishImageActivity;
import com.example.auser.zthacker.ui.activity.found.PublishTextActivity;
import com.example.auser.zthacker.utils.DisplayUtil;
import com.example.auser.zthacker.utils.SPUtils;
import com.example.auser.zthacker.utils.TextUtil;
import com.example.auser.zthacker.utils.ToastUtil;
import com.example.auser.zthacker.utils.VideoFrameImageLoader;
import com.example.auser.zthacker.view.RecycleViewDividerLine;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;
import fm.jiecao.jcvideoplayer_lib.JCVideoPlayer;
import fm.jiecao.jcvideoplayer_lib.JCVideoPlayerManager;
import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by zhengkq on 2017/8/15.
 */

public class MyPublishActivity extends BaseActivity implements OnRefreshListener, OnLoadMoreListener {
    @BindView(R.id.swipe_target)
    RecyclerView recyclerView;
    @BindView(R.id.swipe_layout)
    SwipeToLoadLayout swipe_layout;
    @BindView(R.id.iv_send)
    ImageView iv_send;
    @BindView(R.id.iv_empty)
    ImageView iv_empty;
    @BindView(R.id.tv_empty_text)
    TextView tv_empty_text;
    /*@BindView(R.id.iv_contact_line)
    ImageView iv_contact_line;*/
    private List<PublishInfoList.PublishInfo> publishInfoList = new ArrayList<>();
    private List<String> videoUrlList = new ArrayList<>();
    private boolean isRefresh = false;
    private PublishSelectDialog dialog;
    private ArrayList<LocalMedia> selectList;
    private String userId;
    private GsonBuilder gsonBuilder;
    private FoundCommunityAdapter adapter;
    private VideoFrameImageLoader mVideoFrameImageLoader;

    public static void startActivity(Context context){
        Intent intent = new Intent(context,MyPublishActivity.class);
        context.startActivity(intent);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_message);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        //Fresco.initialize(this);
        initView();
    }

    private void initView() {
        setTop(R.color.black);
        setCentreText("我发布的");
        iv_send.setVisibility(View.VISIBLE);
        //iv_contact_line.setVisibility(View.INVISIBLE);
        userId = SPUtils.getSharedStringData(this, "userId");
        swipe_layout.setOnRefreshListener(this);
        swipe_layout.setOnLoadMoreListener(this);
        /*FullyLinearLayoutManager linearLayoutManager = new FullyLinearLayoutManager(this) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);*/
        recyclerView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
        RecycleViewDividerLine recycleViewDividerLine = new RecycleViewDividerLine(this, RecycleViewDividerLine.HORIZONTAL_LIST,
                DisplayUtil.dip2px(12),getResources().getColor(R.color.tab_bg_fa));
        recyclerView.addItemDecoration(recycleViewDividerLine);
        mVideoFrameImageLoader = new VideoFrameImageLoader(this, recyclerView);

        ApiRequestData.getInstance(this).ShowDialog(null);
        initRequest();
    }

    private void initRequest() {
        ApiRequestData.getInstance(this).getUserPublishList(userId,new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        if (gsonBuilder == null) {
                            gsonBuilder = new GsonBuilder();
                        }
                        initRefresh();
                        NormalObjData<MyCollectionArticlesBean> mData = gsonBuilder
                                .setPrettyPrinting()
                                .disableHtmlEscaping()
                                .create().fromJson(s, new TypeToken<NormalObjData<MyCollectionArticlesBean>>() {
                                }.getType());
                        ApiRequestData.getInstance(MyPublishActivity.this).getDialogDismiss();
                        String code = mData.getCode();
                        if (!TextUtil.isNull(mData.getMsg())){
                            ToastUtil.show(MyPublishActivity.this,mData.getMsg());
                        }
                        if (!TextUtil.isNull(code) && code.equals("0")) {
                            return;
                        }
                        if (publishInfoList!=null&&publishInfoList.size()>0){
                            publishInfoList.clear();
                        }
                        if (mData.getData().getDataList()!=null&&mData.getData().getDataList().size()>0){
                            publishInfoList.addAll(mData.getData().getDataList());
                        }
                        initAdapter(publishInfoList);
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        ToastUtil.show(MyPublishActivity.this, e.getMessage());
                        initRefresh();
                    }
                });
    }

    public void initAdapter(List<PublishInfoList.PublishInfo> list) {
        if (list==null||list.size()==0){
            iv_empty.setVisibility(View.VISIBLE);
            tv_empty_text.setVisibility(View.VISIBLE);
            iv_empty.setImageResource(R.mipmap.img_blankpage_mycollection);
            tv_empty_text.setText("暂无发布内容");
            tv_empty_text.setTextColor(getResources().getColor(R.color.text_90));
        }else {
            iv_empty.setVisibility(View.GONE);
            tv_empty_text.setVisibility(View.GONE);
            if (videoUrlList!=null&&videoUrlList.size()>0){
                videoUrlList.clear();
            }
            for (int i = 0;i<publishInfoList.size();i++){
                if (publishInfoList.get(i).getType().equals("2")){
                    videoUrlList.add(publishInfoList.get(i).getVideoIds());
                }else {
                    videoUrlList.add("");
                }
            }
            String[] videoArray = videoUrlList.toArray(new String[videoUrlList.size()]);
            mVideoFrameImageLoader.setVideoUrls(videoArray);
            //设置适配器
            if (adapter==null){
                adapter = new FoundCommunityAdapter(this,publishInfoList,1);
                adapter.setVideoUrls(mVideoFrameImageLoader);
                recyclerView.setAdapter(adapter);
                recyclerView.addOnChildAttachStateChangeListener(new RecyclerView.OnChildAttachStateChangeListener() {
                    @Override
                    public void onChildViewAttachedToWindow(View view) {

                    }

                    @Override
                    public void onChildViewDetachedFromWindow(View view) {
                        if (JCVideoPlayerManager.getCurrentJcvd() != null) {
                            Log.e("rrrrrrr","dddffggg");
                            JCVideoPlayer videoPlayer = (JCVideoPlayer) JCVideoPlayerManager.getCurrentJcvd();
                            Log.e("ddddddddd",videoPlayer.currentState+"");
                            if (videoPlayer.currentState == JCVideoPlayer.CURRENT_STATE_PLAYING) {
                                //JCVideoPlayer.releaseAllVideos();
                                videoPlayer.release();
                            }
                        }
                    }
                });
            }else {
                adapter.setVideoUrls(mVideoFrameImageLoader);
                adapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public void onRefresh() {
        isRefresh = true;
        initRequest();
    }

    @Override
    public void onLoadMore() {
        isRefresh = false;
        initRequest();
    }

    private void initRefresh() {
        if (isRefresh) {
            swipe_layout.setRefreshing(false);
        } else {
            swipe_layout.setLoadingMore(false);
        }
    }

    @OnClick({R.id.image_back,R.id.iv_send})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.image_back:
                finish();
                break;
            case R.id.iv_send:
                dialog = (PublishSelectDialog) LoadingDialog.showDialogForPublish(this, "纯文字", "图片或视频");
                dialog.setOnClickChoose(new PublishSelectDialog.OnClickChoose() {
                    @Override
                    public void onClick(int id) {
                        switch (id){
                            case 0:
                                PublishTextActivity.startActivity(MyPublishActivity.this);
                                break;
                            case 1:
                                openPicturesSelect();
                                break;
                        }
                    }
                });
                break;
        }
    }

    private void openPicturesSelect() {
        PictureSelector.create(this)
                .openGallery(PictureMimeType.ofAll())// 全部.PictureMimeType.ofAll()、图片.ofImage()、视频.ofVideo()、音频.ofAudio()
                .theme(R.style.picture_default_style)// 主题样式设置 具体参考 values/styles   用法：R.style.picture.white.style
                .maxSelectNum(9)// 最大图片选择数量
                .minSelectNum(1)// 最小选择数量
                .imageSpanCount(3)// 每行显示个数
                .selectionMode(PictureConfig.MULTIPLE)// 多选 or 单选
                .previewImage(true)// 是否可预览图片
                .previewVideo(true)// 是否可预览视频
                .enablePreviewAudio(false) // 是否可播放音频
                .isCamera(true)// 是否显示拍照按钮
                .isZoomAnim(true)// 图片列表点击 缩放效果 默认true
                //.setOutputCameraPath("/CustomPath")// 自定义拍照保存路径
                .enableCrop(false)// 是否裁剪
                .compress(true)// 是否压缩
                .synOrAsy(true)//同步true或异步false 压缩 默认同步
                //.compressSavePath(getPath())//压缩图片保存地址
                //.sizeMultiplier(0.5f)// glide 加载图片大小 0~1之间 如设置 .glideOverride()无效
                .glideOverride(160, 160)// glide 加载宽高，越小图片列表越流畅，但会影响列表图片浏览的清晰度
                .withAspectRatio(1, 1)// 裁剪比例 如16:9 3:2 3:4 1:1 可自定义
                .hideBottomControls(false)// 是否显示uCrop工具栏，默认不显示
                .isGif(false)// 是否显示gif图片
                .freeStyleCropEnabled(true)// 裁剪框是否可拖拽
                .circleDimmedLayer(false)// 是否圆形裁剪
                .showCropFrame(true)// 是否显示裁剪矩形边框 圆形裁剪时建议设为false
                .showCropGrid(true)// 是否显示裁剪矩形网格 圆形裁剪时建议设为false
                .openClickSound(false)// 是否开启点击声音
                .selectionMedia(selectList)// 是否传入已选图片
//                        .videoMaxSecond(15)
//                        .videoMinSecond(10)
                //.previewEggs(false)// 预览图片时 是否增强左右滑动图片体验(图片滑动一半即可看到上一张是否选中)
                //.cropCompressQuality(90)// 裁剪压缩质量 默认100
                .minimumCompressSize(100)// 小于100kb的图片不压缩
                //.cropWH()// 裁剪宽高比，设置如果大于图片本身宽高则无效
                //.rotateEnabled() // 裁剪是否可旋转图片
                //.scaleEnabled()// 裁剪是否可放大缩小图片
                //.videoQuality()// 视频录制质量 0 or 1
                //.videoSecond()//显示多少秒以内的视频or音频也可适用
                //.recordVideoSecond()//录制视频秒数 默认60s
                .forResult(PictureConfig.CHOOSE_REQUEST);//结果回调onActivityResult code
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case PictureConfig.CHOOSE_REQUEST:
                    // 图片选择结果回调
                    selectList = (ArrayList<LocalMedia>)PictureSelector.obtainMultipleResult(data);
                    // 例如 LocalMedia 里面返回三种path
                    // 1.media.getPath(); 为原图path
                    // 2.media.getCutPath();为裁剪后path，需判断media.isCut();是否为true
                    // 3.media.getCompressPath();为压缩后path，需判断media.isCompressed();是否为true
                    // 如果裁剪并压缩了，已取压缩路径为准，因为是先裁剪后压缩的
                    PublishImageActivity.startActivity(MyPublishActivity.this, selectList);
                    break;
            }
        }
    }

    @Subscribe
    public void onEventMainThread(EventPostBean eventPostBean) {
        if (eventPostBean.getType().equals(Config.PUBLISH_IMAGE)){
            userId = SPUtils.getSharedStringData(this, "userId");
            initRequest();
        }else if (eventPostBean.getType().equals(Config.PUBLISH_ZAN)) {
            PublishInfoList.PublishInfo publishInfo = eventPostBean.getPublishInfo();
            for (int i = 0; i < publishInfoList.size(); i++) {
                if (publishInfoList.get(i).getId().equals(publishInfo.getId())
                        &&publishInfoList.get(i).getIsLike()!=publishInfo.getIsLike()) {
                    publishInfoList.get(i).setLikeCount(publishInfo.getLikeCount());
                    publishInfoList.get(i).setIsLike(publishInfo.getIsLike());
                    adapter.notifyDataSetChanged();
                }
            }
        }else if(eventPostBean.getType().equals(Config.PUBLISH_COLLECTION)){
            PublishInfoList.PublishInfo publishInfo = eventPostBean.getPublishInfo();
            for (int i = 0; i < publishInfoList.size(); i++) {
                if (publishInfoList.get(i).getId().equals(publishInfo.getId())
                        &&publishInfoList.get(i).getIsCollect()!=publishInfo.getIsCollect()) {
                    publishInfoList.get(i).setCollectCount(publishInfo.getCollectCount());
                    publishInfoList.get(i).setIsCollect(publishInfo.getIsCollect());
                }
            }
        }else if (eventPostBean.getType().equals(Config.PUBLISH_TEXT_DEL)) {
            Iterator<PublishInfoList.PublishInfo> iterator = publishInfoList.iterator();
            while (iterator.hasNext()) {
                PublishInfoList.PublishInfo next = iterator.next();
                if (next.getId().equals(eventPostBean.getMessage())) {
                    iterator.remove();
                }
            }
            adapter.notifyDataSetChanged();
        }else if (eventPostBean.getType().equals(Config.PUBLISH_COMMENT_SUCCESS)){
            String articleId = eventPostBean.getId();
            for (int i = 0; i < publishInfoList.size(); i++) {
                if (publishInfoList.get(i).getId().equals(articleId)){
                    publishInfoList.get(i).setCommentCount(publishInfoList.get(i).getCommentCount()+1);
                    adapter.notifyItemChanged(i);
                }
            }
        }else if (eventPostBean.getType().equals(Config.PUBLISH_COMMENT_DEL)){
            String articleId = eventPostBean.getId();
            for (int i = 0; i < publishInfoList.size(); i++) {
                if (publishInfoList.get(i).getId().equals(articleId)){
                    publishInfoList.get(i).setCommentCount(publishInfoList.get(i).getCommentCount()-1);
                    adapter.notifyItemChanged(i);
                }
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        JCVideoPlayer.releaseAllVideos();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //根据 Tag 取消请求
        OkGo.getInstance().cancelTag(this);
        EventBus.getDefault().unregister(this);
    }
}
