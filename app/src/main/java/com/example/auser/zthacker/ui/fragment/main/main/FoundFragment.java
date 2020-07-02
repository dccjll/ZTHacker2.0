package com.example.auser.zthacker.ui.fragment.main.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.auser.zthacker.R;
import com.example.auser.zthacker.adapter.MyFragmentPagerAdapter;
import com.example.auser.zthacker.base.BaseFragment;
import com.example.auser.zthacker.base.Config;
import com.example.auser.zthacker.bean.EventPostBean;
import com.example.auser.zthacker.dialog.LoadingDialog;
import com.example.auser.zthacker.dialog.PublishSelectDialog;
import com.example.auser.zthacker.ui.activity.found.PublishImageActivity;
import com.example.auser.zthacker.ui.activity.found.PublishTextActivity;
import com.example.auser.zthacker.ui.activity.search.SearchInputActivity;
import com.example.auser.zthacker.ui.fragment.main.found.CommunityFragment;
import com.example.auser.zthacker.ui.fragment.main.found.NewsChildNewsFragment;
import com.example.auser.zthacker.utils.SPUtils;
import com.example.auser.zthacker.utils.TextUtil;
import com.example.auser.zthacker.view.MyViewPager;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;
import fm.jiecao.jcvideoplayer_lib.JCVideoPlayer;

/**
 * Created by Auser on 2017/8/4.
 */

public class FoundFragment extends BaseFragment {
    @BindView(R.id.tv_community)
    TextView tv_community;
    @BindView(R.id.iv_community_underline)
    ImageView iv_community_underline;
    @BindView(R.id.tv_news)
    TextView tv_news;
    @BindView(R.id.iv_news_underline)
    ImageView iv_news_underline;
    @BindView(R.id.viewPager_home)
    MyViewPager viewPager_home;
    @BindView(R.id.iv_publish)
    ImageView iv_publish;
    private List<Fragment> fragmentList;
    public int currentItem;
    private PublishSelectDialog dialog;
    private MyFragmentPagerAdapter adapter;
    private ArrayList<LocalMedia> selectList = new ArrayList<>();
    private String userId;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setContentView(inflater, container, R.layout.fragment_found);
        EventBus.getDefault().register(this);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        isViewInitFinished = true;
    }

    protected void initView() {
        setTop(R.color.black);
        initFragment();
    }

    private void initFragment() {
        fragmentList = new ArrayList<>();
        fragmentList.add(new CommunityFragment());
        fragmentList.add(new NewsChildNewsFragment());
        //initAdapter();
    }

    private void initAdapter() {
        if (adapter == null) {
            adapter = new MyFragmentPagerAdapter(getChildFragmentManager(), fragmentList);
            viewPager_home.setAdapter(adapter);
            viewPager_home.SetNomScroller(true);
            viewPager_home.setNoScroll(true);
            viewPager_home.setOffscreenPageLimit(1);
            viewPager_home.setCurrentItem(currentItem);
        }
    }

    @OnClick({R.id.rl_community, R.id.rl_news, R.id.iv_publish, R.id.iv_serch})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rl_community:
                currentItem = 0;
                setOneSelect();
                viewPager_home.setCurrentItem(currentItem);
                break;
            case R.id.rl_news:
                currentItem = 1;
                setTwoSelect();
                viewPager_home.setCurrentItem(currentItem);
                break;
            case R.id.iv_serch:
                SearchInputActivity.startActivity(getActivity());
                break;
            case R.id.iv_publish:
                JCVideoPlayer.releaseAllVideos();
                userId = SPUtils.getSharedStringData(getActivity(), "userId");
                if (TextUtil.isNull(userId)) {
                    LoadingDialog.showDialogForLogin(getActivity());
                    return;
                }
                dialog = (PublishSelectDialog) LoadingDialog.showDialogForPublish(getActivity(), "纯文字", "图片或视频");
                dialog.setOnClickChoose(new PublishSelectDialog.OnClickChoose() {
                    @Override
                    public void onClick(int id) {
                        switch (id) {
                            case 0:
                                PublishTextActivity.startActivity(getActivity());
                                break;
                            case 1:
                                if (selectList != null && selectList.size() > 0) {
                                    selectList.clear();
                                }
                                openPicturesSelect();
                                break;
                        }
                    }
                });
                break;
        }
    }

    private void openPicturesSelect() {
        PictureSelector.create(FoundFragment.this)
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

    private void setOneSelect() {
        JCVideoPlayer.releaseAllVideos();
        tv_community.setTextColor(getActivity().getResources().getColor(R.color.text_32));
        iv_community_underline.setVisibility(View.VISIBLE);
        tv_news.setTextColor(getActivity().getResources().getColor(R.color.text_64));
        iv_news_underline.setVisibility(View.INVISIBLE);
        iv_publish.setVisibility(View.VISIBLE);
    }

    private void setTwoSelect() {
        JCVideoPlayer.releaseAllVideos();
        tv_community.setTextColor(getActivity().getResources().getColor(R.color.text_64));
        iv_community_underline.setVisibility(View.INVISIBLE);
        tv_news.setTextColor(getActivity().getResources().getColor(R.color.text_32));
        iv_news_underline.setVisibility(View.VISIBLE);
        iv_publish.setVisibility(View.GONE);
    }

    @Override
    protected void onFragmentVisibleChange(boolean isVisible) {
        if (isVisible) {
            initAdapter();
        }
    }

    @Subscribe
    public void onEventMainThread(String msg) {
        if (msg.equals(Config.MAIN_FOUND_NEWS)){
            currentItem = 1;
            setTwoSelect();
            viewPager_home.setCurrentItem(currentItem);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == getActivity().RESULT_OK) {
            switch (requestCode) {
                case PictureConfig.CHOOSE_REQUEST:
                    // 图片选择结果回调
                    selectList = (ArrayList) PictureSelector.obtainMultipleResult(data);
                    // 例如 LocalMedia 里面返回三种path
                    // 1.media.getPath(); 为原图path
                    // 2.media.getCutPath();为裁剪后path，需判断media.isCut();是否为true
                    // 3.media.getCompressPath();为压缩后path，需判断media.isCompressed();是否为true
                    // 如果裁剪并压缩了，已取压缩路径为准，因为是先裁剪后压缩的
                    PublishImageActivity.startActivity(getActivity(), selectList);
                    break;
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        JCVideoPlayer.releaseAllVideos();
    }
}
