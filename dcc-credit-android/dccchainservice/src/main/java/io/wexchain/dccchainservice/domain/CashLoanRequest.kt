package io.wexchain.dccchainservice.domain

/**
 *Created by liuyang on 2018/12/11.
 */

data class CashLoanRequest(
        val index: Index,
        val idCertInfo: IdCertInfo,
        val bankCardCertInfo: BankCardCertInfo,
        val communicationLogCertInfo: CommunicationLogCertInfo,
        val extraPersonalInfo: ExtraPersonalInfo
) {
    data class Index(
            val address: String
    )

    data class ExtraPersonalInfo(
            val maritalStatus: String,
            val residentialProvince: String,
            val residentialCity: String,
            val residentialDistrict: String,
            val residentialAddress: String,
            val loanUsage: String,
            val workingType: String,
            val workingIndustry: String,
            val workingYears: String,
            val companyProvince: String,
            val companyCity: String,
            val companyDistrict: String,
            val companyAddress: String,
            val companyName: String,
            val companyTel: String,
            val contactInfoList: List<ContactInfo>
    ) {
        data class ContactInfo(
                val relationship: String,
                val name: String,
                val mobile: String
        )
    }

    data class IdCertInfo(
            val borrowerName: String,
            val idCardNo: String
    )

    data class BankCardCertInfo(
            val bankCardNo: String,
            val bankCardMobile: String
    )

    data class CommunicationLogCertInfo(
            val mobile: String,
            val communicationLog: String
    )

}









