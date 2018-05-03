package io.wexchain.dcc.service.frontend.service.marketing.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import io.wexchain.dcc.service.frontend.service.marketing.IdHashService;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * IdHashServiceImpl
 *
 * @author zhengpeng
 */
@Service
public class IdHashServiceImpl implements IdHashService {

    @Autowired
    private OkHttpClient okHttpClient;

    @Value("${dcc.logic.rest.address}")
    private String logicUrl;

    @Override
    public String getIdHashByAddress(String address) {
        try {
            Request request = new Request.Builder()
                    .url(logicUrl + "/dcc/endorsement/id/get/idHash?address=" + address)
                    .build();
            Response response = okHttpClient.newCall(request).execute();//得到Response 对象
            if (response.isSuccessful()) {
                String json = response.body().string();
                JSONObject jsonObject = JSON.parseObject(json);
                String idHash = jsonObject.getObject("result", String.class);
                return idHash;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Boolean getVerifyStatus(String bizCode, String address) {
        try {
            Request request = new Request.Builder()
                    .url(logicUrl + "/marketing/" + bizCode + "/isPassed?address=" + address)
                    .build();
            Response response = okHttpClient.newCall(request).execute();//得到Response 对象
            if (response.isSuccessful()) {
                String json = response.body().string();
                JSONObject jsonObject = JSON.parseObject(json);
                Boolean object = jsonObject.getObject("result", Boolean.class);
                return object;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
