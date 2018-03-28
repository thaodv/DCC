package io.wexchain.passport.gateway.service.cert;

import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.text.CharacterPredicates;
import org.apache.commons.text.RandomStringGenerator;

import io.wexchain.juzix.contract.cert.CertContent;
import io.wexchain.juzix.contract.cert.CertData;
import io.wexchain.juzix.contract.cert.CertOrder;

public class CertServiceProxyMock implements CertServiceProxy {
	private RandomStringGenerator randomStringGenerator = new RandomStringGenerator.Builder()
			.filteredBy(CharacterPredicates.ARABIC_NUMERALS).build();

	@Override
	public String getAbi() {
		return "{}";
	}

	@Override
	public String getContractAddress() {
		return "abc";
	}

	@Override
	public String apply(String signMessageHex) {
		return randomStringGenerator.generate(10);
	}

	@Override
	public boolean hasReceipt(String transactionHash) {
		return true;
	}

	@Override
	public String pass(String signMessageHex) {
		return randomStringGenerator.generate(10);
	}

	@Override
	public String reject(String signMessageHex) {
		return randomStringGenerator.generate(10);
	}

	@Override
	public String revoke(String signMessageHex) {
		return randomStringGenerator.generate(10);
	}

	@Override
	public CertOrder getOrder(String txHash) {
		return getOrder(1L);
	}

	@Override
	public CertOrder getOrder(Long orderId) {
		CertContent ici = new CertContent();
		ici.setDigest1(RandomUtils.nextBytes(10));
		ici.setDigest2(RandomUtils.nextBytes(10));
		ici.setExpired(1);
		CertOrder icd = new CertOrder();
		icd.setApplicant("aaa");
		icd.setOrderId(orderId);
		icd.setContent(ici);
		return icd;
	}

	@Override
	public CertData getData(String address) {
		CertContent ici = new CertContent();
		ici.setDigest1(RandomUtils.nextBytes(10));
		ici.setDigest2(RandomUtils.nextBytes(10));
		ici.setExpired(1);
		CertData icd = new CertData();
		icd.setDataVersion(1L);
		icd.setContent(ici);
		return icd;
	}

	@Override
	public CertData getDataAt(String address, Long blockNumber) {
		CertContent ici = new CertContent();
		ici.setDigest1(RandomUtils.nextBytes(10));
		ici.setDigest2(RandomUtils.nextBytes(10));
		ici.setExpired(1);
		CertData icd = new CertData();
		icd.setDataVersion(1L);
		icd.setContent(ici);
		return icd;
	}

}
