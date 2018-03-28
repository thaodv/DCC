package io.wexchain.passport.gateway.ctrlr.captcha;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;
import org.patchca.color.RandomColorFactory;
import org.patchca.filter.predefined.RippleFilterFactory;
import org.patchca.font.RandomFontFactory;
import org.patchca.service.ConfigurableCaptchaService;
import org.patchca.utils.encoder.EncoderHelper;
import org.patchca.word.RandomWordFactory;

public class CaptchaAdapterImpl implements CaptchaAdapter {

	private static final ConfigurableCaptchaService captchaClient = new ConfigurableCaptchaService();

	static {
		RandomWordFactory wordFactory = new RandomWordFactory();
		wordFactory.setCharacters("23456789ABCDEFGHJKMNPQRSTVUWXYZ");
		wordFactory.setMaxLength(4);
		wordFactory.setMinLength(4);
		RandomFontFactory fontFactory = new RandomFontFactory();
		List<String> families = Arrays.asList("VERDANA");// 指定的字符集，可指定多个，但要求设备上有安装
		fontFactory.setFamilies(families);
		fontFactory.setRandomStyle(false);// 只是用普通字体，不使用黑体
		fontFactory.setMaxSize(30);// 字体大小
		fontFactory.setMinSize(30);
		captchaClient.setHeight(42);// 图片高
		captchaClient.setWidth(95);// 图片宽
		captchaClient.setWordFactory(wordFactory);
		captchaClient.setFontFactory(fontFactory);
		captchaClient.setColorFactory(new RandomColorFactory());
		captchaClient.setFilterFactory(new RippleFilterFactory());
	}

	@Override
	public Pair<String, byte[]> createImg() throws IOException {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		String token = EncoderHelper.getChallangeAndWriteImage(captchaClient, "png", bos);
		return Pair.of(token, bos.toByteArray());
	}

}
