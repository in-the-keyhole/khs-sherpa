package com.khs.sherpa.context;

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

import com.khs.sherpa.context.factory.ManagedBeanFactory;

public interface ApplicationContext extends ManagedBeanFactory {

	public static final String CONTEXT_PATH = "com.khs.sherpa.SHREPA.CONTEXT";
	public static final String SHERPA_CONTEXT = "com.khs.sherpa.SHREPA.CONTEXT";
	
	public static final String SETTINGS_JSONP = "com.khs.sherpa.SETTGINS.JSONP";
	public static final String SETTINGS_ADMIN_USER = "com.khs.sherpa.SETTGINS.ADMIN_USER";
	public static final String SETTINGS_ENDPOINT_AUTH = "com.khs.sherpa.SETTGINS.ENDPOINT_AUTH";
	
	
	public void setAttribute(String key, Object value);
	
	public Object getAttribute(String key);

	public ManagedBeanFactory getManagedBeanFactory();
	
}
