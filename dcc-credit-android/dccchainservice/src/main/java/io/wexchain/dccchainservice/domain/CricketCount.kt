package io.wexchain.dccchainservice.domain

/**
 *Created by liuyang on 2019/1/21.
 */
data class CricketCount(val coin: String,
                        val offlineDurationUnit: Unit,
                        val offlineDuration: String) {

    enum class Unit {
        HOURS,
        MINUTES
    }

    override fun equals(other: Any?): Boolean {
        return super.equals(other)
    }

    override fun hashCode(): Int {
        var result = coin.hashCode()
        result = 31 * result + offlineDurationUnit.hashCode()
        result = 31 * result + offlineDuration.hashCode()
        return result
    }

    companion object {
        fun empty(): CricketCount {
            return CricketCount("-1", Unit.MINUTES, "0")
        }
    }
}