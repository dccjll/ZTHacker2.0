package com.example.auser.zthacker.adapter;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.auser.zthacker.R;
import com.example.auser.zthacker.base.Config;
import com.example.auser.zthacker.bean.TeachingResourseInfoBean;
import com.example.auser.zthacker.bean.HomeClassTypeInfoBean;
import com.example.auser.zthacker.view.RecycleViewDividerLine;

import java.util.List;

import de.greenrobot.event.EventBus;

/**
 * Created by zhengkq on 2017/8/21.
 */

public class HomeTypeAdapter extends RecyclerView.Adapter<HomeTypeAdapter.ViewHolder> {
    LayoutInflater inflater;
    private Context context;
    private List<HomeClassTypeInfoBean> classList;
    private List<TeachingResourseInfoBean> courseRealia;


    public HomeTypeAdapter(Context context, List<HomeClassTypeInfoBean> classList, List<TeachingResourseInfoBean> courseRealia) {
        this.context = context;
        this.classList = classList;
        this.courseRealia = courseRealia;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public HomeTypeAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_home_type, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        viewHolder.tv_name = (TextView) view.findViewById(R.id.tv_name);
        viewHolder.rlv_pictures = (RecyclerView) view.findViewById(R.id.rlv_pictures);
        viewHolder.rlv_pictures.addItemDecoration(new RecycleViewDividerLine(context, RecycleViewDividerLine.VERTICAL_LIST
                , (int) (context.getResources().getDimension(R.dimen.x11)), context.getResources().getColor(R.color.white)));
        viewHolder.tv_know_more = (TextView) view.findViewById(R.id.tv_know_more);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(HomeTypeAdapter.ViewHolder holder, final int position) {
        if (classList != null && position == 0) {
            holder.tv_name.setText("课程系列");
            holder.tv_know_more.setVisibility(View.VISIBLE);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
            linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
            holder.rlv_pictures.setLayoutManager(linearLayoutManager);
            HomeTypeChildAdapter adapter = new HomeTypeChildAdapter(context, classList);
            holder.rlv_pictures.setAdapter(adapter);
            //holder.rlv_pictures.addItemDecoration(new RecycleViewDividerLine(context, RecycleViewDividerLine.VERTICAL_LIST
            //        , DisplayUtil.dip2px(context.getResources().getDimension(R.dimen.space_v_24)), context.getResources().getColor(R.color.white)));

            holder.tv_know_more.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    EventBus.getDefault().post(Config.MAIN_COURSE);
                }
            });
            holder.itemView.setClickable(false);
        } else {
            if (courseRealia != null){
                holder.tv_name.setText("课程教具");
                holder.tv_know_more.setVisibility(View.INVISIBLE);
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
                linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
                holder.rlv_pictures.setLayoutManager(linearLayoutManager);
                HomeCourseToolRecommendAdapter adapter = new HomeCourseToolRecommendAdapter(context, courseRealia);
                holder.rlv_pictures.setAdapter(adapter);
                holder.itemView.setClickable(false);
            }
        }
    }

    @Override
    public int getItemCount() {
        if (courseRealia == null && classList == null) {
            return 0;
        } else if (courseRealia == null && classList != null) {
            return 1;
        } else if (courseRealia != null && classList == null) {
            return 1;
        } else {
            return 2;
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(View view) {
            super(view);
        }

        TextView tv_name;
        RecyclerView rlv_pictures;
        TextView tv_know_more;
    }
}
