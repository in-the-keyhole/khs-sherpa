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

import static com.khs.sherpa.util.Constants.SHERPA_NOT_INITIALIZED;
import static com.khs.sherpa.util.Constants.SHERPA_SERVER;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;

import com.khs.sherpa.annotation.Endpoint;

public class Util {

	public static String msg(String msg) {
		return SHERPA_SERVER+msg;	
	}
	
	public static String errmsg(String msg) {
		return SHERPA_NOT_INITIALIZED+msg;	
	}
	
	public static URL getResource(String resource) {
		ClassLoader cl = Thread.currentThread().getContextClassLoader();
		return cl.getResource(resource);
	}
	
	public static Properties getProperties(String resource) throws IOException {
		return getProperties(Util.getResource(resource));
	}
	
	public static Properties getProperties(URL url) throws IOException {
		Properties properties = new Properties();
		properties.load(url.openStream());
		return properties;
	}
	
	public static <T> T[] append(T[] arr, T element) {
	    final int N = arr.length;
	    arr = Arrays.copyOf(arr, N + 1);
	    arr[N] = element;
	    return arr;
	}
	
	public static String getObjectName(Class<?> type) {
		String name = null;
		if(type.isAnnotationPresent(Endpoint.class)) {
			name = type.getAnnotation(Endpoint.class).value();
			if(StringUtils.isEmpty(name)) {
				name = type.getSimpleName();
			}
		} else if(type.isAnnotationPresent(javax.annotation.ManagedBean.class)) {
			name = type.getAnnotation(javax.annotation.ManagedBean.class).value();
			if(StringUtils.isEmpty(name)) {
				name = type.getSimpleName();
			}
		}  else {
			name = type.getSimpleName();
		}
		return name;
	}
	
}
