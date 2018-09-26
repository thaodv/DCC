package io.wexchain.ipfs.entity

import java.io.Serializable

/**
 * 运营商认证信息
 *Created by liuyang on 2018/8/7.
 */
data class TNPhoneInfo(
        val sameCowmobileAuthenStatus: String,//手机运营商状态
        val sameCowmobileAuthenOrderid: Int,//订单ID
        val sameCowmobileAuthenNumber: String,//预留手机号
        val sameCowmobileAuthenCmData: String,//报告原文json
        val sameCowmobileAuthenExpired: Long//过期时间
) : Serializable