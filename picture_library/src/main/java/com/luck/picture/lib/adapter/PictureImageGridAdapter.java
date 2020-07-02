package com.luck.picture.lib.adapter;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.luck.picture.lib.R;
import com.luck.picture.lib.anim.OptAnimationLoader;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.config.PictureSelectionConfig;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.tools.DateUtils;
import com.luck.picture.lib.tools.DebugUtil;
import com.luck.picture.lib.tools.StringUtils;
import com.luck.picture.lib.tools.VoiceUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * author：luck
 * project：PictureSelector
 * package：com.luck.picture.lib.adapter
 * email：893855882@qq.com
 * data：2016/12/30
 */
public class PictureImageGridAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final static int DURATION = 450;
    private Context context;
    private boolean showCamera = true;
    private OnPhotoSelectChangedListener imageSelectChangedListener;
    private int maxPicSelectNum;
    private List<LocalMedia> images = new ArrayList<LocalMedia>();
    private List<LocalMedia> selectImages = new ArrayList<LocalMedia>();
    private boolean enablePreview;
    private int selectMode = PictureConfig.MULTIPLE;
    private boolean enablePreviewVideo = false;
    private boolean enablePreviewAudio = false;
    private boolean is_checked_num;
    private boolean enableVoice;
    private int overrideWidth, overrideHeight;
    private float sizeMultiplier;
    private Animation animation;
    private PictureSelectionConfig config;
    private int mimeType;
    private boolean zoomAnim;
    private int maxVideoSelectNum = 1;

    public PictureImageGridAdapter(Context context, PictureSelectionConfig config) {
        this.context = context;
        this.config = config;
        this.selectMode = config.selectionMode;
        this.showCamera = config.isCamera;
        this.maxPicSelectNum = config.maxSelectNum;
        this.enablePreview = config.enablePreview;
        this.enablePreviewVideo = config.enPreviewVideo;
        this.enablePreviewAudio = config.enablePreviewAudio;
        this.is_checked_num = config.checkNumMode;
        this.overrideWidth = config.overrideWidth;
        this.overrideHeight = config.overrideHeight;
        this.enableVoice = config.openClickSound;
        this.sizeMultiplier = config.sizeMultiplier;
        this.mimeType = config.mimeType;
        this.zoomAnim = config.zoomAnim;
        animation = OptAnimationLoader.loadAnimation(context, R.anim.modal_in);
    }

    public void setShowCamera(boolean showCamera) {
        this.showCamera = showCamera;
    }

    public void bindImagesData(List<LocalMedia> images) {
        this.images = images;
        notifyDataSetChanged();
    }

    public void bindSelectImages(List<LocalMedia> images) {
        // 这里重新构构造一个新集合，不然会产生已选集合一变，结果集合也会添加的问题
        List<LocalMedia> selection = new ArrayList<>();
        for (LocalMedia media : images) {
            selection.add(media);
        }
        this.selectImages = selection;
        subSelectPosition();
        if (imageSelectChangedListener != null) {
            imageSelectChangedListener.onChange(selectImages);
        }
    }

    public List<LocalMedia> getSelectedImages() {
        if (selectImages == null) {
            selectImages = new ArrayList<>();
        }
        return selectImages;
    }

    public List<LocalMedia> getImages() {
        if (images == null) {
            images = new ArrayList<>();
        }
        return images;
    }

    @Override
    public int getItemViewType(int position) {
        if (showCamera && position == 0) {
            return PictureConfig.TYPE_CAMERA;
        } else {
            return PictureConfig.TYPE_PICTURE;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == PictureConfig.TYPE_CAMERA) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.picture_item_camera, parent, false);
            return new HeaderViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.picture_image_grid_item, parent, false);
            return new ViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        if (getItemViewType(position) == PictureConfig.TYPE_CAMERA) {
            HeaderViewHolder headerHolder = (HeaderViewHolder) holder;
            headerHolder.headerView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (selectImages.size()>0){
                        String pictureType = selectImages.get(0).getPictureType();
                        boolean eqVideo = pictureType.startsWith(PictureConfig.VIDEO);
                        if (eqVideo){
                            Toast.makeText(context, context.getString(R.string.picture_video_select), Toast.LENGTH_LONG).show();
                            return;
                        }
                    }
                    if (imageSelectChangedListener != null) {
                        imageSelectChangedListener.onTakePhoto();
                    }
                }
            });
        } else {
            final ViewHolder contentHolder = (ViewHolder) holder;
            final LocalMedia image = images.get(showCamera ? position - 1 : position);
            image.position = contentHolder.getAdapterPosition();
            final String path = image.getPath();
            final String pictureType = image.getPictureType();
            contentHolder.ll_check.setVisibility(selectMode ==
                    PictureConfig.SINGLE ? View.GONE : View.VISIBLE);
            if (is_checked_num) {
                notifyCheckChanged(contentHolder, image);
            }
            selectImage(contentHolder, isSelected(image), false);

            final int picture = PictureMimeType.isPictureType(pictureType);
            boolean gif = PictureMimeType.isGif(pictureType);
            contentHolder.tv_isGif.setVisibility(gif ? View.VISIBLE : View.GONE);
            if (mimeType == PictureMimeType.ofAudio()) {
                contentHolder.tv_duration.setVisibility(View.VISIBLE);
                Drawable drawable = ContextCompat.getDrawable(context, R.drawable.picture_audio);
                StringUtils.modifyTextViewDrawable(contentHolder.tv_duration, drawable, 0);
            } else {
                Drawable drawable = ContextCompat.getDrawable(context, R.drawable.video_icon);
                StringUtils.modifyTextViewDrawable(contentHolder.tv_duration, drawable, 0);
                contentHolder.tv_duration.setVisibility(picture == PictureConfig.TYPE_VIDEO
                        ? View.VISIBLE : View.GONE);
            }
            boolean eqLongImg = PictureMimeType.isLongImg(image);
            contentHolder.tv_long_chart.setVisibility(eqLongImg ? View.VISIBLE : View.GONE);
            long duration = image.getDuration();
            contentHolder.tv_duration.setText(DateUtils.timeParse(duration));
            if (mimeType == PictureMimeType.ofAudio()) {
                contentHolder.iv_picture.setImageResource(R.drawable.audio_placeholder);
            } else {
                if (overrideWidth <= 0 && overrideHeight <= 0) {
                    Glide.with(context)
                            .load(path)
                            .asBitmap()
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .centerCrop()
                            .placeholder(R.drawable.image_placeholder)
                            .sizeMultiplier(sizeMultiplier)
                            .into(contentHolder.iv_picture);
                } else {
                    Glide.with(context)
                            .load(path)
                            .asBitmap()
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .centerCrop()
                            .placeholder(R.drawable.image_placeholder)
                            .override(overrideWidth, overrideHeight)
                            .into(contentHolder.iv_picture);
                }
            }
            if (enablePreview || enablePreviewVideo || enablePreviewAudio) {
                contentHolder.ll_check.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // 如原图路径不存在或者路径存在但文件不存在
                        if (!new File(path).exists()) {
                            Toast.makeText(context, context.getString(R.string.picture_error), Toast.LENGTH_LONG)
                                    .show();
                            return;
                        }
                        changeCheckboxState(contentHolder, image);
                    }
                });

            }
            contentHolder.contentView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 如原图路径不存在或者路径存在但文件不存在
                    if (!new File(path).exists()) {
                        Toast.makeText(context, context.getString(R.string.picture_error), Toast.LENGTH_LONG)
                                .show();
                        return;
                    }
                    if (picture == PictureConfig.TYPE_IMAGE && (enablePreview
                            || selectMode == PictureConfig.SINGLE)) {
                        int index = showCamera ? position - 1 : position;
                        imageSelectChangedListener.onPictureClick(image, index);
                    } else if (picture == PictureConfig.TYPE_VIDEO && (enablePreviewVideo
                            || selectMode == PictureConfig.SINGLE)) {
                        int index = showCamera ? position - 1 : position;
                        imageSelectChangedListener.onPictureClick(image, index);
                    } else if (picture == PictureConfig.TYPE_AUDIO && (enablePreviewAudio
                            || selectMode == PictureConfig.SINGLE)) {
                        int index = showCamera ? position - 1 : position;
                        imageSelectChangedListener.onPictureClick(image, index);
                    } else {
                        changeCheckboxState(contentHolder, image);
                    }
                }
            });
        }
    }


    @Override
    public int getItemCount() {
        return showCamera ? images.size() + 1 : images.size();
    }

    public class HeaderViewHolder extends RecyclerView.ViewHolder {
        View headerView;
        TextView tv_title_camera;

        public HeaderViewHolder(View itemView) {
            super(itemView);
            headerView = itemView;
            tv_title_camera = (TextView) itemView.findViewById(R.id.tv_title_camera);
            String title = mimeType == PictureMimeType.ofAudio() ?
                    context.getString(R.string.picture_tape)
                    : context.getString(R.string.picture_take_picture);
            tv_title_camera.setText(title);
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView iv_picture,iv_select;
        TextView check;
        TextView tv_duration, tv_isGif, tv_long_chart;
        View contentView;
        RelativeLayout ll_check;

        public ViewHolder(View itemView) {
            super(itemView);
            contentView = itemView;
            iv_picture = (ImageView) itemView.findViewById(R.id.iv_picture);
            iv_select = (ImageView) itemView.findViewById(R.id.iv_select);
            check = (TextView) itemView.findViewById(R.id.check);
            ll_check = (RelativeLayout) itemView.findViewById(R.id.ll_check);
            tv_duration = (TextView) itemView.findViewById(R.id.tv_duration);
            tv_isGif = (TextView) itemView.findViewById(R.id.tv_isGif);
            tv_long_chart = (TextView) itemView.findViewById(R.id.tv_long_chart);
        }
    }

    public boolean isSelected(LocalMedia image) {
        for (LocalMedia media : selectImages) {
            if (media.getPath().equals(image.getPath())) {
                return true;
            }
        }
        return false;
    }

    /**
     * 选择按钮更新
     */
    private void notifyCheckChanged(ViewHolder viewHolder, LocalMedia imageBean) {
        viewHolder.check.setText("");
        for (LocalMedia media : selectImages) {
            if (media.getPath().equals(imageBean.getPath())) {
                imageBean.setNum(media.getNum());
                media.setPosition(imageBean.getPosition());
                viewHolder.check.setText(String.valueOf(imageBean.getNum()));
            }
        }
    }

    /**
     * 改变图片选中状态
     *
     * @param contentHolder
     * @param image
     */

    private void changeCheckboxState(ViewHolder contentHolder, LocalMedia image) {
        boolean isChecked = contentHolder.check.isSelected();
        String pictureType = selectImages.size() > 0 ? selectImages.get(0).getPictureType() : "";
        if (!TextUtils.isEmpty(pictureType)) {
            boolean toEqual = PictureMimeType.mimeToEqual(pictureType, image.getPictureType());
            if (!toEqual) {
                Toast.makeText(context, context.getString(R.string.picture_rule), Toast.LENGTH_LONG)
                        .show();
                return;
            }
        }
        boolean eqImg = image.getPictureType().startsWith(PictureConfig.IMAGE);
        boolean eqVideo = image.getPictureType().startsWith(PictureConfig.VIDEO);
        String str = "";
        if (eqImg && selectImages.size() >= maxPicSelectNum && !isChecked) {
            str = context.getString(R.string.picture_message_max_num, maxPicSelectNum);
            Toast.makeText(context, str, Toast.LENGTH_LONG).show();
            return;
        }
        if (eqVideo && selectImages.size() >= maxVideoSelectNum && !isChecked) {
            str = context.getString(R.string.picture_message_video_max_num, maxVideoSelectNum);
            Toast.makeText(context, str, Toast.LENGTH_LONG).show();
            return;
        }
        if(eqVideo&& image.getSize()/(1024 * 1024)>20){
            str = context.getString(R.string.picture_message_video_too_big);
            Toast.makeText(context, str, Toast.LENGTH_LONG).show();
            return;
        }

        if (isChecked) {
            for (LocalMedia media : selectImages) {
                if (media.getPath().equals(image.getPath())) {
                    selectImages.remove(media);
                    DebugUtil.i("selectImages remove::", config.selectionMedias.size() + "");
                    subSelectPosition();
                    disZoom(contentHolder.iv_picture);
                    break;
                }
            }
        } else {
            selectImages.add(image);
            DebugUtil.i("selectImages add::", config.selectionMedias.size() + "");
            image.setNum(selectImages.size());
            VoiceUtils.playVoice(context, enableVoice);
            zoom(contentHolder.iv_picture);
        }
        //通知点击项发生了改变
        notifyItemChanged(contentHolder.getAdapterPosition());
        selectImage(contentHolder, !isChecked, true);
        if (imageSelectChangedListener != null) {
            imageSelectChangedListener.onChange(selectImages);
        }
    }

    /**
     * 更新选择的顺序
     */
    private void subSelectPosition() {
        if (is_checked_num) {
            int size = selectImages.size();
            for (int index = 0, length = size; index < length; index++) {
                LocalMedia media = selectImages.get(index);
                media.setNum(index + 1);
                notifyItemChanged(media.position);
            }
        }
    }

    public void selectImage(ViewHolder holder, boolean isChecked, boolean isAnim) {
        holder.check.setSelected(isChecked);
        if (isChecked) {
            if (isAnim) {
                if (animation != null) {
                    holder.check.startAnimation(animation);
                }
            }
            holder.iv_select.setBackground(context.getDrawable(R.drawable.icon_tick_blue));
            holder.iv_picture.setColorFilter(ContextCompat.getColor
                    (context, R.color.image_overlay_true), PorterDuff.Mode.SRC_ATOP);
        } else {
            holder.iv_select.setBackground(context.getDrawable(R.drawable.image_select_bg));
            holder.iv_picture.setColorFilter(ContextCompat.getColor
                    (context, R.color.image_overlay_false), PorterDuff.Mode.SRC_ATOP);
        }
    }

    public interface OnPhotoSelectChangedListener {
        void onTakePhoto();

        void onChange(List<LocalMedia> selectImages);


        void onPictureClick(LocalMedia media, int position);
    }

    public void setOnPhotoSelectChangedListener(OnPhotoSelectChangedListener
                                                        imageSelectChangedListener) {
        this.imageSelectChangedListener = imageSelectChangedListener;
    }

    private void zoom(ImageView iv_img) {
        if (zoomAnim) {
            AnimatorSet set = new AnimatorSet();
            set.playTogether(
                    ObjectAnimator.ofFloat(iv_img, "scaleX", 1f, 1.12f),
                    ObjectAnimator.ofFloat(iv_img, "scaleY", 1f, 1.12f)
            );
            set.setDuration(DURATION);
            set.start();
        }
    }

    private void disZoom(ImageView iv_img) {
        if (zoomAnim) {
            AnimatorSet set = new AnimatorSet();
            set.playTogether(
                    ObjectAnimator.ofFloat(iv_img, "scaleX", 1.12f, 1f),
                    ObjectAnimator.ofFloat(iv_img, "scaleY", 1.12f, 1f)
            );
            set.setDuration(DURATION);
            set.start();
        }
    }
}
