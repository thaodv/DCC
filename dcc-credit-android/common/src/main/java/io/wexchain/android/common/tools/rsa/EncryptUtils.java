package io.wexchain.android.common.tools.rsa;

import java.security.PublicKey;

/**
 * @author Created by Wangpeng on 2017/12/26 14:10.
 * Usage:
 */
public class EncryptUtils {
    
    private static EncryptUtils mEncryptUtils;
    
    private EncryptUtils() {
    }
    
    public static EncryptUtils getInstance() {
        if (null == mEncryptUtils) {
            mEncryptUtils = new EncryptUtils();
        }
        return mEncryptUtils;
    }
    
    public String encode(String str, String publicKeyStr) {
        try {
            PublicKey publicKey = RSAUtils.loadPublicKey(publicKeyStr);
            byte[] bytes = RSAUtils.encryptData(str.getBytes(), publicKey);
            return Base64Utils.encode(bytes);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
        
    }
    
}
