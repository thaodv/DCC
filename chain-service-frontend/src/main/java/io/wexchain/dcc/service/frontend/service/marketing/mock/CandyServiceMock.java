package io.wexchain.dcc.service.frontend.service.marketing.mock;

import io.wexchain.dcc.marketing.api.constant.CandyStatus;
import io.wexchain.dcc.service.frontend.model.vo.CandyVo;
import io.wexchain.dcc.service.frontend.service.marketing.CandyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

/**
 * EcoServiceImpl
 *
 * @author zhengpeng
 */
@Service(value = "candyServiceMock")
public class CandyServiceMock implements CandyService {

    private Logger logger = LoggerFactory.getLogger(CandyServiceMock.class);

    @Override
    public List<CandyVo> queryCandyList(String address,String activityCode) {

        List<CandyVo> candyList = new ArrayList<>(16);
        for (int i= 1 ; i<= 16 ; i++){
            CandyVo candy = new CandyVo();
            candy.setId(Long.parseLong(Integer.toString(i)));
            candy.setAmount(new BigInteger(Integer.toString(i)));
            candy.setAssetCode("DCC_JUZIX");
            candy.setAssetUnit("WEI");
            candy.setStatus(CandyStatus.CREATED);
            candyList.add(candy);
        }
        return candyList;
    }

    @Override
    public CandyVo pickCandy(String address, Long candyId) {
        CandyVo candy = new CandyVo();
        candy.setId(candyId);
        candy.setAmount(new BigInteger(candyId.toString()));
        candy.setAssetCode("DCC_JUZIX");
        candy.setAssetUnit("WEI");
        candy.setStatus(CandyStatus.PICKED);
        return candy;
    }
}
