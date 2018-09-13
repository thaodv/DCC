package io.wexchain.cryptoasset.loan.service.processor.order.auditing.advancer.apply;

public class UploadResult {

	private String paramKey;
	private String token;
	private String wexyunMaterialKey;

	public UploadResult(String paramKey, String wexyunMaterialKey, String token) {
		this.paramKey = paramKey;
		this.token = token;
		this.wexyunMaterialKey = wexyunMaterialKey;
	}

	public String getParamKey() {
		return paramKey;
	}

	public void setParamKey(String paramKey) {
		this.paramKey = paramKey;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getWexyunMaterialKey() {
		return wexyunMaterialKey;
	}

	public void setWexyunMaterialKey(String wexyunMaterialKey) {
		this.wexyunMaterialKey = wexyunMaterialKey;
	}
}
