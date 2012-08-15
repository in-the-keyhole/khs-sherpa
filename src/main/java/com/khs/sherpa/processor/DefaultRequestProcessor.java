package com.khs.sherpa.processor;

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
