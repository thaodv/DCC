package io.wexchain.dccchainservice

class DccChainServiceException(message: String? = null,
                               val systemCode: String? = null,
                               val businessCode: String? = null
) : Exception(message)