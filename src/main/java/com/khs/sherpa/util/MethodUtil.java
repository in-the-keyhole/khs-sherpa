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

import com.khs.sherpa.annotation.Action;
import com.khs.sherpa.annotation.MethodRequest;
import com.khs.sherpa.exception.SherpaRuntimeException;

public class MethodUtil {

	public static  Method validateHttpMethods(Method[] methods, String httpMethod) {
		Method method = null;
		for(Method m: methods) {
			Method newMethod = null;
			if(m.isAnnotationPresent(Action.class)) {
				if(m.getAnnotation(Action.class).method().length == 0) {
					newMethod = m;
				} else {
					for(MethodRequest r: m.getAnnotation(Action.class).method()) {
						if(r.toString().equalsIgnoreCase(httpMethod)) {
							newMethod = m;
						}
					}
				}
			} else {
				newMethod = m;
			}
			
			if(newMethod == null) {
				continue;
			}
			
			if(method != null) {
				throw new SherpaRuntimeException("To many methods found for method ["+method.getName()+"]");
			}
			
			method = newMethod;
		}
		return method;
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
	
	public static Action getActionAnnotation(Method method) {
		if(method.isAnnotationPresent(Action.class)) {
			return method.getAnnotation(Action.class);
		}	
		return null;
	}
}
