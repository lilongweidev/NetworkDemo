package com.llw.network;

import android.app.Application;

/**
 * APP运行信息接口
 */
public interface INetworkRequiredInfo {
    String getAppVersionName();
    String getAppVersionCode();
    boolean isDebug();
    Application getApplicationContext();
}
