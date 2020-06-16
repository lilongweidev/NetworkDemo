package com.llw.network.utils;


import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.module.AppGlideModule;

@GlideModule
public class MyAppGlideModule extends AppGlideModule {
    /**
     * Failed to find GeneratedAppGlideModule.
     *     You should include an annotationProcessor
     *     compile dependency on com.github.bumptech.glide:compiler
     *     in your application and a @GlideModule annotated
     *     AppGlideModule implementation or LibraryGlideModules will be silently ignored.
     *     为了解决这个异常提示特意新建了一个工具类，只要继承了AppGlideModule,在加载图片的时候就会自己用到的
     */

}
