package io.wexchain.dcc.service.frontend.model;


public class Lender {

	private String code;

	private String name;

	private String logoUrl;

	private boolean defaultConfig;

	public boolean isDefaultConfig() {
		return defaultConfig;
	}

	public void setDefaultConfig(boolean defaultConfig) {
		this.defaultConfig = defaultConfig;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getLogoUrl() {
		return logoUrl;
	}

	public void setLogoUrl(String logoUrl) {
		this.logoUrl = logoUrl;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
