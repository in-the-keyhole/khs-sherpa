package com.khs.sherpa.processor;

import javax.servlet.http.HttpServletRequest;

public interface RequestProcessor {

	public String getEndpoint(HttpServletRequest request);
	
	public String getAction(HttpServletRequest request);
	
	public String getParmeter(HttpServletRequest request, String value);
}
