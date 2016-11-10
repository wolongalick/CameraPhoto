package com.alick.cameraphoto.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.app.Fragment;
import android.widget.ImageView;

import com.bumptech.glide.DrawableTypeRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

/**
 * Google推荐的图片加载库Glide
 * 博客http://blog.csdn.net/fancylovejava/article/details/44747759
 */
public class GlideUtils {

    public static void showImage(Context context,int resId, ImageView imageView){
        try {
            show(Glide.with(context), resId, imageView);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void showImage(Context context,String uri, ImageView imageView){
        try {
            show(Glide.with(context), uri, imageView);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void showImage(Context context,Bitmap bitmap, ImageView imageView){
        try {
            show(Glide.with(context), bitmap, imageView);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void showImage(Context context,String uri, ImageView imageView,boolean isNeedCache){
        try {
            show(Glide.with(context), uri, imageView,isNeedCache);
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    public static void showImage(Fragment fragment, int resId, ImageView imageView){
        try {
            show(Glide.with(fragment), resId, imageView);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void showImage(Fragment fragment, String uri, ImageView imageView){
        try {
            show(Glide.with(fragment), uri, imageView);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void showImage(Fragment fragment, Bitmap bitmap, ImageView imageView){
        try {
            show(Glide.with(fragment), bitmap, imageView);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void showImage(Fragment fragment, String uri, ImageView imageView, boolean isNeedCache){
        try {
            show(Glide.with(fragment), uri, imageView,isNeedCache);
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    public static void showImage(android.app.Fragment fragment,String uri, ImageView imageView){
        try {
            show(Glide.with(fragment), uri, imageView);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void showImage(android.app.Fragment fragment,String uri, ImageView imageView,boolean isNeedCache){
        try {
            show(Glide.with(fragment), uri, imageView,isNeedCache);
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    public static void showImage(android.app.Fragment fragment,Bitmap bitmap, ImageView imageView){
        try {
            show(Glide.with(fragment), bitmap, imageView);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void showImage(android.app.Fragment fragment,int resId, ImageView imageView){
        try {
            show(Glide.with(fragment), resId, imageView);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private static void show(RequestManager requestManager,String uri, ImageView imageView){
        DrawableTypeRequest<String> drawableTypeRequest = requestManager.load(uri);
        if(uri.endsWith(".gif")){
            //如果是gif图片,则增加asGif()方法
            drawableTypeRequest.asGif().dontAnimate().diskCacheStrategy(DiskCacheStrategy.ALL).into(imageView);
        }
        drawableTypeRequest.dontAnimate().diskCacheStrategy(DiskCacheStrategy.ALL).into(imageView);
    }

    private static void show(RequestManager requestManager,String uri, ImageView imageView,boolean isNeedCache){
        requestManager.load(uri).dontAnimate().skipMemoryCache(!isNeedCache).diskCacheStrategy(isNeedCache ? DiskCacheStrategy.ALL : DiskCacheStrategy.NONE).into(imageView);
    }

    private static void show(RequestManager requestManager,int imageResId, ImageView imageView){
        requestManager.load(imageResId).dontAnimate().diskCacheStrategy(DiskCacheStrategy.ALL).into(imageView);
    }

    private static void show(RequestManager requestManager,Bitmap bitmap, ImageView imageView){
        requestManager.load("").dontAnimate().diskCacheStrategy(DiskCacheStrategy.ALL).into(imageView);
    }

    private static void show(RequestManager requestManager,int imageResId, ImageView imageView,boolean isNeedCache){
        requestManager.load(imageResId).dontAnimate().skipMemoryCache(!isNeedCache).diskCacheStrategy(isNeedCache ? DiskCacheStrategy.ALL : DiskCacheStrategy.NONE).into(imageView);
    }
}
