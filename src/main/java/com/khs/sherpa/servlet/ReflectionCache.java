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

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Logger;

import com.khs.sherpa.annotation.Endpoint;

public class ReflectionCache {
	
	private ReflectionCache() { }
	
	static Logger LOG = Logger.getLogger(ReflectionCache.class.getName());	
		
	static Map<String,Object> typeCache = new HashMap<String, Object>();

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
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void addObject(String name, Object endpoint) {
		if(endpoint.getClass().isAnnotationPresent(Endpoint.class)) {
			if(endpoint.getClass().getAnnotation(Endpoint.class).value().length() > 0) {
				name = endpoint.getClass().getAnnotation(Endpoint.class).value();
			}
			System.out.println("Adding class ["+ endpoint.getClass().getCanonicalName()+"] with name ["+name+"]");
			typeCache.put(name, endpoint);
		}
	}
	
	public static Object getObject(String className) throws ClassNotFoundException {
		String name = className;
		Object obj = typeCache.get(name);
		
		// allow to return null. will check later in process if null
		
//		if (obj == null) {
//			throw new ClassNotFoundException("@Endpoint "+name+" not found initialized");
//		}
		return obj;
	}
	
	public static Class<?> getClass(String className,String pkg) throws ClassNotFoundException {
		String name = pkg+className;
		Class<?> clazz = typeCache.get(name).getClass();
		if (clazz == null) {
			throw new ClassNotFoundException("@Endpoint "+name+" not found initialized");
		}
		return clazz;
	}
}
