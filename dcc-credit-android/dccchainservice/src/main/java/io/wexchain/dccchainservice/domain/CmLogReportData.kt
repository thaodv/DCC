package io.wexchain.dccchainservice.domain


data class CmLogReportData(
		val isComplete: String,
		val reportData: String?,
		val fail: Boolean
){
    fun hasCompleted(): Boolean {
        return isComplete == "Y"
    }
}