package com.llw.networkdemo;

import io.reactivex.Observable;
import retrofit2.http.GET;

public interface ApiService {

    @GET("/api/v2/data/category/Girl/type/Girl/page/1/count/10")
    Observable<MeinvResponse> getList();
}
