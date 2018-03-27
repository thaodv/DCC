package io.wexchain.passport.chain.observer.common.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * <p>
 * 时间处理工具类
 * </p>
 * @author yy
 */
public class DateUtil {
    private final static Logger log                  = LoggerFactory.getLogger(DateUtil.class);

    public final static long    ONE_DAY_SECONDS      = 86400;

    /*
     * private static DateFormat dateFormat = null; private static DateFormat
     * longDateFormat = null; private static DateFormat dateWebFormat = null;
     */
    public final static String  shortFormat          = "yyyyMMdd";
    public final static String  longFormat           = "yyyyMMddHHmmss";
    public final static String  webFormat            = "yyyy-MM-dd";
    public final static String  timeFormat           = "HHmmss";
    public final static String  monthFormat          = "yyyyMM";
    public final static String  chineseDtFormat      = "yyyy年MM月dd日";
    public final static String  newFormat            = "yyyy-MM-dd HH:mm:ss";
    public final static String  noSecondFormat       = "yyyy-MM-dd HH:mm";
    public final static String  todayStartFormat     = "yyyy-MM-dd 00:00:00";
    public final static long    ONE_DAY_MILL_SECONDS = 86400000;

    public static String format(Date date, String format) {
        if (date == null) {
            return null;
        }

        return new SimpleDateFormat(format).format(date);
    }

    public static Date getTodayStartDate(Date date) throws ParseException {
        if (date == null) {
            return null;
        }
        DateFormat dateFormat = new SimpleDateFormat(todayStartFormat);
        return dateFormat.parse(dateFormat.format(date));
    }

}
