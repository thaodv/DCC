package io.wexchain.android.dcc.tl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtil {

    public final static String FORMAT_19 = "yyyy-MM-dd hh:mm:ss";
    public final static String FORMAT_10 = "yyyy-MM-dd";
    public final static String FORMAT_CN_19 = "yyyy年MM月dd日 hh时mm分ss秒";
    public final static String FORMAT_CN_10 = "yyyy年MM月dd日";

    /**
     * 日期转为指定格式
     */
    public static String parsDateToString(Date date, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(date);
    }
    public  static String parsIntToDate(int i, String format){
  /*      SimpleDateFormat formatter = new SimpleDateFormat(format);

        Date date ;
        String ss="";
        try {
               date = formatter.parse(""+i);
             ss=formatter.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return ss;*/
  return parsDateToString(parsStringToDate(i+"",format),format);

    }
    public static void main(String[] args) throws Exception {

        //parsIntToDate(1534211619,FORMAT_10);
        System.out.print(  parsIntToDate(1534211619,FORMAT_10));
    }

    /**
     * 字符串转换为日期
     */
    public static Date parsStringToDate(String formatDate, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        Date date = null;
        try {
            date = sdf.parse(formatDate);
        } catch (ParseException e) {
            System.out.println("输入日期格式不正确，异常信息：" + e);
        }
        return date;
    }

    /**
     * 获得指定日期的前num个月的日期
     */
    public static Date beforeMonth(Date date, int num) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(calendar.MONTH, num * -1);
        Date dBefore = calendar.getTime();
        return dBefore;
    }

    private static Calendar dateToCalendar(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar;
    }

    /**
     * 日期为所在月的第几周
     */
    public static int weekOfMonth(Date date) {
        return dateToCalendar(date).get(Calendar.WEEK_OF_MONTH);
    }

    /**
     * 日期为所在年的第几周
     */
    public static int weekOfYear(Date date) {
        return dateToCalendar(date).get(Calendar.WEEK_OF_YEAR);
    }

    /**
     * 日期为所在年的第几天
     */
    public static int dayOfYear(Date date) {
        return dateToCalendar(date).get(Calendar.DAY_OF_YEAR);
    }

    /**
     * 根据一个时间获取该时间为星期几
     */
    public static String dayOfWeek(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("E");
        return sdf.format(date);
    }

    /**
     * 计算两个时间之间的天数
     */
    public static int countDaysBetween(Date d1, Date d2) {
        Calendar c1 = dateToCalendar(d1);
        Calendar c2 = dateToCalendar(d2);
        return Math.abs(Math.abs(c1.get(Calendar.YEAR) - c2.get(Calendar.YEAR)) * 365 + (c1.get(Calendar.DAY_OF_YEAR) - c2.get(Calendar.DAY_OF_YEAR)));
    }


}