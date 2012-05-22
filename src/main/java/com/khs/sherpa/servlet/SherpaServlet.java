package com.khs.sherpa.servlet;

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
import static com.khs.sherpa.util.Util.errmsg;
import static com.khs.sherpa.util.Util.msg;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.khs.sherpa.annotation.Encode;
import com.khs.sherpa.annotation.Endpoint;
import com.khs.sherpa.json.service.ActivityService;
import com.khs.sherpa.json.service.DefaultActivityService;
import com.khs.sherpa.json.service.DefaultTokenService;
import com.khs.sherpa.json.service.DefaultUserService;
import com.khs.sherpa.json.service.JSONService;
import com.khs.sherpa.json.service.SessionStatus;
import com.khs.sherpa.json.service.SessionTokenService;
import com.khs.sherpa.json.service.UserService;

@Endpoint
public class SherpaServlet extends HttpServlet {

	private Logger LOG = Logger.getLogger(SherpaServlet.class.getName());
	private static final long serialVersionUID = 4345668988238038540L;	
	private JSONService service = new JSONService();
	private Settings settings = new Settings();
	
//	private RequestMapper mapper = null;
	
//	public void adminAuthenticate(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//		String id = request.getParameter("userid");
//		String password = request.getParameter("password");
//		try {
//			this.service.getUserService().adminAuthenticate(id, password);	
//			log(msg("admin authenticated"), id, "n/a");	
//
//		} catch (AuthenticationException e) {
//			this.service.error("Admin Authentication Error Invalid Credentials", response.getOutputStream());
//			log(msg("invalid admin authentication"), id, "n/a");
//		}
//	}

	private void doService(HttpServletRequest request, HttpServletResponse response) throws Exception {
		SessionStatus sessionStatus = null;
		
		SherpaRequest sherpa = new SherpaRequest();
		sherpa.setService(service);
		sherpa.setSettings(settings);
		sherpa.setSessionStatus(sessionStatus);
		sherpa.loadRequest(request, response);
	
		sherpa.setTarget(ReflectionCache.getObject(sherpa.getEndpoint()));
		sherpa.run();
	}
	
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			doService(request, response);
		} catch (Exception e) {
			throw new ServletException(e);
		}
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		try {
			doService(request, response);
		} catch (Exception e) {
			throw new ServletException(e);
		}

//		// sherpa commands
//		if (endpoint == null && isSherpaCommand(action)) {			
//			executeSherpaCommand(request,response,action);
//			return;
//		}
//		
//
//		if (endpoint == null && !action.equals(AUTHENTICATE_ACTION)) {
//			this.service.error("endpoint not specified", response.getOutputStream());
//		}
//
//		boolean isAuthenticate = action.equals(AUTHENTICATE_ACTION);
//
//		Class<?> clazz = null;
//		if (!isAuthenticate) {
//
//			try {
//				clazz = ReflectionCache.getClass(endpoint,this.settings.endpointPackage);
//			} catch (ClassNotFoundException e) {
//				this.service.error("Endpoint " + this.settings.endpointPackage + endpoint + " not found", response.getOutputStream());
//			}
//		} else {
//			clazz = this.getClass();
//		}
//
//		// make sure endpoint is a sherpa endpoint
//
//		Endpoint ep = (Endpoint) clazz.getAnnotation(Endpoint.class);
//		if (ep == null) {
//			this.service.error("Endpoint " + clazz + " is not @SherpaEndpoint", response.getOutputStream());
//		}
//
//		if (isAuthenticate) {
//
//			authenticate(request, response);
//
//		} else {
//
//			boolean log = this.settings.activityLogging;
//			// // validate session token, if authenticated endpoint
//			SessionStatus status = validToken(request);
//			if (ep.authenticated() && status == SessionStatus.NOT_AUTHENTICATED) {
//				this.service.error(INVALID_TOKEN, response.getOutputStream());
//				if (log) {
//					this.service.getActivityService().logActivity("anonymous","INVALID TOKEN");
//				}
//			} else if (ep.authenticated() && status == SessionStatus.TIMED_OUT) {
//				this.service.error(SESSION_TIMED_OUT, response.getOutputStream());
//				if (log) {
//					this.service.getActivityService().logActivity("anonymous","SESSION TIMED OUT");
//				}
//			} else if (ep.authenticated() && status == SessionStatus.INVALID_TOKEN) {
//				this.service.error(INVALID_TOKEN,response.getOutputStream());
//				if (log) {
//					this.service.getActivityService().logActivity("anonymous","INVALID TOKEN");
//				}
//			} else {
//				executeEndpoint(request, response, action, clazz);
//			}
//
//		}

	}
	
