package com.example.administrator.huashixingkong.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

import java.util.ArrayList;


/**
 * Created by Administrator on 2016/1/26.
 */
public class ViewPagerAdapter extends FragmentPagerAdapter {
    ArrayList<Fragment> viewLists;

    public ViewPagerAdapter(FragmentManager fm, ArrayList<Fragment> viewLists) {
        super(fm);
        this.viewLists = viewLists;
    }


    @Override
    public int getCount() {                                                                 //获得size
        // TODO Auto-generated method stub
        return viewLists.size();
    }

    @Override
    public Fragment getItem(int i) {
        return viewLists.get(i);
    }


}
