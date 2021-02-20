package com.bamboy.bimage.page.video;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowInsets;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;

import com.bamboy.bimage.page.video.controller.Controller;
import com.bamboy.bimage.page.video.controller.NormalController;
import com.bamboy.bimage.util.OrientationUtil;
import com.shuyu.gsyvideoplayer.video.StandardGSYVideoPlayer;

import static com.bamboy.bimage.util.OrientationUtil.ORIENTATION_HORIZONTAL_LEFT;
import static com.bamboy.bimage.util.OrientationUtil.ORIENTATION_HORIZONTAL_RIGHT;
import static com.bamboy.bimage.util.OrientationUtil.ORIENTATION_VERTICAL;

public abstract class BaseVideoActivity extends AppCompatActivity {

    protected OrientationUtil mOrientationUtil;
    /**
     * 控制器
     */
    protected Controller mController;

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

    /**
     * 屏幕方向改变
     *
     * @param orientationState 【1：ORIENTATION_VERTICAL：竖屏】
     *                         【2：ORIENTATION_HORIZONTAL_LEFT：左横屏】
     */
    protected void orientationChange(int orientationState){
        switch (orientationState) {
            case ORIENTATION_VERTICAL:
                // 竖屏

                // 旋转屏幕
                mOrientationUtil.setRequestedOrientation(this, ORIENTATION_VERTICAL);
                break;

            case ORIENTATION_HORIZONTAL_LEFT:
                // 左横屏

                // 旋转屏幕
                mOrientationUtil.setRequestedOrientation(this, ORIENTATION_HORIZONTAL_LEFT);
                break;

            case ORIENTATION_HORIZONTAL_RIGHT:
                // 右横屏

                // 旋转屏幕
                mOrientationUtil.setRequestedOrientation(this, ORIENTATION_HORIZONTAL_RIGHT);
                break;
        }
    }

    /**
     * 初始化控制器
     */
    protected void initController(RelativeLayout rl_root, StandardGSYVideoPlayer gsy_video) {
        mController = new NormalController(this, rl_root, gsy_video);
    }
}
