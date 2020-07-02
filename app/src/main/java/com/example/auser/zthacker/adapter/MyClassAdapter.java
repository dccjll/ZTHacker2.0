package com.example.auser.zthacker.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.auser.zthacker.R;
import com.example.auser.zthacker.bean.HomeClassTypeInfoBean;
import com.example.auser.zthacker.bean.TypeSelectBean;

import java.util.List;

/**
 * Created by zhengkq on 2017/9/1.
 */

public class MyClassAdapter extends RecyclerView.Adapter<MyClassAdapter.ViewHolder>{
    private MyClassAdapter.OnRecyclerViewItemClickListener mOnItemClickListener;
    LayoutInflater inflater;
    private Context context;
    private List<HomeClassTypeInfoBean> list;
    public MyClassAdapter(Context context, List<HomeClassTypeInfoBean> list) {
        this.context = context;
        this.list = list;
        inflater = LayoutInflater.from(context);
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_myclass_scan,parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        viewHolder.tv_name = (TextView) view.findViewById(R.id.tv_name);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        HomeClassTypeInfoBean homeClassTypeInfoBean = list.get(position);
        holder.tv_name.setText(homeClassTypeInfoBean.getCourseName());
        holder.itemView.setTag(homeClassTypeInfoBean);
        //判断是否设置了监听器
        if (mOnItemClickListener != null) {
            //为ItemView设置监听器
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnItemClickListener.onItemClick(holder.itemView, position); // 2
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static interface OnRecyclerViewItemClickListener {
        void onItemClick(View view , int position);
    }
    public void setOnItemClickListener(MyClassAdapter.OnRecyclerViewItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        public ViewHolder(View view)
        {
            super(view);
        }
        TextView tv_name;
    }
}
