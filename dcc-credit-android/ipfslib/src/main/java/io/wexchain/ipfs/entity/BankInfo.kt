package io.wexchain.ipfs.entity

import java.io.Serializable

/**
 * 银行卡信息
 *Created by liuyang on 2018/8/7.
 */
data class BankInfo(
        val bankAuthenStatus: String,//银行卡认证状态
        val bankAuthenCode: Int,//银行内部代码
        val bankAuthenCodeName: String,//收款人姓名
        val bankAuthenCodeNumber: String, //卡号
        val bankAuthenMobile: String //预留手机号
) : Serializable