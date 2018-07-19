package io.wexchain.dcc.service.frontend.common.convertor;

import io.wexchain.dcc.marketing.api.model.candy.Candy;
import io.wexchain.dcc.service.frontend.model.vo.CandyVo;
import org.apache.commons.collections.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yy on 2018/5/22.
 */
public class CandyConvertor {

    public static CandyVo convert(Candy from){
        if(from == null){
            return null;
        }
        CandyVo to = new CandyVo();
        to.setId(from.getId());
        to.setAmount(from.getAmount());
        to.setAssetCode(from.getAssetCode());
        to.setAssetUnit(from.getAssetUnit());
        to.setStatus(from.getStatus());
        return to;
    }
    public static List<CandyVo> convert(List<Candy> from){
        if(CollectionUtils.isEmpty(from)){
            return null;
        }
        List<CandyVo> to = new ArrayList<>();
        for (Candy candy : from) {
            to.add(convert(candy));
        }
        return to;
    }
}

