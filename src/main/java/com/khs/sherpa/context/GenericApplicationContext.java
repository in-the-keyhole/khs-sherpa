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

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.ServletContext;

import com.khs.sherpa.context.factory.DefaultManagedBeanFactory;
import com.khs.sherpa.context.factory.ManagedBeanFactory;
import com.khs.sherpa.exception.NoSuchManagedBeanExcpetion;

public class GenericApplicationContext implements ApplicationContext {
	
	public static final String SHERPA_APPLICATION_CONTEXT_ATTRIBUTE = GenericApplicationContext.class.getName() + ".CONTEXT";
	
	private ManagedBeanFactory managedBeanFactory;
	
	private Map<String, Object> attributes = new LinkedHashMap<String, Object>();
	
	public GenericApplicationContext() {
		managedBeanFactory = new DefaultManagedBeanFactory();
	}
	
	public boolean containsManagedBean(Class<?> type) {
		return managedBeanFactory.containsManagedBean(type);
	}

	public boolean containsManagedBean(String name) {
		return managedBeanFactory.containsManagedBean(name);
	}

	public <T> T getManagedBean(Class<T> type) throws NoSuchManagedBeanExcpetion {
		return managedBeanFactory.getManagedBean(type);
	}

	public <T> Collection<T> getManagedBeans(Class<T> type) {
		return managedBeanFactory.getManagedBeans(type);
	}
	
	public Object getManagedBean(String name) throws NoSuchManagedBeanExcpetion {
		return managedBeanFactory.getManagedBean(name);
	}

	public <T> T getManagedBean(String name, Class<T> type) throws NoSuchManagedBeanExcpetion {
		return managedBeanFactory.getManagedBean(name, type);
	}

	public boolean isTypeMatch(String name, Class<?> type) throws NoSuchManagedBeanExcpetion {
		return managedBeanFactory.isTypeMatch(name, type);
	}

	public Class<?> getType(String name) throws NoSuchManagedBeanExcpetion {
		return managedBeanFactory.getType(name);
	}

	public void setAttribute(String key, Object value) {
		attributes.put(key, value);
	}
	
	public Object getAttribute(String key) {
		return attributes.get(key);
	}
	
	public ManagedBeanFactory getManagedBeanFactory() {
		return managedBeanFactory;
	}

	public static ApplicationContext getApplicationContext(ServletContext context) {
		return (ApplicationContext) context.getAttribute(SHERPA_APPLICATION_CONTEXT_ATTRIBUTE);
	}

	public Map<String, Object> getEndpointTypes() {
		return managedBeanFactory.getEndpointTypes();
	}

}
