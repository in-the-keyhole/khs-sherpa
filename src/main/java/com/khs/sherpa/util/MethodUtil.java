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
import java.lang.reflect.Modifier;
import java.util.Collection;

import org.reflections.ReflectionUtils;
import org.reflections.Reflections;

import com.google.common.base.Predicates;
import com.khs.sherpa.annotation.Action;

public class MethodUtil {

	@SuppressWarnings("unchecked")
	public static Collection<Method> getAllMethods(Class<?> clazz) {
		return Reflections.getAllMethods(clazz,
				Predicates.and(
						Predicates.not(SherpaPredicates.withAssignableFrom(Object.class)),
						ReflectionUtils.withModifier(Modifier.PUBLIC),
						Predicates.not(ReflectionUtils.withModifier(Modifier.ABSTRACT)),
						Predicates.not(SherpaPredicates.withGeneric()))
				);
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
	
	public static Method getMethodByName(Collection<Method> methods, String theMethodName) {
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
}
