package com.example.auser.zthacker.http;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.KeyEvent;

import com.example.auser.zthacker.utils.TextUtil;
import com.google.gson.GsonBuilder;
import com.loopj.android.http.RequestParams;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by zhengkq on 2016/12/23.
 */
public class ApiRequestData {
    public Context context;
    private boolean isDdismiss = true;
    private static ApiRequestData ard;
    //public String ServiceUrl = "http://47.96.31.18";
    //public  String ServiceUrl = "http://192.168.3.110:8080";
    public  String ServiceUrl = "http://www.playsteam.cn";
    //public String ServiceUrl = "http://47.96.31.18";//中天阿里云服务器地址
    public  String Base_Url = ServiceUrl+"/ckkj/m/app/";//中天阿里云服务器地址
    //public  String Base_Url = " http://192.168.3.246:9999/m/app/";//web端测试服务器地址
    //登录注册
    public String Register = Base_Url+"mobileLogin/register";//注册
    public String Login = Base_Url+"mobileLogin/login";//登录
    public String SendCaptcha = Base_Url + "mobileLogin/messageCheck";//发送验证码(参数(mobile)手机号,(type:R:注册使用；U:修改密码使用))
    public String CheckCaptcha = Base_Url + "mobileLogin/forgotPwd";//验证验证码是否正确
    public String ResetPass = Base_Url + "mobileLogin/resetPwd";//修改密码
    //首页
    public String HomeData = Base_Url+"appHomePage/homePageInfo";//首页
    public String MainCourseData = Base_Url+"course/getCourseList";//课程
    public String CourseToolRecommend = Base_Url + "course/getCourseRealiaInfo";//教具推荐
    //public String CourseBook =  ServiceUrl+"/ckkj/appH5/page/fly/courseBook.html";//教材H5
    //public String CourseTool =  ServiceUrl+"/ckkj/appH5/page/fly/courseTool.html";//工具H5
    public String MainFoundData = Base_Url+"publishInfo/publishInfoList";//发现
    //搜索
    public String SearchList = Base_Url + "/articles/searchKey";//获取搜索列表
    public String HotSearchList = Base_Url + "/articles/hotSearch";//热搜列表
    //社区
    public String PublishText = Base_Url + "articles/add";//社区发布文章
    public String PublishVideo = Base_Url + "articles/upload";//社区发布视频
    public String PublishList = Base_Url + "articles/getArticleList";//获取社区文章列表
    public String PublishVideoToken = Base_Url + "articles/getToken";//获取社区视频上传的token
    public String PublishTextReport = Base_Url + "report/complaint";//举报
    public String PublishTextLike = Base_Url + "objectLike/save";//点赞
    public String PublishTextCollection = Base_Url + "communityCollect/save";//收藏
    public String PublishTextAttention = Base_Url + "communityFollow/save";//关注
    public String PublishTextUserFollowCount = Base_Url + "articles/getFollowCount";//关注数量
    public String PublishCommentList = Base_Url + "communityCommet/list";//评论列表
    public String PublishSecondCommentList = Base_Url + "communityCommet/list2";//二级评论
    public String PublishTextDel = Base_Url + "articles/delete";//删除文章
    public String PublishCommentDel = Base_Url + "communityCommet/delete";//删除一级评论
    public String PublishReplyComment = Base_Url + "communityCommet/save";//回复评论
    public String PublishUserHomePage = Base_Url + "articles/userHomePage";//用户主页
    //个人中心
    public String MineInfo = Base_Url+"sysAppUser/getAppUserInfo";//个人资料
    public String MineInfoEdit = Base_Url+"sysAppUser/edit";//个人资料编辑
    public String MineUploadPhoto = Base_Url+"sysAppUser/uploadImg";//上传图像
    public String MineConsultSuggestion = Base_Url+"advisory/add";//课程咨询
    public String MineSuggestion = Base_Url+"suggestion/add";//意见反馈
    public String MineChangePass = Base_Url + "mobileLogin/updatePW";//修改密码
    public String MineScanCode = Base_Url+"video/scan";//二维码扫描
    public String MineMyClass = Base_Url+"video/getCourse";//我购买的课程
    public String MineMyClassVideoList = Base_Url+"video/getVideos";//购买的课程视频
    public String MineCollection = Base_Url + "articles/getMCAList";//我的收藏
    public String MinePublish = Base_Url + "articles/getArticleListByUser";//我的发布
    public String MineFollows = Base_Url + "communityFollow/list";//我的关注
    public String MineFans = Base_Url + "communityFollow/list2";//我的粉丝
    //消息
    public String MsgCount = Base_Url + "articles/getMessage";//未读消息个数
    public String MsgMyPublishZan = Base_Url + "articles/getLikeByUser";//给我点赞消息
    public String MsgMyPublishComment = Base_Url + "articles/getCommentBySub";//给我评论消息
    //七牛上传地址
    //public String QiNiuImgKey = "p09f4cre6.bkt.clouddn.com";
    //public String QiNiuVideoKey = "p09f8b5mi.bkt.clouddn.com";
    public String QiNiuImgKey = "http://zt-appimg.playsteam.cn";
    public String QiNiuVideoKey = "http://zt-appvideo.playsteam.cn";

