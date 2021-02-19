package com.bamboy.bimage.page.photolist;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bamboy.bimage.R;
import com.bamboy.bimage.page.base.BaseActivity;
import com.bamboy.bimage.page.media.bean.FileBean;
import com.bamboy.bimage.page.photo.PhotoActivity;
import com.bamboy.bimage.page.photo.callback.PhotoChangeCallback;
import com.bamboy.bimage.page.photolist.fitem.FItemPhoto;
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

public class PhotoListActivity extends BaseActivity implements FreedomCallback, PhotoChangeCallback {

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
            setFileData();//设置图片的显示
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

        // 遍历文件列表
        for (File file : files) {
            if (file == null || !file.exists() || !file.isFile()) {
                continue;
            }
            if (!MediaUtil.getInstance().isPhoto(file.getName())) {
                continue;
            }

            // 添加文件
            mFileList.add(new FileBean(
                    file.getAbsolutePath(),
                    file.getName(),
                    file.lastModified(),
                    file.length()));
        }

        mHandler.sendEmptyMessage(1);
    }

    /**
     * 获取数据结束，开始设置数据
     */
    private void setFileData() {
        // 结束刷新
        finishRefresh();

        if (mList == null) {
            mList = new ArrayList<>();
        } else {
            mList.clear();
        }

        if (!NullUtil.isNull(mFileList)) {
            for (FileBean fileBean : mFileList) {
                mList.add(new FItemPhoto(fileBean));
            }
        }

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
        refreshLayout.setOnRefreshListener((@NonNull final RefreshLayout refreshLayout) -> loadData());

        // 自动刷新
        // autoRefresh();
        loadData();
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
            // 把每行平分成4份
            GridLayoutManager manager = new GridLayoutManager(this, 4);
            manager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    if (position >= mList.size()) {
                        return 1;
                    }

                    FreedomBean bean = mList.get(position);
                    // 获取当前这个条目占几份
                    return bean.getSpanSize(4);
                }
            });
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
        if (freedomBean instanceof FItemPhoto) {
            // FItemPhoto fItemPhoto = (FItemPhoto) freedomBean;

            // 启动大图页
            PhotoActivity.startActivity(this, mFileList, position, this);
        }
    }

    /**
     * 索引变更
     *
     * @param position
     */
    @Override
    public void positionChange(int position) {

    }
}