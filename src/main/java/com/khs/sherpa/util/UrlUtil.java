package com.khs.sherpa.util;

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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

public class UrlUtil {

	// KEEP
	public static String getPath(HttpServletRequest request) {
		String url = request.getRequestURI();
		
		url = StringUtils.removeStart(url, request.getContextPath());
		url = StringUtils.removeStart(url, request.getServletPath());
		
		return url;
	}
	
	// not easily tested
	public static String getRequestBody(HttpServletRequest request) {
		if(request.getMethod().equals("GET") || request.getMethod().equals("DELETE")) {
			return null;
		}
		
		StringBuilder stringBuilder = new StringBuilder();
		BufferedReader bufferedReader = null;
		try {
			InputStream inputStream = request.getInputStream();
			if (inputStream != null) {
				bufferedReader = new BufferedReader(new InputStreamReader(
						inputStream));
				char[] charBuffer = new char[128];
				int bytesRead = -1;
				while ((bytesRead = bufferedReader.read(charBuffer)) > 0) {
					stringBuilder.append(charBuffer, 0, bytesRead);
				}
			} else {
				stringBuilder.append("");
			}
		} catch (IOException ex) {
			// to nothing
		} finally {
			if (bufferedReader != null) {
				try {
					bufferedReader.close();
				} catch (IOException ex) {

				}
			}
		}

		if (StringUtils.isEmpty(stringBuilder)) {
			return stringBuilder.toString();
		}

		if (request.getParameterMap().keySet().size() > 0) {
			return request.getParameterMap().keySet().toArray()[0].toString();
		}

        return null;
	}
//	
//	public static String getParamValue(HttpServletRequest request, String param) {
//		String value = null;
//		String currentUrl = UrlUtil.getPath(request);
//		String path = ReflectionCache.getUrl(currentUrl, request.getMethod());
//		if(StringUtils.isNotEmpty(path)) {
//			if(StringUtils.contains(path, ".")) {
//				path = StringUtils.removeStart(path, request.getMethod() + ".");
//			}
//			value = UrlUtil.getRequestUrlParameter(request, path, param);
//		}
//		
//		if(value == null) {
//			value = UrlUtil.getRequestParameter(request, param);
//		}
//		
//		return value;
//		
//	}
//	
//	protected static String getCacheUrl(String url, String method) {
//		String path = ReflectionCache.getUrl(url, method);
//		if(path == null) {
//			path = ReflectionCache.getUrlMethod(url, method);
//		}
//		return path;
//	}
}
