package com.example.administrator.weather;

import android.app.Application;
import android.content.Context;

import com.thinkland.sdk.android.JuheSDKInitializer;

/**
 * Created by Administrator on 2016/4/2.
 */
public class MyApplication extends Application {
    private static Context mContext;
    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();
        JuheSDKInitializer.initialize(getApplicationContext());
    }

    public static Context getContext(){
        return mContext;
    }
}
