package com.example.auser.zthacker.base;

import java.net.PortUnreachableException;

/**
 * Created by yiwang on 2016/11/2.
 */

public class Config {
    public static String SharedPreferencesData = "Data";
    public static String SharedPreferencesSetting = "Setting";
    public static String LoginOut = "LoginOut";

    public static int MY_PERMISSIONS_REQUEST_CALL_PHONE = 6;
    public static int MY_PERMISSIONS_REQUEST_CALL_PHONE2 = 7;

    public static String MAIN_COURSE = "main_course";
    public static String MAIN_FOUND_COMMUNITY = "main_found_community";
    public static String MAIN_FOUND_NEWS = "main_found_news";

    public static String CLASS_CONSULT = "class_consult";
    public static String COMPLAINT_FEEDBACK = "complaint_feedback";
    public static String SCAN_SUCCESS = "scan_success";

    public static String EDIT_USERNAME = "edit_username";
    public static String EDIT_USERSEX = "edit_usersex";
    public static String EDIT_USERIDENTITY = "edit_useridentity";
    public static String EDIT_USERPHOTO = "edit_userphoto";

    public static String PUBLISH_TEXT_DEL = "publish_text_del";
    public static String PUBLISH_COMMENT_DEL = "publish_comment_del";
    public static String PUBLISH_SECOND_COMMENT_EMPTY = "publish_second_commend_empty";//二级评论删除通知一级评论刷新界面
    public static String PUBLISH_SECOND_COMMENT_SUCCESS = "publish_second_commend_success";//二级评论发布成功通知一级评论刷新界面
    public static String PUBLISH_ZAN = "publish_zan";//点赞刷新社区列表
    public static String NEWS_ZAN = "news_zan";//点赞刷新资讯列表
    public static String PUBLISH_COLLECTION = "publish_collection";//收藏成功刷新社区列表
    public static String PERSONAL_ATTENTION = "personal_attention";//关注刷新列表
    public static String PUBLISH_COMMENT_SUCCESS = "publish_comment_success";//发现中社区评论成功刷新列表
    public static String NEWS_COMMENT_SUCCESS = "news_comment_success";//发现中资讯评论成功刷新列表


    public static String PUBLISH_IMAGE = "publish_image";
    public static String VIDEO_ABOUT_TEACHING_RESOURSE = "video_about_teaching_resourse";//课件
    public static String VIDEO = "video";//视频

    public static String MSG_NOTICE = "msg_notice";
}
