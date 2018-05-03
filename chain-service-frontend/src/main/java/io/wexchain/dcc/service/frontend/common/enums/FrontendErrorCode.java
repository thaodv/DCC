package io.wexchain.dcc.service.frontend.common.enums;

import com.wexmarket.topia.commons.rpc.error.ErrorCode;

public enum FrontendErrorCode implements ErrorCode {

	SYSTEM_ERROR("系统错误", "系统错误"),

	ILLEGAL_ARGUMENT("参数校验未通过", "参数校验未通过"),

	LOGIN_FAILURE("登录失败", "登录失败,用户不存在"),

	FORBIDDEN("拒绝访问", "拒绝访问"),

	ILLEGAL_SIGN("验签未通过", "验签未通过"),

	INVALID_NONCE("无效的noce", "无效的noce"),

	REGISTER_FAIL("注册失败", "注册失败"),

	LOAN_CREDIT_FAIL("进件请求失败", "进件请求失败"),

	QUERY_LOAN_CREDIT_FAIL("查询进件请求失败", "查询进件请求失败"),

	LENDER_NOT_EXIST("借款方不存在", "借款方不存在"),

	CURRENCY_NOT_EXIST("货币代码不存在", "货币代码不存在"),

	LOAN_PRODUCT_NOT_EXIST("借贷产品不存在", "借贷产品不存在"),

	ORDER_NOT_FOUND("订单不存在或未上链", "订单不存在或未上链"),

	INVALID_ORDER_STATUS("订单状态不匹配", "订单状态不匹配"),

	DIGEST_NOT_MATCH("摘要不匹配", "摘要不匹配"),

	BANK_CARD_VALIDATE_FAIL("银行卡校验失败", "银行卡校验失败"),

	BIZ_NOT_SUPPORT("还未支持该业务", "还未支持该业务"),

	GET_RECEIPT_TIMEOUT("获取回执超时", "获取回执超时"),

	UPDATE_ORDER_FAIL("修改订单失败", "修改订单失败"),

	PUK_NOT_FOUNT("公钥未找到", "公钥未找到"),

	GET_PUK_FAIL("获取公钥失败", "获取公钥失败"),

	/** OCR验证失败*/
	OCR_VALIDATE_FAIL("OCR验证失败", "OCR验证失败");



	private String description;

	private String template;

	FrontendErrorCode(String description) {
		this.description = description;
	}

	FrontendErrorCode(String description, String template) {
		this.description = description;
		this.template = template;
	}

	public String getDescription() {
		return description;
	}

	public String getTemplate() {
		return template != null ? template : description;
	}

	public String getCode() {
		return name();
	}
}
