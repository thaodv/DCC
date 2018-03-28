package io.wexchain.passport.gateway.service.risk;

import org.springframework.web.context.request.WebRequest;

public interface RiskMeasurer {
	public AccessRestriction risk(WebRequest webRequest);
}
