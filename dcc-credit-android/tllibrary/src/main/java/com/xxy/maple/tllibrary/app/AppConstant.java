package com.xxy.maple.tllibrary.app;


import com.xxy.maple.tllibrary.BuildConfig;
import com.xxy.maple.tllibrary.retrofit.TlDns;

/**
 * Created by 94369 on 2016/3/17.
 */
public class AppConstant {

    public static class GlobalValue {

        /**
         * 打开钱包时区分的TYPE,用来解析传递过来不同的Data和签名用
         */
        public static final String TYPE_CREATE = "1";  //创建合约
        public static final String TYPE_MORTGAGE = "2"; // 授权和抵押
        public static final String TYPE_LEND = "3"; //授权和出借
        public static final String TYPE_REVERT = "4"; //授权和退还
        public static final String TYPE_INDEMNITY = "5"; //赔付
        public static final String TYPE_CANCLE = "6"; //取消

    }


    /**
     * 需要以键值对持久化到SharePerference文件中的Key常量值
     */
    public static class SaveInfoKey {
        public static final String KEY_ADDRESS = "address";
    }

    /**
     * 存放参与API请求的常量
     */
    public static class ApiParams {

        //中文
        public static String LANGUAGE_CN = "cn";
        //英文
        public static String LANGUAGE_EN = "en";

    }

    public static class WebPageUrl {
        //private static final String LOAN_PREFIX = "http://tleos.xiaoxinyong.com";
        private static final String LOAN_PREFIX = BuildConfig.TL_H5_URL;
        public static final String LOAN_INDEX = LOAN_PREFIX + "/#/Index";


    }


}
