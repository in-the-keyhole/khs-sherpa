package com.khs.sherpa.context.factory;

import com.khs.sherpa.exception.SherpaRuntimeException;
import com.khs.sherpa.util.Util;

abstract class ManagedBean {

	private Class<?> type;
	private String name;

	public abstract boolean isSingletone();
	public abstract boolean isPrototype();
	public abstract Object getInstance();
	
	public ManagedBean(Class<?> type) {
		this.type = type;
		this.name = Util.getObjectName(type);
	}
	
	public Class<?> getType() {
		return type;
	}
	
	public void setType(Class<?> type) {
		this.type = type;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	protected Object createInstance() {
		try {
			return type.getDeclaringClass().newInstance();
		} catch (Exception e) {
			throw new SherpaRuntimeException("Unable to create Managed Bean [" + type + "]");
		}
		
	}
}
