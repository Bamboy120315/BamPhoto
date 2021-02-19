package com.bamboy.bimage.page.main.fitem;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bamboy.bimage.R;
import com.bamboy.bimage.view.freedom.freedom.FreedomBean;
import com.bamboy.bimage.view.freedom.freedom.ViewHolderManager;

import java.util.List;

public class FItemMainBtn extends FreedomBean {

    /**
     * 图片资源ID
     */
    private int imgResource;
    /**
     * 按钮标题
     */
    private String title;
    /**
     * 按钮功能类型
     */
    private int btnType;

    /**
     * 构造
     *
     * @param imgResource 图片资源ID
     * @param title       按钮标题
     * @param btnType     按钮功能类型
     */
    public FItemMainBtn(int imgResource, String title, int btnType) {
        this.imgResource = imgResource;
        this.title = title;
        this.btnType = btnType;
    }

    /**
     * 图片资源ID
     */
    public int getImgResource() {
        return imgResource;
    }

    /**
     * 图片资源ID
     */
    public void setImgResource(int imgResource) {
        this.imgResource = imgResource;
    }

    /**
     * 按钮标题
     */
    public String getTitle() {
        return title;
    }

    /**
     * 按钮标题
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * 按钮功能类型
     */
    public int getBtnType() {
        return btnType;
    }

    /**
     * 按钮功能类型
     */
    public void setBtnType(int btnType) {
        this.btnType = btnType;
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

            vh.iv_logo.setImageResource(getImgResource());

            vh.tv_title.setText(getTitle());

            vh.rl_root.setOnClickListener((View view) ->
                    getCallback(context).onClickCallback(vh.rl_root, position, vh));

        });
    }

    /**
     * ViewHolder
     */
    public static class ViewHolder extends ViewHolderManager.ViewHolder {

        public RelativeLayout rl_root;
        public ImageView iv_logo;
        public TextView tv_title;

        public ViewHolder(ViewGroup viewGroup) {
            super(viewGroup, R.layout.fitem_main_btn);

            rl_root = itemView.findViewById(R.id.rl_root);
            iv_logo = itemView.findViewById(R.id.iv_logo);
            tv_title = itemView.findViewById(R.id.tv_title);
        }
    }
}
