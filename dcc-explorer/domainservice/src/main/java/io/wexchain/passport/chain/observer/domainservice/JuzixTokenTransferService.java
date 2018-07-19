package io.wexchain.passport.chain.observer.domainservice;

import io.wexchain.passport.chain.observer.common.request.QueryJuzixTokenTransferRequest;
import io.wexchain.passport.chain.observer.domain.JuzixTokenTransfer;
import org.springframework.data.domain.Page;

import java.math.BigInteger;

public interface JuzixTokenTransferService {

    Page<JuzixTokenTransfer> queryJuzixTokenTransfer(QueryJuzixTokenTransferRequest request);

    Page<JuzixTokenTransfer> queryDccTransfer(QueryJuzixTokenTransferRequest request);

    BigInteger getDccTotalValue(String txHash);

    BigInteger getDccBalance(String address);

    BigInteger getTotalValue(String tokenAddress, String txHash);

    BigInteger getBalance(String tokenAddress, String address);

}
