package com.krislq.amap;

import android.app.Application;

/**
 * 
 * @author kris
 *
 */
public class AMapApplication extends Application{
    private static AMapApplication mApplication;
    @Override
    public void onCreate() {
        super.onCreate();
        mApplication = this;
    }

    public static AMapApplication getContext() {
        return mApplication;
    }
}
