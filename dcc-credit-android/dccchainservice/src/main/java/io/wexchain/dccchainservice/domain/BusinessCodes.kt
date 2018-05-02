package io.wexchain.dccchainservice.domain

/**
 * Created by lulingzhi on 2017/12/5.
 */

object BusinessCodes {

    /**
     * scf access token invalid access is forbidden
     */
    const val TOKEN_FORBIDDEN = "FORBIDDEN"

    const val TICKET_INVALID = "TICKET_INVALID"
    const val CHALLENGE_FAILURE = "CHALLENGE_FAILURE"
    const val SIGN_MESSAGE_INVALID = "SIGN_MESSAGE_INVALID"
    const val INTERAL_ERROR = "INTERAL_ERROR"

    const val ILLEGAL_ARGUMENT = "ILLEGAL_ARGUMENT"//参数校验未通过
    const val FILE_UPLOAD_ERROR = "FILE_UPLOAD_ERROR"//文件上传错误
    const val ILLEGAL_SIGN = "ILLEGAL_SIGN"//验签未通过
    const val CERT_ORDER_NOT_FOUND = "CERT_ORDER_NOT_FOUND"//订单不存在或未上链
    const val DIGEST_NOT_MATCH = "DIGEST_NOT_MATCH"//摘要不匹配
    const val BANK_CARD_VALIDATE_FAIL = "BANK_CARD_VALIDATE_FAIL"//银行卡校验失败
    const val BIZ_NOT_SUPPORT = "BIZ_NOT_SUPPORT"//还未支持该业务
    const val GET_RECEIPT_TIMEOUT = "GET_RECEIPT_TIMEOUT"//获取回执超时
    const val UPDATE_ORDER_FAIL = "UPDATE_ORDER_FAIL"//修改订单失败
    const val OCR_VALIDATE_FAIL = "OCR_VALIDATE_FAIL"//OCR验证失败
}