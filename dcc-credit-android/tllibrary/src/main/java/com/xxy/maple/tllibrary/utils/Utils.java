package com.xxy.maple.tllibrary.utils;

import android.content.Context;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by Gaoguanqi on 2018/5/29.
 */

public class Utils {
    /**
     * 判断网络是否可用
     */
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();

        return networkInfo != null && networkInfo.isAvailable();
    }


    public static String getString(Context context, int stringID) {
        return getResources(context).getString(stringID);
    }
    /**
     * 获得资源
     */
    private static Resources getResources(Context context) {
        return context.getResources();
    }




    /**
     * 防止快速点击
     *
     * @return 如果两次点击的时间间隔小于2秒 返回true
     */
    private static long lastClickTime;
    public static boolean isFastDoubleClick() {
        long time = System.currentTimeMillis();
        long timeD = time - lastClickTime;
        if (0 < timeD && timeD < 2000) {
            return true;
        }
        lastClickTime = time;
        return false;
    }
}
