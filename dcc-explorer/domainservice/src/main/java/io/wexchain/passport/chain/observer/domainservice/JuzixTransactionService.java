package io.wexchain.passport.chain.observer.domainservice;

import io.wexchain.passport.chain.observer.common.model.ChartPoint;
import io.wexchain.passport.chain.observer.common.model.TransactionInputData;
import io.wexchain.passport.chain.observer.common.request.QueryJuzixTransactionRequest;
import io.wexchain.passport.chain.observer.domain.JuzixTransaction;
import org.springframework.data.domain.Page;

import java.util.List;

public interface JuzixTransactionService {

    JuzixTransaction getJuzixTransactionByHash(String hash);

    JuzixTransaction getJuzixTransactionByHashNullable(String hash);

    Page<JuzixTransaction> queryJuzixTransaction(QueryJuzixTransactionRequest request);

    List<ChartPoint<Integer, Integer>> getTransactionStatistics(int hours);

    long getTotalTransactionNumber();

    TransactionInputData getTransactionInputData(String hash);

}
