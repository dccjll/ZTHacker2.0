package com.example.auser.zthacker.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.auser.zthacker.R;
import com.example.auser.zthacker.bean.VideoInfoBean;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by zhengkq on 2018-1-25.
 */

public class TeachingResourseAdapter extends RecyclerView.Adapter<TeachingResourseAdapter.ViewHolder>{
    LayoutInflater inflater;
    private Activity context;
    private List<VideoInfoBean> list;
    private OnRecyclerViewItemClickListener mOnItemClickListener;

    public TeachingResourseAdapter(Context context, List<VideoInfoBean> list) {
        this.context = (Activity) context;
        inflater = LayoutInflater.from(context);
        this.list = list;
    }

    @Override
    public TeachingResourseAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_teaching_resourse, parent, false);
        final ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final TeachingResourseAdapter.ViewHolder holder, final int position) {
        VideoInfoBean videoInfoBean = list.get(position);
        if (videoInfoBean!=null){
            holder.tv_title.setText(videoInfoBean.getCourseWareName());
        }
        Glide.with(context)
                .load(videoInfoBean.getCourseWareImgUrl())
                .asBitmap()
                .diskCacheStrategy(DiskCacheStrategy.RESULT)
                .placeholder(R.mipmap.img_placeholder_152)
                .fitCenter()
                .override(720,405)
                .thumbnail(0.4f)
                .into(holder.iv_cover);
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

    public interface OnRecyclerViewItemClickListener {
        void onItemClick(View view, int position);
    }

    public void setOnItemClickListener(OnRecyclerViewItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    @Override
    public int getItemCount() {
        return list==null?0:list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        @BindView(R.id.tv_title)
        TextView tv_title;
        @BindView(R.id.iv_cover)
        ImageView iv_cover;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