//	private boolean isSherpaCommand(String action) {		
//		return action.equals(SESSION_ACTION) || action.equals(DEACTIVATE_USER_ACTION);
//	}
//	
//	private void executeSherpaCommand(HttpServletRequest request,HttpServletResponse response,String action) {	
//		try {
//			adminAuthenticate(request, response);
//			
//			if (action.equals(SESSION_ACTION)) {
//				this.service.map(response.getOutputStream(), this.service.getTokenService().sessions());		
//			} else if (action.equals(DEACTIVATE_USER_ACTION)) {
//				String deactivateId = request.getParameter("deactivate");
//				if (deactivateId == null) {
//					this.service.error("deactivate parameter not specified", response.getOutputStream());
//				}
//				this.service.getTokenService().deactivateUser(deactivateId);
//				this.service.info("user id->"+deactivateId+" has been deactivated",response.getOutputStream());			
//			}
//			
//		} catch (ServletException e) {
//			error("Executing sherpa command");
//            throw new RuntimeException(e);
//		} catch (IOException e) {
//			error("Executing sherpa command");
//			throw new RuntimeException(e);
//		}
//		
//	}
//
	
	private void error(String message) {
		LOG.log(Level.SEVERE, errmsg(message));

	}
	
	private Object createInstance(String name) {
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
	
	@Override
	public void init() throws ServletException {
		//TODO: check init for different location
		String configFile = "sherpa.properties";
		
		Properties properties = null;
		try {
			InputStream in = SherpaServlet.class.getClassLoader().getResourceAsStream(configFile);
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
		
		String userClazzName = properties.getProperty("user.service");
		if (userClazzName == null) {
			service.setUserService(new DefaultUserService());
		} else {
			service.setUserService((UserService) createInstance(userClazzName));
		}

		String tokenClazzName = properties.getProperty("token.service");
		if (tokenClazzName == null) {
			service.setTokenService(new DefaultTokenService());
		} else {
			service.setTokenService((SessionTokenService) createInstance(tokenClazzName));
		}
		
		String activityClazzName = properties.getProperty("activity.service");
		if (activityClazzName == null) {
			service.setActivityService(new DefaultActivityService());
		} else {
			service.setActivityService((ActivityService) createInstance(activityClazzName));
		}
		
		// loading settings
		initEndpoint(properties);
		initTimeout(properties);
		initDataTypes(properties);
		initLogging(properties);
		initEncoding(properties);
		
//		// initialize endpoints
		EndpointScanner scanner = new EndpointScanner();
		scanner.classPathScan(this.settings.endpointPackage);
	}
	
	private void initEndpoint(Properties prop) {
		String endpoint = prop.getProperty("endpoint.package");
		if (endpoint != null) {
			this.settings.endpointPackage = endpoint;
			int end = this.settings.endpointPackage.length();
			// add package separator
			if (this.settings.endpointPackage.lastIndexOf('.') != end - 1) {
				this.settings.endpointPackage += ".";
			}
		} else {			
			throw new RuntimeException(SHERPA_NOT_INITIALIZED+"endpoint package location must be defined in sherpa.properties");		
		}
	}
	
	private void initLogging(Properties props) {
		
	    String value = props.getProperty("activity.logging");
		if (value != null) {
			value = value.toUpperCase();
			if (value.equals("N") || value.equals("NO") || value.equals("FALSE")) {
				this.settings.activityLogging = false;
			}		
		} 
		
	}
	
	private void initEncoding(Properties props) {
		
	    String value = props.getProperty("encode.format");
		if (value != null) {
			value = value.toUpperCase();
			if (value.equals(Encode.CSV) || value.equals(Encode.XML) || value.equals(Encode.HTML)) {
				this.settings.encode = value;
			}		
		} 
		
	}
	
	private void initTimeout(Properties props) {
		
		String value = props.getProperty("session.timeout");
		if (value != null) {
			try {
				long timeout = Long.parseLong(value);
				this.service.setSessionTimeout(timeout);
				LOG.info(msg("session timeout set to "+timeout+" ms"));
				
			} catch(NumberFormatException e) {
				
				error("ERROR reading session.timeout value from property file, value must be long");
				throw new RuntimeException("ERROR reading session.timeout value from property file, value must be long");
			}
			
		}
		
		LOG.info(msg("session timeout set to "+this.service.getSessionTimeout()+" ms"));
		
	}
	
	private void initDataTypes(Properties props) {
		
		String value = props.getProperty("date.format");
		if (value != null) {
			this.settings.dateFormat = value;		
		}
		
		value = props.getProperty("date.time.format");
		if (value != null) {
			this.settings.dateTimeFormat = value;		
		}
		
		LOG.info(msg("session timeout set to "+this.service.getSessionTimeout()+" ms"));
		
	}
	
}
