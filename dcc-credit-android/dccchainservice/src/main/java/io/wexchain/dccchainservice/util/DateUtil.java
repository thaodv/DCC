package io.wexchain.dccchainservice.util;

import android.annotation.SuppressLint;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

/**
 * 时间类型转换的工具类
 *
 * @author Wangpeng
 */
@SuppressLint("SimpleDateFormat")
public class DateUtil {
    
    private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
    // public static SimpleDateFormat dateFormat2 = new SimpleDateFormat("yyyy-MM-dd");

    public static String dateFormatString="yyyy-MM-dd";
    public static String dateFormatStringChi="MM月dd日hh:mm:ss";
    public static String dateFormatStringChi2="";
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
  /*  public static void main(String[] args) throws Exception {

        //parsIntToDate(1534211619,FORMAT_10);
        System.out.print( getStringTime(1534211619000l,"yyyy-MM-dd"));
    }*/
    
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
     *
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @param format    时间格式
     * @return
     */
    public static boolean beyond90day(String startTime, String endTime, String format) {
        long temp = getLongTime(endTime, format) - getLongTime(startTime, format);
        long day = temp / 86400000;
        return day < 90 ? true : false;
    }
    
    /**
     * 判断endTime大于startTime
     *
     * @param startTime
     * @param endTime
     * @param format
     * @return
     */
    public static boolean isRightSelect(String startTime, String endTime, String format) {
        long temp = getLongTime(endTime, format) - getLongTime(startTime, format);
        return temp < 0;
    }
    
    /**
     * 获得本周一与当前日期相差的天数
     *
     * @return
     */
    private static int getMondayPlus() {
        Calendar cd = Calendar.getInstance();
        int dayOfWeek = cd.get(Calendar.DAY_OF_WEEK);
        if (dayOfWeek == 1) {
            return -6;
        } else {
            return 2 - dayOfWeek;
        }
    }
    
    /**
     * 获得当前周- 周一的日期
     *
     * @return
     */
    public static String getCurrentMonday() {
        int mondayPlus = getMondayPlus();
        GregorianCalendar currentDate = new GregorianCalendar();
        currentDate.add(GregorianCalendar.DATE, mondayPlus);
        Date monday = currentDate.getTime();
        DateFormat df = DateFormat.getDateInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        String preMonday = dateFormat.format(monday);
        return preMonday;
    }
    
    /**
     * 获得当前周- 周一的日期
     *
     * @return
     */
    public static String getCurrentMonday(String format) {
        int mondayPlus = getMondayPlus();
        GregorianCalendar currentDate = new GregorianCalendar();
        currentDate.add(GregorianCalendar.DATE, mondayPlus);
        Date monday = currentDate.getTime();
        DateFormat df = DateFormat.getDateInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat(format);
        String preMonday = dateFormat.format(monday);
        return preMonday;
    }
    
    
    /**
     * 获得当前周- 周日  的日期
     *
     * @return
     */
    public static String getCurrentSunday() {
        int mondayPlus = getMondayPlus();
        GregorianCalendar currentDate = new GregorianCalendar();
        currentDate.add(GregorianCalendar.DATE, mondayPlus + 6);
        Date monday = currentDate.getTime();
        DateFormat df = DateFormat.getDateInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        String preMonday = dateFormat.format(monday);
        return preMonday;
    }
    
    /**
     * 获得当前周- 周日  的日期
     *
     * @return
     */
    public static String getCurrentSunday(String format) {
        int mondayPlus = getMondayPlus();
        GregorianCalendar currentDate = new GregorianCalendar();
        currentDate.add(GregorianCalendar.DATE, mondayPlus + 6);
        Date monday = currentDate.getTime();
        DateFormat df = DateFormat.getDateInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat(format);
        String preMonday = dateFormat.format(monday);
        return preMonday;
    }
    
    /**
     * 获得当前月--开始日期
     *
     * @return
     */
    public static String getMinMonthDate() {
        Calendar calendar = Calendar.getInstance();
        try {
            calendar.setTime(dateFormat.parse(getCurrentDate()));
            calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMinimum(Calendar.DAY_OF_MONTH));
            return dateFormat.format(calendar.getTime());
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    /**
     * 获得当前月--结束日期
     *
     * @return
     */
    public static String getMaxMonthDate() {
        Calendar calendar = Calendar.getInstance();
        try {
            calendar.setTime(dateFormat.parse(getCurrentDate()));
            calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
            return dateFormat.format(calendar.getTime());
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    /**
     * 获取前2月的开始时间
     *
     * @return
     */
    public static String getPre2Month() {
        Calendar calendar = Calendar.getInstance();
        try {
            calendar.setTime(dateFormat.parse(getCurrentDate()));
            calendar.add(Calendar.MONTH, -2);
            return dateFormat.format(calendar.getTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    /**
     * 获取当前的日期
     *
     * @return
     */
    public static String getCurrentDate() {
        Date date = new Date();
        return dateFormat.format(date);
    }
    
}
