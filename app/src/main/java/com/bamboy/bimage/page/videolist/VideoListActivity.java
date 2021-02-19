package com.bamboy.bimage.page.videolist;

import android.media.MediaMetadataRetriever;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bamboy.bimage.R;
import com.bamboy.bimage.page.base.BaseActivity;
import com.bamboy.bimage.page.media.bean.FileBean;
import com.bamboy.bimage.page.video.VideoActivity;
import com.bamboy.bimage.page.videolist.fitem.FItemVideo;
import com.bamboy.bimage.util.MediaUtil;
import com.bamboy.bimage.util.NullUtil;
import com.bamboy.bimage.view.freedom.freedom.FreedomAdapter;
import com.bamboy.bimage.view.freedom.freedom.FreedomBean;
import com.bamboy.bimage.view.freedom.freedom.FreedomCallback;
import com.bamboy.bimage.view.freedom.freedom.ViewHolderManager;
import com.bamboy.bimage.view.freedom.smartrefresh.SmartRefreshLayout;
import com.bamboy.bimage.view.freedom.smartrefresh.api.RefreshLayout;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class VideoListActivity extends BaseActivity implements FreedomCallback {

    /**
     * 文件夹路径
     */
    private String mDirPath;
    /**
     * 文件夹名称
     */
    private String mDirName;
    /**
     * 下拉刷新容器
     */
    private SmartRefreshLayout refreshLayout;
    /**
     * 列表
     */
    private RecyclerView rv_list;
    /**
     * 适配器
     */
    public FreedomAdapter mAdapter;
    /**
     * 数据源
     */
    public List<FreedomBean> mList;
    /**
     * 数据列表
     */
    private List<FileBean> mFileList;

    /**
     * 刷新回调
     */
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {
                // 显示列表
                setFileData();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_photo_list);

        // 处理标题
        initTitle();

        // 匹配View
        findView();

        // 初始化下拉刷新
        initRefreshLayout();
    }

    /**
     * 处理标题
     */
    private void initTitle() {
        // 标题
        mDirName = getIntent().getStringExtra("dir_name");
        setTitle(mDirName);
    }

    /**
     * 匹配View
     */
    private void findView() {
        rv_list = findViewById(R.id.rv_list);
        refreshLayout = findViewById(R.id.refreshLayout);
    }

    /**
     * 获取文件列表
     */
    private void loadData() {
        if (mFileList == null) {
            mFileList = new ArrayList<>();
        } else {
            mFileList.clear();
        }

        mDirPath = getIntent().getStringExtra("dir_path");
        if (NullUtil.isNull(mDirPath)) {
            mHandler.sendEmptyMessage(1);
            return;
        }

        File fileDir = new File(mDirPath);
        if (fileDir == null || !fileDir.exists() || !fileDir.isDirectory()) {
            mHandler.sendEmptyMessage(1);
            return;
        }

        // 获取文件列表
        File[] files = fileDir.listFiles();

        if (NullUtil.isNull(files)) {
            mHandler.sendEmptyMessage(1);
            return;
        }

        // 文件最后修改时间降序
        Arrays.sort(files, (File f1, File f2) ->
                String.valueOf(f2.lastModified()).compareTo(String.valueOf(f1.lastModified())));

        // 尚未获取到视频时长的视频
        List<File> notDurationList = new ArrayList<>();

        // 遍历文件列表
        for (File file : files) {
            if (file == null || !file.exists() || !file.isFile()) {
                continue;
            }
            if (!MediaUtil.getInstance().isVideo(file.getName())) {
                continue;
            }

            String path = file.getAbsolutePath();
            long duration = 0;

            // 如果存储的有视频时长，则直接获取
            if (!MediaUtil.getInstance().videoDurationMap.containsKey(path)) {
                notDurationList.add(file);
            } else {
                duration = MediaUtil.getInstance().videoDurationMap.get(path);
            }

            // 添加文件
            mFileList.add(new FileBean(
                    path,
                    file.getName(),
                    file.lastModified(),
                    duration,
                    file.length()));
        }

        if (mList == null) {
            mList = new ArrayList<>();
        } else {
            mList.clear();
        }

        if (!NullUtil.isNull(mFileList)) {
            for (FileBean fileBean : mFileList) {
                mList.add(new FItemVideo(fileBean));
            }
        }

        new Thread(() -> {
            try {
                Thread.sleep(400);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            mHandler.sendEmptyMessage(1);
        }).start();

        // 如果有未获取到视频时长的视频，则继续获取时长
        if (!NullUtil.isNull(notDurationList)) {
            for (File file : notDurationList) {
                try {
                    long duration = 0;

                    MediaMetadataRetriever mmr = new MediaMetadataRetriever();
                    mmr.setDataSource(file.getAbsolutePath());
                    duration = Long.parseLong(mmr.extractMetadata
                            (MediaMetadataRetriever.METADATA_KEY_DURATION));

                    if (duration > 0) {
                        MediaUtil.getInstance().videoDurationMap.put(file.getAbsolutePath(), duration);
                    }
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                } catch (RuntimeException e) {
                    e.printStackTrace();
                }
            }

            // mHandler.sendEmptyMessage(2);
        }
    }

    /**
     * 获取数据结束，开始设置数据
     */
    private void setFileData() {
        // 结束刷新
        finishRefresh();

        notifyList();
    }

    // =============================================================================================
    // ======================== 以下是关于列表相关 ===================================================
    // =============================================================================================

    /**
     * 初始化下拉刷新
     */
    private void initRefreshLayout() {
        // 下拉刷新监听，请求数据
        refreshLayout.setOnRefreshListener((@NonNull final RefreshLayout refreshLayout) ->
                new Thread(() -> loadData()).start());

        // 自动刷新
        // autoRefresh();
        new Thread(() -> loadData()).start();
    }

    /**
     * 自动刷新
     */
    private void autoRefresh() {
        if (refreshLayout != null)
            // 自动刷新
            refreshLayout.autoRefresh(300, 370, 1.5f, false);
    }

    /**
     * 结束加载
     */
    public void finishRefresh() {
        if (refreshLayout != null)
            refreshLayout.finishRefresh();
    }

    /**
     * 刷新列表
     */
    public void notifyList() {
        if (isFinishing() || mList == null || rv_list == null) {
            return;
        }

        if (mAdapter == null) {

            mAdapter = new FreedomAdapter(this, mList);
            // 避免刷新时图片闪烁
            mAdapter.setHasStableIds(true);
            LinearLayoutManager manager = new LinearLayoutManager(this);
            rv_list.setLayoutManager(manager);
            rv_list.setItemAnimator(new DefaultItemAnimator());
            rv_list.setAdapter(mAdapter);
        } else {
            mAdapter.notifyDataSetChanged();
        }

        if (rv_list.getVisibility() == View.GONE) {
            rv_list.setVisibility(View.VISIBLE);
        }

        // 初次渐变显示
        if (rv_list.getAlpha() == 0) {
            rv_list.post(() -> {
                if (rv_list != null && rv_list.animate() != null) {
                    rv_list.animate()
                            .alpha(1)
                            .setDuration(300)
                            .setListener(null);
                }
            });
        }
    }

    @Override
    public void onClickCallback(View view, int position, ViewHolderManager.ViewHolder holder) {
        FreedomBean freedomBean = mList.get(position);
        if (freedomBean instanceof FItemVideo) {
            // FItemPhoto fItemPhoto = (FItemPhoto) freedomBean;

            // 启动播放器页
            VideoActivity.startActivity(this, mFileList, position);
        }
    }
}