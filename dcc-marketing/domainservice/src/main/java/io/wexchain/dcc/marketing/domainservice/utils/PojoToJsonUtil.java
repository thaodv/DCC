package io.wexchain.dcc.marketing.domainservice.utils;


import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.serializer.ValueFilter;
import com.wexmarket.topia.commons.rpc.ResultResponse;
import io.wexchain.dcc.marketing.api.model.MarketDetailPo;

import java.util.List;

/**
 * Created by wuxinxin on 2018/7/20.
 */
public class PojoToJsonUtil {

    public static String MarketDetailPoToJson(MarketDetailPo marketDetailPo){
        return JSONObject.toJSONString(marketDetailPo,new ValueFilter() {
            @Override
            public Object process(Object obj, String s, Object v) {
                if(v==null) {
                    return "null";
                }
                return v;
            }
        });
    }

    public static String MarketDetailPoToJson(List<MarketDetailPo> marketDetailPos){
        return JSONObject.toJSONString(marketDetailPos,new ValueFilter() {
            @Override
            public Object process(Object obj, String s, Object v) {
                if(v==null) {
                    return "null";
                }
                return v;
            }
        });
    }

    public static String ResultResponseToJson(ResultResponse resultResponse){
        return JSONObject.toJSONString(resultResponse,new ValueFilter() {
            @Override
            public Object process(Object obj, String s, Object v) {
                if(v==null) {
                    return "null";
                }
                return v;
            }
        }, SerializerFeature.MapSortField);
    }

    public  static MarketDetailPo JsonToMarketDetailPo(String MarketDetailPoJson){
        try {
            return JSONObject.parseObject(MarketDetailPoJson, MarketDetailPo.class);
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

}
