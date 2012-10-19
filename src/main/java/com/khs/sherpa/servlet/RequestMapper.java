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

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;

import com.khs.sherpa.annotation.Param;
import com.khs.sherpa.context.ApplicationContext;
import com.khs.sherpa.context.ApplicationContextAware;
import com.khs.sherpa.exception.SherpaRuntimeException;
import com.khs.sherpa.parser.ParamParser;
import com.khs.sherpa.processor.RequestProcessor;
import com.khs.sherpa.util.UrlUtil;

public class RequestMapper {
	
	private HttpServletRequest request;
	private HttpServletResponse response;
	
	private RequestProcessor requestProcessor;
	
	private ApplicationContext applicationContext;
	
	private Object mapAnnotation(String endpoint,String action,Class<?> type, Param param) {
		String name = param.value();
		
		if(name == null || name.length() == 0) {
			throw new SherpaRuntimeException("parameters required");	
		}
		
		String value = requestProcessor.getParmeter(request, name);
		if(value == null) {
			throw new RuntimeException("Endpoint = "+endpoint+" Action = "+action+" - Parameter name ("+name+") not found in request");		
		}
		
		return this.parseObject(type, value, param);
	}
	
	private Object mapNonAnnotation(String endpoint,String action,Class<?> type) {
		try {
			return applicationContext.getManagedBean(type);
		} catch (Exception e) {
			// DO NOTHING - Not a managed bean;
		}
		
		if(type.isAssignableFrom(HttpServletRequest.class)) {
			return request;
		} else if(type.isAssignableFrom(HttpServletResponse.class)) {
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
		for(ParamParser<?> parser: applicationContext.getManagedBeans(ParamParser.class)) {
			if(parser.isValid(clazz)) {
				if(ApplicationContextAware.class.isAssignableFrom(parser.getClass())) {
					((ApplicationContextAware)parser).setApplicationContext(applicationContext);
				}
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

	public void setRequest(HttpServletRequest request) {
		this.request = request;
	}

	public void setResponse(HttpServletResponse response) {
		this.response = response;
	}

	public void setRequestProcessor(RequestProcessor requestProcessor) {
		this.requestProcessor = requestProcessor;
	}

	public void setApplicationContext(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}
}
