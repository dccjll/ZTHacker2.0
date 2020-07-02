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
import com.example.auser.zthacker.bean.TeachingResourseInfoBean;
import com.example.auser.zthacker.ui.activity.home.CourseAndResourseActivity;

import java.util.List;

/**
 * Created by zhengkq on 2017-12-25.
 */

public class HomeCourseToolRecommendAdapter extends RecyclerView.Adapter<HomeCourseToolRecommendAdapter.ViewHolder> {
    LayoutInflater inflater;
    private Context context;
    private List<TeachingResourseInfoBean> list;

    public HomeCourseToolRecommendAdapter(Context context, List<TeachingResourseInfoBean> list) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.list = list;
    }

    @Override
    public HomeCourseToolRecommendAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_home_course_tool_recommend, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        viewHolder.iv_picture = (ImageView) view.findViewById(R.id.iv_picture);
        viewHolder.tv_title = (TextView) view.findViewById(R.id.tv_title);
        viewHolder.tv_content = (TextView) view.findViewById(R.id.tv_content);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final HomeCourseToolRecommendAdapter.ViewHolder holder, int position) {
        if (list!=null){
            final TeachingResourseInfoBean teachingResourseInfo = list.get(position);
            Glide.with(context)
                    .load(teachingResourseInfo.getFileUrlImg())
                    .asBitmap()
                    .diskCacheStrategy(DiskCacheStrategy.RESULT)
                    .centerCrop()
                    .placeholder(R.mipmap.img_placeholder_210)
                    .override(158,158)
                    .thumbnail(0.2f)
                    .into(holder.iv_picture);
            holder.tv_title.setText(teachingResourseInfo.getFileName());
            holder.tv_content.setText(teachingResourseInfo.getVideoSynopsis());
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //CourseToolActivity.startActivity(context, imageInfoBean);
                    CourseAndResourseActivity.startActivity(context,teachingResourseInfo);
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
        TextView tv_title;
        TextView tv_content;
    }
}
