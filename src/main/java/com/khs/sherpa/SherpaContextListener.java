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
		
		ApplicationContext applicationContext = null;
		
		Class<?> springApplicationContextClass = null;
		try {
			springApplicationContextClass = Class.forName("com.khs.sherpa.sping.SpingApplicationContext");
		} catch (ClassNotFoundException e1) {
			springApplicationContextClass = null;
		}
		
		if(springApplicationContextClass != null) {
			try {
				applicationContext = (GenericApplicationContext) springApplicationContextClass.newInstance();
			} catch (InstantiationException e) {
				e.printStackTrace();
				throw new SherpaRuntimeException(e);
			} catch (IllegalAccessException e) {
				e.printStackTrace();
				throw new SherpaRuntimeException(e);
			}
		} else {
			applicationContext = new GenericApplicationContext();
		}
		
		System.out.println(applicationContext);
		
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
	}
}
