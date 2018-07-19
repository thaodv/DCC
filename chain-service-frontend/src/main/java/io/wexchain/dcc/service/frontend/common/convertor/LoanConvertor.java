package io.wexchain.dcc.service.frontend.common.convertor;

import io.wexchain.cryptoasset.loan.api.model.LoanReport;
import io.wexchain.dcc.service.frontend.model.vo.LoanReportVo;

import java.util.Date;

/**
 * Created by yy on 2018/5/22.
 */
public class LoanConvertor {

    public static LoanReportVo convert(LoanReport from){
        if(from == null){
            return null;
        }
        LoanReportVo to = new LoanReportVo();
        to.setBillList(from.getBillList());
        to.setAmount(from.getAmount());
        to.setApplyDate(from.getApplyDate());
        to.setAssetCode(from.getAssetCode());

        if(from.getBorrowDuration() != null){
            to.setBorrowDurationFrom(new Date(from.getBorrowDuration().getStartMillis()));
            to.setBorrowDurationTo(new Date(from.getBorrowDuration().getEndMillis()));
        }
        to.setStatus(from.getStatus());
        to.setChainOrderId(from.getChainOrderId());
        to.setDeliverDate(from.getDeliverDate());
        to.setBorrowerAddress(from.getBorrowerAddress());
        to.setLoanType(from.getLoanType());
        to.setMortgageStatus(from.getMortgageStatus());
        to.setMortgageUnit(from.getMortgageUnit());
        to.setMortgageAmount(from.getMortgageAmount());
        return to;
    }
}

