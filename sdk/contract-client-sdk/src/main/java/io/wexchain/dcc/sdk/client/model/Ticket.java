package io.wexchain.dcc.sdk.client.model;

public class Ticket {

	private AccessRestriction accessRestriction;

	private String ticket;

	private byte[] image;

	private String answer;

	private int expiredSeconds;

	public String getTicket() {
		return ticket;
	}

	public void setTicket(String ticket) {
		this.ticket = ticket;
	}

	public byte[] getImage() {
		return image;
	}

	public void setImage(byte[] image) {
		this.image = image;
	}

	public AccessRestriction getAccessRestriction() {
		return accessRestriction;
	}

	public void setAccessRestriction(AccessRestriction accessRestriction) {
		this.accessRestriction = accessRestriction;
	}

	public String getAnswer() {
		return answer;
	}

	public void setAnswer(String answer) {
		this.answer = answer;
	}

	public int getExpiredSeconds() {
		return expiredSeconds;
	}

	public void setExpiredSeconds(int expiredSeconds) {
		this.expiredSeconds = expiredSeconds;
	}

}
