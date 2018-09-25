package io.wexchain.android.dcc.constant

/**
 * Created by lulingzhi on 2017/11/24.
 */
object RequestCodes {
    const val SCAN = 0x4001
    const val AUTHENTICATE = 0x4002
    const val IMPORT_PASSPORT = 0x4003
    const val GET_PICTURE = 0x4004
    const val CREATE_TRANSACTION = 0x4005
    const val SCAN_ID_CARD = 0x4006
    const val CAPTURE_HEAD_PORTRAIT = 0x4007
    const val CAPTURE_IMAGE = 0x4008
    const val CHOOSE_BENEFICIARY_ADDRESS = 0x4009

    const val PASSPORT_REQUIRED = 0x5001
    const val AUTH_KEY_REQUIRED = 0x5002
    const val SET_LOCAL_PROTECT = 0x5003
}