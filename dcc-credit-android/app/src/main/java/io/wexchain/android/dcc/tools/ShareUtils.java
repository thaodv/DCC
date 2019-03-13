package io.wexchain.android.dcc.tools;

import android.content.Context;
import android.content.SharedPreferences;

import io.wexchain.android.common.constant.Extras;


/**
 * @author Created by Wangpeng on 16/6/13.
 * usage: SharedPreferences
 */
public class ShareUtils {
    
    private static Context mContext;
    private static SharedPreferences sharedPreferences;
    private static SharedPreferences.Editor editor;
    
    
    public static void init(Context context) {
        mContext = context;
        sharedPreferences = mContext.getSharedPreferences(Extras.SP_SELECTED_NODE_FILE, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }
    
    public static void setString(String key, String value) {
        editor.putString(key, value);
        editor.commit();
    }
    
    public static String getString(String key, String normal) {
        return sharedPreferences.getString(key, normal);
    }
    
    
    public static String getString(String key) {
        return sharedPreferences.getString(key, "");
    }
    
    
    public static void setInteger(String key, int value) {
        editor.putInt(key, value);
        editor.commit();
    }
    
    public static int getInteger(String key, int normal) {
        return sharedPreferences.getInt(key, normal);
    }
    
    public static int getInteger(String key) {
        return sharedPreferences.getInt(key, 0);
    }
    
    
    public static Boolean getBoolean(String key) {
        return sharedPreferences.getBoolean(key, false);
    }
    
    public static Boolean getBoolean(String key, boolean b) {
        return sharedPreferences.getBoolean(key, b);
    }
    
    public static void setBoolean(String key, boolean value) {
        editor.putBoolean(key, value).commit();
    }
    
    /**
     * 移除key
     *
     * @param key
     */
    public static void remove(String key) {
        editor.remove(key).commit();
    }
    
}
