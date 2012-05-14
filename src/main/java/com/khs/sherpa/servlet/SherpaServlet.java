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

import static com.khs.sherpa.util.Constants.AUTHENTICATE_ACTION;
import static com.khs.sherpa.util.Constants.DEACTIVATE_USER_ACTION;
import static com.khs.sherpa.util.Constants.INVALID_TOKEN;
import static com.khs.sherpa.util.Constants.SESSION_ACTION;
import static com.khs.sherpa.util.Constants.SESSION_TIMED_OUT;
import static com.khs.sherpa.util.Constants.SHERPA_NOT_INITIALIZED;
import static com.khs.sherpa.util.Constants.SHERPA_SERVER;
import static com.khs.sherpa.util.Util.msg;

import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.khs.sherpa.annotation.Endpoint;
import com.khs.sherpa.json.service.ActivityService;
import com.khs.sherpa.json.service.AuthenticationException;
import com.khs.sherpa.json.service.DefaultActivityService;
import com.khs.sherpa.json.service.DefaultTokenService;
import com.khs.sherpa.json.service.DefaultUserService;
import com.khs.sherpa.json.service.JSONService;
import com.khs.sherpa.json.service.SessionStatus;
import com.khs.sherpa.json.service.SessionToken;
import com.khs.sherpa.json.service.SessionTokenService;
import com.khs.sherpa.json.service.UserService;

@Endpoint
public class SherpaServlet extends HttpServlet {

	Logger LOG = Logger.getLogger(SherpaServlet.class.getName());

	private static final long serialVersionUID = 4345668988238038540L;	
	private Settings settings = new Settings();
	private JSONService service = new JSONService();
	private RequestMapper mapper = null;
	
	
	public SherpaServlet() {
		super();
	}

	public void authenticate(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String id = request.getParameter("userid");
		String password = request.getParameter("password");
		try {
			SessionToken token = this.service.authenticate(id, password);
			this.service.getTokenService().activate(id, token);
			log("authenticated", id, "n/a");
			this.service.map(response.getOutputStream(), token);

		} catch (AuthenticationException e) {
			this.service.error("Authentication Error Invalid Credentials", response.getOutputStream());
			log(msg("invalid authentication"), id, "n/a");
		}
	}
	
	public void adminAuthenticate(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String id = request.getParameter("userid");
		String password = request.getParameter("password");
		try {
			this.service.getUserService().adminAuthenticate(id, password);	
			log("admin authenticated", id, "n/a");	

		} catch (AuthenticationException e) {
			this.service.error("Admin Authentication Error Invalid Credentials", response.getOutputStream());
			log(msg("invalid admin authentication"), id, "n/a");
		}
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		String action = request.getParameter("action");
		String endpoint = request.getParameter("endpoint");

		if (this.settings.endpointPackage == null) {
			this.service.error("endpointPackage not specified, configure as SherpaServlet init() parameter...", response.getOutputStream());
		}

		if (action == null) {
			this.service.error("action parameter not specified", response.getOutputStream());
		}
		
		// sherpa commands
		if (endpoint == null && isSherpaCommand(action)) {			
			executeSherpaCommand(request,response,action);
			return;
		}
		

		if (endpoint == null && !action.equals(AUTHENTICATE_ACTION)) {
			this.service.error("endpoint not specified", response.getOutputStream());
		}

		boolean isAuthenticate = action.equals(AUTHENTICATE_ACTION);

		Class<?> clazz = null;
		if (!isAuthenticate) {

			try {
				clazz = ReflectionCache.getClass(endpoint,this.settings.endpointPackage);
			} catch (ClassNotFoundException e) {
				this.service.error("Endpoint " + this.settings.endpointPackage + endpoint + " not found", response.getOutputStream());
			}
		} else {
			clazz = this.getClass();
		}

		// make sure endpoint is a sherpa endpoint

		Endpoint ep = (Endpoint) clazz.getAnnotation(Endpoint.class);
		if (ep == null) {
			this.service.error("Endpoint " + clazz + " is not @SherpaEndpoint", response.getOutputStream());
		}

//		if (action == null) {
//			this.service.error("action not specified", response.getOutputStream());
//		}

		if (isAuthenticate) {

			authenticate(request, response);

		} else {

			// // validate session token, if authenticated endpoint
			SessionStatus status = validToken(request);
			if (ep.authenticated() && status == SessionStatus.NOT_AUTHENTICATED) {
				this.service.error(INVALID_TOKEN, response.getOutputStream());
			} else if (ep.authenticated() && status == SessionStatus.TIMED_OUT) {
				this.service.error(SESSION_TIMED_OUT, response.getOutputStream());
			} else {
				executeEndpoint(request, response, action, clazz);
			}

		}

	}
	
