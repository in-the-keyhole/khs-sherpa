package com.khs.sherpa.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

import com.khs.sherpa.servlet.ReflectionCache;

public class UrlUtil {

	public static String getPath(HttpServletRequest request) {
		String url = request.getRequestURI();
		
		url = StringUtils.removeStart(url, request.getContextPath());
		url = StringUtils.removeStart(url, request.getServletPath());
		
		return url;
	}
	
	public static String getParamValue(HttpServletRequest request, String param) {
		String url = ReflectionCache.getUrl(UrlUtil.getPath(request), request.getMethod());
		if(url != null) {
			Pattern pattern = Pattern.compile("\\{"+param+"\\}");
			Matcher matcher = pattern.matcher(url);
			String matcherText = matcher.replaceAll("([^/]*)");
			
			pattern = Pattern.compile(matcherText);
			matcher = pattern.matcher(UrlUtil.getPath(request));
			matcher.find();
			return matcher.group(1);
			
		} else {
			return request.getParameter(param);
		}
		
	}
	
}
