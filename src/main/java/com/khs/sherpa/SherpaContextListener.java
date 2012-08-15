package com.khs.sherpa;

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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.reflections.Reflections;

import com.khs.sherpa.context.ApplicationContext;
import com.khs.sherpa.context.GenericApplicationContext;
import com.khs.sherpa.context.factory.DefaultManagedBeanFactory;
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
import com.khs.sherpa.parser.ParamParser;
import com.khs.sherpa.parser.StringParamParser;

public class SherpaContextListener implements ServletContextListener {

	public static final Logger LOGGER = Logger.getLogger("SHERPA");
	
	public static final String SHERPA_CONFIG_LOCATION = "classpath:/sherpa.properties";
	
	public void contextInitialized(ServletContextEvent servletContextEvent) {
		servletContextEvent.getServletContext().log("Loading Sherpa Context... Start");
		
		String configLocation = servletContextEvent.getServletContext().getInitParameter("sherpaConfigLocation");
		if(configLocation == null) {
			configLocation = SHERPA_CONFIG_LOCATION;
		}
		
		SherpaSettings settings = new SherpaSettings(configLocation);
		
		GenericApplicationContext applicationContext = new GenericApplicationContext();
		DefaultManagedBeanFactory defaultManagedBeanFactory = (DefaultManagedBeanFactory) applicationContext.getManagedBeanFactory();
		
		defaultManagedBeanFactory.loadManagedBean(settings.userService());
		defaultManagedBeanFactory.loadManagedBean(settings.tokenService());
		defaultManagedBeanFactory.loadManagedBean(settings.activityService());
		defaultManagedBeanFactory.loadManagedBean(settings.jsonProvider());
		
		applicationContext.setAttribute(ApplicationContext.SETTINGS_JSONP, settings.jsonpSupport());
		applicationContext.setAttribute(ApplicationContext.SETTINGS_ADMIN_USER, settings.sherpaAdmin());
		applicationContext.setAttribute(ApplicationContext.SETTINGS_ENDPOINT_AUTH, settings.endpointAuthenication());
		
		// load the root domain
		defaultManagedBeanFactory.loadManagedBeans("com.khs.sherpa.endpoint");

		this.setupInitializer(applicationContext);
		
		servletContextEvent.getServletContext().setAttribute(GenericApplicationContext.SHERPA_APPLICATION_CONTEXT_ATTRIBUTE, applicationContext);

//		// initialize parsers
		List<ParamParser<?>> parsers = new ArrayList<ParamParser<?>>();
		parsers.add(new StringParamParser());
		parsers.add(new IntegerParamParser());
		parsers.add(new DoubleParamPaser());
		parsers.add(new FloatParamParser());
		parsers.add(new BooleanParamParser());
		parsers.add(new DateParamParser());
		parsers.add(new CalendarParamParser());
		
		JsonParamParser jsonParamParser = new JsonParamParser();
		try {
			jsonParamParser.setJsonProvider(defaultManagedBeanFactory.getManagedBean(JsonProvider.class));
		} catch (NoSuchManagedBeanExcpetion e) {
			throw new SherpaRuntimeException(e);
		}
		parsers.add(jsonParamParser);
		applicationContext.setAttribute(ApplicationContext.SETTINGS_PARSERS, parsers);
		
		servletContextEvent.getServletContext().log("Loading Sherpa Context... Finsished");
	}

	public void contextDestroyed(ServletContextEvent servletContextEvent) {
		servletContextEvent.getServletContext().log("Distoryed Sherpa Context... Start");
		
		
		servletContextEvent.getServletContext().log("Loading Sherpa Context... Finished");
	}

//	private void setupEvents(SherpaContext context) {
//		Set<Class<?>> events = new HashSet<Class<?>>();
//		for(String path: context.getEndpiontPaths()) {
//			LOGGER.info("Loading Events from path: [" + path + "]");
//			Reflections reflections = new Reflections(path);
//			
//			for(Class<? extends SherpaEvent> clazz : reflections.getSubTypesOf(SherpaEvent.class)) {
//				System.out.println(clazz);
//			}
//		}
//	}
//	
	private void setupInitializer(ApplicationContext applicationContext) {
		Set<Class<?>> initialized = new HashSet<Class<?>>();
		Reflections reflections = new Reflections("com.khs.sherpa");
	    for(Class<? extends SherpaInitializer> clazz : reflections.getSubTypesOf(SherpaInitializer.class)) {
	    	// skip the Initializer if its been loaded
	    	if(initialized.contains(clazz)) {
	    		LOGGER.warning("Initializer [" + clazz + "] is already loaded. Skipping.....");
	    		continue;
	    	}
	    	initialized.add(clazz);
	    	try {
				clazz.newInstance().sherpaInitialize(applicationContext);
			} catch (Exception e) {
				throw new SherpaRuntimeException(e);
			}
	    }
	    
//		for(String path: context.getEndpiontPaths()) {
//			LOGGER.info("Loading Initializer from path: [" + path + "]");
//			Reflections reflections = new Reflections(path);
//		    for(Class<? extends SherpaInitializer> clazz : reflections.getSubTypesOf(SherpaInitializer.class)) {
//		    	// skip the Initializer if its been loaded
//		    	if(initialized.contains(clazz)) {
//		    		LOGGER.warning("Initializer [" + clazz + "] is already loaded. Skipping.....");
//		    		continue;
//		    	}
//		    	this.initializeClass(context, clazz);
//		    	initialized.add(clazz);
//		    }
//		    for(Class<?> clazz : reflections.getTypesAnnotatedWith(Initializer.class)) {
//		    	// skip the Initializer if its been loaded
//		    	if(initialized.contains(clazz)) {
//		    		continue;
//		    	}
//		    	this.initializeClass(context, clazz);
//		    	initialized.add(clazz);
//		    }
//		}
	}
//	
//	private void initializeClass(SherpaContext context, Class<?> clazz) {
//		try {
//			if(SherpaInitializer.class.isAssignableFrom(clazz)) {
//				// Uses SherpaInitializer
//				SherpaInitializer initializer = (SherpaInitializer) clazz.newInstance();
//				initializer.sherpaInitialize(context);
//			} else {
//				// User Annotation Initializer
//				Set<Method> methods = Reflections.getAllMethods(clazz, ReflectionUtils.withAnnotation(Initialize.class));
//				if(methods.size() == 0) {
//					LOGGER.warning("Initializer [" + clazz + "] is missing @Initialize. Skipping.....");
//					return;
//				}
//				Object initializer = clazz.newInstance();
//				for(Method m: methods) {
//					try {
//						if(m.getParameterTypes().length == 0) {
//							m.invoke(initializer, new Object(){});
//						} else {
//							m.invoke(initializer, context);
//						}
//						
//					} catch (IllegalArgumentException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					} catch (InvocationTargetException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//				}
//			}
//		} catch (InstantiationException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IllegalAccessException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
//	
//	private void setupEndpoints(SherpaContext context) {
//		for(String path: context.getEndpiontPaths()) {
//			LOGGER.info("Loading endpoints from path: [" + path + "]");
//			for(Class<?> c: this.getEndpoints(path)) {
//				ReflectionCache.addObject(c.getSimpleName(), c);
//			}
//		}
//	}
//	
//	private Set<Class<?>> getEndpoints(String path) {
//		Reflections reflections = new Reflections(path);
//		return reflections.getTypesAnnotatedWith(Endpoint.class);
//	}
	
}
