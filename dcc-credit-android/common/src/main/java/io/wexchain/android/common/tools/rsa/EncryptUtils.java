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
    
    public static void main(String[] args) {
        
        String publicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAtXGCxvE757OYpMwTU4Wk" +
                "IN08S/jThxnk3kVGi9ZlUCZyv3qrmdHUo0yExKyF4CDFiv0ujsKwwJrsx8LqAGOL" +
                "2CxBdzEbGb1r+F+sXH3qy9xEplIV35d9VknspbNQQCD6IvoYYKAq/hkL8/bGAlcL" +
                "Kv9r6YmU4x1Uz7zBudA/YDyGiYQul3drJd3hRXvbZW2xsdM5gtVCRGetdZJw3Z0g" +
                "ksrIVbj1Zy4Bv7Fs9suHJNbAh3+HwDMXXsv7Vkajcp5/6h8yjMILSzf+jd46tJXC" +
                "WSk0hGE+uHRP/s8SzfRlJSJ7aniN09xu5DhEocIkMbEMHLnaky7zOSy5pb8xMoytXQIDAQAB";
        
        String res = EncryptUtils.getInstance().encode("147258", publicKey);
        System.out.println(res);
    }
    
}
