package com.example.auser.zthacker.ui.activity.found;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.auser.zthacker.R;
import com.example.auser.zthacker.adapter.GridImageAdapter;
import com.example.auser.zthacker.base.BaseActivity;
import com.example.auser.zthacker.base.Config;
import com.example.auser.zthacker.bean.EventPostBean;
import com.example.auser.zthacker.bean.NormalObjData;
import com.example.auser.zthacker.bean.PublishTextBean;
import com.example.auser.zthacker.bean.UtilBean;
import com.example.auser.zthacker.dialog.LoadingDialog;
import com.example.auser.zthacker.dialog.MyPublishDelDialog;
import com.example.auser.zthacker.http.ApiRequestData;
import com.example.auser.zthacker.utils.BitmapUtils;
import com.example.auser.zthacker.utils.ImagePathToFile;
import com.example.auser.zthacker.utils.SPUtils;
import com.example.auser.zthacker.utils.TextUtil;
import com.example.auser.zthacker.utils.ToastUtil;
import com.example.auser.zthacker.utils.UpLoadQiNiuUtil;
import com.example.auser.zthacker.view.FlowLayout;
import com.example.auser.zthacker.view.FullyGridLayoutManager;
import com.example.auser.zthacker.view.RecycleViewDividerLine;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.permissions.RxPermissions;
import com.luck.picture.lib.tools.PictureFileUtils;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by zhengkq on 2017/8/10.
 */

