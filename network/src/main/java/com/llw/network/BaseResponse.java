package com.llw.network;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class BaseResponse {
    @SerializedName("res_code")
    @Expose
    public Integer responseCode;
    @SerializedName("res_error")
    @Expose
    public String responseError;
}
