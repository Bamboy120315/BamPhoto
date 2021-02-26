package com.bamboy.bimage.page.media.menu;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;

import java.util.HashMap;
import java.util.Map;

public class MenuLocationUtil {
    /**
     * 上下文
     */
    private Context context;
    /**
     * 按钮View
     */
    private View btnView;
    /**
     * 占位View宽度
     */
    private float itemViewWidth;
    /**
     * 占位View高度
     */
    private float itemViewHeight;
    /**
     * 屏幕宽度
     */
    private float displayWidth;
    /**
     * 屏幕高度
     */
    private float displayHeight;
    /**
     * 参数 占位View X坐标
     */
    private int[] itemViewLocation;
    private float marginLeft, marginTop, marginRight, marginBottom, btnWidth, btnHeight, btnMargin;

    public MenuLocationUtil(Context context, View btnView, float itemViewWidth, float itemViewHeight, int[] itemViewLocation) {
        this.context = context;
        this.btnView = btnView;
        this.itemViewWidth = itemViewWidth;
        this.itemViewHeight = itemViewHeight;
        this.itemViewLocation = itemViewLocation;

        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);
        // 屏幕宽度
        displayWidth = dm.widthPixels;
        // 屏幕高度
        displayHeight = dm.heightPixels;

        marginLeft = itemViewLocation[0];
        marginTop = itemViewLocation[1];
        marginRight = (int) (displayWidth - (itemViewLocation[0] + itemViewWidth));
        marginBottom = (int) (displayHeight - (itemViewLocation[1] + itemViewHeight));

