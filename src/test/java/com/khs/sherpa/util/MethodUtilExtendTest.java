package com.khs.sherpa.util;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

public class MethodUtilExtendTest {

	class ExtendGenericClass<T> {
		T create(T entity) { return null; }
		T update(Serializable id, T entity) {return null;}
		void delete(Serializable id) { }
		Collection<T> fetch() { return null; }
		T fetchOne(Serializable id) { return null; }
	}
	
	class GenericClass { }
	
	class ConcreteGenericExtendClass extends ExtendGenericClass<GenericClass> {

		public GenericClass create(GenericClass entity) { return null; }
		public GenericClass update(Serializable id, GenericClass entity) { return null; }
		public void delete(Serializable id) { }
		public Collection<GenericClass> fetch() { return null; }
		public GenericClass fetchOne(Serializable id) { return null; }
	}
	
	class ExtendClass {
		Object create(Object entity) { return null; }
		Object update(Serializable id, Object entity) { return null; }
		void delete(Serializable id) { }
		Collection<Object> fetch() { return null; }
		Object fetchOne(Serializable id) { return null; }
	}
	
	class ConcreteExtendClass extends ExtendClass {

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
		List<Method> methods = MethodUtil.getAllMethods(ConcreteExtendClass.class);
		Assert.assertEquals(5, methods.size());
	}
	
	@Test
	public void testGetAllMethodsWithGenericInterface() {
		List<Method> methods = MethodUtil.getAllMethods(ConcreteGenericExtendClass.class);
		Assert.assertEquals(5, methods.size());
	}
	
}
