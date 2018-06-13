package io.wexchain.passport.gateway.service.loan.dcc;

import io.wexchain.juzix.contract.loan.dcc.*;
import io.wexchain.passport.gateway.ctrlr.loan.dcc.QueryOrderByIdHash;
import io.wexchain.passport.gateway.service.contract.ContractProxyJuzixImpl;
import org.apache.commons.lang3.exception.ContextedRuntimeException;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.generated.Bytes32;
import org.web3j.abi.datatypes.generated.Uint256;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

public class AgreementServiceProxyJuzixImpl extends ContractProxyJuzixImpl<AgreementService2> implements AgreementServiceProxy {

	@Override
	public String getContractAddress() {
		return contract.getContractAddress();
	}

	@Override
	public Agreement2 getAgreement(Long agreementId) {
		@SuppressWarnings("rawtypes")
		List<Type> list = null;
		try {
			list = contract.getAgreement(new Uint256(BigInteger.valueOf(agreementId))).get(readTimeoutSecond, TimeUnit.SECONDS);
		} catch (InterruptedException | ExecutionException | TimeoutException e) {
			throw new ContextedRuntimeException(e);
		}
		return DataParser.parseLoanAgreement2(list);
	}

	@Override
	public BigInteger getAgreementArrayLengthByIdHashIndex(byte[] idHash) {
		Uint256 length = null;
		try {
			length = contract.getAgreementArrayLengthByIdHashIndex(new Bytes32(idHash)).get(readTimeoutSecond,
					TimeUnit.SECONDS);
		} catch (InterruptedException | ExecutionException | TimeoutException e) {
			throw new ContextedRuntimeException(e);
		}
		return length.getValue();
	}

	@Override
	public Page<BigInteger> getAgreementArrayLengthByIdHashIndex(QueryOrderByIdHash queryOrderParam) {
		int length = getAgreementArrayLengthByIdHashIndex(queryOrderParam.getIdHash()).intValueExact();
		List<Uint256> list = null;
		if (length > 0 || queryOrderParam.getSize() == 0) {
			int from = queryOrderParam.getNumber() * queryOrderParam.getSize();
			if (from < length) {
				int to = Math.min(length - 1, from + queryOrderParam.getSize());
				if (from <= to) {
					try {
						list = contract
								.queryOrderIdListByIdHashIndex(new Bytes32(queryOrderParam.getIdHash()),
										new Uint256(BigInteger.valueOf(from)), new Uint256(BigInteger.valueOf(to)))
								.get(readTimeoutSecond, TimeUnit.SECONDS).getValue();
					} catch (InterruptedException | ExecutionException | TimeoutException e) {
						throw new ContextedRuntimeException(e);
					}
				}
			}else {
				list = new ArrayList<>();
			}
		} else {
			list = new ArrayList<>();
		}
		List<BigInteger> indexList = list.stream().map(Uint256::getValue).collect(Collectors.toList());
		return new PageImpl<BigInteger>(indexList,
				PageRequest.of(queryOrderParam.getNumber(), queryOrderParam.getSize()), length);
	}

	@Override
	public Page<Agreement2> queryAgreementPageByIdHashIndex(QueryOrderByIdHash queryOrderParam) {
		Page<BigInteger> page = getAgreementArrayLengthByIdHashIndex(queryOrderParam);
		List<Agreement2> collect = page.getContent().stream().map(BigInteger::longValueExact).map(this::getAgreement)
				.collect(Collectors.toList());
		return new PageImpl<>(collect, page.getPageable(), page.getTotalElements());
	}
}
