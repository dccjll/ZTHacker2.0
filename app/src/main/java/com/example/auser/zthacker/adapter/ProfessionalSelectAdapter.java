package com.example.auser.zthacker.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.auser.zthacker.R;
import com.example.auser.zthacker.bean.TypeSelectBean;

import java.util.List;

/**
 * Created by zhengkq on 2017/8/7.
 */

public class ProfessionalSelectAdapter extends RecyclerView.Adapter<ProfessionalSelectAdapter.ViewHolder>{
    private OnRecyclerViewItemClickListener mOnItemClickListener;
    LayoutInflater inflater;
    private Context context;
    private List<TypeSelectBean> list;
    public ProfessionalSelectAdapter(Context context, List<TypeSelectBean> list) {
        this.context = context;
        this.list = list;
        inflater = LayoutInflater.from(context);
    }
    @Override
    public ProfessionalSelectAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_professional_select,parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        viewHolder.tv_name = (TextView) view.findViewById(R.id.tv_name);
        viewHolder.iv_select = (ImageView) view.findViewById(R.id.iv_select);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ProfessionalSelectAdapter.ViewHolder holder, final int position) {
        TypeSelectBean typeSelectBean = list.get(position);
        holder.tv_name.setText(typeSelectBean.getName());
        boolean select = typeSelectBean.isSelect();
        if (select){
            holder.iv_select.setVisibility(View.VISIBLE);
        }else {
            holder.iv_select.setVisibility(View.INVISIBLE);
        }
        //holder.iv_select.setVisibility(select?View.VISIBLE:View.INVISIBLE);
        holder.itemView.setTag(typeSelectBean);
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
       /* holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = 0;i<list.size();i++){
                    if (i==position){
                        list.get(i).setSelect(true);
                    }else {
                        list.get(i).setSelect(false);
                    }
                }
                notifyDataSetChanged();
            }
        });*/
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static interface OnRecyclerViewItemClickListener {
        void onItemClick(View view , int position);
    }
    public void setOnItemClickListener(OnRecyclerViewItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        public ViewHolder(View view)
        {
            super(view);
        }
        TextView tv_name;
        ImageView iv_select;
    }
}
