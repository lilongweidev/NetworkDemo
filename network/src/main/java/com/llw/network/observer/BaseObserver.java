package com.llw.network.observer;

import com.google.gson.Gson;
import com.llw.network.BaseResponse;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import retrofit2.Response;

public abstract class BaseObserver<T> implements Observer<T> {

    //开始
    @Override
    public void onSubscribe(Disposable d) {

    }

    //进行中
    @Override
    public void onNext(T t) {
        onSuccess(t);
    }

    //异常
    @Override
    public void onError(Throwable e) {
        onFailure(e);
    }



    //完成
    @Override
    public void onComplete() {

    }

    //模板方法
    //成功
    public abstract void onSuccess(T t);
    //失败
    public abstract void onFailure(Throwable e);
}
