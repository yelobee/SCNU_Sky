package com.example.administrator.huashixingkong.fragment;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.administrator.huashixingkong.R;
import com.example.administrator.huashixingkong.adapter.ViewPagerAdapter;

import java.util.ArrayList;

public class FragmentDiscuss extends Fragment {
    private ViewPager viewPager;
    private ArrayList<Fragment> lists = new ArrayList<>();
    private ViewPagerAdapter viewPagerAdapter;
    private TextView textView1;
    private TextView textView2;
    private ImageView imageView;
    private Bitmap cursor;
    private int offSet;// 动画图片偏移量
    private int currentItem;// 当前页卡编号
    private Matrix matrix = new Matrix();
    private int bmWidth;// 动画图片宽度
    private Animation animation;
    private View view;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if(view == null){
            view = inflater.inflate(R.layout.fragment_fragment_discuss,container,false);
            viewPager = (ViewPager) view.findViewById(R.id.viewPager);

            textView1 = (TextView) view.findViewById(R.id.fragment_discuss_title1);
            textView2 = (TextView) view.findViewById(R.id.fragment_discuss_title2);
            imageView = (ImageView) view.findViewById(R.id.cursor);

            DiscussViewFragment fragmentPage1 = DiscussViewFragment.newInstance(null);
            lists.add(fragmentPage1);
            CommentFragment fragmentPage2 = CommentFragment.newInstance(null,null);
            lists.add(fragmentPage2);

            initeCursor();

            viewPagerAdapter = new ViewPagerAdapter(getChildFragmentManager(),lists);
            viewPager.setAdapter(viewPagerAdapter);

            viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

                // 当滑动式，顶部的imageView是通过animation缓慢的滑动
                @Override
                public void onPageSelected(int arg0) {
                    switch (arg0) {
                        case 0:
                            if (currentItem == 1) {
                                animation = new TranslateAnimation(
                                        offSet * 2 + bmWidth, 0, 0, 0);
                            }
                            break;
                        case 1:
                            if (currentItem == 0) {
                                animation = new TranslateAnimation(0, offSet * 2
                                        + bmWidth, 0, 0);
                            }
                            break;
                    }
                    currentItem = arg0;
                    animation.setDuration(150); // 光标滑动速度
                    animation.setFillAfter(true);
                    imageView.startAnimation(animation);
                }

                @Override
                public void onPageScrolled(int arg0, float arg1, int arg2) {

                }

                @Override
                public void onPageScrollStateChanged(int arg0) {

                }
            });

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
            Log.d("abc","多了一个");
        }
        ViewGroup parent = (ViewGroup) view.getParent();
        if (parent != null) {
            parent.removeView(view);
        }
        return view;
    }

    private void initeCursor() {
        cursor = BitmapFactory.decodeResource(getResources(), R.drawable.cursor);
        bmWidth = cursor.getWidth();// 获取图片宽度

        DisplayMetrics dm;
        dm = getResources().getDisplayMetrics(); //获取屏幕参数

        offSet = (dm.widthPixels - 2 * bmWidth) / 4;
        matrix.setTranslate(offSet, 0);
        imageView.setImageMatrix(matrix); // 需要imageView的scaleType为matrix
        currentItem = 0;
    }

}
