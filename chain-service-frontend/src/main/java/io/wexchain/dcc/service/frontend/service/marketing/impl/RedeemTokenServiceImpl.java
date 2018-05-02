package io.wexchain.dcc.service.frontend.service.marketing.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.weihui.basic.util.marshaller.json.JsonUtil;
import io.wexchain.dcc.marketing.api.model.RedeemToken;
import io.wexchain.dcc.service.frontend.integration.marketing.RedeemTokenOperationClient;
import io.wexchain.dcc.service.frontend.model.request.ApplyRedeemTokenRequest;
import io.wexchain.dcc.service.frontend.model.vo.RedeemTokenVo;
import io.wexchain.dcc.service.frontend.service.marketing.RedeemTokenService;
import jodd.bean.BeanCopy;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * RedeemTokenServiceImpl
 *
 * @author zhengpeng
 */
@Service
public class RedeemTokenServiceImpl implements RedeemTokenService {

    @Resource
    private RedeemTokenOperationClient redeemTokenOperationClient;

    @Autowired
    private OkHttpClient okHttpClient;

    @Autowired
    private IdHashServiceImpl idHashService;

    @Value("${dcc.logic.rest.address}")
    private String logicUrl;

    private Map<String, String> map = new HashMap<>();

    {
        map.put("10001001", "id");
        map.put("10001002", "bankCard");
        map.put("10001003", "communicationLog");
    }

    @Override
    public RedeemTokenVo applyRedeemToken(ApplyRedeemTokenRequest request) {
        try {
            String idHash = idHashService.getIdHashByAddress(request.getAddress());
            RequestBody requestBody = new FormBody.Builder()
                            .add("scenarioCode", request.getScenarioCode())
                            .add("address", request.getAddress())
                            .add("idHash", idHash)
                            .build();
            Request req = new Request.Builder()
                    .url(logicUrl + "/marketing/" + map.get(request.getScenarioCode()) + "/applyRedeemToken")
                    .post(requestBody)
                    .build();
            Response response = okHttpClient.newCall(req).execute();
            if (response.isSuccessful()) {
                JSONObject jsonObject = JSON.parseObject(response.body().string());
                RedeemToken redeemToken = jsonObject.getObject("result", RedeemToken.class);
                RedeemTokenVo vo = new RedeemTokenVo();
                BeanCopy.fromBean(redeemToken).toBean(vo).copy();
                return vo;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
