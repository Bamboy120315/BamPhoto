package com.bamboy.bimage.util;

public class PrivateDataManager {

    /**
     * 单例工厂
     */
    private static class PrivateDataManagerHolder {
        private static final PrivateDataManager INSTANCE = new PrivateDataManager();
    }

    /**
     * 获取单例
     *
     * @return
     */
    public static final PrivateDataManager getInstance() {
        return PrivateDataManager.PrivateDataManagerHolder.INSTANCE;
    }

    /**
     * 私有构造
     */
    private PrivateDataManager() {

    }



}
