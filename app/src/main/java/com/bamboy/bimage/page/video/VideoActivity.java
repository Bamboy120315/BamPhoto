package com.bamboy.bimage.page.video;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;

import com.bamboy.bimage.R;
import com.bamboy.bimage.page.media.bean.FileBean;
import com.bamboy.bimage.util.MediaUtil;
import com.bamboy.bimage.util.NullUtil;
import com.bamboy.bimage.util.UIUtil;
import com.shuyu.gsyvideoplayer.GSYVideoManager;
import com.shuyu.gsyvideoplayer.listener.GSYMediaPlayerListener;

import java.util.ArrayList;
import java.util.List;

public class VideoActivity extends BaseVideoActivity implements GSYMediaPlayerListener {

    /**
     * 数据列表
     */
    private static List<FileBean> mFileList;
    /**
     * 当前文件
     */
    private static FileBean mCurrentFile;
    /**
     * 当前视频文件的宽高比
     */
    private Bitmap mVideoBitmap = null;
    /**
     * 索引
     */
    private int mPosition;

    /**
     * 容器
     */
    private RelativeLayout rl_root;
    /**
     * 播放器
     */
    private NormalVideoPlayer gsy_video;

    /**
     * 启动界面
     *
     * @param context
     * @param fileList
     * @param position
     */
    public static void startActivity(Context context, List<FileBean> fileList, int position) {
        mFileList = fileList;
        Intent intent = new Intent(context, VideoActivity.class);
        intent.putExtra("position", position);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_video);

        // 匹配View
        findView();

