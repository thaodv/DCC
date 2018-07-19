package io.wexchain.passport.chain.observer.domainservice.impl;

import io.wexchain.passport.chain.observer.common.request.QueryJuzixTokenTransferRequest;
import io.wexchain.passport.chain.observer.common.util.PageUtil;
import io.wexchain.passport.chain.observer.domain.JuzixTokenTransfer;
import io.wexchain.passport.chain.observer.domainservice.JuzixTokenTransferService;
import io.wexchain.passport.chain.observer.repository.JuzixTokenTransferRepository;
import io.wexchain.passport.chain.observer.repository.query.JuzixTokenTransferQueryBuilder;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.FunctionReturnDecoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.request.Transaction;

import java.math.BigInteger;
import java.util.Collections;
import java.util.List;

/**
 * JuzixTokenTransferServiceImpl
 *
 * @author zhengpeng
 */
@Service
public class JuzixTokenTransferServiceImpl implements JuzixTokenTransferService {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private JuzixTokenTransferRepository juzixTokenTransferRepository;

    @Autowired
    private Web3j web3j;

    @Value("${juzix.contract.address.token.dcc}")
    private String dccTokenContractAddress;

    private static final String SENDER_ADDRESS = "0x0000000000000000000000000000000000000000";

    private static final String ERC20_GET_BALANCE_METHOD_NAME = "balanceOf";

    @Override
    public Page<JuzixTokenTransfer> queryJuzixTokenTransfer(QueryJuzixTokenTransferRequest request) {
        PageRequest pageRequest = PageUtil.convert(request, new Sort(Sort.Direction.DESC, "blockTimestamp"));
        return juzixTokenTransferRepository.findAll(JuzixTokenTransferQueryBuilder.query(request),pageRequest);
    }

    @Override
    public Page<JuzixTokenTransfer> queryDccTransfer(QueryJuzixTokenTransferRequest request) {
        request.setContractAddress(dccTokenContractAddress);
        PageRequest pageRequest = PageUtil.convert(request, new Sort(Sort.Direction.DESC, "blockTimestamp"));
        return juzixTokenTransferRepository.findAll(JuzixTokenTransferQueryBuilder.query(request),pageRequest);
    }

    @Override
    public BigInteger getDccTotalValue(String txHash) {
        return getTotalValue(dccTokenContractAddress, txHash);
    }

    @Override
    public BigInteger getDccBalance(String address) {
        return getBalance(dccTokenContractAddress, address);
    }

    @Override
    public BigInteger getTotalValue(String tokenAddress, String txHash) {
        List<String> valueList = juzixTokenTransferRepository.findTotalValue(txHash, tokenAddress);
        BigInteger sum = BigInteger.ZERO;
        if (CollectionUtils.isNotEmpty(valueList)) {
            for (String value : valueList) {
                sum = sum.add(new BigInteger(value));
            }
        }
        return sum;
    }

    @Override
    public BigInteger getBalance(String tokenAddress, String address) {
        try {
            Function function = new Function(ERC20_GET_BALANCE_METHOD_NAME,
                    Collections.singletonList(new Address(address)),
                    Collections.singletonList(new TypeReference<Uint256>() {}));
            String encodedFunction = FunctionEncoder.encode(function);

            org.web3j.protocol.core.methods.response.EthCall response = web3j.ethCall(
                    Transaction.createEthCallTransaction(SENDER_ADDRESS, tokenAddress, encodedFunction),
                    DefaultBlockParameterName.LATEST)
                    .sendAsync().get();

            List<Type> someTypes = FunctionReturnDecoder.decode(
                    response.getValue(), function.getOutputParameters());
            return ((Uint256) someTypes.get(0)).getValue();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Get balance fail, address is " + address, e);
        }
    }
}
