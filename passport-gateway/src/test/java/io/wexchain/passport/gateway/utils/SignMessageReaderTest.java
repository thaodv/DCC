package io.wexchain.passport.gateway.utils;

import java.math.BigInteger;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.test.util.ReflectionTestUtils;
import org.testng.annotations.Test;
import org.web3j.crypto.Sign.SignatureData;
import org.web3j.protocol.core.methods.request.RawTransaction;

import com.godmonth.eth.rlp.web3j.SignMessageParser;

import io.wexchain.juzix.contract.commons.ContractParam;
import io.wexchain.juzix.contract.passport.Ca;
import io.wexchain.passport.gateway.service.ca.CaSignMessageValidator;

public class SignMessageReaderTest {
	@Test
	public void test() {
		String s = "0xf9026a9e015fc91bdf053434363039323937323536333139353033353838363530318504e3b29200840bebc20094e6af6f27f32efd9eb6dc015d9c08f745029e82be80b901e4cee1ac8f000000000000000000000000000000000000000000000000000000000000002000000000000000000000000000000000000000000000000000000000000001900a4d494942496a414e42676b71686b6947397730424151454641414f43415138414d49494243674b4341514541774a555a4245766153484772417650726b4a46500a5174432f49304847737852524254756d38356f746d7062334446717a415065613230334479417341616b76366e70584b41384f366150346c783846306749554b0a774f643345505a765834347a56534464415977676632537a4e65554c7a56386c6f616b417a5a355233695670596e44687165485963456659365149595a6c65640a74764a5441475a614e7861704e4d4f4d7a6667533350745a304b62386275744f4d425142453343494f6b65514a55566d616d56765762384b6b37516545784f710a6e592b4732584c377954455a324273375a4f657a3659726f69466b704a4568466a5043787062307375507753586c783859344d754d663373764f51427946304f0a77714b615839327068476f662b755031746d706252734179706d4b6d625531566d47414b6b44426c742b715a2f336939417a747749553256395969334a5074770a51514944415141420a0000000000000000000000000000000023a0fa894bd8f21784bb30a87c0fd28c8652e193efcef7d31d2606472332ac9d1063a04d1bca7b4bd2a2004df7d9425bd7dbeb782c6338972340ceccbb1eb1a44a1d55";
		Pair<RawTransaction, SignatureData> parseFull = SignMessageParser.parseFull(s);
		System.out.println(ToStringBuilder.reflectionToString(parseFull.getLeft(), ToStringStyle.MULTI_LINE_STYLE));
		System.out.println(ToStringBuilder.reflectionToString(parseFull.getRight(), ToStringStyle.MULTI_LINE_STYLE));
		ContractParam<Ca> ccp = new ContractParam<Ca>();
		ccp.setContractAddress("0xe6af6f27f32efd9eb6dc015d9c08f745029e82be");
		ccp.setMaxGasLimit(new BigInteger("200000000"));
		ccp.setMaxGasPrice(new BigInteger("21000000000"));
		CaSignMessageValidator smv = new CaSignMessageValidator();
		ReflectionTestUtils.setField(smv, "contractParam", ccp);

		smv.validatePut(parseFull, System.currentTimeMillis());
	}

	@Test
	public void test2() {
		String s = "0xf901499e01627aa76c67a32371d9e3f83cd0d6732f880e997e3ace3c0098f30b798f8504e3b29200840bebc20094a1a9e6e9e5f6aa06babf940a327452023379e1cc80b8c44ffbdca0000000000000000000000000000000000000000000000000000000000000004000000000000000000000000000000000000000000000000000000000000000800000000000000000000000000000000000000000000000000000000000000020e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b8550000000000000000000000000000000000000000000000000000000000000020242d51624da16f30078d44be5275da59dd72ec14bcf9d62c3dd00e70e12b90f21ca05d667e435bfc0f3df31434d11d436baf4fe3f972409ecebe3195b01cff004a29a038136ee325d215a8eb4e547c08c9987ee0a6f4b1aa5715f0f05fca931170053a";
		Pair<RawTransaction, SignatureData> parseFull = SignMessageParser.parseFull(s);
		System.out.println(ToStringBuilder.reflectionToString(parseFull.getLeft(), ToStringStyle.MULTI_LINE_STYLE));
		System.out.println(ToStringBuilder.reflectionToString(parseFull.getRight(), ToStringStyle.MULTI_LINE_STYLE));
		ContractParam<Ca> ccp = new ContractParam<Ca>();
		ccp.setContractAddress("0x511587a9fc673f761efba656afd4aa56dd2297f4");
		ccp.setMaxGasLimit(new BigInteger("200000000"));
		ccp.setMaxGasPrice(new BigInteger("21000000000"));
		CaSignMessageValidator smv = new CaSignMessageValidator();
		ReflectionTestUtils.setField(smv, "contractParam", ccp);

		smv.validatePut(parseFull, System.currentTimeMillis());
	}
}