        // 处理数据
        init();
    }

    @Override
    protected void onPause() {
        super.onPause();
        gsy_video.onVideoPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        gsy_video.onVideoResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //释放所有
        gsy_video.setVideoAllCallBack(null);
        GSYVideoManager.releaseAllVideos();
    }

    /**
     * 匹配View
     */
    private void findView() {
        rl_root = findViewById(R.id.rl_root);
        gsy_video = findViewById(R.id.gsy_video);

        // 初始化控制器
        // initController(rl_root, gsy_video);
    }

    /**
     * 处理数据
     */
    private void init() {
        gsy_video.getGSYVideoManager().setListener(this);
        //设置返回键
        gsy_video.getBackButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        rl_root.post(() -> initVideo(-1));
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        // 监听视图，等转屏后父容器绘制完成，重进计算播放器尺寸
        /*ViewTreeObserver observer = rl_root.getViewTreeObserver();
        observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                rl_root.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                // 处理播放器尺寸
                initPlayerSize(rl_root.getWidth(), rl_root.getHeight(), mVideoBitmap.getWidth(), mVideoBitmap.getHeight());
            }
        });*/
    }

    /**
     * 处理视频 准备播放
     *
     * @param position 索引
     */
    private void initVideo(int position) {

        if (position < 0) {
            position = getIntent().getIntExtra("position", 0);
        }
        mPosition = position;

        if (mFileList == null) {
            mFileList = new ArrayList<>();
        }

        if (mPosition >= mFileList.size()) {
            mPosition = mFileList.size() - 1;
        }

        mCurrentFile = mFileList.get(mPosition);

        play();
    }

    /**
     * 播放
     */
    private void play() {

        mVideoBitmap = MediaUtil.getInstance().getFrame(mCurrentFile.getPath(), 0);

        new Thread(() -> {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            rl_root.post(() -> {
                // 处理播放器尺寸
                // initPlayerSize(mVideoBitmap.getWidth(), mVideoBitmap.getHeight());

                // 播放当前文件
                gsy_video.setUp(mCurrentFile.getPath(), true, mCurrentFile.getName());

                // 开始播放
                gsy_video.startPlayLogic();

                if (mController != null) {
                    // 更新视频文件名
                    mController.onUpdateFileName(mCurrentFile.getName());

                    // 更新视频进度
                    mController.updateProgress(0);
                }
            });
        }).start();
    }

    /**
     * 处理播放器尺寸
     */
    private void initPlayerSize(float videoWidth, float videoHeight) {
        initPlayerSize(rl_root.getWidth(), rl_root.getHeight(), videoWidth, videoHeight);
    }

    /**
     * 处理播放器尺寸
     */
    private void initPlayerSize(float rootWidth, float rootHeight, float videoWidth, float videoHeight) {
        // X坐标
        int left = 0;
        // Y坐标
        int top = 0;

        // 视频宽高比
        float videoScale = videoWidth / videoHeight;

        // 视频与容器的宽度比
        float scaleWidth = videoWidth / rootWidth;
        // 视频与容器的高度比
        float scaleHeight = videoHeight / rootHeight;

        if (scaleWidth > scaleHeight) {
            videoWidth = rootWidth;
            videoHeight = videoWidth / videoScale;
            left = 0;
            top = (int) ((rootHeight - videoHeight) / 2);
        } else {
            videoHeight = rootHeight;
            videoWidth = videoHeight * videoScale;
            left = (int) ((rootWidth - videoWidth) / 2);
            top = 0;
        }

        /*Log.i("-=-=-=-= 设置宽高", "设置后："
                + "  rootWidth：" + rootWidth
                + "  rootHeight：" + rootHeight
                + "  videoWidth：" + videoWidth
                + "  videoHeight：" + videoHeight
        );*/

        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) gsy_video.getLayoutParams();
        layoutParams.width = (int) videoWidth;
        layoutParams.height = (int) videoHeight;
        layoutParams.leftMargin = left;
        layoutParams.topMargin = top;
        gsy_video.setLayoutParams(layoutParams);
    }

    // ============================================================================================
    // ------------------------------ 以下是播放器的回调 -------------------------------------
    // ============================================================================================


    @Override
    public void onPrepared() {
        Log.i("-=-=-=-=  播放器回调", "onPrepared");
    }

    @Override
    public void onAutoCompletion() {
        Log.i("-=-=-=-=  播放器回调", "onAutoCompletion");
    }

    @Override
    public void onCompletion() {
        Log.i("-=-=-=-=  播放器回调", "onCompletion");
    }

    @Override
    public void onBufferingUpdate(int percent) {
        Log.i("-=-=-=-=  播放器回调", "onBufferingUpdate  " + percent);
    }

    @Override
    public void onSeekComplete() {
        Log.i("-=-=-=-=  播放器回调", "onSeekComplete");
    }

    @Override
    public void onError(int what, int extra) {
        Log.i("-=-=-=-=  播放器回调", "onError  what：" + what + "  extra：" + extra);
    }

    @Override
    public void onInfo(int what, int extra) {

        String info = "";
        switch (what) {
            case 3:
                // 准备渲染
                info = "准备渲染";

                // 更新视频时长
                if (mController != null) {
                    mController.updateDuration(mCurrentFile.getDuration());
                }
                break;

            case 100:
                // 视频中断，一般是视频源异常或者不支持的视频类型。
                info = "视频中断，一般是视频源异常或者不支持的视频类型。";
                break;

            case 200:
                // 数据错误没有有效的回收
                info = "数据错误没有有效的回收";
                break;

            case 701:
                // 开始缓冲
                info = "开始缓冲";
                break;

            case 702:
                // 缓冲结束
                info = "缓冲结束";
                break;

            case 10000:
                // 一般是视频源有问题或者数据格式不支持，比如音频不是AAC之类的
                info = "一般是视频源有问题或者数据格式不支持，比如音频不是AAC之类的";
                break;

            case 10001:
                // 视频选择信息
                info = "视频选择信息";
                break;

            default:
                // 其他
                break;
        }

        // Log.i("-=-=-=-= Player", "onInfo   i：" + i + " " + info + "    i1：" + i1);
        Log.i("-=-=-=-=  播放器回调", "onInfo  what：" + what + "  extra：" + extra + (NullUtil.isNull(info) ? "" : ("  " + info)));
    }

    @Override
    public void onVideoSizeChanged() {
        Log.i("-=-=-=-=  播放器回调", "onVideoSizeChanged");
    }

    @Override
    public void onBackFullscreen() {
        Log.i("-=-=-=-=  播放器回调", "onBackFullscreen");
    }

    @Override
    public void onVideoPause() {
        Log.i("-=-=-=-=  播放器回调", "onVideoPause");
    }

    @Override
    public void onVideoResume() {
        Log.i("-=-=-=-=  播放器回调", "onVideoResume");
    }

    @Override
    public void onVideoResume(boolean seek) {
        Log.i("-=-=-=-=  播放器回调", "onVideoResume  seek：" + seek);
    }
}