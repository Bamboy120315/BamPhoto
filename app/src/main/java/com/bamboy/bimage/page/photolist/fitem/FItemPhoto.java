package com.bamboy.bimage.page.photolist.fitem;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bamboy.bimage.R;
import com.bamboy.bimage.page.media.bean.FileBean;
import com.bamboy.bimage.view.freedom.freedom.FreedomBean;
import com.bamboy.bimage.view.freedom.freedom.ViewHolderManager;
import com.bumptech.glide.Glide;

import java.util.List;

public class FItemPhoto extends FreedomBean {
    /**
     * 最大高度
     */
    private int maxHeight;

    /**
     * 图片对象
     */
    private FileBean fileBean;

    /**
     * 构造
     *
     * @param fileBean 文件夹对象
     */
    public FItemPhoto(FileBean fileBean) {
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

            vh.rl_root.setOnClickListener((View view) ->
                    getCallback(context).onClickCallback(vh.rl_root, position, vh));
        });
    }

    /**
     * ViewHolder
     */
    public static class ViewHolder extends ViewHolderManager.ViewHolder {

        public RelativeLayout rl_root;
        public ImageView iv_photo;

        public ViewHolder(ViewGroup viewGroup) {
            super(viewGroup, R.layout.fitem_photo);

            rl_root = itemView.findViewById(R.id.rl_root);
            iv_photo = itemView.findViewById(R.id.iv_photo);
        }
    }
}
