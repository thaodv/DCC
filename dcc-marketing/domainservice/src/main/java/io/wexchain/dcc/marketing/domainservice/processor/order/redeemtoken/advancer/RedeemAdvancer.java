package io.wexchain.dcc.marketing.domainservice.processor.order.redeemtoken.advancer;

import com.godmonth.status.advancer.impl.AbstractAdvancer;
import com.godmonth.status.advancer.intf.AdvancedResult;
import com.godmonth.status.transitor.tx.intf.TriggerBehavior;
import io.wexchain.dcc.marketing.api.constant.RedeemTokenStatus;
import io.wexchain.dcc.marketing.domain.RedeemToken;
import io.wexchain.dcc.marketing.domainservice.function.web3.CredentialsSupplier;
import io.wexchain.dcc.marketing.domainservice.processor.order.redeemtoken.RedeemTokenInstruction;
import io.wexchain.dcc.marketing.domainservice.processor.order.redeemtoken.RedeemTokenTrigger;
import io.wexchain.dcc.marketing.ext.integration.web3.TestToken;
import juzix.web3j.NonceRawTransactionManager;
import juzix.web3j.protocol.CustomWeb3j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.core.methods.response.Log;
import org.web3j.protocol.core.methods.response.TransactionReceipt;

import java.math.BigInteger;
import java.util.List;


public class RedeemAdvancer extends AbstractAdvancer<RedeemToken, RedeemTokenInstruction, RedeemTokenTrigger> {
	{
		availableStatus = RedeemTokenStatus.CREATED;
	}

	@Autowired
	private CustomWeb3j web3j;

	@Autowired
	@Qualifier("marketingCredentials")
	private Credentials credentials;

	@Value("${contract.address.dcc}")
	private String dccTokenAddress;

	private static BigInteger GAS_LIMIT = new BigInteger("9999999999999");
	private static BigInteger GAS_PRICE = new BigInteger("2100000000000");
	private static final String TRANSFER_METHOD_HASH = "0xddf252ad1be2c89b69c2b068fc378daa952ba7f163c4a11628f55a4df523b3ef";

	@Override
	public AdvancedResult<RedeemToken, RedeemTokenTrigger> advance(
			RedeemToken redeemToken, RedeemTokenInstruction instruction, Object message) {
		try {
			String activityCode = redeemToken.getScenario().getActivity().getCode();

			TestToken dccToken = TestToken.load(
					"0x625769a40adcd290591cd4a83eae25d374099d4d", web3j,
					new NonceRawTransactionManager(web3j, credentials),
					GAS_PRICE, GAS_LIMIT);

			String supplierAddress = redeemToken.getScenario().getActivity().getSupplierAddress();
			TransactionReceipt transactionReceipt = dccToken.transferFrom(
                    		new Address(supplierAddress),
							new Address(redeemToken.getReceiverAddress()),
							new Uint256(redeemToken.getAmount())).get();
			List<Log> logs = transactionReceipt.getLogs();
			if (CollectionUtils.isNotEmpty(logs)) {
				String topic = logs.get(0).getTopics().get(0);
				if (topic.equals(TRANSFER_METHOD_HASH)) {
					return new AdvancedResult<>(new TriggerBehavior<>(RedeemTokenTrigger.SUCCESS));
				}
			}



			return new AdvancedResult<>(new TriggerBehavior<>(RedeemTokenTrigger.FAIL));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}