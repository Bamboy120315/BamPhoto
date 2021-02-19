package com.bamboy.bimage.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

public class BmViewPager extends ViewPager {

    public BmViewPager(@NonNull Context context) {
        super(context);
    }

    public BmViewPager(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    //重写该方法来解决冲突问题
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        try {
            return super.onInterceptTouchEvent(ev);
        } catch (IllegalArgumentException e) {
            //写自己要处理的error包括报错日志
            e.printStackTrace();
            Log.e("TAG", "onInterceptTouchEvent: ");

            return false;
        }
    }
}
