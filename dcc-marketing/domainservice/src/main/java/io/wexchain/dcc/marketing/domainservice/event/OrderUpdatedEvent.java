package io.wexchain.dcc.marketing.domainservice.event;

public class OrderUpdatedEvent {
	private String owner;
	private String status;

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

}
