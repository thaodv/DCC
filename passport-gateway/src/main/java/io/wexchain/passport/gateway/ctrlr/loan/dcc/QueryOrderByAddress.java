package io.wexchain.passport.gateway.ctrlr.loan.dcc;

import javax.validation.constraints.NotBlank;

import com.wexmarket.topia.commons.pagination.PageParam;

public class QueryOrderByAddress extends PageParam {
	@NotBlank
	private String address;

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}
}
