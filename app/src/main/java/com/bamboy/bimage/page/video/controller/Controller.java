package com.bamboy.bimage.page.video.controller;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bamboy.bimage.util.UIUtil;
import com.bamboy.bimage.view.ijkplayer.IjkPlayer;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public abstract class Controller extends ViewGroup {
    protected Context mContext;
    /**
     * 播放器
     */
    protected IjkPlayer mIjk_video;
    /**
     * 进度
     */
    public TextView tv_progress;
    /**
     * 视频时长
     */
    public TextView tv_duration;
    /**
     * 时间格式化
     */
    private SimpleDateFormat mFormatter;
    /**
     * 视频时长
     */
    public long mDuration;

    public Controller(Context context, IjkPlayer ijk_video) {
        super(context);
        init(context, ijk_video);
    }

    @Override
    protected void onLayout(boolean b, int i, int i1, int i2, int i3) {

    }

    /**
     * 初始化
     *
     * @param context
     */
    private void init(Context context, IjkPlayer ijk_video) {
        mContext = context;
        mIjk_video = ijk_video;
    }

    /**
     * 更新视频时长
     */
    public void updateDuration(long duration) {
        if (mIjk_video == null) {
            UIUtil.setText(tv_progress, "00:00");
            UIUtil.setText(tv_duration, "00:00");
            return;
        }
        duration = Math.max(duration, mIjk_video.getDuration());

        if (duration <= 0) {
            UIUtil.setText(tv_progress, "00:00");
            UIUtil.setText(tv_duration, "00:00");
            return;
        }

        if (mDuration == duration) {
            return;
        }

        mDuration = duration;

        // 格式
        String pattern = "mm:ss";
        long time = mDuration / 1000;
        if ((int) (time / (60 * 60)) > 0) {
            // 时长超过1小时，则用添加小时格式
            pattern = "HH:mm:ss";
        }

        // 格式化
        mFormatter = new SimpleDateFormat(pattern);
        // 设置时区
        mFormatter.setTimeZone(TimeZone.getTimeZone("Etc/GMT-0"));

        if (tv_duration != null) {
            // 视频时长字符串
            String durationStr = mFormatter.format(new Date(mDuration));
            tv_duration.setText(durationStr);
        }
    }

    /**
     * 更新视频进度
     */
    public void updateProgress(long progress) {
        if (tv_progress == null || mDuration <= 0) {
            UIUtil.setText(tv_progress, "00:00");
            UIUtil.setText(tv_duration, "00:00");
            return;
        }

        String progressStr = mFormatter.format(new Date(progress));
        tv_progress.setText(progressStr);
    }

    /**
     * 更新文件名
     *
     * @param fileName 文件名
     */
    public abstract void onUpdateFileName(String fileName);

    /**
     * 播放
     */
    public abstract void onPlay();

    /**
     * 暂停
     */
    public abstract void onPause();

    /**
     * 播放上一个
     */
    public abstract void onPlayPrevious();

    /**
     * 播放下一个
     */
    public abstract void onPlayLatter();

    /**
     * 进度控制
     *
     * @param millisecond 进度
     */
    public abstract void onProgress(long millisecond);

    /**
     * 更新进度
     *
     * @param millisecond 进度
     * @param duration    视频时长
     */
    public abstract void onUpdateProgress(long millisecond, long duration);

    /**
     * 倍速
     *
     * @param speed 倍速
     */
    public abstract void onSpeed(float speed);

    /**
     * 更新倍速显示
     *
     * @param speed 倍速
     */
    public abstract void onUpdateSpeed(float speed);
}
