package com.bamboy.bimage.util;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;

import com.bamboy.bimage.page.media.bean.DirectoryBean;
import com.bamboy.bimage.page.media.bean.FileBean;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MediaUtil {

    /**
     * 单例工厂
     */
    private static class MediaUtilHolder {
        private static final MediaUtil INSTANCE = new MediaUtil();
    }

    /**
     * 获取单例
     *
     * @return
     */
    public static final MediaUtil getInstance() {
        return MediaUtilHolder.INSTANCE;
    }

    /**
     * 私有构造
     */
    private MediaUtil() {
        // 视频时长Map
        videoDurationMap = new HashMap<>();

        // 支持的图片格式
        imageTypes = new ArrayList<>();
        imageTypes.add("png");
        imageTypes.add("jpg");
        imageTypes.add("jpeg");

        // 支持的视频格式
        videoTypes = new ArrayList<>();
        videoTypes.add("rmvb");
        videoTypes.add("avi");
        videoTypes.add("wmv");
        videoTypes.add("mp4");
        videoTypes.add("mpg");
        videoTypes.add("flv");
        videoTypes.add("rm");

        videoTypes.add("3gp");
        videoTypes.add("vob");
        videoTypes.add("mkv");
        videoTypes.add("mov");

        // videoTypes.add("webm");
    }

    /**
     * 支持的图片格式
     */
    public List<String> imageTypes;
    /**
     * 图片类型前缀
     */
    public String imageTypePrefix = "image/";
    /**
     * 支持的视频格式
     */
    public List<String> videoTypes;
    /**
     * 视频时长Map
     */
    public Map<String, Long> videoDurationMap;
    /**
     * 视频类型前缀
     */
    public String videoTypePrefix = "video/";

    /**
     * 数据列表
     */
    public List<DirectoryBean> dirList;

    /**
     * 读取媒体
     */
    public void loadMedia(Context context, int mediaType, Handler handler) {
        if (dirList == null) {
            dirList = new ArrayList<>();
        } else {
            dirList.clear();
        }

        // 获取媒体库
        Cursor cursor = getCursor(context, mediaType);
        if (cursor == null) {
            handler.sendEmptyMessage(1);
            return;
        }

        // 遍历媒体库
        while (cursor.moveToNext()) {
            // 添加文件
            if (mediaType == 1) {
                addImage(cursor, mediaType);
            } else if (mediaType == 2) {
                addVideo(cursor, mediaType);
            }
        }
        cursor.close();

        if (mediaType == 2) {
            for (DirectoryBean dir : dirList) {
                // dir.initVideoBitmap();
            }
        }

        handler.sendEmptyMessage(1);
    }

    /**
     * 获取媒体资源
     *
     * @return
     */
    private Cursor getCursor(Context context, int mediaType) {
        // 根据图片文件格式进行数据库查询
        ContentResolver contentResolver = context.getContentResolver();
        Cursor cursor = null;
        if (mediaType == 1) {
            cursor = contentResolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, getImageProjection(),
                    getImageWhere(), getImageWhereArgs(),
                    MediaStore.Images.Media.DATE_ADDED + " DESC");
        } else if (mediaType == 2) {
            cursor = contentResolver.query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, getVideoProjection(),
                    getVideoWhere(), getVideoWhereArgs(), MediaStore.Video.Media.DATE_ADDED + " DESC ");
        }
        return cursor;
    }

    /**
     * 添加文件
     *
     * @param cursor 媒体对象
     */
    private void addImage(Cursor cursor, int mediaType) {
        // 媒体文件的路径
        String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
        String fileName = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME));
        long size = cursor.getLong(cursor.getColumnIndex(MediaStore.Images.Media.SIZE));

        // 效验文件正确性
        if (NullUtil.isNull(path) || size == 0) {
            return;
        }

        // 媒体对象
        File file = new File(path);
        if (file == null || !file.exists()) {
            return;
        }
        // 媒体父文件夹对象
        File directory = file.getParentFile();

        // 媒体父文件夹路径
        String directoryPath = directory.getAbsolutePath();

        // 处理文件夹对象
        DirectoryBean directoryBean = getDirectory(directoryPath);
        if (directoryBean == null) {
            directoryBean = new DirectoryBean(directoryPath, directory.getName());
            directoryBean.setMediaType(mediaType);
            dirList.add(directoryBean);
        }

        // 媒体文件对象
        FileBean fileBean = new FileBean(file.getAbsolutePath(), fileName, file.lastModified(), size);
        // 添加到父文件夹
        directoryBean.getFiles().add(fileBean);
    }

    /**
     * 添加文件
     *
     * @param cursor 媒体对象
     */
    private void addVideo(Cursor cursor, int mediaType) {
        // 媒体文件的路径
        String path = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA));
        String fileName = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DISPLAY_NAME));
        long duration = cursor.getLong(cursor.getColumnIndex(MediaStore.Video.Media.DURATION));
        long size = cursor.getLong(cursor.getColumnIndex(MediaStore.Video.Media.SIZE));

        if (size == 0 || duration == 0) {
            return;
        }

        // 媒体对象
        File file = new File(path);
        if (file == null || file.lastModified() <= 0) {
            return;
        }

        // 媒体父文件夹对象
        File directory = file.getParentFile();

        // 媒体父文件夹路径
        String directoryPath = directory.getAbsolutePath();

        // 添加到数据集
        MediaUtil.getInstance().videoDurationMap.put(path, duration);

        // 如果是隐私文件夹，则不添加
        /*if (FileInfoManager.hasLockFile(directoryPath)) {
            return;
        }*/

        // 处理文件夹对象
        DirectoryBean directoryBean = getDirectory(directoryPath);
        if (directoryBean == null) {
            directoryBean = new DirectoryBean(directoryPath, directory.getName());
            directoryBean.setMediaType(mediaType);
            dirList.add(directoryBean);
        }

        // 媒体文件对象
        FileBean fileBean = new FileBean(file.getAbsolutePath(), fileName, file.lastModified(), duration, size);
        // 添加到父文件夹
        directoryBean.getFiles().add(fileBean);
    }

    /**
     * 获取图片的Projection
     *
     * @return
     */
    private String[] getImageProjection() {
        String[] projection = {
                // 缩略图
                MediaStore.Images.Media.DATA,
                // 显示的文件名
                MediaStore.Images.Media.DISPLAY_NAME,
                // 文件大小
                MediaStore.Images.Media.SIZE};

        return projection;
    }

    /**
     * 获取图片的where
     *
     * @return
     */
    private String getImageWhere() {
        StringBuffer sb = new StringBuffer();

        for (int i = 0; i < imageTypes.size(); i++) {
            if (i != 0) {
                sb.append(" or ");
            }
            sb.append(MediaStore.Images.Media.MIME_TYPE + "=?");
        }

        return sb.toString();
    }

    /**
     * 获取图片的whereArgs
     *
     * @return
     */
    private String[] getImageWhereArgs() {
        String[] whereArgs = new String[imageTypes.size()];
        for (int i = 0; i < whereArgs.length; i++) {
            whereArgs[i] = imageTypePrefix + imageTypes.get(i);
        }
        return whereArgs;
    }

    /**
     * 判断文件是否是图片
     *
     * @param fileName
     */
    public boolean isPhoto(String fileName) {
        //文件后缀名
        String suffix = fileName.substring(fileName.lastIndexOf(".") + 1);
        // 支持的图片类型中是否有该后缀
        return containsImageType(suffix);
    }

    /**
     * 是在支持的类型中
     *
     * @param suffix
     * @return
     */
    public boolean containsImageType(String suffix) {
        suffix = suffix.toLowerCase();
        for (String imageType : imageTypes) {
            if (imageType.equals(suffix)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 获取视频的Projection
     *
     * @return
     */
    private String[] getVideoProjection() {
        String[] projection = {
                // 缩略图
                MediaStore.Video.Media.DATA,
                // 显示的文件名
                MediaStore.Video.Media.DISPLAY_NAME,
                // 视频文件的总时长
                MediaStore.Video.Media.DURATION,
                // 文件大小
                MediaStore.Video.Media.SIZE};

        return projection;
    }

    /**
     * 获取视频的where
     *
     * @return
     */
    private String getVideoWhere() {
        StringBuffer sb = new StringBuffer();

        for (int i = 0; i < videoTypes.size(); i++) {
            if (i != 0) {
                sb.append(" or ");
            }
            sb.append(MediaStore.Video.Media.MIME_TYPE + "=?");
        }

        return sb.toString();
    }

    /**
     * 获取视频的whereArgs
     *
     * @return
     */
    private String[] getVideoWhereArgs() {
        String[] whereArgs = new String[videoTypes.size()];
        for (int i = 0; i < whereArgs.length; i++) {
            whereArgs[i] = videoTypePrefix + videoTypes.get(i);
        }
        return whereArgs;
    }

    /**
     * 判断文件是否是视频
     *
     * @param fileName
     */
    public boolean isVideo(String fileName) {
        //文件后缀名
        String suffix = fileName.substring(fileName.lastIndexOf(".") + 1);
        // 支持的图片类型中是否有该后缀
        return containsVideoType(suffix);
    }

    /**
     * 是在支持的类型中
     *
     * @param suffix
     * @return
     */
    public boolean containsVideoType(String suffix) {
        suffix = suffix.toLowerCase();
        for (String videoType : videoTypes) {
            if (videoType.toLowerCase().equals(suffix)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 根据路径获取文件夹对象
     *
     * @param path
     * @return
     */
    private DirectoryBean getDirectory(String path) {
        if (NullUtil.isNull(path)) {
            return null;
        }
        for (DirectoryBean directoryBean : dirList) {
            if (path.equals(directoryBean.getPath())) {
                return directoryBean;
            }
        }
        return null;
    }

    /**
     * 获取视频关键帧
     *
     * @param path   文件路径
     * @param timeUs 第几帧，-1为最大关键帧
     * @return
     */
    public Bitmap getFrame(String path, long timeUs) {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(path);
        return retriever.getFrameAtTime(timeUs, MediaMetadataRetriever.OPTION_CLOSEST_SYNC);
    }
}
