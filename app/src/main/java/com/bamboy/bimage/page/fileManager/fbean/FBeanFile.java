package com.bamboy.bimage.page.fileManager.fbean;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bamboy.bimage.R;
import com.bamboy.bimage.util.FileInfoManager;
import com.bamboy.bimage.page.fileManager.FileListActivity;
import com.bamboy.bimage.util.MediaUtil;
import com.bamboy.bimage.util.NullUtil;
import com.bamboy.bimage.util.UIUtil;
import com.bamboy.bimage.view.freedom.freedom.FreedomBean;
import com.bamboy.bimage.view.freedom.freedom.ViewHolderManager;
import com.bamboy.bimage.view.toast.BamToast;
import com.bumptech.glide.Glide;

import java.io.File;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static com.bamboy.bimage.page.fileManager.FileListActivity.isCheckModel;

public class FBeanFile extends FreedomBean {

    /**
     * 文件路径
     */
    private String path;
    /**
     * 文件名
     */
    private String fileName;
    /**
     * 描述
     */
    private String describe;
    /**
     * 是否是文件夹
     */
    private boolean isDirectory;
    /**
     * 是否被选中
     */
    private boolean isSelect;

    /**
     * 构造
     *
     * @param path        文件路径
     * @param fileName    文件名
     * @param isDirectory 是否是文件夹
     */
    public FBeanFile(String path, String fileName, boolean isDirectory) {
        this.path = path;
        this.fileName = fileName;
        this.isDirectory = isDirectory;
        if (isDirectory) {
            this.describe = FileInfoManager.getDes(path);
        } else {
            this.describe = getFileInfo();
        }
    }

    /**
     * 文件路径
     */
    public String getPath() {
        return path;
    }

    /**
     * 文件路径
     */
    public void setPath(String path) {
        this.path = path;
    }

    /**
     * 文件名
     */
    public String getFileName() {
        return fileName;
    }

    /**
     * 文件名
     */
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    /**
     * 描述
     */
    public String getDescribe() {
        return describe;
    }

    /**
     * 描述
     */
    public void setDescribe(String describe) {
        this.describe = describe;
    }

    /**
     * 是否是文件夹
     */
    public boolean isDirectory() {
        return isDirectory;
    }

    /**
     * 是否是文件夹
     */
    public void setDirectory(boolean directory) {
        isDirectory = directory;
    }

    /**
     * 是否被选中
     */
    public boolean isSelect() {
        return isSelect;
    }

    /**
     * 是否被选中
     */
    public void setSelect(boolean select) {
        isSelect = select;
    }

    /**
     * 换算单位
     */
    private final int unit = 1000;
    /**
     * 时间戳格式化工具
     */
    private final SimpleDateFormat fmt = new SimpleDateFormat("yyyy年M月d日", Locale.CHINA);

    /**
     * 获取文件信息
     */
    private String getFileInfo() {
        File file = new File(path);
        if (!file.exists()) {
            return "";
        }

        StringBuffer info = new StringBuffer();

        // 日期
        info.append(fmt.format(new Date(file.lastModified())));

        // 文件大小
        long length = file.length();
        info.append("  ");
        if (length > unit * unit * unit) {
            // 大于1G，显示 *
            float size = (float) length / (float) (unit * unit * unit);
            info.append(new DecimalFormat("#.00").format(size))
                    .append("G");

        } else if (length > unit * unit * 100) {
            // 大于100M，显示 *M
            info.append((int) (length / (unit * unit)))
                    .append("M");

        } else if (length > unit * unit) {
            // 不足100M，显示 *.**M
            float m = length / ((float) unit * unit);
            m = Math.round(m * 100) / 100f;
            info.append(m)
                    .append("M");

        } else if (length > unit * 100) {
            // 大于100K，显示 *K
            info.append((int) (length / unit))
                    .append("K");

        } else if (length > unit) {
            // 不足100K，显示 *.**K
            float m = length / (float) unit;
            m = Math.round(m * 100) / 100f;
            info.append(m)
                    .append("K");

        } else {
            // 不足1K，直接显示 *B
            info.append(length)
                    .append("B");
        }

        return info.toString();
    }

    //==============================================================================================
    //======================= 以 下 是 关 于 条 目 所 需 内 容 ========================================
    //==============================================================================================

    @Override
    protected void initItemType() {
        ViewHolderManager.addItem(this.getClass().toString(), FileViewHolder.class);
        setItemType(this.getClass().toString());
    }

