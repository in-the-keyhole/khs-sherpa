package com.khs.sherpa.servlet.request;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public interface SherpaRequest {

	public Object getAttribute(String name);
	public void setAttribute(String name, Object object);
	
	public void doService(HttpServletRequest request, HttpServletResponse response);
}
