package com.example.auser.zthacker.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.ViewGroup;
import java.util.List;

public class MyFragmentPagerAdapter extends FragmentStatePagerAdapter {
	private List<Fragment> list;
    public MyFragmentPagerAdapter(FragmentManager fm, List<Fragment> list) {
        super(fm);
        this.list = list;
    }

    @Override
    public int getCount() {  
        return list.size();  
    }  
      
    @Override
    public Fragment getItem(int arg0) {
        return list.get(arg0);  
    }
	@Override
	public Object instantiateItem(ViewGroup arg0, int arg1) {
		// TODO Auto-generated method stub
		return super.instantiateItem(arg0, arg1);
	}
	@Override
	 public void destroyItem(ViewGroup container, int position, Object object) {
	   super.destroyItem(container, position, object);       
	 }
}
