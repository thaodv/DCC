package io.wexchain.wexchainservice

import io.wexchain.wexchainservice.domain.BusinessCodes

/**
 * Created by lulingzhi on 2017/11/28.
 */

class WexChainException(message: String? = null,
                        val systemCode: String? = null,
                        val businessCode: String? = null
) : Exception(message) {

}