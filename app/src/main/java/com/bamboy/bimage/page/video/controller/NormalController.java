package com.bamboy.bimage.page.video.controller;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bamboy.bimage.R;
import com.bamboy.bimage.util.UIUtil;
import com.bamboy.bimage.view.VideoSeekBar;
import com.bamboy.bimage.view.ijkplayer.IjkPlayer;
import com.bamboy.bimage.view.scrollpicker.StringScrollPicker;
import com.shuyu.gsyvideoplayer.video.StandardGSYVideoPlayer;
import com.shuyu.gsyvideoplayer.video.base.GSYVideoView;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class NormalController extends Controller {

    /**
     * 父容器
     */
    public ViewGroup mParent;
    /**
     * 容器
     */
    public View viewParent;
    /**
     * 上控制条
     */
    public View ll_top;
    /**
     * 下控制条
     */
    public View rl_bottom;
    /**
     * 返回按钮
     */
    public View iv_back;
    /**
     * 文件名
     */
    public TextView tv_name;
    /**
     * 倍速选择器
     */
    public StringScrollPicker sp_speed;
    /**
     * 进度拖动条
     */
    public VideoSeekBar nsp_progress;
    /**
     * 上一个
     */
    public View iv_previous;
    /**
     * 暂停/播放按钮
     */
    public ImageView iv_play;
    /**
     * 下一个
     */
    public View iv_latter;

    /**
     * 定时器，实时更新视频进度
     */
    private Timer mTimer;

    /**
     * 构造
     *
     * @param context
     * @param context
     * @param videoPlayer
     */
    public NormalController(Context context, ViewGroup parent, GSYVideoView videoPlayer) {
        super(context, videoPlayer);
        mParent = parent;

        // 初始化View
        initView();

        // 初始化倍速选择器
        initSpeed();

        // 拖动条监听
        nsp_progress.setProgressListener((VideoSeekBar progressBar, long progress) ->
                videoPlayer.seekTo(progress));
    }

    /**
     * 开始计时
     */
    private void startTimer() {
        stopTimer();

        mTimer = new Timer();
        mTimer.schedule(new TimerTask() {
            public void run() {
                viewParent.post(() -> updateProgress(-1));
            }
        }, 1000, 1000);
    }

    /**
     * 停止计时
     */
    private void stopTimer() {
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
    }

    /**
     * 初始化View
     */
    private void initView() {
        LayoutInflater inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        viewParent = inflater.inflate(R.layout.controller_normal, null);

        ll_top = viewParent.findViewById(R.id.ll_top);
        rl_bottom = viewParent.findViewById(R.id.rl_bottom);
        iv_back = viewParent.findViewById(R.id.iv_back);
        tv_name = viewParent.findViewById(R.id.tv_name);
        sp_speed = viewParent.findViewById(R.id.sp_speed);
        tv_progress = viewParent.findViewById(R.id.tv_progress);
        nsp_progress = viewParent.findViewById(R.id.nsp_progress);
        tv_duration = viewParent.findViewById(R.id.tv_duration);
        iv_previous = viewParent.findViewById(R.id.iv_previous);
        iv_play = viewParent.findViewById(R.id.iv_play);
        iv_latter = viewParent.findViewById(R.id.iv_latter);

        mParent.addView(this.viewParent);
    }

    /**
     * 初始化倍速选择器
     */
    private void initSpeed() {
        List<CharSequence> speedList = new ArrayList<>();
        speedList.add("0.25X");
        speedList.add("0.5X");
        speedList.add("0.75X");
        speedList.add("1X");
        speedList.add("1.25X");
        speedList.add("1.5X");
        speedList.add("2X");
        speedList.add("2.5X");
        speedList.add("3X");
        speedList.add("4X");

        sp_speed.setData(speedList);
        sp_speed.setSelectedPosition(3);
    }

    /**
     * 更新视频进度
     *
     * @param progress
     */
    public void updateProgress(long progress) {
        if (mVideoPlayer != null) {
            progress = Math.max(progress, mVideoPlayer.getCurrentPositionWhenPlaying());
        }
        super.updateProgress(progress);
        if (progress < 0) {
            UIUtil.setText(tv_progress, "00:00");
            UIUtil.setText(tv_duration, "00:00");
            return;
        }

        // 更新进度条
        nsp_progress.setProgress(progress, mDuration, false);
    }

    /**
     * 更新文件名
     *
     * @param fileName 文件名
     */
    @Override
    public void onUpdateFileName(String fileName) {
        tv_name.setText(fileName);
    }

    /**
     * 播放
     */
    @Override
    public void onPlay() {
        iv_play.setImageResource(R.drawable.ic_pause);

        // 开始计时
        // startTimer();
    }

    /**
     * 暂停
     */
    @Override
    public void onPause() {
        iv_play.setImageResource(R.drawable.ic_play);

        // 结束计时
         stopTimer();
    }

    /**
     * 播放上一个
     */
    @Override
    public void onPlayPrevious() {

    }

    /**
     * 播放下一个
     */
    @Override
    public void onPlayLatter() {

    }

    /**
     * 进度控制
     *
     * @param millisecond 进度
     */
    @Override
    public void onProgress(long millisecond) {

    }

    /**
     * 更新进度
     *
     * @param millisecond 进度
     * @param duration    视频时长
     */
    @Override
    public void onUpdateProgress(long millisecond, long duration) {

    }

    /**
     * 倍速
     *
     * @param speed 倍速
     */
    @Override
    public void onSpeed(float speed) {

    }

    /**
     * 更新倍速显示
     *
     * @param speed 倍速
     */
    @Override
    public void onUpdateSpeed(float speed) {

    }
}
