package com.example.auser.zthacker.adapter;

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
import com.example.auser.zthacker.bean.HomeClassTypeInfoBean;
import com.example.auser.zthacker.ui.activity.course.CourseInfoActivity;
import com.example.auser.zthacker.utils.TextUtil;

import java.util.List;

/**
 * Created by zhengkq on 2017/8/21.
 */

public class HomeTypeChildAdapter extends RecyclerView.Adapter<HomeTypeChildAdapter.ViewHolder> {
    LayoutInflater inflater;
    private Context context;
    private List<HomeClassTypeInfoBean> list;

    public HomeTypeChildAdapter(Context context, List<HomeClassTypeInfoBean> list) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.list = list;
    }

    @Override
    public HomeTypeChildAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_home_type_child, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        viewHolder.iv_picture = (ImageView) view.findViewById(R.id.iv_picture);
        viewHolder.tv_picture_name = (TextView) view.findViewById(R.id.tv_picture_name);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final HomeTypeChildAdapter.ViewHolder holder, int position) {
        if (list!=null){
            final HomeClassTypeInfoBean imageInfoBean = list.get(position);
            Glide.with(context)
                    .load(imageInfoBean.getPicUrl())
                    .asBitmap()
                    .diskCacheStrategy(DiskCacheStrategy.RESULT)
                    .centerCrop()
                    .placeholder(R.mipmap.img_placeholder_240_320)
                    .override(240,300)
                    .thumbnail(0.5f)
                    .into(holder.iv_picture);
            holder.tv_picture_name.setText(imageInfoBean.getCourseName());
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(!TextUtil.isNull(imageInfoBean.getAddLink())){
                        CourseInfoActivity.startActivity(context, imageInfoBean);
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return list==null?0:list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(View view) {
            super(view);
        }

        ImageView iv_picture;
        TextView tv_picture_name;
    }
}
