package com.example.administrator.weather.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by Administrator on 2016/4/9.
 */
public class WeatherService extends Service {
    private final String tag = "WeatherService";
    private WeatherServiceBinder binder = new WeatherServiceBinder();

    public void test(){
        Log.v(tag,"test!!!");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onCreate() {
        Log.v(tag,"onCreate");
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        Log.v(tag,"onDestroy");
        super.onDestroy();
    }

    public class WeatherServiceBinder extends Binder{
        public WeatherService getService(){
            return WeatherService.this;
        }
    }
}