public class PublishImageActivity extends BaseActivity {
    @BindView(R.id.tv_edit)
    TextView tv_edit;
    @BindView(R.id.et_input_text)
    EditText et_input_text;
    @BindView(R.id.tv_count)
    TextView tv_count;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.tv_fl)
    TextView tv_fl;
    @BindView(R.id.fl)
    FlowLayout fl;
    private long firstClick;
    private GridImageAdapter adapter;
    private List<LocalMedia> selectList = new ArrayList<>();
    private List<String> hotTip;
    private String inputContent;
    private MyPublishDelDialog dialog;
    private String userId;
    private GsonBuilder gsonBuilder;
    private PublishTextBean publishTextBean;
    private String pictureType;
    private List<File> imgs;
    private boolean eqImg;
    private boolean eqVideo;
    private String imgToken;
    private String videoToken;
    private EventPostBean eventPostBean;
    private StringBuffer imgsUrl = new StringBuffer();//图片上传七牛完成后返回的地址拼接成字符串
    private int completeIndex;//图片上传完成的第几张


    public static void startActivity(Context context, ArrayList<LocalMedia> selectList) {
        Intent intent = new Intent(context, PublishImageActivity.class);
        intent.putParcelableArrayListExtra("selectList", selectList);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publish_image);
        ButterKnife.bind(this);
        initView(savedInstanceState);
    }

    private void initView(Bundle bundle) {
        setTop(R.color.black);
        setCentreText("发布");
        tv_edit.setText("发布");

        tv_fl.setVisibility(View.GONE);
        fl.setVisibility(View.GONE);

        userId = SPUtils.getSharedStringData(PublishImageActivity.this, "userId");

        hotTip = new ArrayList<>();
        hotTip.add("航模");
        hotTip.add("车模");
        hotTip.add("海模");
        hotTip.add("建模");
        setFlowlayout();
        selectList = getIntent().getParcelableArrayListExtra("selectList");

        et_input_text.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                inputContent = et_input_text.getText().toString();
                tv_count.setText(inputContent.length() + "");
                tv_count.setTextColor(inputContent.length() > 0 ? getResources().getColor(R.color.delete_bg) : getResources().getColor(R.color.text_c0));
            }
        });

        setAdapter();
    }

    private void setFlowlayout() {
        for (int i = 0; i < hotTip.size(); i++) {
            int left, top, right, bottom;
            left = top = right = bottom = (int) getResources().getDimension(R.dimen.x5);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(left, top, right, bottom);
            final TextView mView = new TextView(this);

            mView.setText(hotTip.get(i));
            mView.setTextSize(14);
            if (i == 0) {
                mView.setTextColor(getResources().getColor(R.color.white));
                mView.setBackgroundDrawable(getResources().getDrawable(R.drawable.publish_text_type_select_bg));
            } else {
                mView.setTextColor(getResources().getColor(R.color.text_c0));
                mView.setBackgroundDrawable(getResources().getDrawable(R.drawable.publish_text_type_unselect_bg));
            }
            mView.setTag(i);

            mView.setPadding(2 * left, 2 * left / 3, 2 * left, 2 * left / 3);
            mView.setLayoutParams(params);

            LinearLayout ll = new LinearLayout(this);
            ll.setOrientation(LinearLayout.VERTICAL);
            ll.addView(mView);
            fl.addView(ll);
            mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int tag = (int) v.getTag();
                    for (int i = 0; i < fl.getChildCount(); i++) {
                        LinearLayout linearLayout = (LinearLayout) fl.getChildAt(i);
                        TextView childText = (TextView) linearLayout.getChildAt(0);
                        if ((int) childText.getTag() == tag) {
                            childText.setTextColor(getResources().getColor(R.color.white));
                            childText.setBackgroundDrawable(getResources().getDrawable(R.drawable.publish_text_type_select_bg));
                        } else {
                            childText.setTextColor(getResources().getColor(R.color.text_c0));
                            childText.setBackgroundDrawable(getResources().getDrawable(R.drawable.publish_text_type_unselect_bg));
                        }
                    }
                }
            });
        }
    }

    public void setAdapter() {
        FullyGridLayoutManager fullyGridLayoutManager = new FullyGridLayoutManager(PublishImageActivity.this, 3) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };
        recyclerView.setLayoutManager(fullyGridLayoutManager);
        recyclerView.addItemDecoration(new RecycleViewDividerLine(this, RecycleViewDividerLine.BOTH_SET,
                16, getResources().getColor(R.color.white)));
        adapter = new GridImageAdapter(PublishImageActivity.this, onAddPicClickListener);
        adapter.setList(selectList);
        adapter.setSelectMax(9);
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(new GridImageAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                if (selectList.size() > 0) {
                    LocalMedia media = selectList.get(position);
                    String pictureType = media.getPictureType();
                    int mediaType = PictureMimeType.pictureToVideo(pictureType);
                    switch (mediaType) {
                        case 1:
                            // 预览图片 可自定长按保存路径
                            //PictureSelector.create(MainActivity.this).externalPicturePreview(position, "/custom_file", selectList);
                            PictureSelector.create(PublishImageActivity.this).externalPicturePreview(position, selectList);
                            break;
                    }
                }
            }
        });

        // 清空图片缓存，包括裁剪、压缩后的图片 注意:必须要在上传完成后调用 必须要获取权限
        RxPermissions permissions = new RxPermissions(this);
        permissions.request(Manifest.permission.WRITE_EXTERNAL_STORAGE).subscribe(new Observer<Boolean>() {
            @Override
            public void onSubscribe(Disposable d) {
            }

            @Override
            public void onNext(Boolean aBoolean) {
                if (aBoolean) {
                    PictureFileUtils.deleteCacheDirFile(PublishImageActivity.this);
                } else {
                    Toast.makeText(PublishImageActivity.this,
                            getString(R.string.picture_jurisdiction), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(Throwable e) {
            }

            @Override
            public void onComplete() {
            }
        });
    }

    private GridImageAdapter.onAddPicClickListener onAddPicClickListener = new GridImageAdapter.onAddPicClickListener() {
        @Override
        public void onAddPicClick() {
            // 进入相册 以下是例子：不需要的api可以不写
            PictureSelector.create(PublishImageActivity.this)
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
    };

    @OnClick({R.id.image_back, R.id.tv_edit})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.image_back:
                dialog = (MyPublishDelDialog) LoadingDialog.showDialogForDelAndReport(this, "放弃发布","");
                dialog.setOnClickChoose(new MyPublishDelDialog.OnClickChoose() {
                    @Override
                    public void onClick(int id) {
                        if (id == 1) {
                            finish();
                        }
                    }
                });
                finish();
                break;
            case R.id.tv_edit:
                long secondClick = System.currentTimeMillis();
                if (secondClick - firstClick < 1000) {
                    firstClick = secondClick;
                    return;
                }
                if (selectList.size() == 0) {
                    ToastUtil.show(PublishImageActivity.this, "请添加图片或视频！");
                    return;
                }
                inputContent = et_input_text.getText().toString();
                pictureType = selectList.size() > 0 ? selectList.get(0).getPictureType() : "";

                ApiRequestData.getInstance(this).ShowDialog("上传中...");
                eqImg = pictureType.startsWith(PictureConfig.IMAGE);
                eqVideo = pictureType.startsWith(PictureConfig.VIDEO);
                imgToken = SPUtils.getSharedStringData(PublishImageActivity.this, "imgToken");
                videoToken = SPUtils.getSharedStringData(PublishImageActivity.this, "videoToken");
                if (eqImg) {
                    getPublishTextRequest("1");
                    if (TextUtil.isNull(imgToken)) {
                        getPublishVideoTokenRequest("img");
                    }
                }
                if (eqVideo) {
                    getPublishTextRequest("2");
                    if (TextUtil.isNull(videoToken)) {
                        getPublishVideoTokenRequest("video");
                    }
                }
                break;
        }
    }


    private void getPublishTextRequest(String type) {
        OkGo.post(ApiRequestData.getInstance(this).PublishText)
                .tag(this)
                .params("sysAppUser", userId)
                .params("title", "")
                .params("content", inputContent)
                .params("type", type)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        if (gsonBuilder == null) {
                            gsonBuilder = new GsonBuilder();
                        }

                        NormalObjData<PublishTextBean> mData = gsonBuilder
                                .setPrettyPrinting()
                                .disableHtmlEscaping()
                                .create().fromJson(s, new TypeToken<NormalObjData<PublishTextBean>>() {
                                }.getType());
                        publishTextBean = mData.getData();
                        String code = mData.getCode();
                        if (!TextUtil.isNull(code) && code.equals("0")) {
                            return;
                        }
                        if (publishTextBean != null) {
                            if (eqImg && !TextUtil.isNull(imgToken)) {
                                getPublishVideoQiniuInputRequest(imgToken, selectList.get(0).getPath(), true);
                            }
                            if (eqVideo && !TextUtil.isNull(videoToken)) {
                                getPublishVideoQiniuInputRequest(videoToken, selectList.get(0).getPath(), false);
                            }
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        ApiRequestData.getInstance(PublishImageActivity.this).getDialogDismiss();
                        ToastUtil.show(PublishImageActivity.this, e.getMessage());
                    }
                });
    }


    private void getPublishVideoTokenRequest(final String tokenType) {
        OkGo.get(ApiRequestData.getInstance(this).PublishVideoToken)
                .tag(this)
                .params("tokenType", tokenType)
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
                            ToastUtil.show(PublishImageActivity.this, mData.getMsg());
                            return;
                        }
                        String upToken = mData.getData().getUpToken();
                        Log.e("token", upToken);
                        if (tokenType.equals("img")) {
                            imgToken = upToken;
                            SPUtils.setSharedStringData(PublishImageActivity.this, "imgToken", upToken);
                            if (publishTextBean != null && !TextUtil.isNull(publishTextBean.getArticleId())) {
                                getPublishVideoQiniuInputRequest(imgToken, selectList.get(0).getPath(), true);
                            }
                        } else {
                            videoToken = upToken;
                            SPUtils.setSharedStringData(PublishImageActivity.this, "videoToken", upToken);
                            if (publishTextBean != null && !TextUtil.isNull(publishTextBean.getArticleId())) {
                                getPublishVideoQiniuInputRequest(videoToken, selectList.get(0).getPath(), false);
                            }
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        ToastUtil.show(PublishImageActivity.this, e.getMessage());
                    }
                });
    }


    private void getPublishVideoQiniuInputRequest(String token, String path, boolean isImg) {
        String key = null;
        if (isImg) {
            key = ApiRequestData.getInstance(this).QiNiuImgKey;
            UpLoadQiNiuUtil.getUpimg(this, path, token, key,new handler());
        } else {
            File f = new File(path);
            key = ApiRequestData.getInstance(this).QiNiuVideoKey;
            UpLoadQiNiuUtil.uploadFile(this, f, token, key, isImg, new handler());
        }
    }

    private class handler extends UpLoadQiNiuUtil.ProgressAndisCompleteHandler {

        @Override
        public void progress(int progress) {
            //progressBar.setProgress(progress);
            Log.e("progress", progress + "");
        }

        @Override
        public void isComplete(String videoPath, int type) {
            Log.e("path", videoPath+"/"+type);
            ToastUtil.show(PublishImageActivity.this,videoPath+"/"+type);
            if (type == 2) {
                getPublishVideoInputRequest("", videoPath);
            } else {
                completeIndex++;
                imgsUrl.append(videoPath+",");
                if (completeIndex > selectList.size()-1) {
                    imgsUrl.delete(imgsUrl.length()-1,imgsUrl.length());
                    Log.e("imgsUrl",imgsUrl.toString());
                    ToastUtil.show(PublishImageActivity.this,imgsUrl.toString());
                    getPublishVideoInputRequest(imgsUrl.toString(), "");
                }else {
                    getPublishVideoQiniuInputRequest(imgToken, selectList.get(completeIndex).getPath(), true);
                }
            }
        }
    }

    private void getPublishVideoInputRequest(String imgsPath, String videoPath) {
        OkGo.post(ApiRequestData.getInstance(this).PublishVideo)
                .tag(this)
                .params("articleId", publishTextBean.getArticleId())
                .params("imgsUrl", imgsPath)
                .params("videoUrl", videoPath)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        NormalObjData mData = gsonBuilder
                                .setPrettyPrinting()
                                .disableHtmlEscaping()
                                .create().fromJson(s, new TypeToken<NormalObjData>() {
                                }.getType());
                        ApiRequestData.getInstance(PublishImageActivity.this).getDialogDismiss();
                        String code = mData.getCode();
                        ToastUtil.show(PublishImageActivity.this, mData.getMsg());
                        if (!TextUtil.isNull(code) && code.equals("0")) {
                            ToastUtil.show(PublishImageActivity.this, mData.getMsg());
                            return;
                        }
                        eventPostBean = new EventPostBean();
                        eventPostBean.setType(Config.PUBLISH_IMAGE);
                        EventBus.getDefault().post(eventPostBean);
                        finish();
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        ToastUtil.show(PublishImageActivity.this, e.getMessage());
                    }
                });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case PictureConfig.CHOOSE_REQUEST:
                    // 图片选择结果回调
                    selectList = PictureSelector.obtainMultipleResult(data);
                    // 例如 LocalMedia 里面返回三种path
                    // 1.media.getPath(); 为原图path
                    // 2.media.getCutPath();为裁剪后path，需判断media.isCut();是否为true
                    // 3.media.getCompressPath();为压缩后path，需判断media.isCompressed();是否为true
                    // 如果裁剪并压缩了，已取压缩路径为准，因为是先裁剪后压缩的
                    adapter.setList(selectList);
                    adapter.notifyDataSetChanged();
                    break;
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //根据 Tag 取消请求
        OkGo.getInstance().cancelTag(this);
    }
}
