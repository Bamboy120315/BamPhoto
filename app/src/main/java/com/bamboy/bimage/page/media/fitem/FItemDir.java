package com.bamboy.bimage.page.media.fitem;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bamboy.bimage.R;
import com.bamboy.bimage.page.media.bean.DirectoryBean;
import com.bamboy.bimage.util.NullUtil;
import com.bamboy.bimage.view.freedom.freedom.FreedomBean;
import com.bamboy.bimage.view.freedom.freedom.ViewHolderManager;
import com.bumptech.glide.Glide;

import java.util.List;

public class FItemDir extends FreedomBean {
    /**
     * 最大高度
     */
    private int maxHeight;

    /**
     * 文件夹对象
     */
    private DirectoryBean directoryBean;

    /**
     * 构造
     *
     * @param directoryBean 文件夹对象
     */
    public FItemDir(DirectoryBean directoryBean) {
        this.directoryBean = directoryBean;
    }

    /**
     * 文件夹对象
     */
    public DirectoryBean getDirectoryBean() {
        return directoryBean;
    }

    /**
     * 文件夹对象
     */
    public void setDirectoryBean(DirectoryBean directoryBean) {
        this.directoryBean = directoryBean;
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

            if (getDirectoryBean().getMediaType() == 2) {
                // 如果条目是视频

                // 计算最大高度
                if (maxHeight == 0) {
                    maxHeight = vh.iv_main_picture.getMaxHeight();
                    maxHeight = maxHeight / 3 * 5;
                }
                vh.iv_main_picture.setMaxHeight(maxHeight);
            }

            // 主图
            String mainPicture = getDirectoryBean().getMainPicture();
            if (NullUtil.isNull(mainPicture)) {
                vh.iv_main_picture.setImageResource(R.drawable.ic_btn_type_picture);
            } else {
                Glide.with(context).load(mainPicture).into(vh.iv_main_picture);
            }

            vh.tv_name.setText(getDirectoryBean().getName());
            vh.tv_name.getPaint().setFakeBoldText(true);

            vh.rl_root.setOnClickListener((View view) ->
                    getCallback(context).onClickCallback(vh.rl_root, position, vh));

        });
    }

    /**
     * ViewHolder
     */
    public static class ViewHolder extends ViewHolderManager.ViewHolder {

        public RelativeLayout rl_root;
        public ImageView iv_main_picture;
        public TextView tv_name;

        public ViewHolder(ViewGroup viewGroup) {
            super(viewGroup, R.layout.fitem_dir);

            rl_root = itemView.findViewById(R.id.rl_root);
            iv_main_picture = itemView.findViewById(R.id.iv_main_picture);
            tv_name = itemView.findViewById(R.id.tv_name);
        }
    }
}
