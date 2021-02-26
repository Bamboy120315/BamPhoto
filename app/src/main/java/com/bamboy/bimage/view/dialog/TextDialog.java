package com.bamboy.bimage.view.dialog;

import android.content.Context;
import android.content.DialogInterface;

import androidx.appcompat.app.AlertDialog;

public class TextDialog {

    /**
     * 弹出对话框
     *
     * @param context       上下文
     * @param touchCanceled 点击外部是否消失
     * @param title         标题
     * @param msg           内容
     * @param sureText      确定按钮文案
     * @param sureRun       确定按钮事件
     * @param cancelText    取消按钮文案
     * @param cancelRun     取消按钮文案
     */
    public void alert(Context context, boolean touchCanceled, String title, String msg, String sureText, Runnable sureRun, String cancelText, Runnable cancelRun) {
        AlertDialog.Builder builder =
                new AlertDialog.Builder(context);

        builder.setTitle(title);
        builder.setMessage(msg);

        // 确定的按钮
        builder.setPositiveButton(sureText,
                (DialogInterface dialog, int which) -> sureRun.run());

        // 左边的按钮
        builder.setNegativeButton(cancelText,
                (DialogInterface dialog, int which) -> {
                    dialog.dismiss();
                    cancelRun.run();
                });

        // 创建
        AlertDialog alertDialog = builder.create();
        // 设置点击外部是否消失
        alertDialog.setCanceledOnTouchOutside(touchCanceled);
        // 返回对话框
        alertDialog.show();
    }
}
