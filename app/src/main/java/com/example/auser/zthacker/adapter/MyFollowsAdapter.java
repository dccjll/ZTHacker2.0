package com.example.auser.zthacker.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.auser.zthacker.R;
import com.example.auser.zthacker.base.Config;
import com.example.auser.zthacker.bean.AppPersonalInfoBean;
import com.example.auser.zthacker.bean.EventPostBean;
import com.example.auser.zthacker.bean.NormalObjData;
import com.example.auser.zthacker.bean.UserFollowBean;
import com.example.auser.zthacker.bean.UtilBean;
import com.example.auser.zthacker.dialog.LoadingDialog;
import com.example.auser.zthacker.http.ApiRequestData;
import com.example.auser.zthacker.ui.activity.mine.MyFollowsActivity;
import com.example.auser.zthacker.ui.activity.mine.UserHomePageActivity;
import com.example.auser.zthacker.utils.DisplayUtil;
import com.example.auser.zthacker.utils.SPUtils;
import com.example.auser.zthacker.utils.TextUtil;
import com.example.auser.zthacker.utils.ToastUtil;
import com.example.auser.zthacker.view.GlideCircleTransform;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import java.util.List;

import de.greenrobot.event.EventBus;
import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by zhengkq on 2017-11-17.
 */

public class MyFollowsAdapter extends RecyclerView.Adapter<MyFollowsAdapter.ViewHolder>{
    LayoutInflater inflater;
    private Context context;
    private List<UserFollowBean> list;
    private final String userId;
    private GsonBuilder gsonBuilder;
    private int type;
    private EventPostBean eventPostBean;

    public MyFollowsAdapter(Context context, List<UserFollowBean> list,int type) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.list = list;
        this.type = type;
        userId = SPUtils.getSharedStringData(context, "userId");
    }

    @Override
    public MyFollowsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyFollowsAdapter.ViewHolder(inflater.inflate(R.layout.item_my_follows, parent, false));
    }

    @Override
    public void onBindViewHolder(final MyFollowsAdapter.ViewHolder holder, final int position) {
        final UserFollowBean userFollowBean = list.get(position);
        final AppPersonalInfoBean followUserBean = userFollowBean.getSysAppUser2();
        final AppPersonalInfoBean fansUserBean = userFollowBean.getSysAppUser();
        if (type==0){
            Glide.with(context)
                    .load(followUserBean.getSysAppUerImg())
                    .transform(new GlideCircleTransform(context))
                    .placeholder(R.mipmap.icon_userphote_hl)
                    .override(54,54)
                    .thumbnail(0.2f)
                    .into(holder.simpleDraweeView_head_icon);
            holder.tv_name.setText(followUserBean.getUserName());
        }else if (type==1){
            Glide.with(context)
                    .load(fansUserBean.getSysAppUerImg())
                    .transform(new GlideCircleTransform(context))
                    .placeholder(R.mipmap.icon_userphote_hl)
                    .override(54,54)
                    .thumbnail(0.2f)
                    .into(holder.simpleDraweeView_head_icon);
            holder.tv_name.setText(fansUserBean.getUserName());
        }
        holder.tv_is_attention.setTextColor(!TextUtil.isNull(userFollowBean.getIsFollow())&&userFollowBean.getIsFollow().equals("1")
                ? context.getResources().getColor(R.color.text_64) : context.getResources().getColor(R.color.white));
        holder.tv_is_attention.setText(!TextUtil.isNull(userFollowBean.getIsFollow())&&userFollowBean.getIsFollow().equals("1")
                ? "已关注" : "+关注");
        holder.tv_is_attention.setBackground(!TextUtil.isNull(userFollowBean.getIsFollow())&&userFollowBean.getIsFollow().equals("1")
                ?context.getResources().getDrawable(R.drawable.attention_bg):context.getResources().getDrawable(R.drawable.register_bg));

        holder.tv_is_attention.setOnClickListener(new View.OnClickListener() {
            private String sysAppUser2;
            @Override
            public void onClick(View v) {
                if (TextUtil.isNull(userId)) {
                    LoadingDialog.showDialogForLogin(context);
                    return;
                }
                if (type==0){
                    sysAppUser2 = followUserBean.getId();
                }else if (type==1){
                    sysAppUser2 = fansUserBean.getId();
                }
                ApiRequestData.getInstance(context).ShowDialog(null);
                OkGo.get(ApiRequestData.getInstance(context).PublishTextAttention)//关注
                        .tag(context)
                        .params("sysAppUser", userId)
                        .params("sysAppUser2", sysAppUser2)
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
                                    userFollowBean.setIsFollow(mData.getData().getState()+"");
                                    holder.tv_is_attention.setTextColor(mData.getData().getState() == 1
                                            ? context.getResources().getColor(R.color.text_64) : context.getResources().getColor(R.color.white));
                                    holder.tv_is_attention.setText(mData.getData().getState() == 1 ? "已关注" : "+关注");
                                    holder.tv_is_attention.setBackground(mData.getData().getState() == 1?
                                            context.getResources().getDrawable(R.drawable.attention_bg):context.getResources().getDrawable(R.drawable.register_bg));
                                    eventPostBean = new EventPostBean();
                                    eventPostBean.setType(Config.PERSONAL_ATTENTION);
                                    eventPostBean.setCount(Integer.parseInt(userFollowBean.getIsFollow()));
                                    eventPostBean.setId(followUserBean.getId());
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
        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (type==0){
                    if (!TextUtil.isNull(followUserBean.getId())){
                        UserHomePageActivity.startActivity(context,followUserBean.getId());
                    }
                }else if (type==1){
                    if (!TextUtil.isNull(fansUserBean.getId())){
                        UserHomePageActivity.startActivity(context,fansUserBean.getId());
                    }
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tv_name,  tv_is_attention;
        private ImageView simpleDraweeView_head_icon;

        public ViewHolder(View view) {
            super(view);
            tv_name = (TextView) view.findViewById(R.id.tv_name);
            tv_is_attention = (TextView) view.findViewById(R.id.tv_is_attention);
            simpleDraweeView_head_icon = (ImageView) view.findViewById(R.id.simpleDraweeView_head_icon);
        }
    }
}
