package io.wexchain.dcc.service.frontend.ctrlr.security;

import org.springframework.session.Session;
import org.springframework.session.web.http.HttpSessionStrategy;
import org.springframework.session.web.http.MultiHttpSessionStrategy;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class MultiHttpSessionStrategyAdapter implements MultiHttpSessionStrategy {
	private HttpSessionStrategy delegate;

	/**
	 * Create a new {@link MultiHttpSessionStrategyAdapter} instance.
	 * 
	 * @param delegate
	 *            the delegate HTTP session strategy
	 */
	MultiHttpSessionStrategyAdapter(HttpSessionStrategy delegate) {
		this.delegate = delegate;
	}

	public String getRequestedSessionId(HttpServletRequest request) {
		return this.delegate.getRequestedSessionId(request);
	}

	public void onNewSession(Session session, HttpServletRequest request, HttpServletResponse response) {
		this.delegate.onNewSession(session, request, response);
	}

	public void onInvalidateSession(HttpServletRequest request, HttpServletResponse response) {
		this.delegate.onInvalidateSession(request, response);
	}

	public HttpServletRequest wrapRequest(HttpServletRequest request, HttpServletResponse response) {
		return request;
	}

	public HttpServletResponse wrapResponse(HttpServletRequest request, HttpServletResponse response) {
		return response;
	}
}
