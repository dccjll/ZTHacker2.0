package com.example.auser.zthacker.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.auser.zthacker.R;
import com.example.auser.zthacker.bean.MsgNoneWatchedCountBean;
import com.example.auser.zthacker.bean.PublishInfoList;
import com.example.auser.zthacker.ui.activity.message.MyCommentMesActivity;
import com.example.auser.zthacker.ui.activity.message.MySystemMesActivity;
import com.example.auser.zthacker.ui.activity.message.MyZanMesActivity;

/**
 * Created by zhengkq on 2017/8/16.
 */

public class MyMsgAdapter extends RecyclerView.Adapter<MyMsgAdapter.ViewHolder>{

    LayoutInflater inflater;
    private Context context;
    private MsgNoneWatchedCountBean msgNoneWatchedCountBean;
    public MyMsgAdapter(Context context, MsgNoneWatchedCountBean msgNoneWatchedCountBean) {
        this.context = context;
        this.msgNoneWatchedCountBean = msgNoneWatchedCountBean;
        inflater = LayoutInflater.from(context);
    }
    @Override
    public MyMsgAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_my_message,parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        viewHolder.iv_logo = (ImageView) view.findViewById(R.id.iv_logo);
        viewHolder.tv_name = (TextView) view.findViewById(R.id.tv_name);
        viewHolder.tv_message_count = (TextView) view.findViewById(R.id.tv_message_count);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(MyMsgAdapter.ViewHolder holder, final int position) {
        switch (position){
            case 0:
                holder.iv_logo.setImageResource(R.mipmap.icon_zan_24);
                holder.tv_name.setText("点赞");
                if (msgNoneWatchedCountBean!=null&&msgNoneWatchedCountBean.getLikeCount()>0){
                    holder.tv_message_count.setVisibility(View.VISIBLE);
                    if (msgNoneWatchedCountBean.getLikeCount()<=9){
                        holder.tv_message_count.setText(msgNoneWatchedCountBean.getLikeCount()+"");
                    }else {
                        holder.tv_message_count.setText("9+");
                    }
                }else {
                    holder.tv_message_count.setVisibility(View.GONE);
                }
                break;
            case 1:
                holder.iv_logo.setImageResource(R.mipmap.icon_comment_24);
                holder.tv_name.setText("评论");
                if (msgNoneWatchedCountBean!=null&&msgNoneWatchedCountBean.getCoummentCount()>0){
                    holder.tv_message_count.setVisibility(View.VISIBLE);
                    if (msgNoneWatchedCountBean.getCoummentCount()<=9){
                        holder.tv_message_count.setText(msgNoneWatchedCountBean.getCoummentCount()+"");
                    }else {
                        holder.tv_message_count.setText("9+");
                    }
                }else {
                    holder.tv_message_count.setVisibility(View.GONE);
                }
                break;
            case 2:
                holder.iv_logo.setImageResource(R.mipmap.icon_systemmessage);
                holder.tv_name.setText("系统消息");
                holder.tv_message_count.setVisibility(View.GONE);
                break;
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (position==0){
                    MyZanMesActivity.startActivity(context);
                }else if (position==1){
                    MyCommentMesActivity.startActivity(context);
                }else {
                    MySystemMesActivity.startActivity(context);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return 3;
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        public ViewHolder(View view)
        {
            super(view);
        }
        TextView tv_name;
        ImageView iv_logo;
        TextView tv_message_count;
    }
}
