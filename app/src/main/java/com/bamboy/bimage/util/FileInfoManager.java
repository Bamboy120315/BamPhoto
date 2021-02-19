package com.bamboy.bimage.util;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FileInfoManager {
    /**
     * 系统文件集合
     */
    public static List<String> syatemFileList;
    /**
     * 隐私文件集合【通过本APP添加的隐私文件夹】
     */
    public static List<String> lockFileList;
    /**
     * 隐私文件集合【所有拥有隐私文件的文件夹】
     */
    public static List<String> allLockFileList;
    /**
     * 描述集合
     */
    public static Map<String, String> describeMap;
    /**
     * SD卡根目录
     */
    public static String sdcardPath = "/storage/emulated/0";

    static {
        lockFileList = new ArrayList<>();
        allLockFileList = new ArrayList<>();

        syatemFileList = new ArrayList<>();
        syatemFileList.add(sdcardPath + "/Android");
        syatemFileList.add(sdcardPath + "/sysdata");
        syatemFileList.add(sdcardPath + "/system");

        describeMap = new HashMap<>();
        describeMap.put(sdcardPath, "根目录");
        describeMap.put(sdcardPath + "/Android", "系统数据");
        describeMap.put(sdcardPath + "/alipay", "支付宝");
        describeMap.put(sdcardPath + "/amap", "高德地图");
        describeMap.put(sdcardPath + "/", "");
        describeMap.put(sdcardPath + "/", "");
        describeMap.put(sdcardPath + "/", "");
        describeMap.put(sdcardPath + "/", "");
        describeMap.put(sdcardPath + "/backup", "系统备份");
        describeMap.put(sdcardPath + "/backups", "软件备份");
        describeMap.put(sdcardPath + "/baidu", "百度");
        describeMap.put(sdcardPath + "/BaiduMap", "百度地图");
        describeMap.put(sdcardPath + "/BaiduNetdisk", "百度云");
        describeMap.put(sdcardPath + "/book", "电子书");
        describeMap.put(sdcardPath + "/cmb", "招商银行");
        describeMap.put(sdcardPath + "/com.MobileTicket", "铁路12306");
        describeMap.put(sdcardPath + "/com.sankuai.meituan", "美团团购");
        describeMap.put(sdcardPath + "/com.sina.weibo", "新浪微博");
        describeMap.put(sdcardPath + "/com.tencent.mobileqq", "QQ");
        describeMap.put(sdcardPath + "/data", "应用支持");
        describeMap.put(sdcardPath + "/DCIM", "照片");
        describeMap.put(sdcardPath + "/Download", "系统下载");
        describeMap.put(sdcardPath + "/Fonts", "字体");
        describeMap.put(sdcardPath + "/gifshow", "快手");
        describeMap.put(sdcardPath + "/jd", "京东");
        describeMap.put(sdcardPath + "/JDIM", "京东");
        describeMap.put(sdcardPath + "/keep", "Keep健身");
        describeMap.put(sdcardPath + "/ktv", "全民K歌");
        describeMap.put(sdcardPath + "/libs", "应用支持");
        describeMap.put(sdcardPath + "/meizu", "魅族");
        describeMap.put(sdcardPath + "/mipush", "小米推送");
        describeMap.put(sdcardPath + "/mishop", "小米商城");
        describeMap.put(sdcardPath + "/Mob", "格瓦拉电影");
        describeMap.put(sdcardPath + "/Movies", "视频");
        describeMap.put(sdcardPath + "/Movies/Screenrecords", "录屏");
        describeMap.put(sdcardPath + "/MQ", "网易");
        describeMap.put(sdcardPath + "/Music", "音乐");
        describeMap.put(sdcardPath + "/netease", "网易");
        describeMap.put(sdcardPath + "/Notifications", "通知铃声");
        describeMap.put(sdcardPath + "/Pictures", "图片");
        describeMap.put(sdcardPath + "/Pictures/Screenshots", "截图");
        describeMap.put(sdcardPath + "/Pictures/Share", "Share微博客户端");
        describeMap.put(sdcardPath + "/Pictures/WinXin", "微信");
        describeMap.put(sdcardPath + "/Qmap", "腾讯地图");
        describeMap.put(sdcardPath + "/QQBrowser", "QQ浏览器");
        describeMap.put(sdcardPath + "/qqmusic", "QQ音乐");
        describeMap.put(sdcardPath + "/QQSecureDownload", "腾讯手机管家");
        describeMap.put(sdcardPath + "/Recorder", "录音机");
        describeMap.put(sdcardPath + "/Ringtones", "铃声");
        describeMap.put(sdcardPath + "/sysdata", "系统数据");
        describeMap.put(sdcardPath + "/system", "系统支持");
        describeMap.put(sdcardPath + "/tbs", "QQ轻聊版");
        describeMap.put(sdcardPath + "/video", "视频");
    }

    /**
     * 是否是系统文件
     *
     * @param path 文件路径
     * @return
     */
    public static boolean isSyatemFile(String path) {
        if (!NullUtil.isNull(path) && syatemFileList.contains(path)) {
            return true;
        }

        return false;
    }

    /**
     * 根据文件名获取描述
     *
     * @param key 文件名
     * @return 描述
     */
    public static String getDes(String key) {
        if (NullUtil.isNull(key) || !describeMap.containsKey(key)) {
            return null;
        }

        return describeMap.get(key);
    }

    /**
     * 判断是文件夹
     *
     * @param dirPath
     */
    public static boolean hasLockFile(String dirPath) {
        if (NullUtil.isNull(dirPath)) {
            return false;
        }

        if (allLockFileList.contains(dirPath)) {
            return true;
        }

        File fileAbs = new File(dirPath);
        if (!fileAbs.exists() || !fileAbs.isDirectory()) {
            return false;
        }

        File[] files = fileAbs.listFiles();

        for (File file : files) {
            // 过滤系统文件夹
            if (FileInfoManager.isSyatemFile(file.getAbsolutePath())) {
                continue;
            }

            // 判断是否是拥有.nomedia文件
            if (".nomedia".equals(file.getName()) && !file.isDirectory()) {
                FileInfoManager.allLockFileList.add(dirPath);
                return true;
            }
        }

        return false;
    }
}
