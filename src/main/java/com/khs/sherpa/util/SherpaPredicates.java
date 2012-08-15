package com.khs.sherpa.util;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Nullable;

import com.google.common.base.Predicate;
import com.khs.sherpa.annotation.Action;

public class SherpaPredicates {

	public static <T extends AnnotatedElement> Predicate<T> withActionAnnotationValueEqualTo(final Object value) {
        return new Predicate<T>() {
            public boolean apply(@Nullable T input) {
                return input != null && input.getAnnotation(Action.class).value().equals(value);
            }
        };
    }
	
	public static <T extends AnnotatedElement> Predicate<T> withActionMappingPattern(final String path) {
		final Pattern pattern = Pattern.compile("\\{\\d?\\w+\\}");
		
        return new Predicate<T>() {
            public boolean apply(@Nullable T input) {
            	Matcher matcher = null;
            	if(input != null && input.isAnnotationPresent(Action.class)) {
            		for(String url: input.getAnnotation(Action.class).mapping()) {
            			matcher = pattern.matcher(url);
            			return Pattern.matches(matcher.replaceAll("[^/]*"), path);
            		}
            	}
            	return false;
            }
        };
    }
	
    
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
