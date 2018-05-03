package io.wexchain.dcc.service.frontend.utils;


import org.apache.commons.codec.binary.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.Security;
import java.security.Signature;
import java.security.spec.X509EncodedKeySpec;

/**
 * <p>
 * </p>
 *
 * @author yanyi
 */
public class SignVerifyUtil {
    static {
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
    }

    private static final Logger logger = LoggerFactory.getLogger(SignVerifyUtil.class);

    public static boolean verify(byte[] pubKey,String text,String signature){
        try {
            X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(pubKey);
            KeyFactory kf = KeyFactory.getInstance("RSA", "BC");
            PublicKey publicKey = kf.generatePublic(x509EncodedKeySpec);
            Signature dsa = Signature.getInstance("SHA256withRSA");
            dsa.initVerify(publicKey);
            byte[] bytes = text.getBytes("UTF-8");
            dsa.update(bytes);
            byte[] sig = Hex.decodeHex(signature);
            boolean verify = dsa.verify(sig);
            return verify;
        }catch (Exception e){
            logger.error("验证验签错误：[{}]",e);
            throw new RuntimeException(e);
        }
    }
}
