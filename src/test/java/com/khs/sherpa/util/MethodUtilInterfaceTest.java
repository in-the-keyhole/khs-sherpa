package com.khs.sherpa.util;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

public class MethodUtilInterfaceTest {
	
	interface InterfaceGenericClass<T> {
		T create(T entity);
		T update(Serializable id, T entity);
		void delete(Serializable id);
		Collection<T> fetch();
		T fetchOne(Serializable id);
	}
	
	class GenericClass { }
	
	class ConcreteGenericInterfaceClass implements InterfaceGenericClass<GenericClass> {

		public GenericClass create(GenericClass entity) { return null; }
		public GenericClass update(Serializable id, GenericClass entity) { return null; }
		public void delete(Serializable id) { }
		public Collection<GenericClass> fetch() { return null; }
		public GenericClass fetchOne(Serializable id) { return null; }
	}
	
	interface InterfaceClass {
		Object create(Object entity);
		Object update(Serializable id, Object entity);
		void delete(Serializable id);
		Collection<Object> fetch();
		Object fetchOne(Serializable id);
	}
	
	class ConcreteInterfaceClass implements InterfaceClass {

		public Object create(Object entity) { return null; }
		public Object update(Serializable id, Object entity) { return null; }
		public void delete(Serializable id) { }
		public Collection<Object> fetch() { return null; }
		public Object fetchOne(Serializable id) { return null; }
	}
	
	class ConcreteClass {

		public GenericClass create(GenericClass entity) { return null; }
		public GenericClass update(Serializable id, GenericClass entity) { return null; }
		public void delete(Serializable id) { }
		public Collection<GenericClass> fetch() { return null; }
		public GenericClass fetchOne(Serializable id) { return null; }
	}
	
	@Test
	public void testGetAllMethodsNoInterface() {
		List<Method> methods = MethodUtil.getAllMethods(ConcreteClass.class);
		Assert.assertEquals(5, methods.size());
	}
	
	@Test
	public void testGetAllMethodsWithInterface() {
		List<Method> methods = MethodUtil.getAllMethods(ConcreteInterfaceClass.class);
		Assert.assertEquals(5, methods.size());
	}
	
	@Test
	public void testGetAllMethodsWithGenericInterface() {
		List<Method> methods = MethodUtil.getAllMethods(ConcreteGenericInterfaceClass.class);
		Assert.assertEquals(5, methods.size());
	}
	
}
