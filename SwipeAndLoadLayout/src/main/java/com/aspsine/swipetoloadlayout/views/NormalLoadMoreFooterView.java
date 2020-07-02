package com.aspsine.swipetoloadlayout.views;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.aspsine.swipetoloadlayout.R;
import com.aspsine.swipetoloadlayout.SwipeLoadMoreFooterLayout;

/**
 * Created by Aspsine on 2015/9/2.
 */
public class NormalLoadMoreFooterView extends SwipeLoadMoreFooterLayout {
    private TextView tvLoadMore;
    private ImageView ivSuccess;
    private ProgressBar progressBar;

    private int mFooterHeight;

    public NormalLoadMoreFooterView(Context context) {
        this(context, null);
    }

    public NormalLoadMoreFooterView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NormalLoadMoreFooterView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mFooterHeight = getResources().getDimensionPixelOffset(R.dimen.load_more_footer_height_normal);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        tvLoadMore = (TextView) findViewById(R.id.tvLoadMore);
        ivSuccess = (ImageView) findViewById(R.id.ivSuccess);
        progressBar = (ProgressBar) findViewById(R.id.progressbar);
    }

    @Override
    public void onPrepare() {
        Log.e("NormalLoadMoreFooterView", "onPrepare");

        ivSuccess.setVisibility(GONE);
    }

    @Override
    public void onMove(int y, boolean isComplete, boolean automatic) {
        if (!isComplete) {
            ivSuccess.setVisibility(GONE);
            progressBar.setVisibility(GONE);
            if (-y >= mFooterHeight) {
                tvLoadMore.setText("放开加载");
            } else {
                tvLoadMore.setText("上拉加载");
            }
        }
    }

    @Override
    public void onLoadMore() {
        Log.e("NormalLoadMoreFooterView", "onLoadMore()");
        tvLoadMore.setText("加载中...");
        progressBar.setVisibility(VISIBLE);
        ivSuccess.setVisibility(GONE);
    }
    //上拉底部出现
    @Override
    public void onRelease() {
        Log.e("NormalLoadMoreFooterView", "onRelease()");

    }

    @Override
    public void onComplete() {
        Log.e("NormalLoadMoreFooterView", "onComplete()");
        tvLoadMore.setText("加载完成");
        progressBar.setVisibility(GONE);
        ivSuccess.setVisibility(VISIBLE);
    }
    //上拉底部消失
    @Override
    public void onReset() {
        Log.e("NormalLoadMoreFooterView", "onReset()");
        ivSuccess.setVisibility(GONE);
    }
}
