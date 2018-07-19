package io.wexchain.dcc.service.frontend.model.vo;

import java.math.BigDecimal;
import java.util.Date;

/**
 * MiningRewardRecordVo
 *
 */
public class MiningRewardRecordVo {

	private String address;

	private BigDecimal score;

	private String bonusName;

	private Date createdTime;

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public BigDecimal getScore() {
		return score;
	}

	public void setScore(BigDecimal score) {
		this.score = score;
	}

	public String getBonusName() {
		return bonusName;
	}

	public void setBonusName(String bonusName) {
		this.bonusName = bonusName;
	}

	public Date getCreatedTime() {
		return createdTime;
	}

	public void setCreatedTime(Date createdTime) {
		this.createdTime = createdTime;
	}
}
