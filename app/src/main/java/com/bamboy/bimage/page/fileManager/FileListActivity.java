package com.bamboy.bimage.page.fileManager;

import android.app.Dialog;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bamboy.bimage.R;
import com.bamboy.bimage.page.base.BaseActivity;
import com.bamboy.bimage.page.fileManager.fbean.FBeanFile;
import com.bamboy.bimage.page.media.bean.FileBean;
import com.bamboy.bimage.page.photo.PhotoActivity;
import com.bamboy.bimage.page.video.VideoActivity;
import com.bamboy.bimage.util.FileInfoManager;
import com.bamboy.bimage.util.MediaUtil;
import com.bamboy.bimage.util.NullUtil;
import com.bamboy.bimage.util.SettingsUtil;
import com.bamboy.bimage.view.dialog.TextButtonDialog;
import com.bamboy.bimage.view.freedom.freedom.FreedomAdapter;
import com.bamboy.bimage.view.freedom.freedom.FreedomBean;
import com.bamboy.bimage.view.freedom.freedom.FreedomCallback;
import com.bamboy.bimage.view.freedom.freedom.ViewHolderManager;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FileListActivity extends BaseActivity implements FreedomCallback {
    /**
     * 是否是选择模式
     */
    public static boolean isCheckModel = false;

    /**
     * 路径
     */
    public String mPath;

    /**
     * 路径
     */
    private TextView tv_path;

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
     * 文件列表
     */
    private File[] mFiles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_file_list);

        rv_list = findViewById(R.id.rv_list);
        tv_path = findViewById(R.id.tv_path);

        // 处理路径
        mPath = getIntent().getStringExtra("path");
        if (NullUtil.isNull(mPath)) {
            File docfileAbs = Environment.getExternalStorageDirectory();
            if (docfileAbs != null) {
                mPath = docfileAbs.getAbsolutePath();
            } else {
                mPath = FileInfoManager.sdcardPath;
            }
        }

        File fileAbs = new File(mPath);
        File[] files = fileAbs.listFiles();

        // 处理列表数据
        mFiles = initFiles(files);

        // 处理标题
        initTitle(fileAbs, mFiles);

        // 加载文件列表
        loadFileList();
    }

    @Override
    public void onBackPressed() {
        if (isCheckModel) {
            isCheckModel = false;
            hideDelete();
            notifyList();
            return;
        }
        super.onBackPressed();
    }

    // =============================================================================================
    // ======================== 以下是数据加载相关 ===================================================
    // =============================================================================================

    /**
     * 处理标题
     */
    private void initTitle(File fileAbs, File[] files) {
        if (files == null || files.length == 0) {
            showToast("没有文件");

            rv_list.setVisibility(View.GONE);
        }

        // 标题
        String title = FileInfoManager.getDes(fileAbs.getAbsolutePath());
        if (NullUtil.isNull(title)) {
            title = fileAbs.getName();
        }
        setTitle(title);

        // 显示路径
        tv_path.setText(fileAbs.getAbsolutePath());
    }

    /**
     * 处理列表
     *
     * @param files
     * @return
     */
    private File[] initFiles(File[] files) {
        if (SettingsUtil.getInstance().isShowSystemFile()) {
            return files;
        }

        List<File> fileList = new ArrayList<>();

        for (File file : files) {
            // 过滤系统文件
            if (FileInfoManager.isSyatemFile(file.getAbsolutePath())) {
                continue;
            }

            // 过滤以点开头的文件
            if (file.getName().startsWith(".")) {
                continue;
            }

            fileList.add(file);
        }

        if (NullUtil.isNull(fileList)) {
            return null;
        }

        return fileList.toArray(new File[fileList.size()]);
    }

    /**
     * 加载文件列表
     */
    private void loadFileList() {
        if (NullUtil.isNull(mFiles)) {
            mFiles = new File(mPath).listFiles();
        }

        // 排序
        Arrays.sort(mFiles, (File f1, File f2) -> {
            // 文件夹放在文件前面
            if (f1.isDirectory() != f2.isDirectory()) {
                return f1.isDirectory() ? -1 : 1;
            }
            // 忽略大小写，按文件名排序
            return f1.getName().toLowerCase().compareTo(f2.getName().toLowerCase());
        });

        if (mList == null) {
            mList = new ArrayList<>();
        } else {
            mList.clear();
        }

        for (File file : mFiles) {
            if (file.exists()) {
                mList.add(new FBeanFile(file.getAbsolutePath(), file.getName(), file.isDirectory()));
            }
        }

        notifyList();
    }

    // =============================================================================================
    // ======================== 以下是关于列表相关 ===================================================
    // =============================================================================================

    /**
     * 刷新列表
     */
    public void notifyList() {
        if (isFinishing() || mList == null || rv_list == null) {
            return;
        }

        if (mAdapter == null) {
            mAdapter = new FreedomAdapter(this, mList);
            LinearLayoutManager linearManager = new LinearLayoutManager(this);
            rv_list.setLayoutManager(linearManager);
            rv_list.setItemAnimator(new DefaultItemAnimator());
            rv_list.setAdapter(mAdapter);
        } else {
            mAdapter.notifyDataSetChanged();
        }

        // 初次渐变显示
        if (rv_list.getAlpha() == 0) {
            rv_list.post(() ->
                    rv_list.animate()
                            .alpha(1)
                            .setDuration(300));
        }
    }

    /**
     * 检查选中状态
     */
    private void checkSelectType() {
        boolean hasSelect = false, allSelect = false;

        if (NullUtil.isNull(mList)) {
            hasSelect = false;
            allSelect = false;
        } else {
            allSelect = true;
            for (FreedomBean freedomBean : mList) {
                if (freedomBean instanceof FBeanFile) {
                    FBeanFile fileBean = (FBeanFile) freedomBean;
                    if (fileBean.isSelect()) {
                        hasSelect = true;
                    } else {
                        allSelect = false;
                    }
                }
            }
        }

        // 是否有选中的项
        if (hasSelect) {
            showDelete();

            // 是否全部选中
            if (allSelect) {

            } else {

            }
        } else {
            hideDelete();
        }
    }

    /**
     * 显示删除按钮
     */
    private void showDelete() {

    }

    /**
     * 隐藏删除按钮
     */
    private void hideDelete() {

    }

    // =============================================================================================
    // ======================== 以下是关于列表点击事件 ================================================
    // =============================================================================================

    @Override
    public void onClickCallback(View view, int position, ViewHolderManager.ViewHolder holder) {
        if (NullUtil.isNull(mList) || position >= mList.size()) {
            return;
        }

        FBeanFile fileBean = (FBeanFile) mList.get(position);

        if (view == null) {
            if (isCheckModel) {
                return;
            }
            isCheckModel = true;
            fileBean.setSelect(true);
            notifyList();
            // 检查选中状态
            checkSelectType();
            return;
        }

        if (isCheckModel) {
            // 检查选中状态
            checkSelectType();
        } else {
            // 文件的后缀
            String suffix = fileBean.getFileName().substring(fileBean.getFileName().lastIndexOf(".") + 1);

            // 如果是图片
            if (MediaUtil.getInstance().containsImageType(suffix)) {
                // 处理图片列表
                initPhotoList(fileBean.getFileName());
                return;
            }

            // 如果是视频
            if (MediaUtil.getInstance().containsVideoType(suffix)) {
                // 处理视频列表
                initVideoList(fileBean.getFileName());
                return;
            }

            /*exo.setUseController(true);
            exo.setUrl(fileBean.getPath());
            exo.startPlay();
            showVideoView();*/
        }
    }

    /**
     * 处理图片列表
     */
    private void initPhotoList(String fileName) {
        List<FileBean> fileList = new ArrayList<>();
        int position = 0;

        for (File file : mFiles) {
            if (file == null || !file.exists() || !file.isFile()) {
                continue;
            }
            if (!MediaUtil.getInstance().isPhoto(file.getName())) {
                continue;
            }

            if (fileName.equals(file.getName())) {
                // 记录索引
                position = fileList.size();
            }

            // 添加到数据列表
            fileList.add(new FileBean(
                    file.getAbsolutePath(),
                    file.getName(),
                    file.lastModified(),
                    file.length()));
        }

        // 启动大图页
        PhotoActivity.startActivity(this, fileList, position, null);
    }

    /**
     * 处理视频列表
     */
    private void initVideoList(String fileName) {
        List<FileBean> fileList = new ArrayList<>();
        int position = 0;

        for (File file : mFiles) {
            if (file == null || !file.exists() || !file.isFile()) {
                continue;
            }
            if (!MediaUtil.getInstance().isVideo(file.getName())) {
                continue;
            }

            if (fileName.equals(file.getName())) {
                // 记录索引
                position = fileList.size();
            }

            // 添加到数据列表
            fileList.add(new FileBean(
                    file.getAbsolutePath(),
                    file.getName(),
                    file.lastModified(),
                    file.length()));
        }

        // 启动视频播放页
        VideoActivity.startActivity(this, fileList, position);
    }

    /**
     * 点击事件 --> 删除按钮
     *
     * @param view
     */
    private void clickToDelete(View view) {
        if (NullUtil.isNull(mList)) {
            return;
        }

        List<File> delectFiles = new ArrayList<>();
        for (FreedomBean freedomBean : mList) {
            if (!(freedomBean instanceof FBeanFile)) {
                continue;
            }

            FBeanFile fBean = (FBeanFile) freedomBean;

            if (fBean.isSelect()) {
                File file = new File(fBean.getPath());
                delectFiles.add(file);
            }
        }

        if (NullUtil.isNull(delectFiles)) {
            showToast("没有选中文件");
            return;
        }

        new TextButtonDialog(
                this,
                "即将删除" + delectFiles.size() + "个文件",
                "",
                "取消",
                "确定")
                .setCallback(new TextButtonDialog.DialogCallback() {

                    @Override
                    public void onRight(Dialog dialog) {
                        dialog.dismiss();

                        for (File f : delectFiles) {
                            deleteDir(f);
                        }

                        showToast("删除成功");
                        isCheckModel = false;
                        hideDelete();


                        // 处理列表数据
                        File fileAbs = new File(mPath);
                        File[] files = fileAbs.listFiles();
                        mFiles = initFiles(files);
                        loadFileList();
                    }

                    @Override
                    public void onLeft(Dialog dialog) {
                        dialog.dismiss();
                    }
                }).show();

    }

    /**
     * 递归删除目录下的所有文件及子目录下所有文件
     *
     * @param dir 将要删除的文件目录
     * @return
     */
    private boolean deleteDir(File dir) {
        if (!dir.exists()) return false;
        if (dir.isDirectory()) {
            String[] childrens = dir.list();
            // 递归删除目录中的子目录下
            for (String child : childrens) {
                boolean success = deleteDir(new File(dir, child));
                if (!success) return false;
            }
        }
        // 目录此时为空，可以删除
        return dir.delete();
    }


}