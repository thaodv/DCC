package io.wexchain.dccchainservice.domain

import java.math.BigInteger


data class MarketingActivityScenario(
		val activityCode: Any,
		val code: String,
		val name: String,
		val amount: BigInteger,
		val createdTime: String,
		val qualification: Qualification?
){
	/**
	 * 领取状态：
	 */
    enum class Qualification(
			val description:String
	){
		REDEEMED("已领取"),

		AVAILABLE("可以领取");
	}
}