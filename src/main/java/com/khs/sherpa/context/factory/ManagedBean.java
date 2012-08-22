package com.khs.sherpa.context.factory;

import org.apache.commons.lang3.StringUtils;

import com.khs.sherpa.annotation.Endpoint;
import com.khs.sherpa.exception.SherpaRuntimeException;

abstract class ManagedBean {

	private Class<?> type;
	private String name;

	public abstract boolean isSingletone();
	public abstract boolean isPrototype();
	public abstract Object getInstance();
	
	public ManagedBean(Class<?> type) {
		this.type = type;
		if(type.isAnnotationPresent(Endpoint.class)) {
			name = type.getAnnotation(Endpoint.class).value();
			if(StringUtils.isEmpty(name)) {
				name = type.getSimpleName();
			}
		} else if(type.isAnnotationPresent(javax.annotation.ManagedBean.class)) {
			name = type.getAnnotation(javax.annotation.ManagedBean.class).value();
			if(StringUtils.isEmpty(name)) {
				name = type.getName().substring(0,1).toLowerCase() + type.getName().substring(1);
			}
		}  else {
			name = type.getName().substring(0,1).toLowerCase() + type.getName().substring(1);
		}
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
			return type.newInstance();
		} catch (Exception e) {
			throw new SherpaRuntimeException("Unable to create Managed Bean [" + type + "]");
		}
		
	}
}
