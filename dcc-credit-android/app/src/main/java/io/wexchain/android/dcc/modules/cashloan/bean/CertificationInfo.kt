package io.wexchain.android.dcc.modules.cashloan.bean

import java.io.Serializable

/**
 *Created by liuyang on 2018/10/17.
 */
data class CertificationInfo(
        val MarriageStatus: String,
        val ResideProvince: String?,
        val ResideCity: String?,
        val ResideArea: String?,
        val ResideAddress: String?,
        val LoanPurpose: String?,
        val WorkCategory: String?,
        val WorkIndustry: String?,
        val WorkYear: String?,
        val CompanyProvince: String?,
        val CompanyCity: String?,
        val CompanyArea: String?,
        val CompanyAddress: String?,
        val CompanyName: String?,
        val CompanyPhone: String?,
        val Contacts1Relation: String?,
        val Contacts1Phone: String?,
        val Contacts1Name: String?,
        val Contacts2Relation: String?,
        val Contacts2Phone: String?,
        val Contacts2Name: String?,
        val Contacts3Relation: String?,
        val Contacts3Phone: String?,
        val Contacts3Name: String?
) : Serializable {

    fun isCert(): Boolean {
        return MarriageStatus.check() && ResideProvince.check("选择省份") && ResideCity.check("选择城市") && ResideArea.check("选择地区")
        && !ResideAddress.isNullOrEmpty() && LoanPurpose.check()&&WorkCategory.check()&&WorkIndustry.check()&&!WorkYear.isNullOrEmpty()
        &&CompanyProvince.check("选择省份") && CompanyCity.check("选择城市") && CompanyArea.check("选择地区")&&!CompanyAddress.isNullOrEmpty()
        &&!CompanyName.isNullOrEmpty()&&Contacts1Relation.check()&&!Contacts1Phone.isNullOrEmpty()&&!Contacts1Name.isNullOrEmpty()&&Contacts2Relation.check()
        &&!Contacts2Phone.isNullOrEmpty()&&!Contacts2Name.isNullOrEmpty()&&Contacts3Relation.check() &&!Contacts3Phone.isNullOrEmpty()&&!Contacts3Name.isNullOrEmpty()
    }

    fun String?.check(tag: String = "请选择"): Boolean {
        return this?.let {
            it != tag && it != ""
        } ?: false
    }
}