package io.wexchain.android.dcc.tools;


import java.math.BigDecimal;
import java.nio.charset.Charset;

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
            return money >= 0;
        }
    }

    public static String decodeHex(String data) throws Exception {
        return new String(decodeHex(data.toCharArray()));
    }

/*
    public static byte[] decodeHex(String data) throws  Exception {
        return decodeHex(data.toCharArray());
    }*/

    public static byte[] decodeHex(char[] data) throws  Exception {
        int len = data.length;
        if ((len & 1) != 0) {
            throw new  Exception("Odd number of characters.");
        } else {
            byte[] out = new byte[len >> 1];
            int i = 0;

            for(int j = 0; j < len; ++i) {
                int f = toDigit(data[j], j) << 4;
                ++j;
                f |= toDigit(data[j], j);
                ++j;
                out[i] = (byte)(f & 255);
            }

            return out;
        }
    }

    private static  int toDigit(char ch, int index) throws  Exception {
        int digit = Character.digit(ch, 16);
        if (digit == -1) {
            throw new  Exception("Illegal hexadecimal character " + ch + " at index " + index);
        } else {
            return digit;
        }
    }

    public static String toHex(String data){
        byte[] bytes = hexString2Bytes(data);

        return new String(bytes, Charset.defaultCharset());
    }

    private static byte charToByte(char c) {
        return (byte) "0123456789ABCDEF".indexOf(c);
    }

    /*
     * 16进制字符串转字节数组
     */
    public static byte[] hexString2Bytes(String hex) {

        if ((hex == null) || (hex.equals(""))){
            return null;
        }
        else if (hex.length()%2 != 0){
            return null;
        }
        else{
            hex = hex.toUpperCase();
            int len = hex.length()/2;
            byte[] b = new byte[len];
            char[] hc = hex.toCharArray();
            for (int i=0; i<len; i++){
                int p=2*i;
                b[i] = (byte) (charToByte(hc[p]) << 4 | charToByte(hc[p+1]));
            }
            return b;
        }

    }

}
