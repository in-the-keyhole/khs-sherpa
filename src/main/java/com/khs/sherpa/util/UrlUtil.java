package com.khs.sherpa.util;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

public class UrlUtil {

	public static String getPath(HttpServletRequest request) {
		String url = request.getRequestURI();
		
		url = StringUtils.removeStart(url, request.getContextPath());
		url = StringUtils.removeStart(url, request.getServletPath());
		
		return url;
	}
	
}
