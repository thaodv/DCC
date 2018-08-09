package io.wexchain.android.dcc.tools;

import java.math.BigDecimal;

/**
 * @author Created by Wangpeng on 2018/7/25 17:55.
 * usage:
 */
public class StringUtils {
    
    /**
     * 保留两位小数
     *
     * @param source
     * @return
     */
    public static String getPrice(String source, int count) {
        
        if (null == source) {
            return "";
        } else {
            double money = Double.parseDouble(source);
            BigDecimal res;
            String unit = "";
            if (money < 10000) {
                res = new BigDecimal(money).setScale(count, BigDecimal.ROUND_DOWN);
            } else if (money >= 10000 && money < 100000000) {
                res = new BigDecimal(money).divide(new BigDecimal(10000), count, BigDecimal.ROUND_DOWN);
                unit = "万";
            } else {
                res = new BigDecimal(money).divide(new BigDecimal(100000000), count, BigDecimal
                        .ROUND_DOWN);
                unit = "亿";
            }
            return "￥" + res.toString() + unit;
        }
        
    }
    
    /**
     * 保留两位小数
     *
     * @param source
     * @return
     */
    public static String getTransCount(String source, int count) {
        
        if (null == source) {
            return "";
        } else {
            double money = Double.parseDouble(source);
            BigDecimal res;
            String unit = "";
            if (money < 10000) {
                res = new BigDecimal(money).setScale(count, BigDecimal.ROUND_DOWN);
            } else if (money >= 10000 && money < 100000000) {
                res = new BigDecimal(money).divide(new BigDecimal(10000), count, BigDecimal.ROUND_DOWN);
                unit = "万";
            } else {
                res = new BigDecimal(money).divide(new BigDecimal(100000000), count, BigDecimal
                        .ROUND_DOWN);
                unit = "亿";
            }
            return res.toString() + unit;
        }
        
    }
    
    public static String keep2double(String source, char unit) {
        
        if (null == source) {
            return "";
        } else {
            double money = Double.parseDouble(source);
            
            BigDecimal res = new BigDecimal(money).setScale(2, BigDecimal.ROUND_DOWN);
            if (money > 0) {
                return "+" + res.toString() + unit;
            } else {
                return res.toString() + unit;
            }
        }
    }
    
    public static String keep4double(String source, char unit) {
        
        if (null == source) {
            return "";
        } else {
            double money = Double.parseDouble(source);
            
            BigDecimal res = new BigDecimal(money).setScale(4, BigDecimal.ROUND_DOWN);
            if (money > 0) {
                return "+" + res.toString() + unit;
            } else {
                return res.toString() + unit;
            }
        }
    }
    
    public static Boolean showColor(String source) {
        if (null == source) {
            return true;
        } else {
            double money = Double.parseDouble(source);
            return money >= 0 ? true : false;
        }
    }
}
