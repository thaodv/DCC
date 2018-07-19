package io.wexchain.dcc.service.frontend.common.convertor;

import io.wexchain.dcc.marketing.api.model.EcoRewardRule;
import io.wexchain.dcc.marketing.api.model.MiningRewardRecord;
import io.wexchain.dcc.marketing.api.model.RewardLog;
import io.wexchain.dcc.service.frontend.model.vo.EcoBonusRuleVo;
import io.wexchain.dcc.service.frontend.model.vo.EcoBonusVo;
import io.wexchain.dcc.service.frontend.model.vo.MiningRewardRecordVo;

/**
 * Created by yy on 2018/5/22.
 */
public class RewardConvertor {

    public static EcoBonusVo convertEcoBonusVo(RewardLog from){
        if(from == null){
            return null;
        }
        EcoBonusVo to = new EcoBonusVo();
        to.setAmount(from.getAmount());
        to.setReceiverAddress(from.getReceiverAddress());
        to.setId(from.getId());
        to.setMemo(from.getMemo());
        to.setCreatedTime(from.getCreatedTime().toDate());
        return to;
    }
    public static EcoBonusRuleVo convertEcoBonusRuleVo(EcoRewardRule from){
        if(from == null){
            return null;
        }
        EcoBonusRuleVo to = new EcoBonusRuleVo();
        to.setBonusAmount(from.getScore());
        to.setBonusCode(from.getBonusCode());
        to.setBonusName(from.getBonusName());
        to.setGroupCode(from.getGroupCode());
        return to;
    }

    public static MiningRewardRecordVo convertMiningRewardRecordVo(MiningRewardRecord from){
        if(from == null){
            return null;
        }
        MiningRewardRecordVo to = new MiningRewardRecordVo();
        to.setAddress(from.getAddress());
        to.setCreatedTime(from.getCreatedTime() != null ? from.getCreatedTime().toDate() : null);
        to.setBonusName(from.getBonusName());
        to.setScore(from.getScore());
        return to;
    }
}

