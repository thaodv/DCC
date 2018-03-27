package io.wexchain.passport.gateway.ctrlr;

import java.awt.Color;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.patchca.color.SingleColorFactory;
import org.patchca.filter.predefined.CurvesRippleFilterFactory;
import org.patchca.filter.predefined.RippleFilterFactory;
import org.patchca.font.RandomFontFactory;
import org.patchca.service.ConfigurableCaptchaService;
import org.patchca.utils.encoder.EncoderHelper;
import org.patchca.word.RandomWordFactory;
import org.springframework.util.Base64Utils;
import org.testng.annotations.Test;

public class CpTest {
	@Test
	public void f() throws IOException {
		ConfigurableCaptchaService cs = new ConfigurableCaptchaService();
		cs.setColorFactory(new SingleColorFactory(new Color(25, 60, 170)));
		cs.setFilterFactory(new CurvesRippleFilterFactory(cs.getColorFactory()));

		FileOutputStream fos = new FileOutputStream("target/demo.png");
		String challenge = EncoderHelper.getChallangeAndWriteImage(cs, "png", fos);
		// Challenge text needs to be kept in the session for verification
		fos.close();
	}

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
		captchaClient.setColorFactory(new SingleColorFactory(new Color(248, 182, 43)));
		captchaClient.setFilterFactory(new RippleFilterFactory());
	}

	@Test
	public void fff() throws IOException {
		FileOutputStream fos = new FileOutputStream("target/demo.png");

		String token = EncoderHelper.getChallangeAndWriteImage(captchaClient, "png", fos);

	}

	@Test
	public void test() {
		System.out.println(Base64Utils.encodeToString("aa".getBytes()));
	}
}
