package io.wexchain.cryptoasset.loan.service.function.wexyun;

import com.weihui.finance.contract.api.response.GeneratPDFFileResponse;
import com.wexyun.open.api.domain.credit2.Credit2ApplyAddResult;
import com.wexyun.open.api.domain.file.DownloadFileInfo;
import com.wexyun.open.api.domain.member.Member;
import com.wexyun.open.api.domain.regular.loan.RegularPrepaymentBill;
import com.wexyun.open.api.domain.regular.loan.RepaymentPlan;
import com.wexyun.open.api.enums.RepaymentType;
import com.wexyun.open.api.response.BaseResponse;
import com.wexyun.open.api.response.QueryResponse4Single;
import io.wexchain.cryptoasset.loan.domain.LoanOrder;
import io.wexchain.cryptoasset.loan.service.function.wexyun.model.Credit2Apply;
import io.wexchain.cryptoasset.loan.service.function.wexyun.model.Credit2ApplyAddRequest;

import java.io.File;
import java.math.BigDecimal;
import java.util.List;

public interface WexyunLoanClient {

    QueryResponse4Single<Credit2Apply> getApplyOrder(String applyId);

    Credit2Apply getApplyOrder2(String applyId);

    QueryResponse4Single<Credit2ApplyAddResult> apply(Credit2ApplyAddRequest credit2ApplyAddRequest);

    RepaymentPlan queryFirstRepaymentPlan(String applyId);

    List<RepaymentPlan> queryRepaymentPlan(String applyId);

    RegularPrepaymentBill queryRegularPrepaymentBill(String applyId);

    void repay(String billId, String memberId, BigDecimal amount, RepaymentType repaymentType);

    GeneratPDFFileResponse generateAgreement(LoanOrder loanOrder, Credit2Apply applyOrder, RepaymentPlan repaymentPlan);

    DownloadFileInfo downloadFile(String filePath);

    Member getMemberInfoById(String memberId);

    String getAddressById(String memberId);

    String uploadImageFile(File file);

    BaseResponse verifyAgreement(String applyId, String loanType);
}