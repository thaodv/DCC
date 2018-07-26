package io.wexchain.dccchainservice.util;

import android.annotation.SuppressLint;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * 时间类型转换的工具类
 */
@SuppressLint("SimpleDateFormat")
public class DateUtil {
    
    /**
     * 倒计时
     *
     * @param millisUntilFinished
     * @return
     */
    public static String[] daojishi(long millisUntilFinished) {
        long day = millisUntilFinished / (60 * 60 * 1000 * 24);
        long hour = (millisUntilFinished - day * 60 * 60 * 1000 * 24) / (60 * 60 * 1000);
        long minute = (millisUntilFinished - day * 60 * 60 * 1000 * 24 - hour * 60 * 60 * 1000) / (60 * 1000);
        long second = (millisUntilFinished - day * 60 * 60 * 1000 * 24 - hour * 60 * 60 * 1000 - minute *
                60 * 1000) / 1000;
        
        String sh = "";
        String sm = "";
        String ss = "";
        String sd = "";
        if (day < 10) {
            sd = "0" + String.valueOf(day);
        } else {
            sd = String.valueOf(day);
        }
        if (hour < 10) {
            sh = "0" + String.valueOf(hour);
        } else {
            sh = String.valueOf(hour);
        }
        if (minute < 10) {
            sm = "0" + String.valueOf(minute);
        } else {
            sm = String.valueOf(minute);
        }
        if (second < 10) {
            ss = "0" + String.valueOf(second);
        } else {
            ss = String.valueOf(second);
        }
        
        String[] sa = new String[]{sd, sh, sm, ss};
        return sa;
    }
    
    
    /**
     * 获取字符串时间（将时间戳转成字符串时间）
     *
     * @param currentTime 时间戳（传0代表使用当前时间）
     * @param format      自定义时间格式
     * @return
     */
    public static String getStringTime(long currentTime, String format) {
        SimpleDateFormat formatter = new SimpleDateFormat(format, Locale.CHINA);
        String time;
        if (0 == currentTime) {
            time = formatter.format(new Date());
        } else {
            time = formatter.format(currentTime);
        }
        return time;
    }
    
    /**
     * 返回指定时间的时间戳
     *
     * @param time   指定时间
     * @param format 时间格式
     * @return
     */
    public static long getLongTime(String time, String format) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        try {
            Date date = simpleDateFormat.parse(time);
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            long timeInMillis = cal.getTimeInMillis();
            return timeInMillis;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }
    
    /**
     * 判断两个时间是否在90天之内
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param format 时间格式
     * @return
     */
    public static boolean beyond90day(String startTime, String endTime, String format) {
        long temp = getLongTime(endTime, format) - getLongTime(startTime, format);
        long day = temp / 86400000;
        return day > 90 ? false : true;
    }
    
}
