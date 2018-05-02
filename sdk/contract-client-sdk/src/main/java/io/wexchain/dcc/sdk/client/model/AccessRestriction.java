package io.wexchain.dcc.sdk.client.model;

/**
 * 访问限制
 * 
 * @author shwh1
 *
 */
public enum AccessRestriction {

	/**
	 * 需要验证码
	 */
	CAPTCHA,
	/**
	 * 准许
	 */
	GRANTED,
	
	/**
	 * 拒绝
	 */
	REJECTED
}
