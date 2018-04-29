package io.wexchain.dcc.sdk.client.nonce;

import java.math.BigInteger;
import java.util.UUID;

import org.apache.commons.lang3.ArrayUtils;
import org.bouncycastle.jcajce.provider.digest.SHA3;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.primitives.Longs;

public class NonceCreator {

	private static final Logger logger = LoggerFactory.getLogger(NonceCreator.class);

	public static BigInteger create(String address, Long timestamp) {
		Long currentTimeMillis = System.currentTimeMillis();
		byte[] byteArray = Longs.toByteArray(currentTimeMillis);
		if (byteArray.length < 8) {
			byteArray = ArrayUtils.addAll(new byte[32 - byteArray.length], byteArray);
		}
		logger.trace("{}", byteArray);
		String string = UUID.randomUUID().toString();
		string += address;
		SHA3.DigestSHA3 md = new SHA3.DigestSHA3(256);
		md.update(string.getBytes());
		byte[] digest = md.digest();
		digest = ArrayUtils.subarray(digest, 0, 24);

		byte[] nonce = ArrayUtils.addAll(byteArray, digest);
		return new BigInteger(nonce);
	}

	public static BigInteger create(String address) {
		return create(address, System.currentTimeMillis());
	}
}
