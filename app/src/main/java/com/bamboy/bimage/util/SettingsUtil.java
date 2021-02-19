package com.bamboy.bimage.util;

import android.content.SharedPreferences;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.bamboy.bimage.page.application.BaseApplication;
import com.bamboy.bimage.page.bean.Settings;

import static android.content.Context.MODE_PRIVATE;

public class SettingsUtil {

    /**
     * 配置对象
     */
    private Settings settings;

    /**
     * 单例工厂
     */
    private static class SettingsUtilHolder {
        private static final SettingsUtil INSTANCE = new SettingsUtil();
    }

    /**
     * 获取单例
     *
     * @return
     */
    public static final SettingsUtil getInstance() {
        return SettingsUtilHolder.INSTANCE;
    }

    /**
     * 构造
     */
    private SettingsUtil() {
        // 获取配置
        getFlieDownloadData();

        // 如果配置为空，则创建新的并保存
        if (settings == null) {
            settings = new Settings();
            saveFlieDownloadData();
        }
    }

    // =====================================================================
    // ======================= 以下是关于数据存储 ============================
    // =====================================================================

    /**
     * 视频数据的key
     */
    private final String KEY_SETTINGS = "Settings";

    /**
     * 获取配置数据
     */
    public void getFlieDownloadData() {
        SharedPreferences preferences = BaseApplication.getInstance().getSharedPreferences(KEY_SETTINGS, MODE_PRIVATE);
        String data = preferences.getString(KEY_SETTINGS, "");

        settings = JSONArray.parseObject(data, Settings.class);
    }

    /**
     * 保存配置数据
     */
    public void saveFlieDownloadData() {
        if (settings == null) {
            settings = new Settings();
        }
        String data = JSON.toJSONString(settings);
        if (NullUtil.isNull(data)) {
            return;
        }
        // 保存
        SharedPreferences.Editor editor = BaseApplication.getInstance().getSharedPreferences(KEY_SETTINGS, MODE_PRIVATE).edit();
        editor.putString(KEY_SETTINGS, data);
        editor.apply();
    }

    // =====================================================================
    // ======================= 以下是关于配置存取 ============================
    // =====================================================================

    /**
     * 是否显示系统文件
     *
     * @return
     */
    public boolean isShowSystemFile() {
        return settings.isShowSystemFile();
    }

    /**
     * 更新配置
     *
     * @param showSystemFile
     */
    public void setShowSystemFile(boolean showSystemFile) {
        if (showSystemFile == settings.isShowSystemFile()) {
            return;
        }

        settings.setShowSystemFile(showSystemFile);
        saveFlieDownloadData();
    }


}
