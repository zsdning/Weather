package com.example.administrator.weather.Util;

/**
 * Created by Administrator on 2016/3/12.
 */
public interface HttpCallbackListener {
    void onFinish(String response);

    void onError(Exception e);
}
