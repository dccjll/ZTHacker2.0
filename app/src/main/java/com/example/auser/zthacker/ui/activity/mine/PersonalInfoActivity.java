package com.example.auser.zthacker.ui.activity.mine;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.example.auser.zthacker.R;
import com.example.auser.zthacker.base.BaseActivity;
import com.example.auser.zthacker.base.Config;
import com.example.auser.zthacker.bean.AppPersonalInfoBean;
import com.example.auser.zthacker.bean.EventPostBean;
import com.example.auser.zthacker.bean.NormalObjData;
import com.example.auser.zthacker.bean.UtilBean;
import com.example.auser.zthacker.dialog.HeadImageSettingDialog;
import com.example.auser.zthacker.http.ApiRequestData;
import com.example.auser.zthacker.http.FactoryTools;
import com.example.auser.zthacker.utils.DisplayUtil;
import com.example.auser.zthacker.utils.ImagePathToFile;
import com.example.auser.zthacker.utils.SPUtils;
import com.example.auser.zthacker.utils.TextUtil;
import com.example.auser.zthacker.utils.ToastUtil;
import com.example.auser.zthacker.utils.UpLoadQiNiuUtil;
import com.example.auser.zthacker.utils.localImages.GetRoundBitmap;
import com.example.auser.zthacker.view.GlideCircleTransform;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import java.io.Serializable;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.finalteam.galleryfinal.GalleryFinal;
import cn.finalteam.galleryfinal.model.PhotoInfo;
import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;
import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by zhengkq on 2017/8/4.
 */

public class PersonalInfoActivity extends BaseActivity {
    @BindView(R.id.simpleDraweeView_head_icon)
    ImageView simpleDraweeView_head_icon;
    @BindView(R.id.tv_username)
    TextView tv_username;
    @BindView(R.id.tv_phone)
    TextView tv_phone;
    @BindView(R.id.tv_sex)
    TextView tv_sex;
    @BindView(R.id.tv_type)
    TextView tv_type;

    private static final int MY_PERMISSIONS_REQUEST_CALL_PHONE = 6;
    private static final int MY_PERMISSIONS_REQUEST_CALL_PHONE2 = 7;
    private String realFilePath;
    private AppPersonalInfoBean personalInfo;
    private GsonBuilder gsonBuilder;
    private String imgToken;
    private Bitmap croppedRoundBitmap;

