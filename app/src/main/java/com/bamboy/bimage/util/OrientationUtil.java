package com.bamboy.bimage.util;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.hardware.SensorManager;
import android.provider.Settings;
import android.util.Log;
import android.view.OrientationEventListener;

public class OrientationUtil {

    /**
     * 竖屏
     */
    public static final int ORIENTATION_VERTICAL = 1;
    /**
     * 左横屏
     */
    public static final int ORIENTATION_HORIZONTAL_LEFT = 2;
    /**
     * 右横屏
     */
    public static final int ORIENTATION_HORIZONTAL_RIGHT = 3;

    /**
     * 上次的方向
     */
    private int lastOrientation = 0;

    /**
     * 旋转监听
     */
    private OrientationEventListener mOrientationListener;

    /**
     * 屏幕旋转的回调
     */
    private OrientationCallback mOrientationCallback;

    /**
     * 构造
     *
     * @param orientationCallback 屏幕旋转的回调
     */
    public OrientationUtil(Context context, OrientationCallback orientationCallback) {
        mOrientationCallback = orientationCallback;
        initOrientation(context);
    }

    /**
     * 处理屏幕旋转
     */
    private void initOrientation(Context context) {
        //屏幕方向监听
        mOrientationListener = new OrientationEventListener(context,
                SensorManager.SENSOR_DELAY_NORMAL) {
            @Override
            public void onOrientationChanged(int orientation) {
                // 平放
                if (orientation == -1) {
                    return;
                }

                if (orientation > 350 || orientation < 10) { //0度
                    // 竖屏
                    orientation(ORIENTATION_VERTICAL);
                } else if (orientation > 260 && orientation < 290) { //270度
                    // 左横屏
                    orientation(ORIENTATION_HORIZONTAL_LEFT);
                } else if (orientation > 70 && orientation < 100) { //90度
                    // 右横屏
                    orientation(ORIENTATION_HORIZONTAL_RIGHT);
                /*} else if (orientation > 170 && orientation < 190) { //180度
                    tv.setText("反竖屏");*/
                } else {
                    return;
                }
            }
        };

        mOrientationListener.enable();
    }

    /**
     * 方向改变
     *
     * @param orientation
     */
    private void orientation(int orientation) {
        if (lastOrientation == orientation || mOrientationCallback == null) {
            return;
        }
        lastOrientation = orientation;

        switch (orientation) {
            case ORIENTATION_VERTICAL:              // 竖屏
            case ORIENTATION_HORIZONTAL_LEFT:       // 左横屏
            case ORIENTATION_HORIZONTAL_RIGHT:      // 右横屏
                mOrientationCallback.orientationChange(orientation);
                break;
        }
    }

    /**
     * 是否开始旋转监听
     *
     * @param open
     */
    public void startOrientation(boolean open) {
        if (open) {
            mOrientationListener.enable();
        } else {
            mOrientationListener.disable();
        }
    }

    /**
     * 解决状态栏下拉后屏幕旋转监听开关的问题
     *
     * @param context
     */
    public void startOrientation(Context context) {
        if (mOrientationListener == null) {
            return;
        }
        try {
            if (Settings.System.getInt(context.getContentResolver(), Settings.System.ACCELEROMETER_ROTATION) == 0) {
                Log.i("-=-=-=-=", "startOrientation  false");
                startOrientation(false);
            } else {
                Log.i("-=-=-=-=", "startOrientation  true");
                startOrientation(true);
            }
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * 设置屏幕方向
     *
     * @param activity
     * @param orientation 方向
     *                    【1：ORIENTATION_VERTICAL：竖屏】
     *                    【2：ORIENTATION_HORIZONTAL_LEFT：左横屏】
     *                    【3：ORIENTATION_HORIZONTAL_RIGHT：右横屏】
     */
    public void setRequestedOrientation(Activity activity, int orientation) {
        switch (orientation) {
            case ORIENTATION_VERTICAL:
                // 竖屏
                activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                break;

            case ORIENTATION_HORIZONTAL_LEFT:
                // 左横屏
                activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                break;

            case ORIENTATION_HORIZONTAL_RIGHT:
                // 右横屏
                activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
                break;
        }
    }

    /**
     * 按钮回调
     */
    public interface OrientationCallback {
        /**
         * 方向改变
         *
         * @param orientationState 【1：ORIENTATION_VERTICAL：竖屏】
         *                         【2：ORIENTATION_HORIZONTAL_LEFT：左横屏】
         *                         【3：ORIENTATION_HORIZONTAL_RIGHT：右横屏】
         */
        void orientationChange(int orientationState);
    }
}
