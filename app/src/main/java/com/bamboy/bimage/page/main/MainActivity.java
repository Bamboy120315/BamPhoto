package com.bamboy.bimage.page.main;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.animation.AnticipateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.bamboy.bimage.R;
import com.bamboy.bimage.page.base.BaseActivity;
import com.bamboy.bimage.page.fileManager.FileListActivity;
import com.bamboy.bimage.page.main.fitem.FItemMainBtn;
import com.bamboy.bimage.page.media.MediaActivity;
import com.bamboy.bimage.util.BlurUtil;
import com.bamboy.bimage.util.FileInfoManager;
import com.bamboy.bimage.util.NullUtil;
import com.bamboy.bimage.util.UIUtil;
import com.bamboy.bimage.view.freedom.freedom.FreedomAdapter;
import com.bamboy.bimage.view.freedom.freedom.FreedomBean;
import com.bamboy.bimage.view.freedom.freedom.FreedomCallback;
import com.bamboy.bimage.view.freedom.freedom.ViewHolderManager;
import com.hjq.permissions.OnPermissionCallback;
import com.hjq.permissions.Permission;
import com.hjq.permissions.XXPermissions;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity implements FreedomCallback {

    private RelativeLayout rl_permissions_mask;
    private RelativeLayout rl_permissions;
    private TextView tv_permissions_des;
    private TextView tv_goto_permissions;
    private RecyclerView rv_list;
    private FreedomAdapter mAdapter;
    private List<FreedomBean> mList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_main);

        findView();
        init();
        initPermissions();
    }

    @Override
    protected void onResume() {
        super.onResume();

        // 如果权限遮罩在显示，并且有权限，则关闭权限遮罩
        if (rl_permissions_mask.getTag() != null
                && (Boolean) rl_permissions_mask.getTag()
                && rl_permissions_mask.getVisibility() == View.VISIBLE
                && rl_permissions_mask.getAlpha() > 0.95
                && isHasPermissions()) {
            requestPermissionSuccess();
        }
    }

    private void findView() {
        rl_permissions_mask = findViewById(R.id.rl_permissions_mask);
        rl_permissions = findViewById(R.id.rl_permissions);
        tv_permissions_des = findViewById(R.id.tv_permissions_des);
        tv_goto_permissions = findViewById(R.id.tv_goto_permissions);
        rv_list = findViewById(R.id.rv_list);
    }

    private void init() {
        if (mList == null) {
            mList = new ArrayList<>();
        }

        // 添加条目 --> 水印
        /*mList.add(new FItemMainBtn(
                R.drawable.ic_btn_type_watermark,
                "水印",
                MainBtnType.BTN_TYPE_WATERMARK));*/

        // 添加条目 --> 全部文件
        mList.add(new FItemMainBtn(
                R.drawable.ic_btn_type_file,
                "全部文件",
                MainBtnType.BTN_TYPE_FILE));

        // 添加条目 --> 隐私文件
        mList.add(new FItemMainBtn(
                R.drawable.ic_btn_type_file_lock,
                "隐私文件",
                MainBtnType.BTN_TYPE_FILE_LOCK));

        // 添加条目 --> 相册
        mList.add(new FItemMainBtn(
                R.drawable.ic_btn_type_picture,
                "相册",
                MainBtnType.BTN_TYPE_PICTURE));

        // 添加条目 --> 视频
        mList.add(new FItemMainBtn(
                R.drawable.ic_btn_type_video,
                "视频",
                MainBtnType.BTN_TYPE_VIDEO));

        // 添加条目 --> 设置
        mList.add(new FItemMainBtn(
                R.drawable.ic_btn_type_setting,
                "设置",
                MainBtnType.BTN_TYPE_SETTING));

        // 刷新列表
        notifyList();

        // 处理隐私文件集合
        // new Thread(() -> initLockFileList("")).start();
    }

    private void initPermissions() {
        if (isHasPermissions()) {
            // 有权限，则隐藏遮罩
            rl_permissions_mask.setVisibility(View.GONE);
            return;
        }

        // 没有权限，处理遮罩

        // 动画作为延迟器，尽可能与RecyclerView动画时间贴近
        rl_permissions
                .animate()
                .alpha(1)
                .setDuration(800)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);

                        // 高斯模糊
                        rl_permissions_mask.setBackground(getBlurDrawing());

                        // 显示图片
                        rl_permissions_mask
                                .animate()
                                .alpha(1)
                                .setDuration(300);

                        // 显示文字
                        rl_permissions.setTranslationY(200);
                        rl_permissions
                                .animate()
                                .alpha(1)
                                .translationY(0)
                                .setDuration(500)
                                .setInterpolator(new OvershootInterpolator(1.8f))
                                .setListener(new AnimatorListenerAdapter() {
                                    @Override
                                    public void onAnimationEnd(Animator animation) {
                                        super.onAnimationEnd(animation);

                                        // 请求权限
                                        requestPermission();
                                    }
                                });
                    }
                });
    }

    /**
     * 是否拥有权限
     *
     * @return
     */
    private boolean isHasPermissions() {
        try {
            return XXPermissions.isGrantedPermission(this, Permission.MANAGE_EXTERNAL_STORAGE);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 请求权限
     */
    private void requestPermission() {
        XXPermissions.with(this)
                .permission(Permission.MANAGE_EXTERNAL_STORAGE)
                .request(new OnPermissionCallback() {
                    @Override
                    public void onGranted(List<String> permissions, boolean all) {
                        if (all) {
                            // 允许了权限
                            rl_permissions_mask.post(() -> requestPermissionSuccess());
                        } else {
                            // 拒绝了权限
                            requestPermissionFailure();
                        }
                    }

                    @Override
                    public void onDenied(List<String> permissions, boolean never) {
                        // 拒绝了权限
                        requestPermissionFailure();
                    }
                });
    }

    /**
     * 获取高斯模糊截图
     *
     * @return
     */
    private Drawable getBlurDrawing() {
        Bitmap bitmap = UIUtil.getDrawing(MainActivity.this);
        bitmap = BlurUtil.blurBitmap(MainActivity.this, bitmap, 10);
        return BlurUtil.coverColor(MainActivity.this, bitmap, UIUtil.getColor(MainActivity.this, R.color.white_alpha_B));
    }

    /**
     * 请求权限成功
     */
    private void requestPermissionSuccess() {
        tv_goto_permissions.setClickable(false);

        // 隐藏界面
        rl_permissions_mask
                .animate()
                .alpha(0)
                .setStartDelay(500)
                .setDuration(500);

        // 隐藏文字
        rl_permissions
                .animate()
                .translationY(200)
                .setDuration(500)
                .setStartDelay(500)
                .setInterpolator(new AnticipateInterpolator(1.8f))
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        rl_permissions_mask.setVisibility(View.GONE);
                    }
                });
    }

    /**
     * 请求权限失败
     */
    public void requestPermissionFailure() {
        // 渐变隐藏文字，更新后显示
        rl_permissions
                .animate()
                .alpha(0)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);

                        showGotoPermissions();
                    }
                });
    }

    /**
     * 显示去设置
     */
    private void showGotoPermissions() {
        tv_permissions_des.setText("您已拒绝存储权限，如要继续使用，请允许存储权限");
        tv_goto_permissions.setVisibility(View.VISIBLE);
        tv_goto_permissions.setOnClickListener(this::clickToPermissions);

        rl_permissions
                .animate()
                .alpha(1)
                .setListener(null);
    }

    /**
     * 点击事件 --> 允许权限
     *
     * @param view
     */
    private void clickToPermissions(View view) {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        try {
            rl_permissions_mask.setTag(true);
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 刷新列表
     */
    public void notifyList() {
        if (mList == null || rv_list == null) {
            return;
        }

        if (mAdapter == null) {
            mAdapter = new FreedomAdapter(this, mList);

            // 把每行平分成2份
            /*GridLayoutManager manager = new GridLayoutManager(this, 2);
            manager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    if (position >= mList.size()) {
                        return 1;
                    }

                    FreedomBean bean = mList.get(position);
                    // 获取当前这个条目占几份
                    return bean.getSpanSize(2);
                }
            });*/

            // 瀑布流
            StaggeredGridLayoutManager manager =
                    new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);

            rv_list.setLayoutManager(manager);
            rv_list.setItemAnimator(new DefaultItemAnimator());
            rv_list.setAdapter(mAdapter);
        } else {
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onClickCallback(View view, int position, ViewHolderManager.ViewHolder holder) {
        if (NullUtil.isNull(mList)) {
            return;
        }
        FreedomBean fBean = mList.get(position);

        // 点击事件 --> 主页按钮
        if (fBean instanceof FItemMainBtn) {
            clickToMainBtn((FItemMainBtn) fBean);
        }
    }

    /**
     * 点击事件 --> 主页按钮
     *
     * @param btnItem
     */
    private void clickToMainBtn(FItemMainBtn btnItem) {
        switch (btnItem.getBtnType()) {
            case MainBtnType.BTN_TYPE_FILE:
                // 按钮类型 --> 文件
                startActivity(new Intent(this, FileListActivity.class));
                break;

            case MainBtnType.BTN_TYPE_FILE_LOCK:
                // 按钮类型 --> 隐私文件
                showToast("隐私文件");
                break;

            case MainBtnType.BTN_TYPE_PICTURE:
                // 按钮类型 --> 相册
                startActivity(new Intent(this, MediaActivity.class));
                break;

            case MainBtnType.BTN_TYPE_VIDEO:
                // 按钮类型 --> 视频
                Intent intent = new Intent(this, MediaActivity.class);
                intent.putExtra("mediaType", 2);
                startActivity(intent);
                break;

            case MainBtnType.BTN_TYPE_SETTING:
                // 按钮类型 --> 设置
                showToast("设置");
                break;
        }
    }

    /**
     * 处理隐私文件集合
     */
    private void initLockFileList(String path) {
        if (NullUtil.isNull(path)) {
            File docfileAbs = Environment.getExternalStorageDirectory();
            if (docfileAbs != null) {
                path = docfileAbs.getAbsolutePath();
            } else {
                path = FileInfoManager.sdcardPath;
            }
        }

        File fileAbs = new File(path);
        File[] files = fileAbs.listFiles();

        for (File file : files) {
            // 过滤系统文件夹
            if (FileInfoManager.isSyatemFile(file.getAbsolutePath())) {
                continue;
            }

            // 判断是否是拥有.nomedia文件
            if (".nomedia".equals(file.getName()) && !file.isDirectory()) {
                Log.i("-=-=-=-=", "找到隐私文件夹：" + path);
                FileInfoManager.allLockFileList.add(path);
                continue;
            }

            // 过滤以点开头的文件
            if (file.getName().startsWith(".") || file.getName().startsWith("com.")) {
                continue;
            }

            // 如果是文件夹，则扫描内部文件
            if (file.isDirectory()) {
                initLockFileList(file.getAbsolutePath());
            }
        }
    }
}