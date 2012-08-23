package com.khs.sherpa.context;

import com.khs.sherpa.context.factory.ManagedBeanFactory;

public interface ApplicationContext extends ManagedBeanFactory {

	public static final String SETTINGS_JSONP = "com.khs.sherpa.SETTGINS.JSONP";
	public static final String SETTINGS_ADMIN_USER = "com.khs.sherpa.SETTGINS.ADMIN_USER";
	public static final String SETTINGS_ENDPOINT_AUTH = "com.khs.sherpa.SETTGINS.ENDPOINT_AUTH";
	
	public void setAttribute(String key, Object value);
	
	public Object getAttribute(String key);

	public ManagedBeanFactory getManagedBeanFactory();
	
}
