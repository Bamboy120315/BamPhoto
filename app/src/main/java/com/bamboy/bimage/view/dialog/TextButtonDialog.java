package com.bamboy.bimage.view.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bamboy.bimage.R;
import com.bamboy.bimage.util.NullUtil;

public class TextButtonDialog extends Dialog {

    /**
     * 自身对象
     */
    private TextButtonDialog mDialog;
    /**
     * 是否是短按钮模式
     */
    private boolean mIsShort = false;

    /**
     * 标题
     */
    private TextView tv_titlel;
    /**
     * 内容
     */
    private TextView tv_content;
    /**
     * 取消按钮
     */
    private TextView btn_left;
    /**
     * 确定按钮
     */
    private TextView btn_right;
    /**
     * 单个短按钮
     */
    private TextView btn_single;

    /**
     * 构造
     *
     * @param context 上下文
     */
    public TextButtonDialog(Context context) {
        super(context, R.style.dialog_text);
        init();
    }

    /**
     * 构造
     *
     * @param context 上下文
     * @param title   标题
     */
    public TextButtonDialog(Context context, String title) {
        super(context, R.style.dialog_text);
        init();

        setTitle(title);
        setContentText("");
        setBtnTextToLeft("");
        setBtnTextToRight("");
    }

    /**
     * 构造
     *
     * @param context 上下文
     * @param title   标题
     */
    public TextButtonDialog(Context context, String title, String content, String left, String right) {
        super(context, R.style.dialog_text);
        init();

        setTitle(title);
        setContentText(content);
        setBtnTextToLeft(left);
        setBtnTextToRight(right);
    }

    /**
     * 构造
     *
     * @param context       上下文
     * @param title         标题
     * @param content       内容
     * @param singleBtnText 单个短按钮文案
     */
    public TextButtonDialog(Context context, String title, String content, String singleBtnText) {
        super(context, R.style.dialog_text);
        mIsShort = true;
        init();

        setTitle(title);
        setContentText(content);
        setBtnTextToSingle(singleBtnText);
    }

    @Override
    public void show() {
        if (getContext() == null || (getContext() instanceof Activity && ((Activity) getContext()).isFinishing())) {
            return;
        }
        super.show();
    }

