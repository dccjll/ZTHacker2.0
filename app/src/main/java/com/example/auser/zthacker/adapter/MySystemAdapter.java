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
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.auser.zthacker.R;

/**
 * Created by zhengkq on 2017-11-16.
 */

public class MySystemAdapter extends RecyclerView.Adapter<MySystemAdapter.ViewHolder>{
    LayoutInflater inflater;
    private Context context;
    public MySystemAdapter(Context context) {
        this.context = context;
        inflater = LayoutInflater.from(context);
    }
    @Override
    public MySystemAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_my_system,parent, false);
        MySystemAdapter.ViewHolder viewHolder = new MySystemAdapter.ViewHolder(view);
        viewHolder.tv_time = (TextView) view.findViewById(R.id.tv_time);
        viewHolder.tv_content = (TextView) view.findViewById(R.id.tv_content);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(MySystemAdapter.ViewHolder holder, int position) {
        holder.tv_time.setText("一个小时前");
        holder.tv_content.setText("您发布的“如何成为一个成功的人！”违反了公共平台协议的内容");
    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        public ViewHolder(View view)
        {
            super(view);
        }
        TextView tv_time,tv_content;
    }
}
