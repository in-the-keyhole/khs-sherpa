package com.khs.sherpa.context.factory;

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
			return type.newInstance();
		} catch (Exception e) {
			throw new SherpaRuntimeException("Unable to create Managed Bean [" + type + "]");
		}
		
	}
}
