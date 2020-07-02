package com.example.auser.zthacker.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.auser.zthacker.R;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.tools.DebugUtil;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import fm.jiecao.jcvideoplayer_lib.JCUtils;

/**
 * author：luck
 * project：PictureSelector
 * package：com.luck.pictureselector.adapter
 * email：893855882@qq.com
 * data：16/7/27
 */
public class GridImageAdapter extends
        RecyclerView.Adapter<GridImageAdapter.ViewHolder> {
    public static final int TYPE_CAMERA = 1;
    public static final int TYPE_PICTURE = 2;
    private LayoutInflater mInflater;
    private List<LocalMedia> list = new ArrayList<>();
    private int selectMax = 9;
    private Context context;
    /**
     * 点击添加图片跳转
     */
    private onAddPicClickListener mOnAddPicClickListener;

    public interface onAddPicClickListener {
        void onAddPicClick();
    }

    public GridImageAdapter(Context context, onAddPicClickListener mOnAddPicClickListener) {
        this.context = context;
        mInflater = LayoutInflater.from(context);
        this.mOnAddPicClickListener = mOnAddPicClickListener;
    }

    public void setSelectMax(int selectMax) {
        this.selectMax = selectMax;
    }

    public void setList(List<LocalMedia> list) {
        this.list = list;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView mImg;
        ImageView ll_del;
        ImageView iv_picture;
        ImageView iv_video_play;
        TextView tv_video_time;

        public ViewHolder(View view) {
            super(view);
            mImg = (ImageView) view.findViewById(R.id.iv_add);
            ll_del = (ImageView) view.findViewById(R.id.iv_close);
            iv_picture = (ImageView) view.findViewById(R.id.iv_picture);
            iv_video_play = (ImageView) view.findViewById(R.id.iv_video_play);
            tv_video_time = (TextView) view.findViewById(R.id.tv_video_time);
        }
    }

    @Override
    public int getItemCount() {
        if (list.size() < selectMax) {
            return list.size() + 1;
        } else {
            return list.size();
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (isShowAddItem(position)) {
            return TYPE_CAMERA;
        } else {
            return TYPE_PICTURE;
        }
    }

    /**
     * 创建ViewHolder
     */
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = mInflater.inflate(R.layout.item_publish_image,
                viewGroup, false);
        final ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    private boolean isShowAddItem(int position) {
        int size = list.size() == 0 ? 0 : list.size();
        return position == size;
    }

    /**
     * 设置值
     */
    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int position) {
        //少于8张，显示继续添加的图标
        viewHolder.iv_picture.setVisibility(View.INVISIBLE);
        String pictureType = list.size() > 0 ? list.get(0).getPictureType() : "";
        boolean eqImg = pictureType.startsWith(PictureConfig.IMAGE);
        if (getItemViewType(position) == TYPE_CAMERA) {
            if(eqImg||list.size()==0){
                viewHolder.mImg.setVisibility(View.VISIBLE);
                viewHolder.mImg.setImageResource(R.mipmap.icon_addpictures);
                viewHolder.mImg.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mOnAddPicClickListener.onAddPicClick();
                    }
                });
                viewHolder.ll_del.setVisibility(View.INVISIBLE);
                viewHolder.iv_video_play.setVisibility(View.INVISIBLE);
            }else {
                viewHolder.mImg.setVisibility(View.INVISIBLE);
                viewHolder.ll_del.setVisibility(View.INVISIBLE);
                viewHolder.iv_video_play.setVisibility(View.INVISIBLE);
            }
        } else {
            if (eqImg){
                viewHolder.iv_video_play.setVisibility(View.INVISIBLE);
            }else {
                viewHolder.iv_video_play.setVisibility(View.VISIBLE);
            }
            viewHolder.ll_del.setVisibility(View.VISIBLE);
            viewHolder.ll_del.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int index = viewHolder.getAdapterPosition();
                    // 这里有时会返回-1造成数据下标越界,具体可参考getAdapterPosition()源码，
                    // 通过源码分析应该是bindViewHolder()暂未绘制完成导致，知道原因的也可联系我~感谢
                    if (index != RecyclerView.NO_POSITION) {
                        list.remove(index);
                        notifyItemRemoved(index);
                        notifyItemRangeChanged(index, list.size());
                        if (list.size()==0){
                            notifyDataSetChanged();
                        }
                        DebugUtil.i("delete position:", index + "--->remove after:" + list.size());
                    }
                }
            });
            LocalMedia media = list.get(position);
            int mimeType = media.getMimeType();
            String path = "";
            if (media.isCut() && !media.isCompressed()) {
                // 裁剪过
                path = media.getCutPath();
            } else if (media.isCompressed() || (media.isCut() && media.isCompressed())) {
                // 压缩过,或者裁剪同时压缩过,以最终压缩过图片为准
                path = media.getCompressPath();
            } else {
                // 原图
                path = media.getPath();
            }
            Log.i("mimeType", mimeType + "");
            // 图片
            if (media.isCompressed()) {
                Log.i("compress image result:", new File(media.getCompressPath()).length() / 1024 + "k");
                Log.i("压缩地址::", media.getCompressPath());
            }

            Log.i("原图地址::", media.getPath());
            //int pictureType = PictureMimeType.isPictureType(media.getPictureType());
            if (media.isCut()) {
                Log.i("裁剪地址::", media.getCutPath());
            }
            if (!eqImg){
                viewHolder.tv_video_time.setText(JCUtils.stringForTime((int)media.getDuration()));
            }
            //if (mimeType == PictureMimeType.ofImage()) {
            Glide.with(viewHolder.itemView.getContext())
                    .load(path)
                    .asBitmap()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(R.mipmap.img_placeholder_152)
                    .override(158,117)
                    .into(viewHolder.mImg);
            //}
            //itemView 的点击事件
            if (mItemClickListener != null) {
                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int adapterPosition = viewHolder.getAdapterPosition();
                        mItemClickListener.onItemClick(adapterPosition, v);
                    }
                });
            }
        }
    }

    protected OnItemClickListener mItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(int position, View v);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mItemClickListener = listener;
    }
}