    @Override
    protected void initBindView(List list) {
        setViewHolderBindListener((Context context, ViewHolderManager.ViewHolder viewHolder, int position) -> {
            final FileViewHolder vh = (FileViewHolder) viewHolder;

            // 图标
            if (isDirectory()) {
                vh.iv_icon.setImageResource(R.drawable.ic_files);
            } else {
                // 是文件，判断是否是MP4
                File file = new File(getPath());
                String fileName = file.getName();

                // 后缀
                String suffix = fileName.substring(fileName.lastIndexOf(".") + 1);

                // 是否是支持的媒体文件
                if (MediaUtil.getInstance().containsImageType(suffix)
                        || MediaUtil.getInstance().containsVideoType(suffix) ) {
                    // 是支持的媒体文件，显示缩略图
                    Glide.with(context).load(getPath()).placeholder(R.drawable.ic_file).into(vh.iv_icon);
                } else {
                    // 不是支持的媒体文件，显示文件图标
                    vh.iv_icon.setImageResource(R.drawable.ic_file);
                }
            }

            // 设置文件名
            UIUtil.setText(vh.tv_name, getFileName());
            // 设置描述
            UIUtil.setText(vh.tv_describe, getDescribe());

            // 条目背景色
            if (isCheckModel && isSelect) {
                vh.rl_root.setBackgroundColor(UIUtil.getColor(context, R.color.colorEasy));
                vh.iv_check.setVisibility(View.VISIBLE);
            } else {
                vh.rl_root.setBackgroundColor(UIUtil.getColor(context, R.color.transparent));
                vh.iv_check.setVisibility(View.GONE);
            }

            // 点击事件
            vh.rl_click_root.setOnClickListener((View view) -> clickToRoot(context, position, vh));

            // 长按事件
            vh.rl_click_root.setOnLongClickListener((View v) -> {
                longClickToRoot(context, position, vh);
                return false;
            });
        });
    }

    /**
     * 条目点击事件
     */
    private void clickToRoot(Context context, int position, FileViewHolder vh) {
        if (isCheckModel) {
            // 选择模式，点击切换选择状态
            if (isSelect) {
                // 当前是选中状态，点击后取消选择
                isSelect = false;
                vh.rl_root.setBackgroundColor(UIUtil.getColor(context, R.color.transparent));
                vh.iv_check.setVisibility(View.GONE);
            } else {
                // 当前是非选中状态，点击后选中条目
                isSelect = true;
                vh.rl_root.setBackgroundColor(UIUtil.getColor(context, R.color.colorEasy));
                vh.iv_check.setVisibility(View.VISIBLE);
            }
            getCallback(context).onClickCallback(vh.rl_click_root, position, vh);
        } else {
            // 普通模式，点击后跳转新页面
            if (isDirectory()) {
                // 是文件夹，点击后打开此文件夹
                Intent intent = new Intent(context, FileListActivity.class);
                intent.putExtra("path", getPath());
                context.startActivity(intent);
            } else {
                // 是文件，判断是否是支持的媒体文件
                File file = new File(getPath());
                String fileName = file.getName();
                String suffix = fileName.substring(fileName.lastIndexOf(".") + 1);

                // 是否是支持的媒体文件
                if (MediaUtil.getInstance().containsImageType(suffix)
                        || MediaUtil.getInstance().containsVideoType(suffix) ) {
                    getCallback(context).onClickCallback(vh.rl_click_root, position, vh);
                } else {
                    BamToast.show(context, "暂不支持此类型文件预览");
                }
            }
        }
    }

    /**
     * 条目长按事件
     */
    private void longClickToRoot(Context context, int position, FileViewHolder vh) {
        getCallback(context).onClickCallback(null, position, vh);
    }

    public static class FileViewHolder extends ViewHolderManager.ViewHolder {
        /**
         * 容器
         */
        public RelativeLayout rl_root, rl_click_root;
        /**
         * 图标
         */
        public ImageView iv_icon;
        /**
         * 文件名
         */
        public TextView tv_name;
        /**
         * 描述
         */
        public TextView tv_describe;
        /**
         * 选择框
         */
        public ImageView iv_check;

        public FileViewHolder(ViewGroup viewGroup) {
            super(viewGroup, R.layout.fitem_file);

            rl_root = itemView.findViewById(R.id.rl_root);
            rl_click_root = itemView.findViewById(R.id.rl_click_root);
            iv_icon = itemView.findViewById(R.id.iv_icon);
            tv_name = itemView.findViewById(R.id.tv_name);
            tv_describe = itemView.findViewById(R.id.tv_describe);
            iv_check = itemView.findViewById(R.id.iv_check);
        }
    }
}
