package com.khs.sherpa.util;

import java.lang.reflect.Method;

import javax.annotation.Nullable;

import com.google.common.base.Predicate;
import com.khs.sherpa.annotation.Action;

public class SherpaPredicates {

    public static Predicate<Method> withAssignableFrom(final Class<?> type) {
        return new Predicate<Method>() {
            public boolean apply(@Nullable Method input) {
            	return input != null && input.getDeclaringClass().isAssignableFrom(type);
            }
        };
    }
 
    public static Predicate<Method> withInterface() {
        return new Predicate<Method>() {
            public boolean apply(@Nullable Method input) {
            	System.out.println(input.getName() + " + " + input.isBridge() + " - " + input.getDeclaringClass());
            	return input != null && input.isBridge();
            }
        };
    }
    
    public static Predicate<Method> withGeneric() {
        return new Predicate<Method>() {
            public boolean apply(@Nullable Method input) {
            	return input != null && input.isBridge();
            }
        };
    }
    
    public static Predicate<Method> withSherpaActionDisabled() {
        return new Predicate<Method>() {
            public boolean apply(@Nullable Method input) {
            	return input != null 
            			&& input.getAnnotation(Action.class) != null
            			&& input.getAnnotation(Action.class).disabled();
            }
        };
    }
	
}
