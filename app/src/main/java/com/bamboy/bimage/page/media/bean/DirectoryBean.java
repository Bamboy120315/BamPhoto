package com.bamboy.bimage.page.media.bean;

import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;

import com.bamboy.bimage.util.MediaUtil;
import com.bamboy.bimage.util.NullUtil;

import java.util.ArrayList;
import java.util.List;

public class DirectoryBean {
    /**
     * 文件夹路径
     */
    private String path;
    /**
     * 文件夹名称
     */
    private String name;
    /**
     * 文件列表
     */
    private List<FileBean> files;
    /**
     * 媒体类型
     * 【1：图片】
     * 【2：视频】
     */
    private int mediaType;
    /**
     * 视频主图
     */
    private Bitmap videoBitmap;

    /**
     * 构造
     */
    public DirectoryBean() {
        files = new ArrayList<>();
    }

    /**
     * 构造
     *
     * @param path 文件夹路径
     * @param name 文件夹名称
     */
    public DirectoryBean(String path, String name) {
        this.path = path;
        this.name = name;
        files = new ArrayList<>();
    }

    // =============================================================================================
    // ====================== get、set ==============================

    /**
     * 文件夹路径
     */
    public String getPath() {
        return path;
    }

    /**
     * 文件夹路径
     */
    public void setPath(String path) {
        this.path = path;
    }

    /**
     * 文件夹名称
     */
    public String getName() {
        return name;
    }

    /**
     * 文件夹名称
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 文件列表
     */
    public List<FileBean> getFiles() {
        return files;
    }

    /**
     * 文件列表
     */
    public void setFiles(List<FileBean> files) {
        this.files = files;
    }

    /**
     * 媒体类型
     * 【1：图片】
     * 【2：视频】
     */
    public int getMediaType() {
        return mediaType;
    }

    /**
     * 媒体类型
     * 【1：图片】
     * 【2：视频】
     */
    public void setMediaType(int mediaType) {
        this.mediaType = mediaType;
    }

    /**
     * 视频主图
     */
    public Bitmap getVideoBitmap() {
        if (videoBitmap != null) {
            return videoBitmap;
        }
        if (getMediaType() == 2) {
            // 获取最大关键帧
            initVideoBitmap();
        }
        return videoBitmap;
    }

    /**
     * 获取最大关键帧
     */
    public void initVideoBitmap() {
        if (videoBitmap == null) {
            videoBitmap = MediaUtil.getInstance().getFrame(getMainPicture(), -1);
        }
    }

    /**
     * 视频主图
     */
    public void setVideoBitmap(Bitmap videoBitmap) {
        this.videoBitmap = videoBitmap;
    }

    /**
     * 获取主图
     *
     * @return
     */
    public String getMainPicture() {
        if (NullUtil.isNull(files)) {
            return null;
        }

        FileBean mainFileBean = files.get(0);

        if (mainFileBean == null) {
            return null;
        }

        return mainFileBean.getPath();
    }
}
