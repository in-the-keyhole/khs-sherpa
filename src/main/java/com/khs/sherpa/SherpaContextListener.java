package com.khs.sherpa;


import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.reflections.Reflections;

import com.khs.sherpa.annotation.Endpoint;
import com.khs.sherpa.endpoint.SherpaEndpoint;
import com.khs.sherpa.parser.BooleanParamParser;
import com.khs.sherpa.parser.CalendarParamParser;
import com.khs.sherpa.parser.DateParamParser;
import com.khs.sherpa.parser.DoubleParamPaser;
import com.khs.sherpa.parser.FloatParamParser;
import com.khs.sherpa.parser.IntegerParamParser;
import com.khs.sherpa.parser.JsonParamParser;
import com.khs.sherpa.parser.ParamParser;
import com.khs.sherpa.parser.StringParamParser;
import com.khs.sherpa.servlet.ReflectionCache;

public class SherpaContextListener implements ServletContextListener {

	public static final String SHERPA_CONFIG_LOCATION = "classpath:/sherpa.properties";
	
	public void contextInitialized(ServletContextEvent servletContextEvent) {
		servletContextEvent.getServletContext().log("Loading Sherpa Context... Start");
		
		String configLocation = servletContextEvent.getServletContext().getInitParameter("sherpaConfigLocation");
		if(configLocation == null) {
			configLocation = SHERPA_CONFIG_LOCATION;
		}
		
		SherpaContext sherpaContext = new SherpaContext(servletContextEvent.getServletContext());
		sherpaContext.setSherpaSettings(new SherpaSettings(configLocation));
		
		
		// initialize parsers
		List<ParamParser<?>> parsers = new ArrayList<ParamParser<?>>();
		parsers.add(new StringParamParser());
		parsers.add(new IntegerParamParser());
		parsers.add(new DoubleParamPaser());
		parsers.add(new FloatParamParser());
		parsers.add(new BooleanParamParser());
		parsers.add(new DateParamParser());
		parsers.add(new CalendarParamParser());
		
		JsonParamParser jsonParamParser = new JsonParamParser();
		jsonParamParser.setJsonProvider(sherpaContext.getSherpaSettings().jsonProvider());
		parsers.add(jsonParamParser);

		
		// initialize endpoints
//		EndpointScanner scanner = new EndpointScanner();
//		scanner.classPathScan(sherpaContext.getSherpaSettings().endpoint());
		
		Reflections reflections = new Reflections(sherpaContext.getSherpaSettings().endpoint());
		Set<Class<?>> annotated = reflections.getTypesAnnotatedWith(Endpoint.class);
		
		for(Class<?> c: annotated) {
			ReflectionCache.addObject(c.getSimpleName(), c);
		}
		
		// hard code sherpa endpoint
		ReflectionCache.addObject(SherpaEndpoint.class.getName(), SherpaEndpoint.class);
		
		servletContextEvent.getServletContext().log("Loading Sherpa Context... Finsished");
	}

	public void contextDestroyed(ServletContextEvent servletContextEvent) {
		servletContextEvent.getServletContext().log("Distoryed Sherpa Context... Start");
		
		
		servletContextEvent.getServletContext().log("Loading Sherpa Context... Finished");
	}

}
