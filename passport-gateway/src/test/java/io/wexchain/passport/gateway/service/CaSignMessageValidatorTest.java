package io.wexchain.passport.gateway.service;

import java.math.BigInteger;

import org.apache.commons.lang3.tuple.Pair;
import org.joda.time.DateTime;
import org.springframework.test.util.ReflectionTestUtils;
import org.testng.annotations.Test;
import org.web3j.crypto.Sign.SignatureData;
import org.web3j.protocol.core.methods.request.RawTransaction;

import com.godmonth.eth.rlp.web3j.SignMessageParser;

import io.wexchain.juzix.contract.commons.ContractParam;
import io.wexchain.juzix.contract.passport.Ca;
import io.wexchain.passport.gateway.service.ca.CaSignMessageValidator;

public class CaSignMessageValidatorTest {

	@Test
	public void validatePut() {
		Pair<RawTransaction, SignatureData> parseFull = SignMessageParser.parseFull(
				"0xf8889e015ffd04eb049249d9788186ecf019b495543cfefc10ba950abcbcd17b4c8504e3b29200840bebc20094e6af6f27f32efd9eb6dc015d9c08f745029e82be80842e8a24001ca035abf009c3a890c99191fb280185f6bc5f867394519d8b4ecef0827ff7eeb509a056998d5e26fa60eb18e8a5b552c42728fd402e4e39da69805322a143e8e38248");
		CaSignMessageValidator messageValidator = new CaSignMessageValidator();
		ContractParam<Ca> caContractParam = new ContractParam<Ca>();
		caContractParam.setContractAddress("0xe6af6f27f32efd9eb6dc015d9c08f745029e82be");
		caContractParam.setMaxGasLimit(new BigInteger("200000000"));
		caContractParam.setMaxGasPrice(new BigInteger("21000000000"));

		ReflectionTestUtils.setField(messageValidator, "contractParam", caContractParam);

		messageValidator.validatePut(parseFull, new DateTime(2017, 11, 16, 16, 0).getMillis());
	}
}
