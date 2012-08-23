package com.khs.sherpa.context.factory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletContext;

import org.reflections.Reflections;

import com.khs.sherpa.SherpaSettings;
import com.khs.sherpa.annotation.Endpoint;
import com.khs.sherpa.exception.NoSuchManagedBeanExcpetion;
import com.khs.sherpa.exception.SherpaRuntimeException;
import com.khs.sherpa.json.service.JsonProvider;
import com.khs.sherpa.parser.BooleanParamParser;
import com.khs.sherpa.parser.CalendarParamParser;
import com.khs.sherpa.parser.DateParamParser;
import com.khs.sherpa.parser.DoubleParamPaser;
import com.khs.sherpa.parser.FloatParamParser;
import com.khs.sherpa.parser.IntegerParamParser;
import com.khs.sherpa.parser.JsonParamParser;
import com.khs.sherpa.parser.StringParamParser;

public class DefaultManagedBeanFactory implements ManagedBeanFactory, InitManageBeanFactory {

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

	public <T> Collection<T> getManagedBeans(Class<T> type) {
		List<T> list = new ArrayList<T>();
		for(ManagedBean bean: managedBeans) {
			if(type.isAssignableFrom(bean.getType())) {
				list.add((T)bean.getInstance());
			}
		}
		return list;
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

	public Map<String, Object> getEndpointTypes() {
		Map<String, Object> map = new HashMap<String, Object>();
		for(ManagedBean bean: managedBeans) {
			if(bean.getType().isAnnotationPresent(Endpoint.class)) {
				map.put(bean.getName(), bean.getInstance());
			}
		}
		return map;
	}

	public void init(SherpaSettings settings, ServletContext context) {
		this.loadManagedBean(settings.userService());
		this.loadManagedBean(settings.tokenService());
		this.loadManagedBean(settings.activityService());
		this.loadManagedBean(settings.jsonProvider());
		
		this.loadManagedBean(StringParamParser.class);
		this.loadManagedBean(IntegerParamParser.class);
		this.loadManagedBean(DoubleParamPaser.class);
		this.loadManagedBean(FloatParamParser.class);
		this.loadManagedBean(BooleanParamParser.class);
		this.loadManagedBean(DateParamParser.class);
		this.loadManagedBean(CalendarParamParser.class);
		this.loadManagedBean(JsonParamParser.class);
		
		try {
			this.getManagedBean(JsonParamParser.class).setJsonProvider(this.getManagedBean(JsonProvider.class));
		} catch (NoSuchManagedBeanExcpetion e) {
			e.printStackTrace();
			throw new SherpaRuntimeException(e);
		}
		
		// load the root domain
		this.loadManagedBeans("com.khs.sherpa.endpoint");		
	}

}
