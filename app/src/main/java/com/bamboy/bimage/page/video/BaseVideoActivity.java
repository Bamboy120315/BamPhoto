package com.bamboy.bimage.page.video;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowInsets;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;

import com.bamboy.bimage.util.OrientationUtil;

public abstract class BaseVideoActivity extends AppCompatActivity {

    protected OrientationUtil mOrientationUtil;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 处理屏幕旋转
        mOrientationUtil = new OrientationUtil(this,
                (int orientationState) -> orientationChange(orientationState));

        // 状态栏设成透明
        setStatusBarTransparent();
    }

    /**
     * 状态栏设成透明
     */
    protected void setStatusBarTransparent() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            View decorView = getWindow().getDecorView();
            decorView.setOnApplyWindowInsetsListener((v, insets) -> {
                WindowInsets defaultInsets = v.onApplyWindowInsets(insets);
                return defaultInsets.replaceSystemWindowInsets(
                        defaultInsets.getSystemWindowInsetLeft(),
                        0,
                        defaultInsets.getSystemWindowInsetRight(),
                        defaultInsets.getSystemWindowInsetBottom());
            });
            ViewCompat.requestApplyInsets(decorView);
            getWindow().setStatusBarColor(ContextCompat.getColor(this, android.R.color.transparent));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        // 开启屏幕旋转监听
        mOrientationUtil.startOrientation(true);
    }

    @Override
    protected void onPause() {
        super.onPause();

        // 关闭屏幕旋转监听
        mOrientationUtil.startOrientation(false);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // 关闭屏幕旋转监听
        mOrientationUtil.startOrientation(false);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        // 解决状态栏下拉后屏幕旋转监听开关的问题
        mOrientationUtil.startOrientation(hasFocus);
    }

    /*@Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Log.i("-=-=-=-=", "onConfigurationChanged");
        // 解决状态栏下拉后屏幕旋转监听开关的问题
        // mOrientationUtil.startOrientation(this);
    }*/

    /**
     * 屏幕方向改变
     *
     * @param orientationState 【1：ORIENTATION_VERTICAL：竖屏】
     *                         【2：ORIENTATION_HORIZONTAL_LEFT：左横屏】
     */
    protected abstract void orientationChange(int orientationState);
}
