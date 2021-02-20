package com.bamboy.bimage.page.video;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;

import com.bamboy.bimage.R;
import com.bamboy.bimage.page.media.bean.FileBean;
import com.bamboy.bimage.page.video.controller.Controller;
import com.bamboy.bimage.page.video.controller.NormalController;
import com.bamboy.bimage.util.MediaUtil;
import com.bamboy.bimage.view.ijkplayer.IjkPlayer;
import com.bamboy.bimage.view.ijkplayer.VideoPlayerListener;

import java.util.ArrayList;
import java.util.List;

import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;

import static com.bamboy.bimage.util.OrientationUtil.ORIENTATION_HORIZONTAL_LEFT;
import static com.bamboy.bimage.util.OrientationUtil.ORIENTATION_HORIZONTAL_RIGHT;
import static com.bamboy.bimage.util.OrientationUtil.ORIENTATION_VERTICAL;

public class IJKVideoActivity extends BaseVideoActivity {

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
    private IjkPlayer ijk_video;
    /**
     * 控制器
     */
    private Controller mController;

    /**
     * 启动界面
     *
     * @param context
     * @param fileList
     * @param position
     */
    public static void startActivity(Context context, List<FileBean> fileList, int position) {
        mFileList = fileList;
        Intent intent = new Intent(context, IJKVideoActivity.class);
        intent.putExtra("position", position);
        context.startActivity(intent);
    }

    /**
     * 隐藏ActionBar和StatusBar
     */
    private void hideActionStatusBar() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // 全屏展示
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                // 全屏显示，隐藏状态栏和导航栏，拉出状态栏和导航栏显示一会儿后消失。
                getWindow().getDecorView().setSystemUiVisibility(
                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                                | View.SYSTEM_UI_FLAG_FULLSCREEN
                                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
            } else {
                // 全屏显示，隐藏状态栏
                getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // hideActionStatusBar();
        setContentView(R.layout.act_video_ijk);

        // 匹配View
        findView();

        // 处理数据
        init();
    }

    @Override
    protected void onDestroy() {
        ijk_video.stop();
        super.onDestroy();
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        // 监听视图，等转屏后父容器绘制完成，重进计算播放器尺寸
        ViewTreeObserver observer = rl_root.getViewTreeObserver();
        observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                rl_root.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                // 处理播放器尺寸
                initPlayerSize(rl_root.getWidth(), rl_root.getHeight(), mVideoBitmap.getWidth(), mVideoBitmap.getHeight());
            }
        });
    }

    /**
     * 屏幕方向改变
     *
     * @param orientationState 【1：ORIENTATION_VERTICAL：竖屏】
     *                         【2：ORIENTATION_HORIZONTAL_LEFT：左横屏】
     */
    @Override
    protected void orientationChange(int orientationState) {
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
     * 匹配View
     */
    private void findView() {
        rl_root = findViewById(R.id.rl_root);
        ijk_video = new IjkPlayer(this, rl_root);
    }

    /**
     * 处理数据
     */
    private void init() {
        mPosition = getIntent().getIntExtra("position", 0);

        if (mFileList == null) {
            mFileList = new ArrayList<>();
        }

        if (mPosition >= mFileList.size()) {
            return;
        }

        mCurrentFile = mFileList.get(mPosition);

        // 初始化播放器
        initIjkPlayer();

        // 初始化控制器
        initController();
    }

    /**
     * 初始化控制器
     */
    private void initController() {
        // mController = new NormalController(this, rl_root, g);
    }

    /**
     * 初始化播放器
     */
    private void initIjkPlayer() {
        //加载native库
        try {
            IjkMediaPlayer.loadLibrariesOnce(null);
            IjkMediaPlayer.native_profileBegin("libijkplayer.so");
        } catch (Exception e) {
            this.finish();
        }

        // 初始化播放器监听
        initIjkPlayerListener();

        // 播放
        rl_root.post(() -> play(mCurrentFile.getPath()));
    }

    /**
     * 初始化播放器监听
     */
    private void initIjkPlayerListener() {
        ijk_video.setListener(new VideoPlayerListener() {
            @Override
            public void onVideoSizeChanged(IMediaPlayer iMediaPlayer, int i, int i1, int i2, int i3) {
                /*Log.i("-=-=-=-= Player", "onVideoSizeChanged"
                        + "  i：" + i
                        + "  i1：" + i1
                        + "  i2：" + i2
                        + "  i3：" + i3
                );*/
            }

            @Override
            public void onSeekComplete(IMediaPlayer iMediaPlayer) {
                // Log.i("-=-=-=-= Player", "onSeekComplete");
            }

            @Override
            public void onPrepared(IMediaPlayer iMediaPlayer) {
                // Log.i("-=-=-=-= Player", "onPrepared");
            }

            @Override
            public boolean onInfo(IMediaPlayer iMediaPlayer, int i, int i1) {
                String info = "";
                switch (i) {
                    case 3:
                        // 准备渲染
                        info = "准备渲染";

                        // 更新视频时长
                        mController.updateDuration(mCurrentFile.getDuration());
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

                return false;
            }

            @Override
            public boolean onError(IMediaPlayer iMediaPlayer, int i, int i1) {
                /*Log.i("-=-=-=-= Player", "onError"
                        + "  i：" + i
                        + "  i1：" + i1
                );*/
                return false;
            }

            @Override
            public void onCompletion(IMediaPlayer iMediaPlayer) {
                // Log.i("-=-=-=-= Player", "onCompletion");
            }

            @Override
            public void onBufferingUpdate(IMediaPlayer iMediaPlayer, int i) {
                // Log.i("-=-=-=-= Player", "onBufferingUpdate  i：" + i);
            }
        });
    }

    /**
     * 播放
     */
    private void play(String path) {

        mVideoBitmap = MediaUtil.getInstance().getFrame(path, 0);

        new Thread(() -> {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            rl_root.post(() -> {
                // 处理播放器尺寸
                initPlayerSize(mVideoBitmap.getWidth(), mVideoBitmap.getHeight());

                // 播放当前文件
                ijk_video.setVideoPath(path);

                // 开始播放
                mController.onPlay();

                // 更新视频文件名
                mController.onUpdateFileName(mCurrentFile.getName());

                // 更新视频进度
                mController.updateProgress(0);
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

        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) ijk_video.surfaceView.getLayoutParams();
        layoutParams.width = (int) videoWidth;
        layoutParams.height = (int) videoHeight;
        layoutParams.leftMargin = left;
        layoutParams.topMargin = top;
        ijk_video.surfaceView.setLayoutParams(layoutParams);

        if (ijk_video.surfaceView.getParent() == null) {
            rl_root.addView(ijk_video.surfaceView, 0);
        }
    }
}