package io.wexchain.dccchainservice.domain

enum class LoanType (
        val description:String
){
    LOAN("普通借贷"),//已失效
    MORTGAGE("抵押借贷"),//已创建

}