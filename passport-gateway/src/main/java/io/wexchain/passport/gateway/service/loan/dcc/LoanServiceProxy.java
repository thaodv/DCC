package io.wexchain.passport.gateway.service.loan.dcc;

import java.math.BigInteger;
import java.util.List;

import org.springframework.data.domain.Page;

import io.wexchain.juzix.contract.loan.dcc.LoanOrder;
import io.wexchain.juzix.contract.loan.dcc.LoanOrderUpdatedEvent;
import io.wexchain.juzix.contract.loan.dcc.LoanService;
import io.wexchain.passport.gateway.ctrlr.loan.dcc.QueryOrderByAddress;
import io.wexchain.passport.gateway.service.contract.ContractProxy;

public interface LoanServiceProxy extends ContractProxy<LoanService> {

	/**
	 *
	 * @param signMessageHex
	 * @return tx hash
	 */
	String apply(String signMessageHex);

	/**
	 *
	 * @param signMessageHex
	 * @return tx hash
	 */
	String cancel(String signMessageHex);

	/**
	 *
	 * @param signMessageHex
	 * @return tx hash
	 */
	String audit(String signMessageHex);

	/**
	 *
	 * @param signMessageHex
	 * @return tx hash
	 */
	String approve(String signMessageHex);

	/**
	 *
	 * @param signMessageHex
	 * @return tx hash
	 */
	String reject(String signMessageHex);

	/**
	 *
	 * @param signMessageHex
	 * @return tx hash
	 */
	String deliver(String signMessageHex);

	/**
	 *
	 * @param signMessageHex
	 * @return tx hash
	 */
	String deliverFailure(String signMessageHex);

	/**
	 *
	 * @param signMessageHex
	 * @return tx hash
	 */
	String receive(String signMessageHex);

	/**
	 *
	 * @param signMessageHex
	 * @return tx hash
	 */
	String confirmRepay(String signMessageHex);

	/**
	 *
	 * @param signMessageHex
	 * @return tx hash
	 */
	String updateRepayDigest(String signMessageHex);

	List<LoanOrder> getOrder(String txHash);

	LoanOrder getOrder(Long orderId);

	List<LoanOrderUpdatedEvent> getOrderUpdatedEvents(String txHash);

	BigInteger getMinFee();

	BigInteger getOrderArrayLengthByBorrowerIndex(String address);

	Page<BigInteger> queryOrderIdPageByBorrowIndex(QueryOrderByAddress queryOrderParam);

	Page<LoanOrder> queryOrderPageByBorrowIndex(QueryOrderByAddress queryOrderParam);

}
