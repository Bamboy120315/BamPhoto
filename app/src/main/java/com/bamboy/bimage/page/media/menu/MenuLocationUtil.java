package com.bamboy.bimage.page.media.menu;

import java.util.HashMap;
import java.util.Map;

public class MenuLocationUtil {
    /**
     * 参数 占位View X坐标
     */
    public final static String PARAM_ITEMVIEW_X = "itemViewX";
    /**
     * 参数 占位View Y坐标
     */
    public final static String PARAM_ITEMVIEW_Y = "itemViewY";
    /**
     * 参数 占位View 宽
     */
    public final static String PARAM_ITEMVIEW_WIDTH = "itemViewWidth";
    /**
     * 参数 占位View 高
     */
    public final static String PARAM_ITEMVIEW_HEIGHT = "itemViewHeight";
    /**
     * 参数 菜单按钮 宽
     */
    public final static String PARAM_BTN_WIDTH = "btnWidth";
    /**
     * 参数 菜单按钮 高
     */
    public final static String PARAM_BTN_HEIGHT = "btnHeight";
    /**
     * 参数 菜单按钮 间距
     */
    public final static String PARAM_BTN_MARGIN = "btnMargin";
    /**
     * 参数 顶部的空间
     */
    public final static String PARAM_MARGIN_TOP = "marginTop";
    /**
     * 参数 底部的空间
     */
    public final static String PARAM_MARGIN_BOTTOM = "marginBottom";

    /**
     * 获取位置信息 --> 上边
     *
     * @param param
     * @return
     */
    public static Map<String, Integer> getInfoToTop(Map<String, Float> param) {
        // 隐私按钮坐标
        float privateLeft = param.get(PARAM_ITEMVIEW_X) + param.get(PARAM_ITEMVIEW_WIDTH) / 2 - param.get(PARAM_BTN_WIDTH) / 2;
        float privateTop = param.get(PARAM_ITEMVIEW_Y) - param.get(PARAM_BTN_HEIGHT) - param.get(PARAM_BTN_MARGIN) * 3;

        // 重命名按钮坐标
        float renameLeft = privateLeft - param.get(PARAM_BTN_WIDTH) - param.get(PARAM_BTN_MARGIN);
        float renameTop = privateTop + param.get(PARAM_BTN_MARGIN) * 2;

        // 删除按钮坐标
        float deleteLeft = privateLeft + param.get(PARAM_BTN_WIDTH) + param.get(PARAM_BTN_MARGIN);
        float deleteTop = privateTop + param.get(PARAM_BTN_MARGIN) * 2;

        return getMap(renameLeft, renameTop, privateLeft, privateTop, deleteLeft, deleteTop);
    }

    /**
     * 获取位置信息 --> 下边
     *
     * @param param
     * @return
     */
    public static Map<String, Integer> getInfoToBottom(Map<String, Float> param) {
        // 隐私按钮坐标
        float privateLeft = param.get(PARAM_ITEMVIEW_X) + param.get(PARAM_ITEMVIEW_WIDTH) / 2 - param.get(PARAM_BTN_WIDTH) / 2;
        float privateTop = param.get(PARAM_ITEMVIEW_Y) + param.get(PARAM_ITEMVIEW_HEIGHT) + param.get(PARAM_BTN_MARGIN) * 3;

        // 重命名按钮坐标
        float renameLeft = privateLeft - param.get(PARAM_BTN_WIDTH) - param.get(PARAM_BTN_MARGIN);
        float renameTop = privateTop - param.get(PARAM_BTN_MARGIN) * 2;

        // 删除按钮坐标
        float deleteLeft = privateLeft + param.get(PARAM_BTN_WIDTH) + param.get(PARAM_BTN_MARGIN);
        float deleteTop = privateTop - param.get(PARAM_BTN_MARGIN) * 2;

        return getMap(renameLeft, renameTop, privateLeft, privateTop, deleteLeft, deleteTop);
    }

    /**
     * 获取位置信息 --> 左边
     *
     * @param param
     * @return
     */
    public static Map<String, Integer> getInfoToLeft(Map<String, Float> param) {
        // 显示菜单的空间
        float menuSpaceHeight = param.get(PARAM_ITEMVIEW_HEIGHT);
        if (param.get(PARAM_MARGIN_TOP) < 0) {
            menuSpaceHeight += Math.abs(param.get(PARAM_MARGIN_TOP));
        } else if (param.get(PARAM_MARGIN_BOTTOM) < 0) {
            menuSpaceHeight -= Math.abs(param.get(PARAM_MARGIN_BOTTOM));
        }

        // 隐私按钮坐标
        float privateLeft = param.get(PARAM_ITEMVIEW_X) - param.get(PARAM_BTN_WIDTH) - param.get(PARAM_BTN_MARGIN) * 3;
        float privateTop = param.get(PARAM_ITEMVIEW_Y) + menuSpaceHeight / 2 - param.get(PARAM_BTN_HEIGHT) / 2;

        // 重命名按钮坐标
        float renameLeft = privateLeft + param.get(PARAM_BTN_MARGIN) * 2;
        float renameTop = privateTop - param.get(PARAM_BTN_HEIGHT) - param.get(PARAM_BTN_MARGIN);

        // 删除按钮坐标
        float deleteLeft = privateLeft + param.get(PARAM_BTN_MARGIN) * 2;
        float deleteTop = privateTop + param.get(PARAM_BTN_HEIGHT) + param.get(PARAM_BTN_MARGIN);

        return getMap(renameLeft, renameTop, privateLeft, privateTop, deleteLeft, deleteTop);
    }

