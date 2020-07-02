package com.example.auser.zthacker.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
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

public class CourseAdapter extends RecyclerView.Adapter<CourseAdapter.ViewHolder>{
    LayoutInflater inflater;
    private Context context;
    private List<HomeClassTypeInfoBean> list;
    public CourseAdapter(Context context,List<HomeClassTypeInfoBean> list) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.list = list;
    }
    @Override
    public CourseAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_course,parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        viewHolder.iv_image = (ImageView) view.findViewById(R.id.iv_image);
        viewHolder.tv_title = (TextView) view.findViewById(R.id.tv_title);
        viewHolder.rl_more = (RelativeLayout) view.findViewById(R.id.rl_more);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(CourseAdapter.ViewHolder holder, int position) {
        final HomeClassTypeInfoBean courseInfoBean = list.get(position);
        holder.tv_title.setText(courseInfoBean.getCourseName());
        Glide.with(context)
                .load(courseInfoBean.getPicUrl())
                .asBitmap()
                .diskCacheStrategy(DiskCacheStrategy.RESULT)
                .placeholder(R.mipmap.img_placeholder_320)
                .centerCrop()
                .override(490,240)
                .into(holder.iv_image);

       /* Glide.with(context).load(courseInfoBean.getPicUrl())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .centerCrop()
                .placeholder(R.mipmap.img_placeholder_320)
                .error(R.mipmap.img_placeholder_320)
                .crossFade().into(holder.iv_image);*/
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtil.isNull(courseInfoBean.getAddLink())){
                    CourseInfoActivity.startActivity(context,courseInfoBean);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return list==null?0:list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        public ViewHolder(View view)
        {
            super(view);
        }
        TextView tv_title;
        ImageView iv_image;
        RelativeLayout rl_more;
    }
}
