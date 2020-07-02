package com.example.auser.zthacker.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.UiThread;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.auser.zthacker.R;

import java.util.List;

/**
 * Created by zhengkq on 2017/8/14.
 */

public class FoundCommunityPicturesAdapter extends RecyclerView.Adapter<FoundCommunityPicturesAdapter.ViewHolder> {
    private OnRecyclerViewItemClickListener mOnItemClickListener;
    private Context context;
    LayoutInflater inflater;
    private List<String> list;
    private int type;


    /*
    * list  图片地址集合
    * type  类型（0-新闻资讯展示效果  1-社区发布展示效果）
    * */
    public FoundCommunityPicturesAdapter(Context context, List<String> list, int type) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.list = list;
        this.type = type;
    }

    @Override
    public FoundCommunityPicturesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_publish_image, parent, false);
        FoundCommunityPicturesAdapter.ViewHolder viewHolder = new FoundCommunityPicturesAdapter.ViewHolder(view);
        viewHolder.iv_picture = (ImageView) view.findViewById(R.id.iv_picture);
        viewHolder.iv_add = (ImageView) view.findViewById(R.id.iv_add);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final FoundCommunityPicturesAdapter.ViewHolder holder, final int position) {
        final String path = list.get(position);

        switch (type) {
            case 0:
                holder.iv_picture.setVisibility(View.VISIBLE);
                holder.iv_add.setVisibility(View.GONE);
               /* Picasso.with(context)
                        .load(path)
                        .placeholder(R.mipmap.img_placeholder_152)
                        .error(R.mipmap.img_placeholder_152)
                        .resize(158,117)
                        .into(holder.iv_picture);*/
                Glide.with(context)
                        .load(path)
                        .asBitmap()
                        .diskCacheStrategy(DiskCacheStrategy.RESULT)
                        .centerCrop()
                        .override(158, 117)
                        .placeholder(R.mipmap.img_placeholder_152)
                        .skipMemoryCache(false)
                        .thumbnail(0.2f)
                        .dontAnimate()
                        .into(holder.iv_picture);
                break;
            case 1:
                holder.iv_picture.setVisibility(View.GONE);
                holder.iv_add.setVisibility(View.VISIBLE);
                /*Picasso.with(context)
                        .load(path)
                        .placeholder(R.mipmap.img_placeholder_210)
                        .error(R.mipmap.img_placeholder_210)
                        .resize(158,158)
                        .into(holder.iv_add);*/
                Glide.with(context)
                        .load(path)
                        .asBitmap()
                        .diskCacheStrategy(DiskCacheStrategy.RESULT)
                        .centerCrop()
                        .override(158, 158)
                        .placeholder(R.mipmap.img_placeholder_210)
                        .skipMemoryCache(false)
                        .thumbnail(0.2f)
                        .dontAnimate()
                        .into(holder.iv_add);
                break;
        }
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
        if (list != null && list.size() > 0) {
            return list.size() > 9 ? 9 : list.size();
        } else {
            return 0;
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(View view) {
            super(view);
        }

        ImageView iv_picture;
        ImageView iv_add;
    }
}
