package com.llw.network.utils;

import android.content.Context;
import android.widget.Toast;

public class ToastUtils {
    //长消息提示
    public static void LongToast(Context context, CharSequence llw) {
        Toast.makeText(context.getApplicationContext(), llw, Toast.LENGTH_LONG).show();
    }
    //短消息提示
    public static void ShortToast(Context context, CharSequence llw) {
        Toast.makeText(context.getApplicationContext(), llw, Toast.LENGTH_SHORT).show();
    }
}