	private boolean isSherpaCommand(String action) {		
		return action.equals(SESSION_ACTION) || action.equals(DEACTIVATE_USER_ACTION);
	}
	
	private void executeSherpaCommand(HttpServletRequest request,HttpServletResponse response,String action) {	
		try {
			adminAuthenticate(request, response);
			
			if (action.equals(SESSION_ACTION)) {
				this.service.map(response.getOutputStream(), this.service.getTokenService().sessions());		
			} else if (action.equals(DEACTIVATE_USER_ACTION)) {
				String deactivateId = request.getParameter("deactivate");
				if (deactivateId == null) {
					this.service.error("deactivate parameter not specified", response.getOutputStream());
				}
				this.service.getTokenService().deactivateUser(deactivateId);
				this.service.info("user id->"+deactivateId+" has been deactivated",response.getOutputStream());			
			}
			
		} catch (ServletException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	//@TODO add class and method cache
	private void executeEndpoint(HttpServletRequest request, HttpServletResponse response, String action, Class<?> clazz) throws IOException {
		boolean found = false;	
		try {
			Method[] methods = clazz.getMethods();
			for (Method m : methods) {
				if (m.getName().equals(action)) {
					found = true;

					Class<?>[] types = m.getParameterTypes();
					Object[] params = null;
					// get parameters
					if (types.length > 0) {
						Annotation[][] parameters = m.getParameterAnnotations();
						// Annotation[] annotations = parameters[0];
						params = new Object[types.length];
						int i = 0;
						for (Annotation[] annotations : parameters) {
							for (Annotation annotation : annotations) {
								Object result = map(clazz.getName(),m.getName(),types[i], request, annotation);
								params[i] = result;
								i++;
							}
						}

					}

					try {
						Object target = clazz.newInstance();
						Object result = m.invoke(target, params);
						this.service.map(response.getOutputStream(), result);
						this.service.getActivityService().logActivity("anonymous","executed endpoint:"+target.getClass().getName()+" method:"+m.getName()+" parameters:"+params);
						
					} catch (Throwable e) {
						this.service.error("action " + action + " with  parameter types " + types + " failed", response.getOutputStream());
						throw new RuntimeException(e);
					}
				}
			}
		} catch (Exception e) {
			this.service.error(e, response.getOutputStream());
			throw new RuntimeException(e);

		}
		if (!found) {
			this.service.error("Action " + action + " not found...", response.getOutputStream());
		}
	}

	private Object map(String endpoint,String action,Class<?> type, HttpServletRequest request, Annotation annotation) {
		return mapper.map(endpoint,action,type,request,annotation);
	}

	private void log(String action, String email, String token) {
		LOG.info(msg(SHERPA_SERVER+String.format("Executed - %s,%s,%s ", action, email, token)));
	}

	protected SessionStatus validToken(HttpServletRequest request) {
		String userid = request.getParameter("userid");
		String token = request.getParameter("token");
		return validToken(token, userid);
	}

	protected SessionStatus validToken(String token, String userid) {
		if (token == null || userid == null) {
			return SessionStatus.NOT_AUTHENTICATED;
		}
		return this.service.validToken(token, userid);
	}

	private void initUserService(String serviceName) {

		try {
			Class<?> clazz = Class.forName(serviceName);
			UserService service = (UserService) clazz.newInstance();
			this.service.setUserService(service);

		} catch (ClassNotFoundException e) {
			error("User service class not found " + serviceName);
			throw new RuntimeException();
		} catch (InstantiationException e) {
			error("User service class could be instantiated " + serviceName);
			throw new RuntimeException();
		} catch (IllegalAccessException e) {
			error("User service class could not be accessed " + serviceName);
			throw new RuntimeException();
		}

	}
	
	private void initTokenService(String serviceName) {
		try {
			Class<?> clazz = Class.forName(serviceName);
			SessionTokenService service = (SessionTokenService) clazz.newInstance();
			this.service.setTokenService(service);

		} catch (ClassNotFoundException e) {
			error("Token service class not found " + serviceName);
			throw new RuntimeException();
		} catch (InstantiationException e) {
			error("Token service class could be instantiated " + serviceName);
			throw new RuntimeException();
		} catch (IllegalAccessException e) {
			error("Token service class could not be accessed " + serviceName);
			throw new RuntimeException();
		}

		
	}
	
	private void initActivityService(String serviceName) {

		try {
			Class<?> clazz = Class.forName(serviceName);
			ActivityService service = (ActivityService) clazz.newInstance();
			this.service.setActivityService(service);

		} catch (ClassNotFoundException e) {
			error("User service class not found " + serviceName);
			throw new RuntimeException();
		} catch (InstantiationException e) {
			error("User service class could be instantiated " + serviceName);
			throw new RuntimeException();
		} catch (IllegalAccessException e) {
			error("User service class could not be accessed " + serviceName);
			throw new RuntimeException();
		}

	}
	
	

	private void error(String message) {
		LOG.log(Level.SEVERE, msg(SHERPA_NOT_INITIALIZED + message));

	}

	@Override
	public void init() throws ServletException {
		
		// read default properties
		Properties properties = null;
		try {
			InputStream in = this.getClass().getClassLoader().getResourceAsStream("sherpa.properties");
		    if (in != null) {
		      properties = new Properties();
			  properties.load(in);
			  LOG.info(msg(SHERPA_SERVER+"sherpa properties loaded"));
			}
		} catch (IOException e) {
         // does'nt exist...
			   error("sherpa properties not found, defaults applied...");
		}
		
		if (properties == null) {			
			throw new RuntimeException(SHERPA_NOT_INITIALIZED+"property file sherpa.properties must be defined in classpath");
		}
		
		String value = properties.getProperty("endpoint.package");
		
		// Get the value of an initialization parameter
		//String value = getServletConfig().getInitParameter("endpoint-package");
		String serviceClazzName = properties.getProperty("user.service");
		if (serviceClazzName == null) {
			this.service.setUserService(new DefaultUserService());
		} else {
			initUserService(serviceClazzName);
		}
		serviceClazzName = properties.getProperty("token.service");
		if (serviceClazzName == null) {
			this.service.setTokenService(new DefaultTokenService());
		} else {
			initTokenService(serviceClazzName);
		}
		serviceClazzName = properties.getProperty("activity.service");
		if (serviceClazzName == null) {
			this.service.setActivityService(new DefaultActivityService());
		} else {
		    initActivityService(serviceClazzName);				
		}
		
		if (value != null) {
			this.settings.endpointPackage = value;
			int end = this.settings.endpointPackage.length();
			// add package seperator
			if (this.settings.endpointPackage.lastIndexOf('.') != end - 1) {
				this.settings.endpointPackage += ".";
			}
		} else {
			
			throw new RuntimeException(SHERPA_NOT_INITIALIZED+"endpoint package location must be defined in sherpa.properties");
			
		}
		
		initTimeout(properties);
		initDataTypes(properties);
		// request mapper
		this.mapper = new RequestMapper(this.settings);
		// initialize endpoints
		EndpointScanner scanner = new EndpointScanner();
		scanner.classPathScan(this.settings.endpointPackage);


	}
	
	
	private void initTimeout(Properties props) {
		
		String value = props.getProperty("session.timeout");
		if (value != null) {
			try {
				long timeout = Long.parseLong(value);
				this.service.setSessionTimeout(timeout);
				LOG.info(msg(SHERPA_SERVER+"session timeout set to "+timeout+" ms"));
				
			} catch(NumberFormatException e) {
				
				error("ERROR reading session.timeout value from property file, value must be long");
				throw new RuntimeException("ERROR reading session.timeout value from property file, value must be long");
			}
			
		}
		
		LOG.info(msg(SHERPA_SERVER+"session timeout set to "+this.service.getSessionTimeout()+" ms"));
		
		
		
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
		
		LOG.info(msg(SHERPA_SERVER+"session timeout set to "+this.service.getSessionTimeout()+" ms"));
		
		
		
	}
	
	
	
	
	
}
