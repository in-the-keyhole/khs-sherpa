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

import java.util.Collection;
import java.util.Map;

import com.khs.sherpa.exception.NoSuchManagedBeanExcpetion;

public interface ManagedBeanFactory {

	/**
	 * @param type
	 * @return 
	 */
	public boolean containsManagedBean(Class<?> type);
	
	/**
	 * @param name
	 * @return  
	 */
	public boolean containsManagedBean(String name);
	
	/**
	 * @param type
	 * @return 
	 * @throws NoSuchManagedBeanExcpetion
	 */
	public <T> T getManagedBean(Class<T> type) throws NoSuchManagedBeanExcpetion;
	
	/**
	 * @param type
	 * @return
	 */
	public <T> Collection<T> getManagedBeans(Class<T> type);
	
	/**
	 * @param name
	 * @return
	 * @throws NoSuchManagedBeanExcpetion
	 */
	public Object getManagedBean(String name) throws NoSuchManagedBeanExcpetion;
	
	/**
	 * @param name
	 * @param type
	 * @return
	 * @throws NoSuchManagedBeanExcpetion 
	 */
	public <T> T getManagedBean(String name, Class<T> type) throws NoSuchManagedBeanExcpetion;
	
	/**
	 * @param name
	 * @param type
	 * @return
	 * @throws NoSuchManagedBeanExcpetion
	 */
	public boolean isTypeMatch(String name, Class<?> type) throws NoSuchManagedBeanExcpetion;
	
	/**
	 * @param name
	 * @return
	 * @throws NoSuchManagedBeanExcpetion
	 */
	public Class<?> getType(String name) throws NoSuchManagedBeanExcpetion;
	
	/**
	 * @return
	 */
	public Map<String, Object> getEndpointTypes();
}
