package com.bamboy.bimage.util;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.DecelerateInterpolator;

/**
 * Created by Administrator on 2018/11/20/020.  动画工具类
 */

public class AnimationUtil {

    private static AnimationUtil mInstance = null;

    private AnimationUtil() {
    }

    public static AnimationUtil getInstance() {
        synchronized ("") {
            if (mInstance == null) {
                mInstance = new AnimationUtil();
            }
            return mInstance;
        }
    }

    /**
     * @param view 身份向下隐藏控制条
     */
    public void roleBackView(View view) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(view, "translationY", 0);
        animator.setDuration(300);
        animator.start();
    }

    /**
     * @param view 身份向上显示控制条
     */
    public void rolemoveUpView(View view) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(view, "translationY", -100);
        animator.setDuration(300);
        animator.start();
    }

    /**
     * 揭露工具箱
     */
    public void revealContent(View view, boolean isShow, int duration) {
        int bX = view.getWidth() / 2;
        int by = view.getHeight() / 2;

        // 结束时半径
        float endRadius = (float) Math.hypot(bX, by);

        // 弹窗圆形动画
        Animator animator = ViewAnimationUtils.createCircularReveal(view,
                bX,
                by,
                isShow ? 0 : endRadius,
                isShow ? endRadius : 0);
        animator.setDuration(duration);
        animator.setInterpolator(new DecelerateInterpolator(2f));
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                if (isShow) {
                    view.setAlpha(1f);
                } else {
                    view.setAlpha(0.99f);
                }
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                if (!isShow) {
                    view.setVisibility(View.GONE);
                }
            }
        });
        animator.start();
    }
}
