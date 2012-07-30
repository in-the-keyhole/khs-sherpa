package com.khs.sherpa.servlet;

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

import java.lang.annotation.Annotation;

import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

import com.khs.sherpa.SherpaContext;
import com.khs.sherpa.annotation.Param;
import com.khs.sherpa.exception.SherpaRuntimeException;
import com.khs.sherpa.json.service.ActivityService;
import com.khs.sherpa.json.service.SessionTokenService;
import com.khs.sherpa.json.service.UserService;
import com.khs.sherpa.parser.ParamParser;
import com.khs.sherpa.util.UrlUtil;

public class RequestMapper {
	
	private ServletContext context;
	private ServletRequest request;
	private ServletResponse response;
	
	private Object mapAnnotation(String endpoint,String action,Class<?> type, Param param) {
		String name = param.value();
		
		if(name == null || name.length() == 0) {
			throw new SherpaRuntimeException("parameters required");	
		}
		
		//String value = request.getParameter(name);
		String value = UrlUtil.getParamValue((HttpServletRequest) request, name);
		if(value == null) {
			throw new RuntimeException("Endpoint = "+endpoint+" Action = "+action+" - Parameter name ("+name+") not found in request");		
		}
		
		return this.parseObject(type, value, param);
	}
	
	private Object mapNonAnnotation(String endpoint,String action,Class<?> type) {
		if(type.isAssignableFrom(SessionTokenService.class)) {
			return this.getSherpaContext().getSessionTokenService();
		} else if(type.isAssignableFrom(UserService.class)) {
			return this.getSherpaContext().getUserService();
		} else if(type.isAssignableFrom(ActivityService.class)) {
			return this.getSherpaContext().getActivityService();
		} else if(type.isAssignableFrom(ServletRequest.class)) {
			return request;
		} else if(type.isAssignableFrom(ServletResponse.class)) {
			return response;
		} else {
			String body = UrlUtil.getRequestBody((HttpServletRequest) request);
			if(StringUtils.isNotEmpty(body)) {
				return this.parseObject(type, body, null);
			}
		}
		return null;
	}
	
	private Object parseObject(Class<?> clazz, String value, Param annotation) {

		for(ParamParser<?> parser: this.getSherpaContext().getParser()) {
			if(parser.isValid(clazz)) {
				return parser.parse(value, annotation, clazz);
			}
		}
		
		return null;
	}
	
	
	public Object map(String endpoint,String action,Class<?> type, Annotation annotation) {
		// load all params that do not have a annotation?
		if(annotation != null && annotation.annotationType().isAssignableFrom(Param.class)) {
			return mapAnnotation(endpoint, action, type, (Param) annotation);
		} 
		
		return mapNonAnnotation(endpoint, action, type);
	}
	
	public ServletRequest getRequest() {
		return request;
	}

	public void setRequest(ServletRequest request) {
		this.request = request;
	}

	public ServletResponse getResponse() {
		return response;
	}

	public void setResponse(ServletResponse response) {
		this.response = response;
	}
	
	public ServletContext getContext() {
		return context;
	}

	public void setContext(ServletContext context) {
		this.context = context;
	}

	private SherpaContext getSherpaContext() {
		return SherpaContext.getSherpaContext(context);
	}

}
