package io.wexchain.passport.gateway.ctrlr.captcha;

import java.io.IOException;

import org.apache.commons.lang3.tuple.Pair;

public interface CaptchaAdapter {
	Pair<String, byte[]> createImg() throws IOException;
}
