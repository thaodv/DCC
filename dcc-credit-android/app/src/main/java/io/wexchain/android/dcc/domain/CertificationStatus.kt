package io.wexchain.android.dcc.domain

data class CertificationStatus (
        val certificationType: CertificationType,
        val certificationProvider:String,
        val certificationStatus: String
)
