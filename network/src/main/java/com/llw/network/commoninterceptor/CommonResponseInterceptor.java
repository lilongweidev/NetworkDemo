package com.llw.network.commoninterceptor;

import android.util.Log;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Response;

/**
 * 返回拦截器
 */
public class CommonResponseInterceptor implements Interceptor {

    private static final String TAG = "ResponseInterceptor";

    @Override
    public Response intercept(Chain chain) throws IOException {
        long requestTime = System.currentTimeMillis();
        Response response = chain.proceed(chain.request());
        //可以对网络进行优化，记录该接口请求耗时
        Log.d(TAG, "requestTime=" + (System.currentTimeMillis() - requestTime) + "ms");
        return response;
    }
}
