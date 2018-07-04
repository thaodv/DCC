package io.wexchain.dccchainservice.domain
import com.google.gson.annotations.SerializedName
import java.math.BigInteger


data class MineCandy(
    @SerializedName("id") val id: Long,
    @SerializedName("assetCode") val assetCode: String,
    @SerializedName("assetUnit") val assetUnit: String,
    @SerializedName("amount") val amount: BigInteger,
    @SerializedName("status") val status: Status
){
    enum class Status(description:String){
        CREATED("已创建"),

        PICKED("已领取"),

        DELIVERED("已发放");
    }
}