package io.wexchain.passport.gateway.ctrlr.commons;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

public class SignMessageRequest extends ChallengeForm {

	@Size(max = 10000)
	@NotEmpty
	private String signMessage;

	public String getSignMessage() {
		return signMessage;
	}

	public void setSignMessage(String signMessage) {
		this.signMessage = signMessage;
	}

}
