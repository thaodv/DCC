package io.wexchain.passport.gateway.service.loan.dcc;

import io.wexchain.juzix.contract.loan.dcc.*;
import io.wexchain.passport.gateway.ctrlr.loan.dcc.QueryOrderByIdHash;
import io.wexchain.passport.gateway.service.contract.ContractProxy;
import org.springframework.data.domain.Page;

import java.math.BigInteger;

public interface AgreementServiceProxy extends ContractProxy<AgreementService> {

	Agreement getAgreement(Long agreementId);

	BigInteger getAgreementArrayLengthByIdHashIndex(byte[] idHash);

	Page<BigInteger> getAgreementArrayLengthByIdHashIndex(QueryOrderByIdHash queryOrderParam);

	Page<Agreement> queryAgreementPageByIdHashIndex(QueryOrderByIdHash queryOrderParam);
}
