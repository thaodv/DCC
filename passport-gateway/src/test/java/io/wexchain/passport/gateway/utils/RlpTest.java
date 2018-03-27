package io.wexchain.passport.gateway.utils;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.language.bm.Rule.RPattern;
import org.apache.commons.lang3.ArrayUtils;
import org.testng.annotations.Test;

import juzix.web3j.module.rlpdecode.RLP;
import juzix.web3j.module.rlpdecode.RLPElement;
import juzix.web3j.module.rlpdecode.RLPList;

public class RlpTest {
	@Test
	public void testDec() {
		String s = "0xf8ca6685174876e801831e84809463ad21420411703900de0cd11bfeef2f0387171280b864cee1ac8f0000000000000000000000000000000000000000000000000000000000000020000000000000000000000000000000000000000000000000000000000000000668656c6c6f3200000000000000000000000000000000000000000000000000001ba09f48b1db5a9494cdf45bc132277c8022187482e7a32284d42924e26aa52a0bb0a07914b3599dcf84032934ba101a6a8ffa9a2ceae4e3e5869ec94422d8d95869b8";
		byte[] bytes = s.getBytes();

		RLPList rlpElement = RLP.decode2(bytes);
		rlpElement.size();

		for (int i = 0; i < rlpElement.size(); i++) {
			RLPElement rlpElement2 = rlpElement.get(i);
			byte[] rlpData = rlpElement2.getRLPData();
			System.out.println(Hex.encodeHexString(rlpData));
		}

	}
}
