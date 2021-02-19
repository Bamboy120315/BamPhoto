package com.bamboy.bimage.page.videolist.fitem;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bamboy.bimage.R;
import com.bamboy.bimage.page.media.bean.FileBean;
import com.bamboy.bimage.util.MediaUtil;
import com.bamboy.bimage.view.freedom.freedom.FreedomBean;
import com.bamboy.bimage.view.freedom.freedom.ViewHolderManager;
import com.bumptech.glide.Glide;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class FItemVideo extends FreedomBean {
    /**
     * 图片对象
     */
    private FileBean fileBean;

    /**
     * 构造
     *
     * @param fileBean 文件夹对象
     */
    public FItemVideo(FileBean fileBean) {
        this.fileBean = fileBean;
    }

    /**
     * 图片对象
     */
    public FileBean getFileBean() {
        return fileBean;
    }

    /**
     * 图片对象
     */
    public void setFileBean(FileBean fileBean) {
        this.fileBean = fileBean;
    }

    //==============================================================================================
    //======================= 以 下 是 关 于 条 目 所 需 内 容 ========================================
    //==============================================================================================

    @Override
    protected void initItemType() {
        ViewHolderManager.addItem(this.getClass().toString(), ViewHolder.class);
        setItemType(this.getClass().toString());
    }

    @Override
    public int getSpanSize(int spanCount) {
        return super.getSpanSize(1);
    }

    @Override
    protected void initBindView(List list) {
        setViewHolderBindListener((Context context, ViewHolderManager.ViewHolder viewHolder, int position) -> {
            final ViewHolder vh = (ViewHolder) viewHolder;

            Glide.with(context).load(getFileBean().getPath()).into(vh.iv_photo);

            vh.tv_name.setText(getFileBean().getName());
            vh.tv_name.getPaint().setFakeBoldText(true);
            vh.tv_des.setText(getFileInfo());

            vh.rl_root.setOnClickListener((View view) ->
                    getCallback(context).onClickCallback(vh.rl_root, position, vh));
        });
    }

    /**
     * 换算单位
     */
    private final int unit = 1000;

    /**
     * 获取文件信息
     */
    private String getFileInfo() {
        if (getFileBean() == null) {
            return "";
        }

        StringBuffer info = new StringBuffer();

        // 文件大小
        long length = getFileBean().getSize();
        if (length > unit * unit * unit) {
            // 大于1G，显示 *
            float g = length / ((float) unit * unit * unit);
            g = Math.round(g * 100) / 100f;
            info.append(g)
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

        // 如果视频时长仍未获取，则先不获取
        if (getFileBean().getDuration() == 0) {
            if (!MediaUtil.getInstance().videoDurationMap.containsKey(getFileBean().getPath())) {
                return info.toString();
            }
            getFileBean().setDuration(MediaUtil.getInstance().videoDurationMap.get(getFileBean().getPath()));
        }

        info.append("   ");

        // 格式
        String pattern = "mm:ss";
        long time = getFileBean().getDuration() / 1000;
        if ((int) (time / (60 * 60)) > 0) {
            // 时长超过1小时，则用添加小时格式
            pattern = "HH:mm:ss";
        }

        // 格式化
        SimpleDateFormat formatter = new SimpleDateFormat(pattern);
        // 设置时区
        formatter.setTimeZone(TimeZone.getTimeZone("Etc/GMT-0"));
        // 视频时长字符串
        String timeStr = formatter.format(new Date(getFileBean().getDuration()));
        info.append(timeStr);

        return info.toString();
    }

    /**
     * ViewHolder
     */
    public static class ViewHolder extends ViewHolderManager.ViewHolder {

        public RelativeLayout rl_root;
        public ImageView iv_photo;
        public TextView tv_name;
        public TextView tv_des;

        public ViewHolder(ViewGroup viewGroup) {
            super(viewGroup, R.layout.fitem_video);

            rl_root = itemView.findViewById(R.id.rl_root);
            iv_photo = itemView.findViewById(R.id.iv_photo);
            tv_name = itemView.findViewById(R.id.tv_name);
            tv_des = itemView.findViewById(R.id.tv_des);
        }
    }
}
