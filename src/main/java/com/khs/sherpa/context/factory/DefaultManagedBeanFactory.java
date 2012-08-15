package com.khs.sherpa.context.factory;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

import org.reflections.Reflections;

import com.khs.sherpa.annotation.Endpoint;
import com.khs.sherpa.exception.NoSuchManagedBeanExcpetion;

public class DefaultManagedBeanFactory implements ManagedBeanFactory {

	// @Endpoint & @ManagedBean
	private Set<ManagedBean> managedBeans = new LinkedHashSet<ManagedBean>();
	
	public boolean containsManagedBean(Class<?> type) {
		for(ManagedBean bean: managedBeans) {
			if(type.isAssignableFrom(bean.getType())) {
				return true;
			}
		}
		return false;
	}

	public boolean containsManagedBean(String name) {
		for(ManagedBean bean: managedBeans) {
			if(bean.getName().equals(name)) {
				return true;
			}
		}
		return false;
	}

	public <T> T getManagedBean(Class<T> type) throws NoSuchManagedBeanExcpetion {
		for(ManagedBean bean: managedBeans) {
			if(type.isAssignableFrom(bean.getType())) {
				return (T) bean.getInstance();
			}
		}
		throw new NoSuchManagedBeanExcpetion(type.getName());
	}

	public Object getManagedBean(String name) throws NoSuchManagedBeanExcpetion {
		for(ManagedBean bean: managedBeans) {
			if(bean.getName().equals(name)) {
				return bean.getInstance();
			}
		}
		throw new NoSuchManagedBeanExcpetion(name);
	}

	public <T> T getManagedBean(String name, Class<T> type) throws NoSuchManagedBeanExcpetion {
		return (T) this.getManagedBean(name);
	}

	public boolean isTypeMatch(String name, Class<?> type) throws NoSuchManagedBeanExcpetion {
		return this.getType(name).isAssignableFrom(type);
	}

	public Class<?> getType(String name) throws NoSuchManagedBeanExcpetion {
		return this.getManagedBean(name).getClass();
	}

	public int managedBeansCount() {
		return managedBeans.size();
	}
	
	public void loadManagedBeans(String path) {
		Reflections reflections = new Reflections(path);
//		this.loadManagedBeans(reflections.getTypesAnnotatedWith(javax.annotation.ManagedBean.class));
		this.loadManagedBeans(reflections.getTypesAnnotatedWith(com.khs.sherpa.annotation.Endpoint.class));
	}
	
	public void loadManagedBeans(Set<Class<?>> types) {
		for(Class<?> type: types) {
			this.loadManagedBean(type);
		}
	}
	
	public void loadManagedBean(Class<?> type) {
		ManagedBean bean = new ManagedSingletonBean(type);
		managedBeans.add(bean);
	}

	public Collection<Class<?>> getEndpointTypes() {
		Set<Class<?>> clazzs = new HashSet<Class<?>>();
		for(ManagedBean bean: managedBeans) {
			if(bean.getType().isAnnotationPresent(Endpoint.class)) {
				clazzs.add(bean.getType());
			}
		}
		return clazzs;
	}
}
