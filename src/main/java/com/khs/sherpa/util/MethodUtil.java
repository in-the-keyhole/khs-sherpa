package com.khs.sherpa.util;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import com.khs.sherpa.annotation.Action;

public class MethodUtil {

	public static List<Method> getAllMethods(Class<?> clazz) {
		List<Method> methods = new ArrayList<Method>();
		
		for(Method method: clazz.getMethods()) {
			// skip all method assignable from Object class
			if(method.getDeclaringClass().isAssignableFrom(Object.class)) {
				continue;
			} else if(method.isAnnotationPresent(Action.class) && method.getAnnotation(Action.class).disabled()) {
				continue;
			}
			methods.add(method);
		}
		return methods;
	}
	
	public static String getMethodName(Method method) {
		if(method.isAnnotationPresent(Action.class)) {
			if(method.getAnnotation(Action.class).value().trim().length() > 0) {
				return method.getAnnotation(Action.class).value().trim();
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
	
}
