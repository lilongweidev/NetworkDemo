package com.llw.network;

import android.content.Context;

import com.llw.network.commoninterceptor.CommonRequestInterceptor;
import com.llw.network.commoninterceptor.CommonResponseInterceptor;
import com.llw.network.environment.EnvironmentActivity;
import com.llw.network.errorhandler.ExceptionHandle;
import com.llw.network.errorhandler.HttpErrorHandler;

import java.io.InputStream;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.security.cert.CertificateFactory;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Cache;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class NetworkApi {
    //获取APP运行状态及版本信息，用于日志打印
    private static INetworkRequiredInfo iNetworkRequiredInfo;
    private static HashMap<String, Retrofit> retrofitHashMap = new HashMap<>();

    private static String mBaseUrl;//API访问地址
    private static OkHttpClient mOkHttpClient;
    private static boolean mIsFormal = true;//是否为正式环境

    public static void init(INetworkRequiredInfo networkRequiredInfo) {
        iNetworkRequiredInfo = networkRequiredInfo;
        //当初始化这个NetworkApi时，判断这个网络状态
        mIsFormal = EnvironmentActivity.isOfficialEnvironment(networkRequiredInfo.getApplicationContext());

        if (!mIsFormal) {
            mBaseUrl = "https://cn.bing.com";//测试环境
        } else {
            mBaseUrl = "https://gank.io";//正式环境
        }
    }

    public static <T> T createService(Class<T> serviceClass) {

        return getRetrofit(serviceClass).create(serviceClass);
    }

    //配置OkHttp
    private static OkHttpClient getOkHttpClient() {
        if (mOkHttpClient == null) {//为空则重新创建
            OkHttpClient.Builder okHttpClientBuilder = new OkHttpClient.Builder();//构建
            int cacheSize = 100 * 1024 * 1024;//10MB  设置缓存大小
            //获得当前的手机自带的存储空间中的当前包文件的路径
            okHttpClientBuilder.cache(new Cache(iNetworkRequiredInfo.getApplicationContext().getCacheDir(),cacheSize));
            okHttpClientBuilder.connectTimeout(15, TimeUnit.SECONDS);//设置请求超时时长为15秒
            okHttpClientBuilder.addInterceptor(new CommonRequestInterceptor(iNetworkRequiredInfo));//添加请求拦截器，如果接口有请求头的话，可以放在这个拦截器里面
            okHttpClientBuilder.addInterceptor(new CommonResponseInterceptor());//添加返回拦截器，可用于查看接口的请求耗时，对于网络优化有帮助
            //当程序在Debug过程中就打印所有参数日志，方便调试用，而程序是否在Debug中，要在app主模块里面的Application中判断
            if(iNetworkRequiredInfo != null && iNetworkRequiredInfo.isDebug()){
                //初始化日志拦截器，这个拦截器是要build.gradle的里面添加依赖库才能使用的。
                //implementation 'com.squareup.okhttp3:logging-interceptor:3.9.0'
                HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
                //设置要打印日志的内容等级，BODY为主要内容，还有BASIC、HEADERS、NONE、
                httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
                okHttpClientBuilder.addInterceptor(httpLoggingInterceptor);//添加日志打印拦截器到OkHttp中
            }
            mOkHttpClient = okHttpClientBuilder.build();//配置完毕
        }

        return mOkHttpClient;//返回
    }

    //配置Retrofit
    private static Retrofit getRetrofit(Class serviceClass){
        if(retrofitHashMap.get(mBaseUrl + serviceClass.getName()) != null){
            return retrofitHashMap.get(mBaseUrl + serviceClass.getName());
        }
        //初始化Retrofit  Retrofit是对OKHttp的封装，通常是对网络请求做处理，也可以处理返回数据。
        Retrofit.Builder retrofitBuilder = new Retrofit.Builder();
        retrofitBuilder.baseUrl(mBaseUrl);//设置访问地址，到这里时，已经根据你切换的网络环境得到了对应API访问地址
        retrofitBuilder.client(getOkHttpClient());//放入OkHttp
        //设置https访问(验证证书，请把服务器给的证书文件放在R.raw文件夹下)
        retrofitBuilder.addConverterFactory(GsonConverterFactory.create());//设置数据解析器，会自动把请求返回的结果（json字符串）自动转化成与其结构相符的实体Bean
        retrofitBuilder.addCallAdapterFactory(RxJava2CallAdapterFactory.create());//配置回调库，采用RxJava  对网络返回做处理
        Retrofit retrofit = retrofitBuilder.build();//配置完成
        retrofitHashMap.put(mBaseUrl + serviceClass.getName(),retrofit);//放入
        return retrofit;//返回
    }

    //配置RxJava的线程切换
    public static  <T> ObservableTransformer<T, T> applySchedulers(final Observer<T> observer) {
        return new ObservableTransformer<T, T>() {
            @Override
            public ObservableSource<T> apply(Observable<T> upstream) {
                Observable<T> observable = (Observable<T>)upstream//订阅
                        .subscribeOn(Schedulers.io())//线程转换
                        .observeOn(AndroidSchedulers.mainThread())
                        .map(getAppErrorHandler())//判断有没有500的错误，有则进入getAppErrorHandler
                        .onErrorResumeNext(new HttpErrorHandler<T>());//判断有没有400的错误
                observable.subscribe(observer);
                return observable;//返回转换订阅后的observable
            }
        };
    }



    //错误码处理
    protected static  <T> Function<T, T> getAppErrorHandler() {
        return new Function<T, T>() {
            @Override
            public T apply(T response) throws Exception {
                //response 出现500之类的错误
                if (response instanceof BaseResponse && (((BaseResponse) response).responseCode >= 500)) {
                    ExceptionHandle.ServerException exception = new ExceptionHandle.ServerException();//通过这个异常处理，得到用户可以知道的原因
                    exception.code = ((BaseResponse) response).responseCode;
                    exception.message = ((BaseResponse) response).responseError != null ? ((BaseResponse) response).responseError : "";
                    throw exception;
                }
                return response;//返回错误信息
            }
        };
    }





}