    public static void startActivity(Context context, AppPersonalInfoBean personalInfo) {
        Intent intent = new Intent(context, PersonalInfoActivity.class);
        intent.putExtra("personalInfo", (Serializable) personalInfo);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_info);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        setTop(R.color.black);
        setCentreText("个人资料");
        initView();
    }

    private void initView() {
        Intent intent = getIntent();
        personalInfo = (AppPersonalInfoBean) intent.getSerializableExtra("personalInfo");
        if (!TextUtil.isNull(personalInfo.getPhoto())) {
            Glide.with(this)
                    .load(personalInfo.getPhoto())
                    .transform(new GlideCircleTransform(this))
                    .placeholder(R.mipmap.icon_userphote_hl)
                    .override(40,40)
                    .thumbnail(0.2f)
                    .into(simpleDraweeView_head_icon);
        }
        tv_username.setText(personalInfo.getUserName());
        tv_phone.setText(personalInfo.getPhoneNumber());
        tv_sex.setText(personalInfo.getSex());
        tv_type.setText(personalInfo.getIdentity());
    }

    @OnClick({R.id.image_back, R.id.rl_photo, R.id.rl_user_name, R.id.rl_sex, R.id.rl_type})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.image_back:
                finish();
                break;
            case R.id.rl_photo:
                final HeadImageSettingDialog dialog = new HeadImageSettingDialog(PersonalInfoActivity.this);
                WindowManager.LayoutParams attributes = dialog.getWindow().getAttributes();
                attributes.height = WindowManager.LayoutParams.WRAP_CONTENT;
                attributes.width = WindowManager.LayoutParams.MATCH_PARENT;
                attributes.gravity = Gravity.BOTTOM;
                dialog.setCanceledOnTouchOutside(true);
                dialog.setOnClickChoose(new HeadImageSettingDialog.OnClickChoose() {
                    @Override
                    public void onClick(int id) {
                        if (id == 2) {
                            if (ContextCompat.checkSelfPermission(PersonalInfoActivity.this,
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                    != PackageManager.PERMISSION_GRANTED) {
                                ActivityCompat.requestPermissions(PersonalInfoActivity.this,
                                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                        Config.MY_PERMISSIONS_REQUEST_CALL_PHONE);
                            } else {
                                initChooseImage();
                            }
                        } else if (id == 1) {
                            if (ContextCompat.checkSelfPermission(PersonalInfoActivity.this,
                                    Manifest.permission.CAMERA)
                                    != PackageManager.PERMISSION_GRANTED) {
                                ActivityCompat.requestPermissions(PersonalInfoActivity.this,
                                        new String[]{Manifest.permission.CAMERA},
                                        Config.MY_PERMISSIONS_REQUEST_CALL_PHONE2);
                            } else {
                                initTakePhoto();
                            }
                        }
                    }
                });
                dialog.show();
                break;
            case R.id.rl_user_name:
                ResetUserNameActivity.startActivity(this, personalInfo.getUserName());
                break;
            case R.id.rl_sex:
                ResetSexActivity.startActivity(this, personalInfo.getSex());
                break;
            case R.id.rl_type:
                ResetIDActivity.startActivity(this, personalInfo.getIdentity());
                break;
        }
    }

    private void initTakePhoto() {
        if (FactoryTools.isSDCard(PersonalInfoActivity.this)) {//拍照
            GalleryFinal.openCamera(3, new GalleryFinal.OnHanlderResultCallback() {
                @Override
                public void onHanlderSuccess(int reqeustCode, List<PhotoInfo> resultList) {
                    realFilePath = resultList.get(0).getPhotoPath();
                    //uri = ImageAbsolutePath.getImageContentUri(RegisterSettingNameActivity.this, realFilePath);
                    if (realFilePath != null && realFilePath.length() != 0) {
                        initImage(realFilePath);
                    }
                }

                @Override
                public void onHanlderFailure(int requestCode, String errorMsg) {
                    FactoryTools.setToastShow(getApplicationContext(), errorMsg);
                }
            });
        }
    }

    private void initChooseImage() {
        if (FactoryTools.isSDCard(PersonalInfoActivity.this)) {
            GalleryFinal.openGallerySingle(3, new GalleryFinal.OnHanlderResultCallback() {
                @Override
                public void onHanlderSuccess(int reqeustCode, List<PhotoInfo> resultList) {
                    realFilePath = resultList.get(0).getPhotoPath();
                    if (realFilePath != null && realFilePath.length() != 0) {
                        initImage(realFilePath);
                    }
                }

                @Override
                public void onHanlderFailure(int requestCode, String errorMsg) {
                    FactoryTools.setToastShow(getApplicationContext(), errorMsg);
                }
            });
        }
    }

    private void initImage(final String realFilePath) {
        Bitmap bitmap = ImagePathToFile.ImagePathToByte(realFilePath);
        croppedRoundBitmap = GetRoundBitmap.getCroppedRoundBitmap(bitmap, simpleDraweeView_head_icon.getWidth() / 2);
        simpleDraweeView_head_icon.setImageBitmap(croppedRoundBitmap);
        //byte[] bytes = BitmapUtils.bitmapToBase64(bitmap);
        //File file = ImagePathToFile.operaFileData(realFilePath, bytes);
        ApiRequestData.getInstance(this).ShowDialog(null);
        imgToken = SPUtils.getSharedStringData(PersonalInfoActivity.this, "imgToken");
        if (TextUtil.isNull(imgToken)) {
            getTokenRequest();
        }else {
            getPublishVideoQiniuInputRequest(imgToken, realFilePath, true);
        }
    }

    private void getTokenRequest() {
        OkGo.post(ApiRequestData.getInstance(this).PublishVideoToken)
                .tag(this)
                .params("tokenType", "img")
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        if (gsonBuilder == null) {
                            gsonBuilder = new GsonBuilder();
                        }
                        NormalObjData<UtilBean> mData = gsonBuilder
                                .setPrettyPrinting()
                                .disableHtmlEscaping()
                                .create().fromJson(s, new TypeToken<NormalObjData<UtilBean>>(){
                                }.getType());
                        String code = mData.getCode();
                        ToastUtil.show(PersonalInfoActivity.this, mData.getMsg());
                        if (!TextUtil.isNull(code) && code.equals("0")) {
                            return;
                        }
                        String upToken = mData.getData().getUpToken();
                        Log.e("token", upToken);
                        imgToken = upToken;
                        SPUtils.setSharedStringData(PersonalInfoActivity.this, "imgToken", upToken);
                        getPublishVideoQiniuInputRequest(imgToken, realFilePath, true);
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        ToastUtil.show(PersonalInfoActivity.this, e.getMessage());
                    }
                });
    }

    private void getPublishVideoQiniuInputRequest(String imgToken, String realFilePath, boolean b) {
        String key = ApiRequestData.getInstance(this).QiNiuImgKey;
        UpLoadQiNiuUtil.getUpimg(this, realFilePath, imgToken, key,new handler());
    }

    private class handler extends UpLoadQiNiuUtil.ProgressAndisCompleteHandler {

        @Override
        public void progress(int progress) {
            //progressBar.setProgress(progress);
        }

        @Override
        public void isComplete(String path, int type) {
            getImageInputRequest(path);
        }
    }

    private void getImageInputRequest(String path){
        OkGo.post(ApiRequestData.getInstance(this).MineUploadPhoto)// 请求方式和请求url
                .tag(this)
                .params("id", SPUtils.getSharedStringData(this, "userId"))
                .params("imgUrl", path)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        if (gsonBuilder == null) {
                            gsonBuilder = new GsonBuilder();
                        }
                        ApiRequestData.getInstance(PersonalInfoActivity.this).getDialogDismiss();
                        NormalObjData mData = gsonBuilder
                                .setPrettyPrinting()
                                .disableHtmlEscaping()
                                .create().fromJson(s, new TypeToken<NormalObjData>() {
                                }.getType());
                        if (mData.isResult()) {
                            ToastUtil.show(PersonalInfoActivity.this, "修改成功！");
                            EventPostBean eventPostBean = new EventPostBean();
                            eventPostBean.setType(Config.EDIT_USERPHOTO);
                            eventPostBean.setBitmap(croppedRoundBitmap);
                            EventBus.getDefault().post(eventPostBean);
                        } else {
                            Log.e("false", mData.getMsg());
                            ToastUtil.show(PersonalInfoActivity.this, mData.getMsg());
                        }
                    }

                    @Override
                    public void upProgress(long currentSize, long totalSize, float progress, long networkSpeed) {
                        //这里回调上传进度(该回调在主线程,可以直接更新ui)
                        if (currentSize == totalSize) {
                            ApiRequestData.getInstance(PersonalInfoActivity.this).getDialogDismiss();
                        }
                        //mProgressBar.setProgress((int) (100 * progress));
                        //mTextView2.setText("已上传" + currentSize/1024/1024 + "MB, 共" + totalSize/1024/1024 + "MB;");
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        Log.e("error", e.getMessage());
                        ApiRequestData.getInstance(PersonalInfoActivity.this).getDialogDismiss();
                        ToastUtil.show(PersonalInfoActivity.this, e.getMessage());
                    }
                });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == Config.MY_PERMISSIONS_REQUEST_CALL_PHONE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                initChooseImage();
            } else {
                // Permission Denied
                Toast.makeText(PersonalInfoActivity.this, "没有使用SD卡的权限，请在权限管理中开启", Toast.LENGTH_SHORT).show();
            }
        }

        if (requestCode == Config.MY_PERMISSIONS_REQUEST_CALL_PHONE2) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                initTakePhoto();
            } else {
                // Permission Denied
                Toast.makeText(PersonalInfoActivity.this, "没有使用相机的权限，请在权限管理中开启", Toast.LENGTH_SHORT).show();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Subscribe
    public void onEventMainThread(EventPostBean eventPostBean) {
        if (eventPostBean.getType().equals(Config.EDIT_USERNAME)) {
            //setLoginDialog();
            personalInfo.setUserName(eventPostBean.getMessage());
            tv_username.setText(eventPostBean.getMessage());
        } else if (eventPostBean.getType().equals(Config.EDIT_USERSEX)) {
            personalInfo.setSex(eventPostBean.getMessage());
            tv_sex.setText(eventPostBean.getMessage());
        } else if (eventPostBean.getType().equals(Config.EDIT_USERIDENTITY)) {
            personalInfo.setIdentity(eventPostBean.getMessage());
            tv_type.setText(eventPostBean.getMessage());
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        //根据 Tag 取消请求
        OkGo.getInstance().cancelTag(this);
    }
}
