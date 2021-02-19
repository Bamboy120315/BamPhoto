package com.bamboy.bimage.util;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowInsets;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;

import java.lang.reflect.Field;

public class UIUtil {

    /**
     * 设置Button的文案
     *
     * @param button View
     * @param text   文案
     */
    public static void setText(Button button, String text) {
        setText(button, text, true);
    }

    /**
     * 设置Button的文案
     *
     * @param button View
     * @param text   文案
     * @param isGone 为空时是否隐藏
     */
    public static void setText(Button button, String text, boolean isGone) {
        setText((TextView) button, text, isGone);
    }

    /**
     * 设置TextView的文案
     *
     * @param textView View
     * @param text     文案
     */
    public static void setText(TextView textView, String text) {
        setText(textView, text, true);
    }

    /**
     * 设置TextView的文案
     *
     * @param textView View
     * @param text     文案
     * @param isGone   为空时是否隐藏
     */
    public static void setText(TextView textView, String text, boolean isGone) {
        if (textView == null)
            return;

        if (text != null) {
            // 设置Text
            textView.setText(text);

            // 如果TextView没有显示，则显示
            if (textView.getVisibility() != View.VISIBLE)
                textView.setVisibility(View.VISIBLE);

        } else {
            // 设置TextView为空
            textView.setText("");

            // 如果TextView是显示状态，并且允许其隐藏，则显示
            if (textView.getVisibility() != View.GONE && isGone)
                textView.setVisibility(View.GONE);
        }
    }

    /**
     * 设置TextView的文案
     *
     * @param textView View
     * @param text     文案
     */
    public static void setTextToAlpha(TextView textView, String text) {
        if (textView == null)
            return;

        if (text != null) {
            // 设置Text
            textView.setText(text);

            // 如果TextView没有显示，则显示
            if (textView.getAlpha() != 1)
                textView.
                        animate()
                        .alpha(1)
                        .setDuration(150);

        } else {
            // 设置TextView为空
            textView.setText("");

            // 如果TextView是显示状态，则隐藏
            if (textView.getAlpha() != 0)
                textView.setAlpha(0);
        }
    }

    /**
     * 根据Color资源ID 获取颜色值
     *
     * @param context
     * @param colorResourceId Color资源ID
     * @return
     */
    public static int getColor(Context context, int colorResourceId) {
        if (context == null) {
            return 0x00000000;
        }

        return context.getResources().getColor(colorResourceId);
    }

    /**
     * 把状态栏设成透明
     */
    public static void setStatusBarTransparent(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return;
        }
        View decorView = activity.getWindow().getDecorView();
        decorView.setOnApplyWindowInsetsListener((v, insets) -> {
            WindowInsets defaultInsets = v.onApplyWindowInsets(insets);
            return defaultInsets.replaceSystemWindowInsets(
                    defaultInsets.getSystemWindowInsetLeft(),
                    0,
                    defaultInsets.getSystemWindowInsetRight(),
                    defaultInsets.getSystemWindowInsetBottom());
        });
        ViewCompat.requestApplyInsets(decorView);
        activity.getWindow().setStatusBarColor(ContextCompat.getColor(activity, android.R.color.transparent));
    }

    /**
     * 显示隐藏状态栏，全屏不变，只在有全屏时有效
     * @param enable
     */
    public static void setStatusBarVisibility(Activity activity, boolean enable) {
        WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
        if (enable) {
            lp.flags |= WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN;
        } else {
            lp.flags &= (~WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
        }
        activity.getWindow().setAttributes(lp);
        activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
    }

    /**
     * 设置是否全屏
     * @param enable
     */
    public static void setFullScreen(Activity activity, boolean enable) {
        WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
        if (enable) {
            lp.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
        } else {
            lp.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
        activity.getWindow().setAttributes(lp);
        activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
    }

    /**
     * 设置沉浸TitleBar
     */
    public static boolean setImmerseTitleBar(Activity activity) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                Window window = activity.getWindow();
                window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                        WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

                // Android 7.0以上 去除状态栏半透明底色
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    try {
                        Class decorViewClazz = Class.forName("com.android.internal.policy.DecorView");
                        Field field = decorViewClazz.getDeclaredField("mSemiTransparentStatusBarColor");
                        field.setAccessible(true);
                        field.setInt(activity.getWindow().getDecorView(), Color.TRANSPARENT);  //改为透明
                    } catch (Exception e) {
                        e.printStackTrace();
                    } catch (Error e) {
                        e.printStackTrace();
                    }
                }

                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
        } catch (Error e) {
        }
        return false;
    }


    /**
     * 获取状态栏高度
     *
     * @param context
     * @return 状态栏高度
     */
    public static int getBarHeight(Context context) {
        try {
            int barHeight = 0;

            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
                barHeight = 0;
            } else {
                int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
                if (resourceId > 0) {
                    barHeight = context.getResources().getDimensionPixelSize(resourceId);
                }
            }
            return barHeight;
        } catch (Exception e) {
            return 0;
        } catch (Error e) {
            return 0;
        }
    }

    /**
     * 获取屏幕截屏 【不包含状态栏】
     *
     * @param activity
     * @param containTopBar 是否包含状态栏
     * @return
     */
    public static Bitmap getScreenshot(Activity activity, boolean containTopBar) {
        try {
            Window window = activity.getWindow();
            View view = window.getDecorView();
            view.setDrawingCacheEnabled(true);
            view.buildDrawingCache(true);
            Bitmap bmp1 = view.getDrawingCache();
            /**
             * 除去状态栏和标题栏
             **/
            int height = containTopBar ? 0 : getBarHeight(activity);
            return Bitmap.createBitmap(bmp1, 0, height, bmp1.getWidth(), bmp1.getHeight() - height);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 获取Activity截图
     *
     * @param activity
     * @return bitmap 截图
     */
    public static Bitmap getDrawing(Activity activity) {
        View view = ((ViewGroup) activity.findViewById(android.R.id.content)).getChildAt(0);
        return getDrawing(view);
    }

    /**
     * 获取View截图
     *
     * @param view
     * @return 截图
     */
    public static Bitmap getDrawing(View view) {
        try {
            view.setDrawingCacheEnabled(true);
            Bitmap tBitmap = view.getDrawingCache();
            // 拷贝图片，否则在setDrawingCacheEnabled(false)以后该图片会被释放掉
            tBitmap = tBitmap.createBitmap(tBitmap);
            view.setDrawingCacheEnabled(false);
            return tBitmap;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
