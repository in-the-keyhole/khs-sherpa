package com.khs.sherpa.processor;

/*
 * Copyright 2012 the original author or authors.
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

import javax.servlet.http.HttpServletRequest;

public class DefaultRequestProcessor implements RequestProcessor {

	public String getEndpoint(HttpServletRequest request) {
		String endpoint = request.getParameter("endpoint");
		if(endpoint != null && endpoint.equals("null")) {
			endpoint = null;
		}
		return endpoint;
	}

	public String getAction(HttpServletRequest request) {
		return request.getParameter("action");
	}

	public String getParmeter(HttpServletRequest request, String value) {
		return request.getParameter(value);
	}

}
