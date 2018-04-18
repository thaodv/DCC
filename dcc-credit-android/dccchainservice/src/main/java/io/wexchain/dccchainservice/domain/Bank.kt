package io.wexchain.dccchainservice.domain


data class Bank(
		val bankName: String,
		val bankCode: String
){

    fun getLogoUrl() = "http://open.dcc.finance/images/bank/$bankCode@2x.png"
}