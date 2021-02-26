package com.bamboy.bimage.view.dialog;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.bamboy.bimage.R;
import com.bamboy.bimage.util.MediaUtil;
import com.bamboy.bimage.util.UIUtil;
import com.bamboy.bimage.view.freedom.smartrefresh.util.DensityUtil;
import com.bamboy.bimage.view.freedom.smartrefresh.view.SlopeProgress;

import java.io.File;

import static com.bamboy.bimage.util.FileInfoManager.LOCK_FILE_POSTFIX;

public class LockFileDialog extends Dialog {

    /**
     * 文件夹路径
     */
    private String mFiledir;
    /**
     * 执行逻辑
     */
    private Runnable mRun;
    /**
     * 标题
     */
    private TextView tv_title;
    /**
     * 内容
     */
    private TextView tv_context;
    /**
     * 只隐藏新文件按钮
     */
    private TextView tv_close;
    /**
     * 隐藏所有文件按钮
     */
    private TextView tv_all_lock;

    /**
     * 构造
     *
     * @param context
     */
    public LockFileDialog(@NonNull Context context, String filedir, Runnable run) {
        super(context);
        mFiledir = filedir;
        mRun = run;
        init();
    }

    /**
     * 初始化
     */
    private void init() {
        LayoutInflater inflater = (LayoutInflater) getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.dialog_lock_file, null);
        addContentView(layout, new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        tv_title = layout.findViewById(R.id.tv_title);
        tv_context = layout.findViewById(R.id.tv_context);
        tv_close = layout.findViewById(R.id.tv_close);
        tv_all_lock = layout.findViewById(R.id.tv_all_lock);

        // 初始化事件
        initListener();
        // 设置 点击外部不关闭dialog
        setCanceledOnTouchOutside(false);
        // 设置 返回键不关闭dialog
        setCancelable(false);

        setContentView(layout);
    }

    /**
     * 初始化事件
     */
    private void initListener() {
        tv_all_lock.setOnClickListener((View view) -> allLock());
        tv_close.setOnClickListener((View view) -> {
            tv_close.setClickable(false);
            tv_all_lock.setClickable(false);
            dismiss();
        });
    }

    /**
     * 全部加密
     */
    private void allLock() {
        // 隐藏View
        hideView();

        new Thread(() -> {
            File filedir = new File(mFiledir);
            File[] files = filedir.listFiles();

            for (File file : files) {
                String fileName = file.getName();
                // 文件名
                String name = fileName.substring(0, fileName.lastIndexOf("."));
                // 后缀
                String suffix = fileName.substring(fileName.lastIndexOf(".") + 1);

                // 先过滤非图片视频
                if (!MediaUtil.getInstance().containsImageType(suffix)
                        && !MediaUtil.getInstance().containsVideoType(suffix)) {
                    continue;
                }

                String newFileName = "";
                if (!name.contains(LOCK_FILE_POSTFIX)) {
                    newFileName = name + LOCK_FILE_POSTFIX + "." + suffix;
                } else {
                    newFileName = name.substring(0, fileName.lastIndexOf(LOCK_FILE_POSTFIX)) + "." + suffix;
                }

                // 重命名
                File newFile = new File(mFiledir, newFileName);
                file.renameTo(newFile);
            }

            tv_title.post(() -> {
                mRun.run();
                dismiss();
            });
        }).start();
    }

    /**
     * 隐藏View
     */
    private void hideView() {
        tv_close.setClickable(false);
        tv_all_lock.setClickable(false);

        // 更新文字
        tv_title.animate()
                .alpha(0f)
                .translationY(-20)
                .setDuration(100)
                .setStartDelay(200)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {

                        tv_title.setText("正在隐藏…");

                        tv_title.setTranslationY(20);
                        tv_title.post(() -> tv_title.animate()
                                .alpha(1f)
                                .translationY(0)
                                .setDuration(100)
                                .setStartDelay(0)
                                .setListener(null));
                    }
                });

        // 隐藏内容
        tv_context.animate()
                .alpha(0)
                .scaleX(0.3f)
                .scaleY(0.3f)
                .setDuration(200)
                .setStartDelay(200)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        super.onAnimationStart(animation);
                        // 初始化进度条
                        RelativeLayout rootView = (RelativeLayout) tv_title.getParent();
                        SlopeProgress sp_progress = new SlopeProgress(getContext());
                        sp_progress.setAlpha(0.01f);
                        int height = DensityUtil.dp2px(50);
                        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(height, height);
                        params.addRule(RelativeLayout.CENTER_IN_PARENT);
                        sp_progress.setLayoutParams(params);

                        sp_progress.setRingColor(UIUtil.getColor(getContext(), R.color.colorPrimary));
                        sp_progress.setMaxProgress(100);
                        sp_progress.setProgress(0);
                        sp_progress.setLine(height / 8.2f);

                        // 添加到弹窗
                        rootView.addView(sp_progress);

                        // 显示进度条
                        sp_progress.post(() -> {
                            sp_progress.startAnim();
                            sp_progress.animate()
                                    .alpha(1)
                                    .setDuration(200)
                                    .setStartDelay(100)
                                    .setStartDelay(200);
                        });
                    }
                });

        // 隐藏按钮
        tv_close.setPivotX(1);
        tv_close.animate()
                .alpha(0)
                .scaleX(0f)
                .scaleY(0.3f)
                .setDuration(200)
                .setStartDelay(200);

        // 隐藏按钮
        tv_all_lock.setPivotX(1);
        tv_all_lock.animate()
                .alpha(0)
                .scaleX(0f)
                .scaleY(0.3f)
                .setDuration(200)
                .setStartDelay(200);
    }

}