        btnWidth = btnView.getWidth();
        btnHeight = btnView.getHeight();
        btnMargin = btnWidth * 0.25f;
    }

    /**
     * 初始化位置数据
     *
     * @return
     */
    public Map<String, Integer> initMenuLocation() {
        Map<String, Integer> locationParam;

        if (marginLeft > btnWidth && marginRight > btnWidth && Math.abs(marginLeft - marginRight) < displayWidth * 0.5) {
            // 上下模式

            if (itemViewLocation[1] > (displayHeight - itemViewLocation[1])) {
                // 上边
                locationParam = getInfoToTop();
            } else {
                // 下边
                locationParam = getInfoToBottom();
            }
        } else if (marginTop > btnWidth && marginBottom > btnWidth && Math.abs(itemViewLocation[1] - (displayHeight - itemViewLocation[1])) < displayHeight * 0.58) {
            // 左右模式

            if (marginLeft > marginRight) {
                // 左边
                locationParam = getInfoToLeft();
            } else {
                // 右边
                locationParam = getInfoToRight();
            }
        } else if (itemViewHeight > btnHeight * 5 && marginTop > 0 - btnHeight * 3 && (displayHeight - itemViewLocation[1]) > btnHeight * 4) {
            // 左右模式

            if (marginLeft > marginRight) {
                // 左边
                locationParam = getInfoToLeft();
            } else {
                // 右边
                locationParam = getInfoToRight();
            }
        } else if (marginLeft > marginRight) {
            if (marginTop > marginBottom) {
                // 四角模式  左上方
                locationParam = getInfoToLeftTop();
            } else {
                // 四角模式  左下方
                locationParam = getInfoToLeftBottom();
            }
        } else {
            if (marginTop > marginBottom) {
                // 四角模式  右上方
                locationParam = getInfoToRightTop();
            } else {
                // 四角模式  右下方
                locationParam = getInfoToRightBottom();
            }
        }

        return locationParam;
    }

    /**
     * 获取位置信息 --> 上边
     *
     * @return
     */
    private Map<String, Integer> getInfoToTop() {
        // 隐私按钮坐标
        float privateLeft = itemViewLocation[0] + itemViewWidth / 2 - btnWidth / 2;
        float privateTop = itemViewLocation[1] - btnHeight - btnMargin * 3;

        // 重命名按钮坐标
        float renameLeft = privateLeft - btnWidth - btnMargin;
        float renameTop = privateTop + btnMargin * 2;

        // 删除按钮坐标
        float deleteLeft = privateLeft + btnWidth + btnMargin;
        float deleteTop = privateTop + btnMargin * 2;

        return getMap(renameLeft, renameTop, privateLeft, privateTop, deleteLeft, deleteTop);
    }

    /**
     * 获取位置信息 --> 下边
     *
     * @return
     */
    private Map<String, Integer> getInfoToBottom() {
        // 隐私按钮坐标
        float privateLeft = itemViewLocation[0] + itemViewWidth / 2 - btnWidth / 2;
        float privateTop = itemViewLocation[1] + itemViewHeight + btnMargin * 3;

        // 重命名按钮坐标
        float renameLeft = privateLeft - btnWidth - btnMargin;
        float renameTop = privateTop - btnMargin * 2;

        // 删除按钮坐标
        float deleteLeft = privateLeft + btnWidth + btnMargin;
        float deleteTop = privateTop - btnMargin * 2;

        return getMap(renameLeft, renameTop, privateLeft, privateTop, deleteLeft, deleteTop);
    }

    /**
     * 获取位置信息 --> 左边
     *
     * @return
     */
    private Map<String, Integer> getInfoToLeft() {
        // 显示菜单的空间
        float menuSpaceHeight = itemViewHeight;
        if (marginTop < 0) {
            menuSpaceHeight += Math.abs(marginTop);
        } else if (marginBottom < 0) {
            menuSpaceHeight -= Math.abs(marginBottom);
        }

        // 隐私按钮坐标
        float privateLeft = itemViewLocation[0] - btnWidth - btnMargin * 3;
        float privateTop = itemViewLocation[1] + menuSpaceHeight / 2 - btnHeight / 2;

        // 重命名按钮坐标
        float renameLeft = privateLeft + btnMargin * 2;
        float renameTop = privateTop - btnHeight - btnMargin;

        // 删除按钮坐标
        float deleteLeft = privateLeft + btnMargin * 2;
        float deleteTop = privateTop + btnHeight + btnMargin;

        return getMap(renameLeft, renameTop, privateLeft, privateTop, deleteLeft, deleteTop);
    }

    /**
     * 获取位置信息 --> 右边
     *
     * @return
     */
    private Map<String, Integer> getInfoToRight() {
        // 显示菜单的空间
        float menuSpaceHeight = itemViewHeight;
        if (marginTop < 0) {
            menuSpaceHeight += Math.abs(marginTop);
        } else if (marginBottom < 0) {
            menuSpaceHeight -= Math.abs(marginBottom);
        }

        // 隐私按钮坐标
        float privateLeft = itemViewLocation[0] + itemViewWidth + btnMargin * 3;
        float privateTop = itemViewLocation[1] + menuSpaceHeight / 2 - btnHeight / 2;

        // 重命名按钮坐标
        float renameLeft = privateLeft - btnMargin * 2;
        float renameTop = privateTop - btnHeight - btnMargin;

        // 删除按钮坐标
        float deleteLeft = privateLeft - btnMargin * 2;
        float deleteTop = privateTop + btnHeight + btnMargin;

        return getMap(renameLeft, renameTop, privateLeft, privateTop, deleteLeft, deleteTop);
    }

    /**
     * 获取位置信息 --> 左上
     *
     * @return
     */
    private Map<String, Integer> getInfoToLeftTop() {
        // 隐私按钮坐标
        float privateLeft = itemViewLocation[0] - btnWidth;
        float privateTop = itemViewLocation[1] - btnHeight;

        // 重命名按钮坐标
        float renameLeft = privateLeft - btnMargin;
        float renameTop = privateTop + btnHeight + btnMargin;

        // 删除按钮坐标
        float deleteLeft = privateLeft + btnWidth + btnMargin;
        float deleteTop = privateTop - btnMargin;

        return getMap(renameLeft, renameTop, privateLeft, privateTop, deleteLeft, deleteTop);
    }

    /**
     * 获取位置信息 --> 左下
     *
     * @return
     */
    private Map<String, Integer> getInfoToLeftBottom() {
        // 隐私按钮坐标
        float privateLeft = itemViewLocation[0] - btnWidth;
        float privateTop = itemViewLocation[1] + itemViewHeight;

        // 重命名按钮坐标
        float renameLeft = privateLeft - btnMargin;
        float renameTop = privateTop - btnHeight - btnMargin;

        // 删除按钮坐标
        float deleteLeft = privateLeft + btnWidth + btnMargin;
        float deleteTop = privateTop + btnMargin;

        return getMap(renameLeft, renameTop, privateLeft, privateTop, deleteLeft, deleteTop);
    }

    /**
     * 获取位置信息 --> 右上
     *
     * @return
     */
    private Map<String, Integer> getInfoToRightTop() {
        // 隐私按钮坐标
        float privateLeft = itemViewLocation[0] + itemViewWidth;
        float privateTop = itemViewLocation[1] - btnHeight;

        // 重命名按钮坐标
        float renameLeft = privateLeft - btnWidth - btnMargin;
        float renameTop = privateTop - btnMargin;

        // 删除按钮坐标
        float deleteLeft = privateLeft + btnMargin;
        float deleteTop = privateTop + btnHeight + btnMargin;

        return getMap(renameLeft, renameTop, privateLeft, privateTop, deleteLeft, deleteTop);
    }

    /**
     * 获取位置信息 --> 右下
     *
     * @return
     */
    private Map<String, Integer> getInfoToRightBottom() {
        // 隐私按钮坐标
        float privateLeft = itemViewLocation[0] + itemViewWidth;
        float privateTop = itemViewLocation[1] + itemViewHeight;

        // 重命名按钮坐标
        float renameLeft = privateLeft + btnMargin;
        float renameTop = privateTop - btnHeight - btnMargin;

        // 删除按钮坐标
        float deleteLeft = privateLeft - btnWidth - btnMargin;
        float deleteTop = privateTop + btnMargin;

        return getMap(renameLeft, renameTop, privateLeft, privateTop, deleteLeft, deleteTop);
    }


    /**
     * 转化成Map返回
     *
     * @param renameLeft
     * @param renameTop
     * @param privateLeft
     * @param privateTop
     * @param deleteLeft
     * @param deleteTop
     * @return
     */
    private static Map<String, Integer> getMap(
            float renameLeft, float renameTop,
            float privateLeft, float privateTop,
            float deleteLeft, float deleteTop) {

        Map<String, Integer> locationMap = new HashMap<>();
        locationMap.put("renameLeft", (int) renameLeft);
        locationMap.put("renameTop", (int) renameTop);
        locationMap.put("privateLeft", (int) privateLeft);
        locationMap.put("privateTop", (int) privateTop);
        locationMap.put("deleteLeft", (int) deleteLeft);
        locationMap.put("deleteTop", (int) deleteTop);
        return locationMap;
    }
}
