package com.example.auser.zthacker.base;

import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.auser.zthacker.R;
import com.example.auser.zthacker.http.ApiRequestData;
import com.google.gson.GsonBuilder;

import butterknife.ButterKnife;


public abstract class BaseFragment extends Fragment {
    public View view;
    private boolean isHaveTop = true;
   // public GsonBuilder gsonBuilder;
    //当前Fragment是否处于可见状态标志，防止因ViewPager的缓存机制而导致回调函数的触发
    private boolean isFragmentVisible;
    //是否是第一次开启网络加载
    public boolean isFirst;
    //private ApiRequestData apiRequestData;
    public boolean isViewInitFinished;

    public void setContentView(LayoutInflater inflater, ViewGroup container, int layoutResID){
        view = inflater.inflate(layoutResID, container, false);
        ButterKnife.bind(this, view);
        initView();
        if (isFragmentVisible && !isFirst) {
            onFragmentVisibleChange(true);
        }
    }

    /*public void initRequest() {
        if (gsonBuilder==null){
            gsonBuilder = new GsonBuilder();
        }
    }*/
    //初始化view
    protected abstract void initView();

    //该方法在fragment的所有方法之前调用
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            isFragmentVisible = true;
        }
        if (view == null) {
            return;
        }
        //可见，并且第一次加载
        if (!isFirst&&isViewInitFinished&&isFragmentVisible){
            onFragmentVisibleChange(true);
            return;
        }
        //由可见——>不可见 已经加载过
        if (isFragmentVisible) {
            onFragmentVisibleChange(false);
            isFragmentVisible = false;
        }
    }
    /**
     * 当前fragment可见状态发生变化时会回调该方法
     * 如果当前fragment是第一次加载，等待onCreateView后才会回调该方法，其它情况回调时机跟 {@link #setUserVisibleHint(boolean)}一致
     * 在该回调方法中你可以做一些加载数据操作，甚至是控件的操作.
     *
     * @param isVisible true  不可见 -> 可见
     *                  false 可见  -> 不可见
     */
    protected void onFragmentVisibleChange(boolean isVisible) {

    }


    public void setTop(int id){
        View imageView_top = view.findViewById(R.id.imageView_top);
        if (isHaveTop) {
            imageView_top.setVisibility(View.VISIBLE);
        } else {
            imageView_top.setVisibility(View.GONE);
        }
        imageView_top.setBackgroundColor(getResources().getColor(id));
    }

    /*public ApiRequestData ApiRequestData.getInstance(this){
        if (apiRequestData==null&&ApiRequestData.getInstance(getActivity())!=this.apiRequestData){
            apiRequestData = ApiRequestData.getInstance(getActivity());
        }
        return apiRequestData;
    }*/
}
