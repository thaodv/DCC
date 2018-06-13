package io.wexchain.dcc.service.frontend.service.marketing.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.wexmarket.topia.commons.pagination.PageParam;
import com.wexmarket.topia.commons.pagination.Pagination;
import com.wexmarket.topia.commons.pagination.SortPageParam;
import com.wexmarket.topia.commons.rpc.ListResultResponse;
import io.wexchain.dcc.marketing.api.constant.RedeemTokenStatus;
import io.wexchain.dcc.marketing.api.model.RedeemToken;
import io.wexchain.dcc.marketing.api.model.request.ApplyBonusRequest;
import io.wexchain.dcc.marketing.api.model.request.QueryRedeemTokenRequest;
import io.wexchain.dcc.service.frontend.integration.marketing.BonusOperationClient;
import io.wexchain.dcc.service.frontend.integration.marketing.RedeemTokenOperationClient;
import io.wexchain.dcc.service.frontend.model.request.ApplyRedeemTokenRequest;
import io.wexchain.dcc.service.frontend.model.vo.RedeemTokenVo;
import io.wexchain.dcc.service.frontend.service.marketing.RedeemTokenService;
import io.wexchain.dcc.service.frontend.utils.ResultResponseValidator;
import jodd.bean.BeanCopy;
import okhttp3.*;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.*;

/**
 * RedeemTokenServiceImpl
 *
 * @author zhengpeng
 */
@Service
public class RedeemTokenServiceImpl implements RedeemTokenService {

    private Logger logger = LoggerFactory.getLogger(RedeemTokenServiceImpl.class);

    @Resource
    private RedeemTokenOperationClient redeemTokenOperationClient;
    @Resource
    private BonusOperationClient bonusOperationClient;

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
            logger.info("id hash:{}", idHash);
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
                if(vo.getAmount() != null){
                    vo.getAmount().setScale(0);
                }
                return vo;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public List<RedeemToken> queryBonus(String address) {
        QueryRedeemTokenRequest queryRedeemTokenRequest = new QueryRedeemTokenRequest();
        queryRedeemTokenRequest.setActivityCode("10002");
        queryRedeemTokenRequest.setScenarioCodeList(Collections.singletonList("10002001"));
        queryRedeemTokenRequest.setAddress(address);
        queryRedeemTokenRequest.setStatusList(Collections.singletonList(RedeemTokenStatus.CREATED));
        ListResultResponse<RedeemToken> redeemTokenListResultResponse = redeemTokenOperationClient.queryRedeemToken(queryRedeemTokenRequest);
        List<RedeemToken> listResult = ResultResponseValidator.getListResult(redeemTokenListResultResponse);
        if(CollectionUtils.isNotEmpty(listResult)){
            listResult.forEach(redeemToken -> {
                if(redeemToken.getAmount() != null){
                    redeemToken.getAmount().setScale(0);
                }
            });
        }

        return listResult;
    }

    @Override
    public RedeemToken applyBonus(String address, Long redeemTokenId) {
        ApplyBonusRequest applyBonusRequest = new ApplyBonusRequest();
        applyBonusRequest.setAddress(address);
        applyBonusRequest.setActivityCode("10002");
        applyBonusRequest.setScenarioCode("10002001");
        applyBonusRequest.setRedeemTokenId(redeemTokenId);
        RedeemToken redeemToken = ResultResponseValidator.getResult(bonusOperationClient.applyBonus(applyBonusRequest));
        if(redeemToken != null){
            redeemToken.getAmount().setScale(0);
        }
        return redeemToken;
    }

}
