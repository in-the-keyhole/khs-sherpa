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

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import com.khs.sherpa.annotation.Action;

public class MethodUtil {

	public static List<Method> getAllMethods(Class<?> clazz) {

		List<Method> methods = new ArrayList<Method>();
		
		for(Method method: clazz.getDeclaredMethods()) {
			if(isMethodValid(method)) {
				methods.add(method);
			}
		}
		return methods;
	}
	
	public static String getMethodName(Method method) {
		Action action = MethodUtil.getActionAnnotation(method);
		if(action != null) {
			if(action.value().trim().length() > 0) {
				return action.value().trim();
			}
		}
		
		return method.getName();
	}
	
	public static Method getMethodByName(List<Method> methods, String theMethodName) {
		for(Method method: methods) {
			if(theMethodName.equals(MethodUtil.getMethodName(method))) {
				return method;
			}
		}
		return null;
	}
	
	public static Method getMethodByName(Class<?> clazz, String theMethodName) {
		return MethodUtil.getMethodByName(MethodUtil.getAllMethods(clazz), theMethodName);
	}
	
	public static Action getActionAnnotation(Method method) {
		if(method.isAnnotationPresent(Action.class)) {
			return method.getAnnotation(Action.class);
		}	
		return null;
	}
	
	private static boolean isMethodValid(Method method) {
		// skip all method assignable from Object class
		if(method.getDeclaringClass().isAssignableFrom(Object.class)) {
			return false;
		} else if(method.isAnnotationPresent(Action.class) && MethodUtil.getActionAnnotation(method).disabled()) {
			return false;
		} else if(method.isBridge()) {
			return false;
		}
		return true;
	}
}
