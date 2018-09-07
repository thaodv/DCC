package io.wexchain.ipfs.entity

import java.io.Serializable
import java.util.*

/**
 * 身份证认证信息
 *Created by liuyang on 2018/8/7.
 */
data class IdInfo(
        val positivePhoto: String, //身份证正面照
        val backPhoto: String,//身份证反面
        val facePhoto: String,//头像
        val idAuthenStatus: String,//实名认证状态
        val orderId: Int, //订单id
        val similarity: String, //相似度
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
) : Serializable