    private ProgressDialog pDialog;

    private ApiRequestData() {
    }

    public static ApiRequestData getInstance(Context context) {
        if (ard == null) {
            synchronized (ApiRequestData.class) {
                ApiRequestData apiRequestData = ard;
                if (apiRequestData == null) {
                    apiRequestData = new ApiRequestData();
                    ard = apiRequestData;
                }
            }
        }
        ard.context = context;
        return ard;
    }

    /**
     * 启动数据加载效果
     * @param message
     * @return
     */
    public Dialog ShowDialog(String message){
        if (((Activity) context).isFinishing()) {
            return null;
        }
        if (message == null) {
            message = "数据加载中...";
        }

        getDialogDismiss();
        pDialog = new ProgressDialog(context);
        pDialog.setCanceledOnTouchOutside(false);
        pDialog.setMessage(message);
        pDialog.show();
        pDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialogInterface, int keyCode, KeyEvent keyEvent) {
                if (keyCode == KeyEvent.KEYCODE_BACK
                        && keyEvent.getRepeatCount() == 0) {
                    pDialog = null;
                }
                return false;
            }
        });
        isDdismiss = true;
        return pDialog;
    }
    /**
     * 关闭数据加载效果
     * @return
     */
    public Dialog getDialogDismiss(){
        if (pDialog != null && pDialog.isShowing() && isDdismiss) {
            pDialog.dismiss();
            //cancel();
            pDialog = null;
        }
        return pDialog;
    }

    //文章列表
    public void getPublishList(String sysAppUser,int indexfrom, StringCallback stringCallback){
        if (TextUtil.isNull(sysAppUser)){
            OkGo.get(PublishList)
                    .tag(this)
                    .params("dataIndex",indexfrom)
                    .execute(stringCallback);
        }else {
            OkGo.get(PublishList)
                    .tag(this)
                    .params("sysAppUser", sysAppUser)
                    .params("dataIndex",indexfrom)
                    .execute(stringCallback);
        }
    }

    //个人发布文章列表
    public void getUserPublishList(String sysAppUser, StringCallback stringCallback){
        OkGo.post(MinePublish)
                .tag(this)
                .params("sysAppUser", sysAppUser)
                .execute(stringCallback);
    }
    //个人消息-未读个数
    public void getMsgCount(String sysAppUser,int zanMsgIndex,int commentMsgIndex, StringCallback stringCallback){
        OkGo.post(MsgCount)
                .tag(this)
                .params("sysAppUser", sysAppUser)
                .params("dataIndex",zanMsgIndex)//点赞
                .params("dataIndex2",commentMsgIndex)//评论
                .execute(stringCallback);
    }
    //个人消息-点赞列表
    public void getMsgMyPublishZanList(String sysAppUser, StringCallback stringCallback){
        OkGo.post(MsgMyPublishZan)
                .tag(this)
                .params("sysAppUser", sysAppUser)
                .execute(stringCallback);
    }
    //个人消息-评论列表
    public void getMsgPublishCommentList(String sysAppUser, StringCallback stringCallback){
        OkGo.post(MsgMyPublishComment)
                .tag(this)
                .params("sysAppUser", sysAppUser)
                .execute(stringCallback);
    }
    //教具推荐列表
    public void getCourseToolRecommendList(String id, StringCallback stringCallback){
        OkGo.post(CourseToolRecommend)
                .tag(this)
                .params("id", id)
                .execute(stringCallback);
    }
    //搜索列表
    public void getSearchList(String key, StringCallback stringCallback){
        OkGo.post(SearchList)
                .tag(this)
                .params("key", key)
                .execute(stringCallback);
    }
    //热搜
    public void getHotSearchList(StringCallback stringCallback){
        OkGo.post(HotSearchList)
                .tag(this)
                .execute(stringCallback);
    }
}
