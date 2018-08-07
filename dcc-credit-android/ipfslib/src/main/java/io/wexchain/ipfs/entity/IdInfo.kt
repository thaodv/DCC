package io.wexchain.ipfs.entity

import java.io.Serializable
import java.util.*

/**
 * 身份证认证信息
 *Created by liuyang on 2018/8/7.
 */
data class IdInfo(
        val positivePhoto: ByteArray, //身份证正面照
        val backPhoto: ByteArray,//身份证反面
        val facePhoto: ByteArray,//头像
        val idAuthenStatus: String,//实名认证状态
        val orderId: Int, //订单id
        val similarity: Int, //相似度
        val userAddress: String,//地址
        val userName: String,//姓名
        val userNumber: String,//身份证号码
        val userNation: String,//民族
        val userSex: String,//性别
        val userTimeLimit: String,//过期时间
        val userAuthority: String,//签名机构
        val userYear: Int, //出生年
        val userMonth: Int,//出生月
        val userDay: Int//出生日
) : Serializable {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as IdInfo

        if (!Arrays.equals(positivePhoto, other.positivePhoto)) return false
        if (!Arrays.equals(backPhoto, other.backPhoto)) return false
        if (!Arrays.equals(facePhoto, other.facePhoto)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = Arrays.hashCode(positivePhoto)
        result = 31 * result + Arrays.hashCode(backPhoto)
        result = 31 * result + Arrays.hashCode(facePhoto)
        return result
    }
}