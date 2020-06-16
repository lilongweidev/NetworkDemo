package com.llw.networkdemo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.os.Bundle;
import android.view.View;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.Target;
import com.google.gson.Gson;
import com.llw.network.NetworkApi;
import com.llw.network.observer.BaseObserver;
import com.llw.network.utils.KLog;
import com.llw.network.utils.ToastUtils;
import com.llw.networkdemo.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding mainBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        mainBinding.btnGetData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NetworkApi.createService(ApiService.class)
                        .getList()
                        .compose(NetworkApi.applySchedulers(new BaseObserver<MeinvResponse>() {
                            @Override
                            public void onSuccess(MeinvResponse meinvResponse) {
                                if(meinvResponse.getData() !=null){
                                    KLog.json("Result",new Gson().toJson(meinvResponse));
                                    String imgUrl = meinvResponse.getData().get(1).getImages().get(0);
                                    Glide.with(MainActivity.this).load(imgUrl).into(mainBinding.ivPicture);
                                    mainBinding.ivPicture.setVisibility(View.VISIBLE);
                                    mainBinding.btnGetData.setVisibility(View.GONE);
                                }else {
                                    mainBinding.ivPicture.setVisibility(View.GONE);
                                }
                            }

                            @Override
                            public void onFailure(Throwable e) {
                                ToastUtils.ShortToast(MainActivity.this,"异常信息");
                            }
                        }));

            }
        });
    }
}
