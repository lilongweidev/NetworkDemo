package com.llw.networkdemo;

import android.app.Application;

import com.llw.network.NetworkApi;

public class DemoApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
            NetworkApi.init(new NetworkRequestInfo(this));
    }
}
