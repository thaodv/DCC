package io.wexchain.dcc.sdk.client.model;

import java.util.List;

public class Web3Request<S> {
	private String jsonrpc = "2.0";
	private String method;
	private List<S> params;
	private long id;

	public String getJsonrpc() {
		return jsonrpc;
	}

	public void setJsonrpc(String jsonrpc) {
		this.jsonrpc = jsonrpc;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public List<S> getParams() {
		return params;
	}

	public void setParams(List<S> params) {
		this.params = params;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

}
