package io.wexchain.ipfs.entity

import java.io.Serializable

/**
 * 运营商认证信息
 *Created by liuyang on 2018/8/7.
 */
data class PhoneInfo(
        val mobileAuthenStatus: String,//手机运营商状态
        val mobileAuthenNumber: String//预留手机号
) : Serializable