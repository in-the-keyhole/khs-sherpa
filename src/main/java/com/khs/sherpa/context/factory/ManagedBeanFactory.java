package com.khs.sherpa.context.factory;

import java.util.Collection;

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
	public Collection<Class<?>> getEndpointTypes();
}
