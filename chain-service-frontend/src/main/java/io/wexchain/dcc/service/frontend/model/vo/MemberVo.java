package io.wexchain.dcc.service.frontend.model.vo;


import java.util.Date;

/**
 * <p>
 * 会员信息
 * </p>
 * 
 */
public class MemberVo {

    /**
     * 会员id
     */
    private String            memberId;

    /**
     * 创建时间
     */
    private Date              gmtCreate;

    /**
     * 会员标识信息集合
     */
    private String            loginName;

    /**
     * 邀请人会员id
     */
    private String            inviteMemberId;

    /**
     * 邀请码
     */
    private String            inviteCode;

    public String getMemberId() {
        return memberId;
    }

    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }

    public Date getGmtCreate() {
        return gmtCreate;
    }

    public void setGmtCreate(Date gmtCreate) {
        this.gmtCreate = gmtCreate;
    }

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    public String getInviteMemberId() {
        return inviteMemberId;
    }

    public void setInviteMemberId(String inviteMemberId) {
        this.inviteMemberId = inviteMemberId;
    }

    public String getInviteCode() {
        return inviteCode;
    }

    public void setInviteCode(String inviteCode) {
        this.inviteCode = inviteCode;
    }
}
