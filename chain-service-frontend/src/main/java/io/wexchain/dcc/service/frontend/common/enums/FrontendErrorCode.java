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

	LOAN_APPLY_FAIL("进件请求失败", "进件请求失败: %s"),

	GET_LOAN_ORDER_DETAIL_FAIL("查询订单详情失败", "查询订单详情失败: %s"),

	QUERY_LOAN_ORDER_BILL_FAIL("查询还币账单失败", "查询还币账单失败: %s"),

	CONFIRM_REPAYMENT_FAIL("确认还币失败", "确认还币失败: %s"),

	ADVANCE_FAIL("推进失败", "推进失败: %s"),

	QUERY_LOAN_ORDER_FAIL("查询订单列表失败", "查询订单列表失败: %s"),

	CANCEL_LOAN_ORDER_FAIL("取消订单失败", "取消订单失败: %s"),

	LENDER_NOT_EXIST("借款方不存在", "借款方不存在"),

	CURRENCY_NOT_EXIST("货币代码不存在", "货币代码不存在"),

	LOAN_PRODUCT_NOT_EXIST("借贷产品不存在", "借贷产品不存在"),

	PUK_NOT_FOUNT("公钥未找到", "公钥未找到"),

	GET_PUK_FAIL("获取公钥失败", "获取公钥失败"),

	DOWNLOAD_FAIL("下载文件错误", "下载文件错误"),

	FILE_NOT_EXIST("文件不存在", "文件不存在"),
	MAX_UPLOAD_SIZE_EXCEEDED("上传文件不超过3MB"),

	UPLOAD_METHOD_NOT_SUPPORT("文件上传方式不支持", "文件上传方式不支持"),

	QUERY_REWARD_LOG_FAIL("查询奖励记录失败", "查询奖励记录失败：%s"),

	GET_TOTAL_REWARD_AMOUNT_FAIL("查询用户奖励金额", "查询用户奖励金额：%s"),

	QUERY_ECO_REWARD_RULE_FAIL("查询奖励规则失败", "查询奖励规则失败"),

	GET_ECO_REWARD_STATISTICS_INFO("查询生态奖励统计失败", "查询生态奖励统计失败"),

	CERT_ID_EXCEED("身份证认证未通过或已过期", "身份证认证未通过或未通过或已过期"),
	BANK_CARD_EXCEED("银行卡认未通过或证已过期", "银行卡认证未通过或已过期"),
	COMMUNICATION_LOG_EXCEED("手机运营商认证未通过或已过期", "手机运营商认证未通过或已过期"),
	VERSION_TOO_OLD("因借币升级改造，请您下载更新并重新进行认证", "因借币升级改造，请您下载更新并重新进行认证");

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
