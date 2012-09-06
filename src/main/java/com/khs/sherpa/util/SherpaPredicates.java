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
