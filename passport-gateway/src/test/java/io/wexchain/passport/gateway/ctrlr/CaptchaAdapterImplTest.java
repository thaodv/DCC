package io.wexchain.passport.gateway.ctrlr;

import java.io.IOException;

import org.apache.commons.lang3.tuple.Pair;
import org.testng.annotations.Test;

import io.wexchain.passport.gateway.ctrlr.captcha.CaptchaAdapterImpl;

public class CaptchaAdapterImplTest {

	@Test
	public void create() throws IOException {
		CaptchaAdapterImpl adapterImpl = new CaptchaAdapterImpl();
		Pair<String, byte[]> createImg = adapterImpl.createImg();
		System.out.println(createImg.getLeft());
		System.out.println(createImg.getRight().length);
	}

}
