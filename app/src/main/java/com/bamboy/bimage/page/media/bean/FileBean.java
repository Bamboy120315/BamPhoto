package com.bamboy.bimage.page.media.bean;

public class FileBean {
    /**
     * 文件路径
     */
    private String path;
    /**
     * 文件名
     */
    private String name;
    /**
     * 文件更新时间
     */
    private long lastTime;
    /**
     * 视频文件的时长
     */
    private long duration;
    /**
     * 文件的大小
     */
    private long size;

    /**
     * 构造
     */
    public FileBean() {
    }

    /**
     * 构造
     *
     * @param path     文件路径
     * @param name     文件名
     * @param lastTime 文件更新时间
     * @param size     文件的大小
     */
    public FileBean(String path, String name, long lastTime, long size) {
        this.path = path;
        this.name = name;
        this.lastTime = lastTime;
        this.size = size;
    }

    /**
     * 构造
     *
     * @param path     文件路径
     * @param name     文件名
     * @param lastTime 文件更新时间
     * @param duration 视频文件的时长
     * @param size     文件的大小
     */
    public FileBean(String path, String name, long lastTime, long duration, long size) {
        this.path = path;
        this.name = name;
        this.lastTime = lastTime;
        this.duration = duration;
        this.size = size;
    }

    // =============================================================================================
    // ====================== get、set ==============================

    /**
     * 文件路径
     */
    public String getPath() {
        return path;
    }

    /**
     * 文件路径
     */
    public void setPath(String path) {
        this.path = path;
    }

    /**
     * 文件名
     */
    public String getName() {
        return name;
    }

    /**
     * 文件名
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 文件更新时间
     */
    public long getLastTime() {
        return lastTime;
    }

    /**
     * 文件更新时间
     */
    public void setLastTime(long lastTime) {
        this.lastTime = lastTime;
    }

    /**
     * 视频文件的时长
     */
    public long getDuration() {
        return duration;
    }

    /**
     * 视频文件的时长
     */
    public void setDuration(long duration) {
        this.duration = duration;
    }

    /**
     * 文件的大小
     */
    public long getSize() {
        return size;
    }

    /**
     * 文件的大小
     */
    public void setSize(long size) {
        this.size = size;
    }
}
