package com.llw.network.commoninterceptor;


import com.llw.network.INetworkRequiredInfo;
import com.llw.network.utils.DateUtil;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 请求拦截器
 */
public class CommonRequestInterceptor implements Interceptor {
    private INetworkRequiredInfo requiredInfo;
    public CommonRequestInterceptor(INetworkRequiredInfo requiredInfo){
        this.requiredInfo = requiredInfo;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        String timeStr = DateUtil.getTimeStr();//时间
        Request.Builder builder = chain.request().newBuilder();
        builder.addHeader("os","android");
        builder.addHeader("appVersion",this.requiredInfo.getAppVersionCode());//app版本号
        //访问服务器API的健全参数,没有的话可以不用
//        builder.addHeader("Source","source");
//        builder.addHeader("Authorization","Authorization");
        builder.addHeader("Date",timeStr);
        return chain.proceed(builder.build());
    }
}
