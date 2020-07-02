package com.example.auser.zthacker.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.auser.zthacker.R;
import com.example.auser.zthacker.app.AppApplication;
import com.example.auser.zthacker.base.Config;
import com.example.auser.zthacker.bean.CommunityCommentBean;
import com.example.auser.zthacker.bean.EventPostBean;
import com.example.auser.zthacker.bean.NormalObjData;
import com.example.auser.zthacker.dialog.LoadingDialog;
import com.example.auser.zthacker.dialog.MyPublishDelDialog;
import com.example.auser.zthacker.http.ApiRequestData;
import com.example.auser.zthacker.ui.activity.found.CommunityCommentInfoActivity;
import com.example.auser.zthacker.ui.activity.found.CommunityPicInfoActivity;
import com.example.auser.zthacker.ui.activity.found.CommunityVideoInfoActivity;
import com.example.auser.zthacker.ui.activity.found.NewsInfoActivity;
import com.example.auser.zthacker.ui.activity.found.ReportActivity;
import com.example.auser.zthacker.ui.activity.mine.UserHomePageActivity;
import com.example.auser.zthacker.utils.DisplayUtil;
import com.example.auser.zthacker.utils.SPUtils;
import com.example.auser.zthacker.utils.TextUtil;
import com.example.auser.zthacker.utils.TimeUtil;
import com.example.auser.zthacker.utils.ToastUtil;
import com.example.auser.zthacker.view.GlideCircleTransform;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import java.util.Iterator;
import java.util.List;

import de.greenrobot.event.EventBus;
import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by zhengkq on 2017/10/31.
 */

public class FirstClassCommentAdapter extends RecyclerView.Adapter<FirstClassCommentAdapter.ViewHolder>{
    LayoutInflater inflater;
    private Context context;
    private MyPublishDelDialog dialog;
    private OnRecyclerViewClickListener onRecyclerViewClickListener = null;
    private String userId;
    private GsonBuilder gsonBuilder;
    private List<CommunityCommentBean> list;
    private int type;//0-图片社区详情 1-视频社区详情 2-评论详情 3-消息评论 4-资讯详情

