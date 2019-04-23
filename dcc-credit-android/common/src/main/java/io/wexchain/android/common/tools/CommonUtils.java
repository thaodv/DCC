package io.wexchain.android.common.tools;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Created by Wangpeng on 2018/10/24 15:37.
 * usage:
 */
public class CommonUtils {
    
    /**
     * 校验密码 （字母数字特殊符号至少2种混合）
     *
     * @param password
     * @return
     */
    public static boolean checkPassword(String password) {
        String criteriaPassword = "^(?![\\d]+$)(?![a-zA-Z]+$)(?![^\\da-zA-Z]+$).{8,20}$";
        Pattern pattern = Pattern.compile(criteriaPassword);
        Matcher matcher = pattern.matcher(password);
        return matcher.matches();
    }
    
    /**
     * 获取mac地址
     *
     * @return
     */
    public static String getMacAddress() {
        String macAddress = "";
        StringBuffer buf = new StringBuffer();
        NetworkInterface networkInterface = null;
        try {
            networkInterface = NetworkInterface.getByName("eth1");
            if (networkInterface == null) {
                networkInterface = NetworkInterface.getByName("wlan0");
            }
            if (networkInterface == null) {
                return "02:00:00:00:00:02";
            }
            byte[] addr = networkInterface.getHardwareAddress();
            for (byte b : addr) {
                buf.append(String.format("%02X:", b));
            }
            if (buf.length() > 0) {
                buf.deleteCharAt(buf.length() - 1);
            }
            macAddress = buf.toString();
        } catch (SocketException e) {
            e.printStackTrace();
            return "02:00:00:00:00:02";
        }
        return macAddress;
    }
    
    /**
     * 根据语言返回响应的货币价值-人民币兑换美元
     *
     * @param bigDecimal   人民币价值
     * @param exchangeRate 人民币兑美元汇率
     * @param scale        精度
     * @return
     */
    public static String showMoneyValue(BigDecimal bigDecimal, BigDecimal exchangeRate, int scale) {
        if (isRMB()) {
            return bigDecimal.setScale(scale, RoundingMode.DOWN).toPlainString();
        } else {
            return bigDecimal.divide(exchangeRate, scale, RoundingMode.DOWN).toPlainString();
        }
    }
    
    /**
     * 根据语言返回响应的货币价值-美元兑换人民币
     *
     * @param bigDecimal   美元价值
     * @param exchangeRate 人民币兑美元汇率
     * @param scale        精度
     * @return
     */
    public static String showMoneyValue2(BigDecimal bigDecimal, BigDecimal exchangeRate, int scale) {
        if (isRMB()) {
            return bigDecimal.multiply(exchangeRate).setScale(scale, RoundingMode.DOWN).toPlainString();
        } else {
            return bigDecimal.setScale(scale, RoundingMode.DOWN).toPlainString();
        }
    }
    
    public static String showCurrencySymbol() {
        return isRMB() ? "￥" : "$";
    }
    
    public static boolean isRMB() {
        if (Locale.CHINESE.equals(Locale.getDefault()) || Locale.SIMPLIFIED_CHINESE.equals(Locale
                .getDefault())) {
            return true;
        } else {
            return false;
        }
    }
    
    public static String getLanguage() {
        return isRMB() ? "cn" : "en";
    }
    
    public static String getMinDigit(int n) {
        return new BigDecimal("1").divide(new BigDecimal("10").pow(Math.abs(n)), Math.abs(n), RoundingMode
                .DOWN).toPlainString();
    }
    
}
