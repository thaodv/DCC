package io.wexchain.passport.gateway.service.risk;

import org.springframework.web.context.request.WebRequest;

public class RiskMeasurerStaticImpl implements RiskMeasurer {

	private  AccessRestriction  accessRestriction;

	public void setAccessRestriction(AccessRestriction accessRestriction) {
		this.accessRestriction = accessRestriction;
	}

	public AccessRestriction risk(WebRequest webRequest) {
		return accessRestriction;
	}

}
