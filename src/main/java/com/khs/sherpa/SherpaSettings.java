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
import static com.khs.sherpa.util.Constants.SHERPA_NOT_INITIALIZED;
import static com.khs.sherpa.util.Util.msg;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang3.StringUtils;

import com.khs.sherpa.annotation.Encode;
import com.khs.sherpa.json.service.DefaultActivityService;
import com.khs.sherpa.json.service.DefaultTokenService;
import com.khs.sherpa.json.service.DefaultUserService;
import com.khs.sherpa.json.service.GsonJsonProvider;
import com.khs.sherpa.servlet.SherpaServlet;
import com.khs.sherpa.util.Defaults;

public class SherpaSettings {

	private static Logger LOG = Logger.getLogger(SherpaSettings.class.getName());
	
	protected Properties properties;
	
	public SherpaSettings(String configFile) {
		
		try {
			
			InputStream in = null;
			if(configFile.startsWith("classpath:")) {
				in = SherpaServlet.class.getClassLoader().getResourceAsStream(configFile.substring("classpath:".length()));
			} else {
				in = new FileInputStream(configFile);
			}
		    if (in != null) {
		    	properties = new Properties();
		    	properties.load(in);
		    	LOG.info(msg("sherpa properties loaded"));
			}
		} catch (IOException e) {
			LOG.log(Level.SEVERE, "sherpa properties not found, defaults applied...");
         // does'nt exist...
		}
		
		if (properties == null) {			
			throw new RuntimeException(SHERPA_NOT_INITIALIZED+"property file sherpa.properties must be defined in classpath");
		}
	}
	
	
	public SherpaSettings(Properties properties) {
		this.properties = properties;
	}
	
	public String sherpaAdmin() {
	    String value = properties.getProperty("sherpa.role");
		if (value != null) {
			return value;
		} 
		return Defaults.SHERPA_ADMIN;
	}
	
	public String encoding() {
	    String value = properties.getProperty("encode.format");
		if (value != null) {
			value = value.toUpperCase();
			if (value.equals(Encode.CSV) || value.equals(Encode.XML) || value.equals(Encode.HTML)) {
				return value;
			}		
		}
		return Defaults.ENCODE;
	}
	
	public boolean logging() {
	    String value = properties.getProperty("activity.logging");
		if (value != null) {
			value = value.toUpperCase();
			if (value.equalsIgnoreCase("N") || value.equalsIgnoreCase("NO") || value.equalsIgnoreCase("FALSE")) {
				return false;
			}		
		} 
		return Defaults.ACTIVITY_LOG;
	}
	
	public boolean endpointAuthenication() {
		String value = properties.getProperty("endpoint.authentication");
		if(value != null) {
			if (value.equalsIgnoreCase("N") || value.equalsIgnoreCase("NO") || value.equalsIgnoreCase("FALSE")) {
				return false;
			}	
		}
		return Defaults.ENDPOINT_AUTHENTICATION;
	}
	
	public boolean jsonpSupport() {
	    String value = properties.getProperty("jsonp.support");
		if (value != null) {
			value = value.toUpperCase();
			if (value.equalsIgnoreCase("Y") || value.equalsIgnoreCase("YES") || value.equalsIgnoreCase("TRUE")) {
				return true;
			}		
		} 
		return Defaults.JSONP_SUPPORT;
	}
	
	
	public String dateFormat() {
		String value = properties.getProperty("date.format");
		if (value != null) {
			return value;		
		}
		return Defaults.DATE_FORMAT;
	}
	
	public String dateTimeFormat() {
		String value = properties.getProperty("date.time.format");
		if (value != null) {
			return value;		
		}
		return Defaults.DATE_TIME_FORMAT;
	}
	
	public long timeout() {
		String value = properties.getProperty("session.timeout");
		if (value != null) {
			try {
				long timeout = Long.parseLong(value);
				LOG.info(msg("session timeout set to "+timeout+" ms"));
				return timeout;
				
			} catch(NumberFormatException e) {
				throw new RuntimeException("ERROR reading session.timeout value from property file, value must be long");
			}
		}
		LOG.info(msg("session timeout set to "+Defaults.SESSION_TIMEOUT+" ms"));
		return Defaults.SESSION_TIMEOUT;
	}
	
	public Class<?> jsonProvider() {
		String userClazzName = properties.getProperty("json.provider");
		if (userClazzName == null) {
			return GsonJsonProvider.class;
		} else {
			return getInstanceClass(userClazzName);
		}
	}
	
	public String endpoint() {
		String endpoint = properties.getProperty("endpoint.package");
		if (endpoint != null) {
			int end = endpoint.length();
			// add package separator
			if (endpoint.lastIndexOf('.') != end - 1) {
				endpoint += ".";
			}
		} else {			
			throw new RuntimeException(SHERPA_NOT_INITIALIZED+"endpoint package location must be defined in sherpa.properties");		
		}
		return endpoint;
	}
	
	public Class<?> userService() {
		String userClazzName = properties.getProperty("user.service");
		if (userClazzName == null) {
			try {
				return Class.forName("com.khs.sherpa.sping.SpringAuthentication");
			} catch (ClassNotFoundException e1) {
				return DefaultUserService.class;
			}
			
		} else {
			return getInstanceClass(userClazzName);
		}
	}
	
	public Class<?> tokenService() {
		String tokenClazzName = properties.getProperty("token.service");
		if (tokenClazzName == null) {
			return DefaultTokenService.class;
		} else {
			return getInstanceClass(tokenClazzName);
		}
	}
	
	public Class<?> activityService() {
		String activityClazzName = properties.getProperty("activity.service");
		if (activityClazzName == null) {
			return DefaultActivityService.class;
		} else {
			return getInstanceClass(activityClazzName);
		}
	}
	
	public boolean serverEnabled() {
		String value = properties.getProperty("server.enabled");
		if(value.equalsIgnoreCase("true")) {
			return true;
		}
		return false;
	}
	
	public String serverToken() {
		String value = properties.getProperty("server.token");
		if(StringUtils.isNotEmpty(value)) {
			return value;
		}
		return null;
	}
	
	public String serverUrl() {
		String value = properties.getProperty("server.url");
		if(!StringUtils.isEmpty(value)) {
			// always remove the ending /
			if(StringUtils.endsWith(value, "/")) {
				value = StringUtils.removeEnd(value, "/");
			}
			return value;
		}
		return null;
	}
	
	protected Class<?> getInstanceClass(String name) {
		try {
			return Class.forName(name);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException("User service class not found " + name);
		}
	}
	
	protected Object createInstance(String name) {
		try {
			Class<?> clazz = Class.forName(name);
			return clazz.newInstance();

		} catch (ClassNotFoundException e) {
			throw new RuntimeException("User service class not found " + name);
		} catch (InstantiationException e) {
			throw new RuntimeException("User service class could be instantiated " + name);
		} catch (IllegalAccessException e) {
			throw new RuntimeException("User service class could not be accessed " + name);
		}

	}

}
