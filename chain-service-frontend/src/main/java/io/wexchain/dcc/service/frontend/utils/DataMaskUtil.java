/**
 * 
 */
package io.wexchain.dcc.service.frontend.utils;

import org.apache.commons.lang3.StringUtils;

/**
 * <p>数据打掩码处理</p>
 * @author huangfeitao
 * @version $Id: DataProcessUtil.java, v 0.1 2015年6月4日 上午10:17:08 huangfeitao Exp $
 */
public class DataMaskUtil {

    /**默认掩码*/
    private static String DEFAULT_MASK   = "*";

    /**手机号掩码*/
    private static String MOBILE_MASK    = "****";

    private static String BANK_CARD_MASK    = "***************";

    /**身份证掩码*/
    private static String IDCERT_MASK_15 = "*************";
    private static String IDCERT_MASK_18 = "****************";
    private static String COMPANY_MASK = "...";

    /**
     *  姓名转换成除首位外为*格式
     *  例如：云金融 => 云**
     */
    public static String maskName(String s) {
        if (StringUtils.isBlank(s)) {
            return StringUtils.EMPTY;
        }
        if (s.length() == 1) {
            return s;
        }
        if (s.length() == 2) {
            return s.substring(0, 1) + DEFAULT_MASK;
        }
        StringBuilder temp = new StringBuilder(s.substring(0, 1));
        for (int i = 1; i < s.length() - 1; i++) {
            temp.append(DEFAULT_MASK);
        }
        return temp.append(s.substring(s.length() - 1, s.length())).toString();
    }
    /**
     *  超过5位公司名称转换成省略号
     *
     */
    public static String companyMaskName(String s) {
        if (StringUtils.isBlank(s)) {
            return StringUtils.EMPTY;
        }
        if (s.length() > 5) {
            StringBuilder temp = new StringBuilder(s.substring(0, 5));
            return temp.append(COMPANY_MASK).toString();
        }
        return s;
    }
    /**
     *  手机号码打码
     *  例如：18611111111 => 186****1111
     */
    public static String maskMobile(String s) {
        if (StringUtils.isBlank(s)) {
            return StringUtils.EMPTY;
        }
        return StringUtils.substring(s, 0, 3) + MOBILE_MASK + StringUtils.substring(s, -4);
    }

    /**
     * 身份证打码,兼容15位身份证
     * 例如： 420222198901012222 => 4****************2
     */
    public static String maskIdCert(String s) {
        if (StringUtils.isBlank(s)) {
            return StringUtils.EMPTY;
        }
        String mask = s.length() == 18 ? IDCERT_MASK_18 : IDCERT_MASK_15;
        return StringUtils.substring(s, 0, 1) + mask + StringUtils.substring(s, -1);
    }

    /**
     * 银行卡打码
     * 例如： 6212261001062141240 => ***************1240
     */
    public static String maskBankCardNo(String s) {
        if (StringUtils.isBlank(s)) {
            return StringUtils.EMPTY;
        }
        return  BANK_CARD_MASK+ StringUtils.substring(s, -4, s.length());
    }

    /**
     * 邮箱打码
     */
    public static String maskEmail(String s) {
        if (StringUtils.isBlank(s)) {
            return StringUtils.EMPTY;
        }

        int atIndex = s.indexOf("@");

        if (atIndex != -1 && atIndex != 0) {
            int replaceIndex = atIndex % 2 == 0 ? atIndex / 2 : (atIndex - 1) / 2;
            return StringUtils.substring(s, 0, replaceIndex) + DEFAULT_MASK
                   + StringUtils.substring(s, replaceIndex + 1);
        }
        return s;
    }

    /**
     * 普通文本打码
     */
    public static String maskText(String s) {
        if (StringUtils.isBlank(s)) {
            return StringUtils.EMPTY;
        }
        if (s.length() == 1) {
            return s;
        }

        int replaceIndex = s.length() % 2 == 0 ? s.length() / 2 : (s.length() - 1) / 2;
        return StringUtils.substring(s, 0, replaceIndex) + DEFAULT_MASK
               + StringUtils.substring(s, replaceIndex + 1);
    }

    public static void main(String[] args) {

        //        System.out.println(maskEmail("hft1989@yeah.net"));
        //        System.out.println(maskEmail("hft198@yeah.net"));
        //        System.out.println(maskEmail("h@yeah.net"));
        //        System.out.println(maskEmail("hf@yeah.net"));
        //        System.out.println(maskEmail("hft@yeah.net"));
        //        System.out.println(maskEmail("@yeah.net"));
                System.out.println(maskBankCardNo("6212261001062141240"));


    }
}
