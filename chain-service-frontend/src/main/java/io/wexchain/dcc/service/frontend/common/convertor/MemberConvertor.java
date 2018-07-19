package io.wexchain.dcc.service.frontend.common.convertor;

import com.wexyun.open.api.domain.member.Member;
import io.wexchain.dcc.service.frontend.model.vo.MemberVo;
import org.apache.commons.collections.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yy on 2018/5/22.
 */
public class MemberConvertor {

    public static MemberVo convert(Member from,boolean displayInvite){
        if(from == null){
            return null;
        }
        MemberVo to = new MemberVo();

        to.setGmtCreate(from.getGmtCreate());
        to.setInviteMemberId(from.getInviteMemberId());
        if(!displayInvite){
            to.setInviteCode(from.getInviteCode());
        }
        to.setMemberId(from.getMemberId());
        if(CollectionUtils.isNotEmpty(from.getIdentitys())){
            to.setLoginName(from.getIdentitys().get(0).getIdentity());
        }

        return to;
    }
    public static List<MemberVo> convert(List<Member> from){
        if(CollectionUtils.isEmpty(from)){
            return null;
        }
        List<MemberVo> to = new ArrayList<>();
        for (Member member : from) {
            to.add(convert(member,true));
        }
        return to;
    }
}

