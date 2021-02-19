package com.bamboy.bimage.page.photo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import androidx.viewpager.widget.ViewPager;

import com.bamboy.bimage.R;
import com.bamboy.bimage.page.media.bean.FileBean;
import com.bamboy.bimage.page.photo.adapter.PhotoAdapter;
import com.bamboy.bimage.page.photo.callback.PhotoChangeCallback;

import java.util.List;

public class PhotoActivity extends Activity {

    /**
     * 数据列表
     */
    private static List<FileBean> mFileList;
    /**
     * 索引
     */
    private int mPosition;
    /**
     * 图片浏览器
     */
    private ViewPager vp_photo;
    /**
     * 适配器
     */
    private PhotoAdapter mAdapter;
//    private Photo2Adapter mAdapter;
    /**
     * 图片变更回调
     */
    private static PhotoChangeCallback mPhotoChangeCallback;

    /**
     * 启动界面
     *
     * @param context
     * @param fileList
     * @param position
     * @param photoChangeCallback
     */
    public static void startActivity(Context context, List<FileBean> fileList, int position, PhotoChangeCallback photoChangeCallback) {
        mFileList = fileList;
        mPhotoChangeCallback = photoChangeCallback;
        Intent intent = new Intent(context, PhotoActivity.class);
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
        hideActionStatusBar();
        setContentView(R.layout.act_photo);

        // 匹配View
        findView();

        // 处理数据
        init();
    }

    /**
     * 匹配View
     */
    private void findView() {
        vp_photo = findViewById(R.id.vp_photo);
    }

    /**
     * 处理数据
     */
    private void init() {
        mAdapter = new PhotoAdapter(this, getLayoutInflater().from(this), mFileList);
        vp_photo.setAdapter(mAdapter);

        mPosition = getIntent().getIntExtra("position", 0);
        vp_photo.setCurrentItem(mPosition, false);
    }

}