    public FirstClassCommentAdapter(Context context,List<CommunityCommentBean> list,int type) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.list = list;
        this.type = type;

    }
    @Override
    public FirstClassCommentAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_community_first_comment,parent, false);
        FirstClassCommentAdapter.ViewHolder viewHolder = new FirstClassCommentAdapter.ViewHolder(view);
        viewHolder.simpleDraweeView_head_icon = (ImageView) view.findViewById(R.id.simpleDraweeView_head_icon);
        viewHolder.tv_name = (TextView) view.findViewById(R.id.tv_name);
        viewHolder.tv_comment = (TextView) view.findViewById(R.id.tv_comment);
        viewHolder.tv_time = (TextView) view.findViewById(R.id.tv_time);
        viewHolder.tv_reply = (TextView) view.findViewById(R.id.tv_reply);
        viewHolder.tv_del_and_report = (TextView) view.findViewById(R.id.tv_del_and_report);
        viewHolder.tv_comment_more = (TextView) view.findViewById(R.id.tv_comment_more);
        viewHolder.ll_user = (LinearLayout) view.findViewById(R.id.ll_user);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final FirstClassCommentAdapter.ViewHolder holder, final int position) {
        final CommunityCommentBean communityCommentBean = list.get(position);
        holder.tv_comment.setText(communityCommentBean.getContent());
        userId = SPUtils.getSharedStringData(context, "userId");
        //holder.tv_comment_more.setText("查看全部"+communityCommentBean.getCommunityComment().size()+"条评论");
        Glide.with(context)
                .load(communityCommentBean.getSysAppUerImg())
                .transform(new GlideCircleTransform(context))
                .placeholder(R.mipmap.icon_userphote_hl)
                .diskCacheStrategy(DiskCacheStrategy.RESULT)
                .skipMemoryCache(false)
                .override(54,54)
                .thumbnail(0.2f)
                .into(holder.simpleDraweeView_head_icon);
        if (type==2){
            if (!TextUtil.isNull(communityCommentBean.getRelevantId())){
                String ss = communityCommentBean.getUserName() + " 回复 "+communityCommentBean.getUserName2();
                SpannableString spannableString = new SpannableString(ss);
                spannableString.setSpan(new ForegroundColorSpan(context.getResources().getColor(R.color.text_64))
                        ,communityCommentBean.getUserName().length()+1
                        ,communityCommentBean.getUserName().length()+3
                        , Spanned.SPAN_INCLUSIVE_INCLUSIVE);
                holder.tv_name.setText(spannableString);
            }else {
                holder.tv_name.setText(communityCommentBean.getUserName());
            }
        }else {
            holder.tv_name.setText(communityCommentBean.getUserName());
        }
        if (type==3){
            holder.tv_time.setVisibility(View.GONE);
        }else {
            holder.tv_time.setVisibility(View.VISIBLE);
            holder.tv_time.setText(TimeUtil.formatDateStr2Desc(communityCommentBean.getCreateDate(), "yyyy-MM-dd HH:mm:ss"));
        }
        if (communityCommentBean.getSysAppUser()!=null&&
                communityCommentBean.getSysAppUser().equals(userId)){
            holder.tv_del_and_report.setText("删除");
        }else {
            holder.tv_del_and_report.setText("举报");
        }
        holder.tv_del_and_report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (communityCommentBean.getSysAppUser()!=null&&
                        communityCommentBean.getSysAppUser().equals(userId)) {
                    removeDate(communityCommentBean, "删除","");
                } else {
                    removeDate(communityCommentBean, "举报","");
                }
            }
        });
        if (TextUtil.isNull(communityCommentBean.getCommetsum())||communityCommentBean.getCommetsum().equals("0")){
            holder.tv_comment_more.setVisibility(View.GONE);
        }else {
            holder.tv_comment_more.setVisibility(View.VISIBLE);
            holder.tv_comment_more.setText("查看全部"+communityCommentBean.getCommetsum()+"条回复>>");
            holder.tv_comment_more.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (type==4){
                        CommunityCommentInfoActivity.startActivity(context,communityCommentBean);
                    }else {
                        CommunityCommentInfoActivity.startActivity(context,communityCommentBean);
                    }
                }
            });
        }
        holder.ll_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserHomePageActivity.startActivity(context, communityCommentBean.getSysAppUser());
            }
        });
        //判断是否设置了监听器
        if (onRecyclerViewClickListener != null) {
            //为ItemView设置监听器
            holder.tv_reply.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onRecyclerViewClickListener.onClick(holder.tv_reply, position); // 2
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    private void removeDate(final CommunityCommentBean communityCommentBean, final String del,final String report) {
        if (TextUtil.isNull(userId)) {
            LoadingDialog.showDialogForLogin(context);
            return;
        }
        dialog = (MyPublishDelDialog) LoadingDialog.showDialogForDelAndReport(context,del,report);
        this.dialog.setOnClickChoose(new MyPublishDelDialog.OnClickChoose() {
            @Override
            public void onClick(int id) {
                if (id == 1) {
                    if (del.equals("删除")) {
                        ApiRequestData.getInstance(context).ShowDialog(null);
                        OkGo.get(ApiRequestData.getInstance(context).PublishCommentDel)//删除一级评论
                                .tag(context)
                                .params("id", communityCommentBean.getId())
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
                                        Iterator<CommunityCommentBean> iterator = list.iterator();
                                        while (iterator.hasNext()) {
                                            CommunityCommentBean next = iterator.next();
                                            if (next.equals(communityCommentBean)) {
                                                iterator.remove();
                                            }
                                        }
                                        notifyDataSetChanged();
                                        if (type==0){
                                            ((CommunityPicInfoActivity)context).setEmptyVisible(list.size());
                                        }else if (type==1){
                                            ((CommunityVideoInfoActivity)context).setEmptyVisible(list.size());
                                        }else if (type==2){
                                            ((CommunityCommentInfoActivity)context).setEmptyVisible(list.size());
                                        }else if (type==4){
                                            ((NewsInfoActivity)context).setEmptyVisible(list.size());
                                        }
                                        EventPostBean eventPostBean = new EventPostBean();
                                        eventPostBean.setType(Config.PUBLISH_COMMENT_DEL);
                                        eventPostBean.setId(AppApplication.getInstance().getCurrentArticleId());
                                        EventBus.getDefault().post(eventPostBean);
                                    }

                                    @Override
                                    public void onError(Call call, Response response, Exception e) {
                                        super.onError(call, response, e);
                                        ToastUtil.show(context, e.getMessage());
                                        ApiRequestData.getInstance(context).getDialogDismiss();
                                    }
                                });


                    } else if (del.equals("举报")) {
                        ReportActivity.startActivity(context, communityCommentBean.getSysAppUser(),communityCommentBean.getId(), 0);
                    }
                }
            }
        });
    }

   /* public ApiRequestData ApiRequestData.getInstance(this) {
        if (apiRequestData == null && ApiRequestData.getInstance(context) != this.apiRequestData) {
            apiRequestData = ApiRequestData.getInstance(context);
        }
        return apiRequestData;
    }
*/
    public  interface OnRecyclerViewClickListener {
        void onClick(View view , int position);
    }
    public void setOnItemClickListener(FirstClassCommentAdapter.OnRecyclerViewClickListener listener) {
        this.onRecyclerViewClickListener = listener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        public ViewHolder(View view)
        {
            super(view);
        }
        ImageView simpleDraweeView_head_icon;
        TextView tv_name,tv_comment,tv_time,tv_reply,tv_del_and_report,tv_comment_more;
        LinearLayout ll_user;
    }
}
