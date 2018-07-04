package io.wexchain.dcc.marketing.common.constant;

/**
 * TimeUnit
 *
 * @author zheng peng
 */
public enum TimeUnit {

	MINUTES("分钟"),
	HOURS("小时"),
	DAYS("天");

	private String description;

	TimeUnit(String description) {
		this.description = description;
	}

	public String getDescription() {
		return this.description;
	}
}
