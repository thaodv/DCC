/*
 * Copyright 2004, 2005, 2006 Acegi Technology Pty Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.wexchain.dcc.service.frontend.ctrlr.security;

import io.wexchain.dcc.service.frontend.ctrlr.AuthenticationController;
import io.wexchain.dcc.service.frontend.model.request.AuthenticationRequest;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class NonceAuthenticationFilter extends
		AbstractAuthenticationProcessingFilter {

	private AuthenticationController authenticationController;

	public static final String NONCE = "nonce";

	public NonceAuthenticationFilter(AuthenticationController authenticationController) {
		super(new AntPathRequestMatcher("/login", "POST"));
		this.authenticationController = authenticationController;
		SimpleUrlAuthenticationFailureHandler failedHandler = (SimpleUrlAuthenticationFailureHandler)getFailureHandler();
		failedHandler.setDefaultFailureUrl("/member/login?validNonceError");
	}

	// ~ Methods
	// ========================================================================================================

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse res=(HttpServletResponse)response;
		String nonce = request.getParameter(NONCE);

		if (!requiresAuthentication(req, res)) {
			chain.doFilter(request, response);
			return;
		}
		try {
			authenticationController.validateNonce(nonce);
		}catch (Exception e){
			unsuccessfulAuthentication((HttpServletRequest)request, (HttpServletResponse)response, new InsufficientAuthenticationException("签名验证不通过"));
			return;
		}


		chain.doFilter(request,response);
	}

	public Authentication attemptAuthentication(HttpServletRequest request,
			HttpServletResponse response) throws AuthenticationException {
		return null;
	}

}
