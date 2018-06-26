package com.xxy.maple.tllibrary.utils;


import android.util.Log;


public final class LogUtils {

    private LogUtils() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    private static boolean isShow = true;
    //private static boolean isShow = false;

    public static void logGGQ(String msg){
        if(isShow){
            Log.i("GGQ", msg);
        }
    }
}