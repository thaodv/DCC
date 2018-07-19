package io.wexchain.dcc.service.frontend.model.vo;

import io.wexchain.dcc.marketing.api.constant.CandyStatus;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigInteger;

/**
 * 糖果
 * 
 * @author shwh1
 *
 */
public class CandyVo {

	/**
	 * id
	 */
	@NotNull
	private Long id;

	/**
	 * 资产代码
	 */
	@NotBlank
	private String assetCode;
	/**
	 * 糖果单位
	 */
	@NotBlank
	private String assetUnit;

	/**
	 * 糖果额度
	 */
	@NotNull
	@DecimalMin(value = "0", inclusive = false)
	private BigInteger amount;

	/**
	 * 状态
	 */
	@NotNull
	private CandyStatus status;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getAssetCode() {
		return assetCode;
	}

	public void setAssetCode(String assetCode) {
		this.assetCode = assetCode;
	}

	public String getAssetUnit() {
		return assetUnit;
	}

	public void setAssetUnit(String assetUnit) {
		this.assetUnit = assetUnit;
	}

	public BigInteger getAmount() {
		return amount;
	}

	public void setAmount(BigInteger amount) {
		this.amount = amount;
	}

	public CandyStatus getStatus() {
		return status;
	}

	public void setStatus(CandyStatus status) {
		this.status = status;
	}

}
