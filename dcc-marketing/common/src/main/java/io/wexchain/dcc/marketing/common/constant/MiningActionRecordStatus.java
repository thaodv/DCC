package io.wexchain.dcc.marketing.common.constant;

/**
 * MiningActionRecordStatus
 *
 * @author fu qiliang
 */
public enum MiningActionRecordStatus {
	REJECTED("已拒绝"),
	ACCEPTED("已接受"),
	BOOKED("已登记");

	private String description;

	private MiningActionRecordStatus(String description) {
		this.description = description;
	}

	public String getDescription() {
		return this.description;
	}
}
