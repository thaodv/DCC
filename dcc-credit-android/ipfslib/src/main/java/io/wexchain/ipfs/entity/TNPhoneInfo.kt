package io.wexchain.ipfs.entity

import java.io.Serializable

/**
 * 运营商认证信息
 *Created by liuyang on 2018/8/7.
 */
data class TNPhoneInfo(
      


        val sameCowMobileAuthenStatus: String,//手机运营商状态
        val sameCowMobileAuthenOrderid: String,//订单ID
        val sameCowMobileAuthenNumber: String,//预留手机号
        val sameCowMobileAuthenCmData: String,//报告原文json
        val sameCowMobileAuthenExpired: String,//过期时间
        val sameCowMobileAuthenNonce: String//Nonce


) : Serializable