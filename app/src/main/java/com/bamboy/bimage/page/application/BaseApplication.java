package com.bamboy.bimage.page.application;

import android.app.Application;

public class BaseApplication extends Application {

    public static BaseApplication context;

    public static BaseApplication getInstance() {
        return context;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        context = this;
    }
}
