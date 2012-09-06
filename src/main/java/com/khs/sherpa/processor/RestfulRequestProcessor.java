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

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.reflections.ReflectionUtils;
import org.reflections.Reflections;

import com.google.common.base.Predicates;
import com.khs.sherpa.annotation.Action;
import com.khs.sherpa.annotation.Endpoint;
import com.khs.sherpa.context.ApplicationContext;
import com.khs.sherpa.exception.SherpaEndpointNotFoundException;
import com.khs.sherpa.util.MethodUtil;
import com.khs.sherpa.util.SherpaPredicates;
import com.khs.sherpa.util.UrlUtil;

public class RestfulRequestProcessor implements RequestProcessor {

	private ApplicationContext applicationContext;
	private Method method;
	private String path;
	
	public RestfulRequestProcessor() { }
	
	public RestfulRequestProcessor(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}

	public String getEndpoint(HttpServletRequest request) {
		Map<String, Object> map = applicationContext.getEndpointTypes();
		Collection<Method> methods = new HashSet<Method>();
		
		for(Entry<String, Object> entry: map.entrySet()) {
			Collection<Method> m = Reflections.getAllMethods(entry.getValue().getClass(), 
					Predicates.and(
							ReflectionUtils.withAnnotation(Action.class),
							SherpaPredicates.withActionMappingPattern(UrlUtil.getPath(request))
						)
					);
			methods.addAll(m);
		}
		
		method = MethodUtil.validateHttpMethods(methods.toArray(new Method[] {}), request.getMethod());
		if(method != null) {
			Class<?> type = method.getDeclaringClass();
			if(type.isAnnotationPresent(Endpoint.class)) {
				if(StringUtils.isNotEmpty(type.getAnnotation(Endpoint.class).value())) {
					return type.getAnnotation(Endpoint.class).value();
				}
			}
			return type.getSimpleName();
		}
		throw new SherpaEndpointNotFoundException("no endpoint for url [" + UrlUtil.getPath(request) + "]");
	}

	public String getAction(HttpServletRequest request) {
		final Pattern pattern = Pattern.compile("\\{\\d?\\w+\\}");
		
    	Matcher matcher = null;
    	if(method.isAnnotationPresent(Action.class)) {
    		for(String url: method.getAnnotation(Action.class).mapping()) {
    			matcher = pattern.matcher(url);
    			if(Pattern.matches(matcher.replaceAll("[^/]*"), UrlUtil.getPath(request))) {
    				path = url;
    			}
    		}
    	}
		return MethodUtil.getMethodName(method);
	}

	public String getParmeter(HttpServletRequest request, String value) {
		String currentUrl = UrlUtil.getPath(request);
		Pattern pattern = Pattern.compile("(\\{\\w+\\})");
		Matcher matcher = pattern.matcher(path);
		int cnt = 0;
		while(matcher.find()) {
			if(matcher.group().equals("{"+value+"}")) {
				String matcherText = matcher.replaceAll("([^/]*)");
				pattern = Pattern.compile(matcherText);
				matcher = pattern.matcher(currentUrl);
				matcher.find();
				return matcher.group(cnt+1);
			}
			cnt++;
		}
		return null;
	}

	public void setApplicationContext(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}
}