    /**
     * 获取位置信息 --> 右边
     *
     * @param param
     * @return
     */
    public static Map<String, Integer> getInfoToRight(Map<String, Float> param) {
        // 显示菜单的空间
        float menuSpaceHeight = param.get(PARAM_ITEMVIEW_HEIGHT);
        if (param.get(PARAM_MARGIN_TOP) < 0) {
            menuSpaceHeight += Math.abs(param.get(PARAM_MARGIN_TOP));
        } else if (param.get(PARAM_MARGIN_BOTTOM) < 0) {
            menuSpaceHeight -= Math.abs(param.get(PARAM_MARGIN_BOTTOM));
        }

        // 隐私按钮坐标
        float privateLeft = param.get(PARAM_ITEMVIEW_X) + param.get(PARAM_ITEMVIEW_WIDTH) + param.get(PARAM_BTN_MARGIN) * 3;
        float privateTop = param.get(PARAM_ITEMVIEW_Y) + menuSpaceHeight / 2 - param.get(PARAM_BTN_HEIGHT) / 2;

        // 重命名按钮坐标
        float renameLeft = privateLeft - param.get(PARAM_BTN_MARGIN) * 2;
        float renameTop = privateTop - param.get(PARAM_BTN_HEIGHT) - param.get(PARAM_BTN_MARGIN);

        // 删除按钮坐标
        float deleteLeft = privateLeft - param.get(PARAM_BTN_MARGIN) * 2;
        float deleteTop = privateTop + param.get(PARAM_BTN_HEIGHT) + param.get(PARAM_BTN_MARGIN);

        return getMap(renameLeft, renameTop, privateLeft, privateTop, deleteLeft, deleteTop);
    }

    /**
     * 获取位置信息 --> 左上
     *
     * @param param
     * @return
     */
    public static Map<String, Integer> getInfoToLeftTop(Map<String, Float> param) {
        // 隐私按钮坐标
        float privateLeft = param.get(PARAM_ITEMVIEW_X) - param.get(PARAM_BTN_WIDTH);
        float privateTop = param.get(PARAM_ITEMVIEW_Y) - param.get(PARAM_BTN_HEIGHT);

        // 重命名按钮坐标
        float renameLeft = privateLeft - param.get(PARAM_BTN_MARGIN);
        float renameTop = privateTop + param.get(PARAM_BTN_HEIGHT) + param.get(PARAM_BTN_MARGIN);

        // 删除按钮坐标
        float deleteLeft = privateLeft + param.get(PARAM_BTN_WIDTH) + param.get(PARAM_BTN_MARGIN);
        float deleteTop = privateTop - param.get(PARAM_BTN_MARGIN);

        return getMap(renameLeft, renameTop, privateLeft, privateTop, deleteLeft, deleteTop);
    }

    /**
     * 获取位置信息 --> 左下
     *
     * @param param
     * @return
     */
    public static Map<String, Integer> getInfoToLeftBottom(Map<String, Float> param) {
        // 隐私按钮坐标
        float privateLeft = param.get(PARAM_ITEMVIEW_X) - param.get(PARAM_BTN_WIDTH);
        float privateTop = param.get(PARAM_ITEMVIEW_Y) + param.get(PARAM_ITEMVIEW_HEIGHT);

        // 重命名按钮坐标
        float renameLeft = privateLeft - param.get(PARAM_BTN_MARGIN);
        float renameTop = privateTop - param.get(PARAM_BTN_HEIGHT) - param.get(PARAM_BTN_MARGIN);

        // 删除按钮坐标
        float deleteLeft = privateLeft + param.get(PARAM_BTN_WIDTH) + param.get(PARAM_BTN_MARGIN);
        float deleteTop = privateTop + param.get(PARAM_BTN_MARGIN);

        return getMap(renameLeft, renameTop, privateLeft, privateTop, deleteLeft, deleteTop);
    }

    /**
     * 获取位置信息 --> 右上
     *
     * @param param
     * @return
     */
    public static Map<String, Integer> getInfoToRightTop(Map<String, Float> param) {
        // 隐私按钮坐标
        float privateLeft = param.get(PARAM_ITEMVIEW_X) + param.get(PARAM_ITEMVIEW_WIDTH);
        float privateTop = param.get(PARAM_ITEMVIEW_Y) - param.get(PARAM_BTN_HEIGHT);

        // 重命名按钮坐标
        float renameLeft = privateLeft - param.get(PARAM_BTN_WIDTH) - param.get(PARAM_BTN_MARGIN);
        float renameTop = privateTop - param.get(PARAM_BTN_MARGIN);

        // 删除按钮坐标
        float deleteLeft = privateLeft + param.get(PARAM_BTN_MARGIN);
        float deleteTop = privateTop + param.get(PARAM_BTN_HEIGHT) + param.get(PARAM_BTN_MARGIN);

        return getMap(renameLeft, renameTop, privateLeft, privateTop, deleteLeft, deleteTop);
    }

    /**
     * 获取位置信息 --> 右下
     *
     * @param param
     * @return
     */
    public static Map<String, Integer> getInfoToRightBottom(Map<String, Float> param) {
        // 隐私按钮坐标
        float privateLeft = param.get(PARAM_ITEMVIEW_X) + param.get(PARAM_ITEMVIEW_WIDTH);
        float privateTop = param.get(PARAM_ITEMVIEW_Y) + param.get(PARAM_ITEMVIEW_HEIGHT);

        // 重命名按钮坐标
        float renameLeft = privateLeft + param.get(PARAM_BTN_MARGIN);
        float renameTop = privateTop - param.get(PARAM_BTN_HEIGHT) - param.get(PARAM_BTN_MARGIN);

        // 删除按钮坐标
        float deleteLeft = privateLeft - param.get(PARAM_BTN_WIDTH) - param.get(PARAM_BTN_MARGIN);
        float deleteTop = privateTop + param.get(PARAM_BTN_MARGIN);

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
