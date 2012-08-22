package com.khs.sherpa.context;

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
