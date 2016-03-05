package com.example.administrator.huashixingkong.myview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;

import com.example.administrator.huashixingkong.R;


public class DiscussView extends FrameLayout{

    public DiscussView(Context context) {
        this(context,null);
    }

    public DiscussView(Context context, AttributeSet attrs) {
        this(context,attrs,0);
    }

    public DiscussView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater.from(context).inflate(R.layout.view_discuss, this);
    }
}
