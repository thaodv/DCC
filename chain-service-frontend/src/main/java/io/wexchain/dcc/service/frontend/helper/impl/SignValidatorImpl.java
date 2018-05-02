package io.wexchain.dcc.service.frontend.helper.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.weihui.basic.util.marshaller.json.JsonUtil;
import com.wexmarket.topia.commons.basic.exception.ErrorCodeValidate;
import com.wexyun.open.api.client.process.MapDataConcator;
import io.wexchain.dcc.service.frontend.common.enums.FrontendErrorCode;
import io.wexchain.dcc.service.frontend.helper.CaRests;
import io.wexchain.dcc.service.frontend.helper.SignValidator;
import io.wexchain.dcc.service.frontend.model.request.SignBaseRequest;
import io.wexchain.dcc.service.frontend.utils.SignVerifyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * <p> 实名验证服务 </p>
 * 
 * @author yanyi
 */
@Service
public class SignValidatorImpl implements SignValidator {
    private static final Logger logger = LoggerFactory.getLogger(SignValidatorImpl.class);
    //Map类型
    private static TypeReference<Map<String, Object>> MAP_REFER = new TypeReference<Map<String, Object>>() {
    };
    private static ObjectMapper objectMapper = new ObjectMapper();
    static {
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    @Autowired
    private CaRests caRests;

    @Override
    public boolean validateSign(SignBaseRequest baseRequest) {
        byte[] pubKey = caRests.getPubKey(baseRequest.getAddress());
        ErrorCodeValidate.isTrue(pubKey != null && pubKey.length > 0, FrontendErrorCode.PUK_NOT_FOUNT);
        //获取所有参数
        Map<String, Object> allParamsMap = getAllParams(baseRequest);
        //去除空参
        Map<String, Object> signParamsMap = removeNullParam(allParamsMap);

        //进行参数排序
        Map<String, Object> sortedParamsMap = new TreeMap<String, Object>(signParamsMap);

        //生成签名字符串
        String signStr = MapDataConcator.concat(sortedParamsMap);
        //执行验签
        boolean verify = SignVerifyUtil.verify(pubKey, signStr, baseRequest.getSignature());
        return verify;
    }

    private Map<String, Object> getAllParams(SignBaseRequest baseRequest){
        try {
            return objectMapper.readValue(JsonUtil.toJson(baseRequest), MAP_REFER);
        } catch (Exception e) {
            logger.error("json反序列化异常", e);
            throw new IllegalArgumentException("由JSON字符串时转换为对象时异常", e);
        }
    }

    private Map<String, Object> removeNullParam(Map<String, Object> paramsMap){
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
