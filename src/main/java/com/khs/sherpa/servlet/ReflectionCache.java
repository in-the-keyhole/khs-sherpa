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
import java.util.logging.Logger;

public class ReflectionCache {
	
	private ReflectionCache() { }
	
static Logger LOG = Logger.getLogger(ReflectionCache.class.getName());	
	
static Map<String,Class<?>> typeCache = new HashMap<String,Class<?>>();
static Map<String,Method> methodCache = new HashMap<String,Method>();

   public static void put(String className,Class<?> clazz) {
	   typeCache.put(className,clazz);
   }
	
	public static Class<?> getClass(String className,String pkg) throws ClassNotFoundException {
		String name = pkg+className;
		Class<?> clazz = typeCache.get(name);
		if (clazz == null) {
			throw new ClassNotFoundException("@Endpoint "+name+" not found initialized");
		}
		return clazz;
	}
		
	public static Method getMethod(Class<?> clazz,String methodName) {
	
		Method method = methodCache.get(methodName);
		if (method == null) {
				for (Method m: clazz.getMethods()) {
					if (methodName.equals(m.getName())) {
						method = m;
					}				
					methodCache.put(methodName,m);
					LOG.info("Sherpa->Method "+methodName+" not in cache, adding...");
				}			
		}
		
		return method;
	}
	
}
