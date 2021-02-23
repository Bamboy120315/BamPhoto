package com.bamboy.bimage.page.media;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.bamboy.bimage.R;
import com.bamboy.bimage.page.base.BaseActivity;
import com.bamboy.bimage.page.media.bean.DirectoryBean;
import com.bamboy.bimage.page.media.fitem.FItemDir;
import com.bamboy.bimage.page.media.menu.PhotoMenuPopupWindow;
import com.bamboy.bimage.page.photolist.PhotoListActivity;
import com.bamboy.bimage.page.videolist.VideoListActivity;
import com.bamboy.bimage.util.MediaUtil;
import com.bamboy.bimage.view.clickanimview.ClickAnimRelativeLayout;
import com.bamboy.bimage.view.freedom.freedom.FreedomAdapter;
import com.bamboy.bimage.view.freedom.freedom.FreedomBean;
import com.bamboy.bimage.view.freedom.freedom.FreedomCallback;
import com.bamboy.bimage.view.freedom.freedom.FreedomLongClickCallback;
import com.bamboy.bimage.view.freedom.freedom.ViewHolderManager;
import com.bamboy.bimage.view.freedom.smartrefresh.SmartRefreshLayout;
import com.bamboy.bimage.view.freedom.smartrefresh.api.RefreshLayout;
import com.bamboy.bimage.view.toast.BamToast;

import java.util.ArrayList;
import java.util.List;

public class MediaActivity extends BaseActivity implements FreedomCallback, FreedomLongClickCallback {

    /**
     * 媒体类型
     * 【1：图片】
     * 【2：视频】
     */
    private int mMediaType = 1;

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
     * 菜单弹窗
     */
    public PhotoMenuPopupWindow mMenuPopupWindow;

    /**
     * 刷新回调
     */
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            setMediaAdapterData();//设置图片的显示
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_media);

        // 获取媒体类型
        mMediaType = getIntent().getIntExtra("mediaType", 1);

        // 处理标题
        initTitle();

        // 匹配View
        findView();

        // 初始化下拉刷新
        initRefreshLayout();
    }

    @Override
    public void onBackPressed() {
        if (mMenuPopupWindow != null) {
            mMenuPopupWindow.dismiss();
            return;
        }
        super.onBackPressed();
    }

    /**
     * 处理标题
     */
    private void initTitle() {
        // 标题
        String title = mMediaType == 1 ? "相册" : "视频";
        setTitle(title);
    }

    /**
     * 匹配View
     */
    private void findView() {
        refreshLayout = findViewById(R.id.refreshLayout);
        rv_list = findViewById(R.id.rv_list);
    }

    /**
     * 初始化媒体数据
     */
    private void initMedias() {
        if (!Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            showToast("检测到没有内存卡");
            return;
        }

        // 读取媒体
        new Thread(() -> MediaUtil.getInstance().loadMedia(this, mMediaType, mHandler)).start();
    }

    /**
     * 处理结束
     */
    private void setMediaAdapterData() {
        // 结束刷新
        finishRefresh();

        if (mList == null) {
            mList = new ArrayList<>();
        } else {
            mList.clear();
        }

        for (DirectoryBean directoryBean : MediaUtil.getInstance().dirList) {
            mList.add(new FItemDir(directoryBean));
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
        refreshLayout.setOnRefreshListener((@NonNull final RefreshLayout refreshLayout) -> initMedias());

        // 自动刷新
        // autoRefresh();
        initMedias();
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
            int spanCount = mMediaType == 1 ? 3 : 2;
            // 瀑布流
            StaggeredGridLayoutManager manager =
                    new StaggeredGridLayoutManager(spanCount, StaggeredGridLayoutManager.VERTICAL);
            rv_list.setLayoutManager(manager);
            rv_list.setItemAnimator(new DefaultItemAnimator());
            // 条目不重新检测宽高
            rv_list.setHasFixedSize(true);
            rv_list.setAdapter(mAdapter);
        } else {
            mAdapter.notifyDataSetChanged();
        }

        if (rv_list.getVisibility() == View.GONE) {
            rv_list.setVisibility(View.VISIBLE);
        }

        // 初次渐变显示
        if (rv_list.getAlpha() == 0) {
            rv_list.setAlpha(0.01f);
            rv_list.post(() -> {
                if (rv_list != null && rv_list.animate() != null) {
                    rv_list.animate()
                            .alpha(1)
                            .setDuration(300)
                            .setStartDelay(200)
                            .setListener(null);
                }
            });
        }
    }

    @Override
    public void onClickCallback(View view, int position, ViewHolderManager.ViewHolder holder) {
        FreedomBean freedomBean = mList.get(position);
        if (freedomBean instanceof FItemDir) {
            FItemDir fItemDir = (FItemDir) freedomBean;

            Class<?> classType = null;
            if (fItemDir.getDirectoryBean().getMediaType() == 1) {
                classType = PhotoListActivity.class;
            } else if (fItemDir.getDirectoryBean().getMediaType() == 2) {
                classType = VideoListActivity.class;
            }

            if (classType == null) {
                BamToast.show(this, "不支持的文件类型");
                return;
            }

            Intent intent = new Intent(this, classType);
            intent.putExtra("dir_path", fItemDir.getDirectoryBean().getPath());
            intent.putExtra("dir_name", fItemDir.getDirectoryBean().getName());
            startActivity(intent);
        }
    }

    @Override
    public boolean onLongClickCallback(View view, int position, ViewHolderManager.ViewHolder holder) {
        FreedomBean freedomBean = mList.get(position);
        if (freedomBean instanceof FItemDir) {

            // 长按成功，立刻执行抬起动画
            if (view instanceof ClickAnimRelativeLayout) {
                ((ClickAnimRelativeLayout) view).actionUp();
            }

            // 处理菜单弹窗
            mMenuPopupWindow = new PhotoMenuPopupWindow(this, view, false);
            mMenuPopupWindow.setOnDismissListener(() -> mMenuPopupWindow = null);
            mMenuPopupWindow.showAtLocation(view, Gravity.NO_GRAVITY, 0, 0);
            return true;
        }

        return false;
    }
}