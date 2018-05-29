package io.wexchain.dccchainservice.domain
import com.google.gson.annotations.SerializedName

data class ScfAccountInfo(
    @SerializedName("memberId") val memberId: String,
    @SerializedName("gmtCreate") val gmtCreate: Long,
    @SerializedName("loginName") val loginName: String,
    @SerializedName("inviteMemberId") val inviteMemberId: String?,
    @SerializedName("inviteCode") val inviteCode: String?
){
    companion object {
        val ABSENT = ScfAccountInfo("ABSENT",-1L,"ABSENT",null,null)
    }
}