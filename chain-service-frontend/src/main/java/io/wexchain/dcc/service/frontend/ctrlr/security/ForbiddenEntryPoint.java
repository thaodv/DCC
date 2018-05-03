/*
 * Copyright 2002-2016 the original author or authors.
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

import com.weihui.basic.util.marshaller.json.JsonUtil;
import com.wexmarket.topia.commons.basic.rpc.utils.BaseResponseUtils;
import com.wexmarket.topia.commons.rpc.SystemCode;
import io.wexchain.dcc.service.frontend.common.enums.FrontendErrorCode;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
public class ForbiddenEntryPoint implements AuthenticationEntryPoint {
	private static final Log logger = LogFactory.getLog(ForbiddenEntryPoint.class);

	/**
	 * Always returns a 403 error code to the client.
	 */
	public void commence(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException arg2) throws IOException, ServletException {
		response.setContentType("application/json;charset=utf8");
		response.getWriter().print(
				JsonUtil.toJson(BaseResponseUtils.codeBaseResponse(SystemCode.SUCCESS,
						FrontendErrorCode.FORBIDDEN.name(),
						FrontendErrorCode.FORBIDDEN.getDescription())));
	}
}