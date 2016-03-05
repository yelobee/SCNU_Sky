package com.example.administrator.huashixingkong.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.administrator.huashixingkong.R;
import com.example.administrator.huashixingkong.adapter.ViewPagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class FragmentDiscuss extends Fragment {
    private ViewPager viewPager;
    private ArrayList<Fragment> lists = new ArrayList<Fragment>();
    private ViewPagerAdapter viewPagerAdapter;
    private TextView textView1;
    private TextView textView2;
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_fragment_discuss,container,false);
        viewPager = (ViewPager) view.findViewById(R.id.viewPager);

        textView1 = (TextView) view.findViewById(R.id.fragment_discuss_title1);
        textView2 = (TextView) view.findViewById(R.id.fragment_discuss_title2);
        DiscussViewFragment fragmentPage1 = DiscussViewFragment.newInstance("a");
        lists.add(fragmentPage1);
        DiscussViewFragment fragmentPage2 = DiscussViewFragment.newInstance("b");
        lists.add(fragmentPage2);

        viewPagerAdapter = new ViewPagerAdapter(getChildFragmentManager(),lists);
        viewPager.setAdapter(viewPagerAdapter);
        textView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewPager.setCurrentItem(0);
            }
        });

        textView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewPager.setCurrentItem(1);
            }
        });

        return view;
    }

}
