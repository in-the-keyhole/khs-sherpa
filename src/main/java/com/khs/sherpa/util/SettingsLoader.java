package com.khs.sherpa.util;

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

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.khs.sherpa.annotation.Encode;
import com.khs.sherpa.json.service.ActivityService;
import com.khs.sherpa.json.service.DefaultActivityService;
import com.khs.sherpa.json.service.DefaultTokenService;
import com.khs.sherpa.json.service.DefaultUserService;
import com.khs.sherpa.json.service.GsonJsonProvider;
import com.khs.sherpa.json.service.JsonProvider;
import com.khs.sherpa.json.service.SessionTokenService;
import com.khs.sherpa.json.service.UserService;
import com.khs.sherpa.servlet.SherpaServlet;

public class SettingsLoader {

	private static final Logger LOGGER = LoggerFactory.getLogger(SettingsLoader.class);
	
	protected Properties properties;
	
	public String decodeInputConfigFilePath(String configFile){
		
		String path = null;
		if(configFile !=null){
			try {
				path = URLDecoder.decode(configFile, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				throw new RuntimeException(SHERPA_NOT_INITIALIZED+"property file sherpa.properties must be defined in classpath");
			}
		}
		return path;
	}
	public SettingsLoader (){
		
	}
	public SettingsLoader(String configFile) {
		try {
		
				InputStream in = SherpaServlet.class.getClassLoader().getResourceAsStream(decodeInputConfigFilePath(configFile));
				if (in != null) {
					properties = new Properties();
					properties.load(in);
					LOGGER.info(msg("sherpa properties loaded"));
				}
			}
		catch (IOException e) {
			LOGGER.error("sherpa properties not found, defaults applied...");
         // does'nt exist...
		}
		
		if (properties == null) {			
			throw new RuntimeException(SHERPA_NOT_INITIALIZED+"property file sherpa.properties must be defined in classpath");
		}
		
	}
	
	public SettingsLoader(Properties properties) {
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
			if (value.equals("N") || value.equals("NO") || value.equals("FALSE")) {
				return false;
			}		
		} 
		return Defaults.ACTIVITY_LOG;
	}
	
	public boolean jsonpSupport() {
	    String value = properties.getProperty("jsonp.support");
		if (value != null) {
			value = value.toUpperCase();
			if (value.equals("Y") || value.equals("YES") || value.equals("TRUE")) {
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
				LOGGER.info(msg("session timeout set to "+timeout+" ms"));
				return timeout;
				
			} catch(NumberFormatException e) {
				throw new RuntimeException("ERROR reading session.timeout value from property file, value must be long");
			}
		}
		LOGGER.info(msg("session timeout set to "+Defaults.SESSION_TIMEOUT+" ms"));
		return Defaults.SESSION_TIMEOUT;
	}
	
	public JsonProvider jsonProvider() {
		String userClazzName = properties.getProperty("json.provider");
		if (userClazzName == null) {
			return new GsonJsonProvider();
		} else {
			return (GsonJsonProvider) createInstance(userClazzName);
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
	
	public UserService userService() {
		String userClazzName = properties.getProperty("user.service");
		if (userClazzName == null) {
			return new DefaultUserService();
		} else {
			return (UserService) createInstance(userClazzName);
		}
	}
	
	public SessionTokenService tokenService() {
		String tokenClazzName = properties.getProperty("token.service");
		if (tokenClazzName == null) {
			return new DefaultTokenService();
		} else {
			return (SessionTokenService) createInstance(tokenClazzName);
		}
	}
	
	public ActivityService activityService() {
		String activityClazzName = properties.getProperty("activity.service");
		if (activityClazzName == null) {
			return new DefaultActivityService();
		} else {
			return (ActivityService) createInstance(activityClazzName);
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
