package io.wexchain.dcc.service.frontend;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wexyun.open.api.client.process.MapDataConcator;
import com.wexyun.open.api.util.json.JsonUtil;
import io.wexchain.dcc.service.frontend.model.request.LoginRequest;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.io.FileUtils;
import org.testng.annotations.Test;

import java.io.File;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.Security;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * Created by yy on 2018/4/26.
 */
public class SignTest {

    static {
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
    }

    private static ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void getSign() throws Exception {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("0x8fd566776e41d7168ea07700c2cd4a35be55b6d8");
        loginRequest.setPassword("");
        loginRequest.setNonce("75ee77f77cf74fb5b7d59559ec1594bf");
        loginRequest.setAddress("0x8fd566776e41d7168ea07700c2cd4a35be55b6d8");
        String signStr = getSignStr(loginRequest);
        System.out.println(signStr);
        String signature = getSignature(signStr);
        System.out.println("address=0xdeca0c99d8deb636f77f6b04c40997733c2faf67&nonce=75ee77f77cf74fb5b7d59559ec1594bf&username=0xdeca0c99d8deb636f77f6b04c40997733c2faf67");
        System.out.println(signature);

    }

    private static String getSignStr(Object o) throws Exception {
        Map<String, Object> allMap = objectMapper.readValue(JsonUtil.toJson(o), new TypeReference<Map<String, Object>>() {
        });
        //去除空参
        Map<String, Object> signParamsMap = removeNullParam(allMap);

        //进行参数排序
        Map<String, Object> sortedParamsMap = new TreeMap<String, Object>(signParamsMap);

        //生成签名字符串
        return MapDataConcator.concat(sortedParamsMap);
    }

    private static String getSignature(String signStr) throws Exception {
        byte[] prKeyByte = FileUtils.readFileToByteArray(new File("src/test/resources/pri.key"));

        KeyFactory kf = KeyFactory.getInstance("RSA","BC");

        PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(prKeyByte);

        PrivateKey generatePrivate = kf.generatePrivate(pkcs8EncodedKeySpec);

        Signature dsa = Signature.getInstance("SHA256withRSA");
        dsa.initSign(generatePrivate);

        byte[] strByte = signStr.getBytes("UTF-8");
        dsa.update(strByte);
        return Hex.encodeHexString(dsa.sign());
    }

    private static Map<String, Object> removeNullParam(Map<String, Object> paramsMap){
        Map<String, Object> paramsMapCopy = new HashMap<String, Object>(paramsMap.size());
        paramsMapCopy.putAll(paramsMap);
        Set<Map.Entry<String, Object>> entries = paramsMap.entrySet();
        for (Map.Entry<String, Object> entry : entries) {
            if (entry.getValue() == null) {
                paramsMapCopy.remove(entry.getKey());
            }
        }
        return paramsMapCopy;
    }
}
