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

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.khs.sherpa.annotation.Action;
import com.khs.sherpa.annotation.Endpoint;
import com.khs.sherpa.annotation.MethodRequest;
import com.khs.sherpa.util.MethodUtil;

public class ReflectionCache {
	
	private ReflectionCache() { }
	
	private static Logger LOG = Logger.getLogger(ReflectionCache.class.getName());	
		
	private static Map<String,Object> typeCache = new HashMap<String, Object>();

	private static Map<String, String> urlCache = new HashMap<String, String>();
	
	public static Map<String,Object> getTypeCache() {
		return typeCache;
	}
	
	// TODO: MD - work on this class. Clean this up
	public static void addTypes(Map<String, Class<?>> endpointClasses) {
		for(Entry<String, Class<?>> entry: endpointClasses.entrySet()) {
			try {
				ReflectionCache.addObject(entry.getKey(), entry.getValue().newInstance());
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
	}

	public static void addObjects(Map<String, Object> endpointClasses) {
		for(Entry<String, Object> entry: endpointClasses.entrySet()) {
			ReflectionCache.addObject(entry.getKey(), entry.getValue());
		}
	}
	
	public static void addObject(String name, Class<?> endpoint) {
		try {
			ReflectionCache.addObject(name, endpoint.newInstance());
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}
	
	public static void addObject(String name, Object endpoint) {
		if(endpoint.getClass().isAnnotationPresent(Endpoint.class)) {
			if(endpoint.getClass().getAnnotation(Endpoint.class).value().length() > 0) {
				name = endpoint.getClass().getAnnotation(Endpoint.class).value();
			}
			LOG.info("Adding class ["+ endpoint.getClass().getCanonicalName()+"] with name ["+name+"]");
			typeCache.put(name, endpoint);
			
			for(Method m: MethodUtil.getAllMethods(endpoint.getClass())) {
				String methodName = name + "." + m.getName();
				Action action = MethodUtil.getActionAnnotation(m);
				if(action != null) {
					if(action.mapping().length > 0) {
						for(String url: action.mapping()) {
							if(action.method().length > 0) {
								for(MethodRequest mr: action.method()) {
									String mu = mr.toString() + "." + url;
									urlCache.put(mu, methodName);
									LOG.info("Adding URL ["+mu+"] to ["+methodName+"]");
									System.out.println("Adding URL ["+mu+"] to ["+methodName+"]");
								}
							} else {
								urlCache.put(url, methodName);
								LOG.info("Adding URL ["+url+"] to ["+methodName+"]");
								System.out.println("Adding URL ["+url+"] to ["+methodName+"]");
							}
						} 
					}
				}
			}
			
		}
	}
	
	public static Object getObject(String className) throws ClassNotFoundException {
		return typeCache.get(className);
	}
	
	public static Class<?> getClass(String className,String pkg) throws ClassNotFoundException {
		String name = pkg+className;
		Class<?> clazz = typeCache.get(name).getClass();
		if (clazz == null) {
			throw new ClassNotFoundException("@Endpoint "+name+" not found initialized");
		}
		return clazz;
	}
	

	public static String getUrl(String url, String method) {
		Pattern pattern = null;
		Matcher matcher = null;
		for(Entry<String, String> entry : ReflectionCache.urlCache.entrySet()) {
			String mapping = entry.getKey();
			pattern = Pattern.compile("\\{\\d?\\w+\\}");
			matcher = pattern.matcher(mapping);
			String matcherText = matcher.replaceAll("[^/]*");
			
			if(Pattern.matches(matcherText, url)) {
				return entry.getKey();
			} else if(Pattern.matches(matcherText, method + "." + url)) {
				return entry.getKey();
			}
		}
		return null;
	}
	
	public static String getUrlMethod(String url, String method) {
		String stringUrl = ReflectionCache.getUrl(url, method);
		if(stringUrl == null) {
			stringUrl = ReflectionCache.getUrl(method + "." + url, method);
		}
		if(url != null) {
			return urlCache.get(stringUrl);
		}
		return null;
	}
}
