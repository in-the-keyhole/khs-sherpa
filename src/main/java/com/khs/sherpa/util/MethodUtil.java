package com.khs.sherpa.util;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class MethodUtil {

	public static List<Method> getAllMethods(Class<?> clazz) {
		List<Method> methods = new ArrayList<Method>();
		
		for(Method method: clazz.getMethods()) {
			// skip all method assignable from Object class
			if(method.getDeclaringClass().isAssignableFrom(Object.class)) {
				continue;
			}
			methods.add(method);
		}
		return methods;
	}
	
}
