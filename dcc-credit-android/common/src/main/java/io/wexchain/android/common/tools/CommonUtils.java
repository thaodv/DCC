package io.wexchain.android.common.tools;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Locale;

/**
 * @author Created by Wangpeng on 2018/10/24 15:37.
 * usage:
 */
public class CommonUtils {
    
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
    
    public static BigDecimal showMoney(BigDecimal bigDecimal, BigDecimal exchangeRate, int scale, int
            roundingMode) {
        if (isRMB()) {
            return bigDecimal;
        } else {
            return bigDecimal.divide(exchangeRate, scale, roundingMode);
        }
    }
    
    public static String showCurrencySymbol() {
        return isRMB() ? "￥" : "$";
    }
    
    public static boolean isRMB() {
        if ("zh".equals(Locale.getDefault().getLanguage())) {
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
