package io.wexchain.dcc.service.frontend.model.request;

import javax.validation.constraints.Min;

/**
 * 分页参数
 * 
 * @author shenyue
 *
 */
public class AuthenticationPageRequest extends AuthenticationRequest{

	/**
	 * 分页号
	 */
	@Min(0)
	private int number;
	/**
	 * 页尺寸
	 */
	@Min(0)
	private int size;

	public AuthenticationPageRequest() {
	}

	/**
	 *
	 * @param number
	 * @param size
	 */
	public AuthenticationPageRequest(int number, int size) {
		this.size = size;
		this.number = number;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}


}