    /**
     * 创建Dialog
     *
     * @return Dialog
     */
    private TextButtonDialog createDialog() {
        LayoutInflater inflater = (LayoutInflater) getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(mIsShort ? R.layout.dialog_text_short_button : R.layout.dialog_text_button, null);
        addContentView(layout, new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        tv_titlel = layout.findViewById(R.id.tv_title);
        tv_content = layout.findViewById(R.id.tv_content);

        if (mIsShort) {
            btn_single = layout.findViewById(R.id.btn_single);
            // 加粗
            btn_single.getPaint().setFakeBoldText(true);
        } else {
            btn_left = layout.findViewById(R.id.btn_left);
            btn_right = layout.findViewById(R.id.btn_right);
            // 加粗
            btn_left.getPaint().setFakeBoldText(true);
            btn_right.getPaint().setFakeBoldText(true);
        }

        setContentView(layout);

        return this;
    }

    /**
     * 初始化数据
     */
    private void init() {
        mDialog = this;
        createDialog();

        if (mIsShort) {
            setCallback(new DialogSingleBtnCallback() {
                @Override
                public void onBtn(Dialog dialog) {
                    dismiss();
                }
            });
        } else {
            setCallback(new DialogCallback() {
                @Override
                public void onRight(Dialog dialog) {
                    dismiss();
                }

                @Override
                public void onLeft(Dialog dialog) {
                    dismiss();
                }
            });
        }
    }

    /**
     * 设置标题文字
     *
     * @param title 标题文字
     */
    public TextButtonDialog setTitle(String title) {
        if (NullUtil.isNull(title)) {
            tv_titlel.setVisibility(View.GONE);
            if (mIsShort) {
                // 如果没有标题，则调大内容的字号
                tv_content.setTextSize(16f);
            }
        } else {
            tv_titlel.setText(title);
            tv_titlel.setVisibility(View.VISIBLE);
        }
        return this;
    }

    /**
     * 设置标题文字
     *
     * @param contentText 内容文字
     */
    public TextButtonDialog setContentText(String contentText) {
        if (NullUtil.isNull(contentText)) {
            tv_content.setVisibility(View.GONE);
        } else {
            tv_content.setText(contentText);
            tv_content.setVisibility(View.VISIBLE);
        }
        return this;
    }

    /**
     * 设置右边按钮文字
     *
     * @param btnText 按钮文字
     */
    public TextButtonDialog setBtnTextToRight(String btnText) {
        if (NullUtil.isNull(btnText)) {
            btn_right.setVisibility(View.GONE);
        } else {
            btn_right.setText(btnText);
            btn_right.setVisibility(View.VISIBLE);
        }

        return this;
    }

    /**
     * 设置左边按钮文字
     *
     * @param btnText 按钮文字
     */
    public TextButtonDialog setBtnTextToLeft(String btnText) {
        if (NullUtil.isNull(btnText)) {
            btn_left.setVisibility(View.GONE);
        } else {
            btn_left.setText(btnText);
            btn_left.setVisibility(View.VISIBLE);
        }

        return this;
    }

    /**
     * 设置单个短边按钮的文字
     *
     * @param btnText 按钮文字
     */
    public TextButtonDialog setBtnTextToSingle(String btnText) {
        if (NullUtil.isNull(btnText)) {
            btn_single.setVisibility(View.GONE);
        } else {
            btn_single.setText(btnText);
            btn_single.setVisibility(View.VISIBLE);
        }

        return this;
    }

    /**
     * 设置右边按钮文本颜色
     *
     * @param color 颜色值
     * @return
     */
    public TextButtonDialog setBtnColorToRight(int color) {
        btn_right.setTextColor(color);
        return this;
    }

    /**
     * 设置左边按钮文本颜色
     *
     * @param color 颜色值
     * @return
     */
    public TextButtonDialog setBtnColorToLeft(int color) {
        btn_left.setTextColor(color);
        return this;
    }

    /**
     * 设置单个短按钮的文本颜色
     *
     * @param color 颜色值
     * @return
     */
    public TextButtonDialog setBtnColorToSingle(int color) {
        btn_single.setTextColor(color);
        return this;
    }

    /**
     * 设置点击返回按钮是否消失
     *
     * @param isDismiss
     * @return
     */
    public TextButtonDialog setReturnButtonDismiss(boolean isDismiss) {
        setCancelable(isDismiss);
        return this;
    }

    /**
     * 设置按钮点击事件
     *
     * @param callback 点击事件
     */
    public TextButtonDialog setCallback(final DialogCallback callback) {
        if (btn_right != null) {
            btn_right.setOnClickListener((View v) -> {
                if (callback != null)
                    callback.onRight(mDialog);
            });
        }

        if (btn_left != null) {
            btn_left.setOnClickListener((View v) -> {
                dismiss();
                if (callback != null)
                    callback.onLeft(mDialog);
            });
        }
        return this;
    }

    /**
     * 设置按钮点击事件
     *
     * @param callback 点击事件
     */
    public TextButtonDialog setCallback(final DialogSingleBtnCallback callback) {
        if (btn_single != null) {
            btn_single.setOnClickListener((View v) -> {
                if (callback != null)
                    callback.onBtn(mDialog);
            });
        }
        return this;
    }

    /**
     * 按钮回调
     */
    public abstract static class DialogCallback {

        /**
         * 弹窗右边按钮回调
         */
        public abstract void onRight(Dialog dialog);

        /**
         * 弹窗左边按钮回调
         */
        public abstract void onLeft(Dialog dialog);
    }

    /**
     * 按钮回调
     */
    public abstract static class DialogSingleBtnCallback {

        /**
         * 按钮回调
         */
        public abstract void onBtn(Dialog dialog);
    }
}