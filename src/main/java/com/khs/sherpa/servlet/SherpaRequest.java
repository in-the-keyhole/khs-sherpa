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

import static com.khs.sherpa.util.Util.msg;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.logging.Logger;

import javax.annotation.security.DenyAll;
import javax.annotation.security.RolesAllowed;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;

import com.khs.sherpa.SherpaContext;
import com.khs.sherpa.annotation.Action;
import com.khs.sherpa.annotation.ContentType;
import com.khs.sherpa.annotation.Endpoint;
import com.khs.sherpa.annotation.Param;
import com.khs.sherpa.exception.SherpaActionNotFoundException;
import com.khs.sherpa.exception.SherpaInvalidUsernamePassword;
import com.khs.sherpa.exception.SherpaPermissionExcpetion;
import com.khs.sherpa.exception.SherpaRuntimeException;
import com.khs.sherpa.json.service.Authentication;
import com.khs.sherpa.json.service.JsonProvider;
import com.khs.sherpa.json.service.SessionStatus;
import com.khs.sherpa.json.service.SessionToken;
import com.khs.sherpa.util.Constants;
import com.khs.sherpa.util.JsonUtil;
import com.khs.sherpa.util.MethodUtil;
import com.khs.sherpa.util.UrlUtil;

class SherpaRequest {

	private static Logger LOG = Logger.getLogger(SherpaRequest.class.getName());
	
	private String endpoint;
	private String action;
	
	private Object target;
	private Object responseData;
	
	private ServletContext servletContext;
	private HttpServletRequest servletRequest;
	private HttpServletResponse servletResponse;
	
	private SessionStatus sessionStatus = null;
	
	private String userid;
	private String token;
	
	public String getEndpoint() {
		return endpoint;
	}
	
	public void setEndpoint(String endpoint) {
		this.endpoint = endpoint;
	}
	
	public String getAction() {
		return action;
	}
	
	public void setAction(String action) {
		this.action = action;
	}

	public Object getResponseData() {
		return responseData;
	}

	public void setTarget(Object target) {
		this.target = target;
	}

	public void setServletRequest(HttpServletRequest servletRequest) {
		this.servletRequest = servletRequest;
	}

	public void setSessionStatus(SessionStatus sessionStatus) {
		this.sessionStatus = sessionStatus;
	}

	public HttpServletResponse getServletResponse() {
		return servletResponse;
	}

	public void setServletResponse(HttpServletResponse servletResponse) {
		this.servletResponse = servletResponse;
	}

	public void loadRequest(HttpServletRequest request, HttpServletResponse response) {
		
		String urlMethod = ReflectionCache.getUrlMethod(UrlUtil.getPath(request), request.getMethod());
		if(StringUtils.isNotEmpty(urlMethod)) {
			String[] str = StringUtils.split(urlMethod, ".");
			this.setEndpoint(str[0]);
			this.setAction(str[1]);
		} else {
			this.setEndpoint(request.getParameter("endpoint"));
			this.setAction(request.getParameter("action"));
		}
		
		this.setServletRequest(request);
		this.setServletResponse(response);
	}
	
	private boolean isAuthRequest(HttpServletRequest request) {
		String endpoint = this.getEndpoint();
		if(endpoint != null && endpoint.equals("null")) {
			endpoint =  null;
		}
		
		String action = this.getAction();
		
		if(endpoint == null && action.equals(Constants.AUTHENTICATE_ACTION)) {
			return true;
		}
		return false;
		
	}
	
	private void validateMethod(Method method) throws SherpaRuntimeException {
		if(target.getClass().getAnnotation(Endpoint.class).authenticated()) {
			if(!sessionStatus.equals(SessionStatus.AUTHENTICATED)) {
				throw new SherpaPermissionExcpetion("token is invalid or has expired");
			} else if(method.isAnnotationPresent(DenyAll.class)) {
				throw new SherpaPermissionExcpetion("method ["+method.getName()+"] in class ["+target.getClass().getCanonicalName()+"] has `@DenyAll` annotation" );
			} else if(method.isAnnotationPresent(RolesAllowed.class)) {
				for(String role: method.getAnnotation(RolesAllowed.class).value()) {
					if(getSherpaContext().getSessionTokenService().hasRole(userid, token, role)) {
						return ;
					}
				}
				throw new SherpaPermissionExcpetion("method ["+method.getName()+"] in class ["+target.getClass().getCanonicalName()+"] has `@RolesAllowed` annotation" );
			}
		}
	}
	
	private Object invokeMethod(Method method) {
		try {
			SherpaStats.startMethod(servletRequest);
			Object obj = method.invoke(target, this.getParams(method));
			SherpaStats.endMethod(servletRequest);
			return obj;
		} catch (Exception e) {
			e.printStackTrace();
			throw new SherpaActionNotFoundException("unable to execute method ["+method.getName()+"] in class ["+target.getClass().getCanonicalName()+"]");
		}
	}
	
	private Object[] getParams(Method method) {
		RequestMapper map = new RequestMapper();
		map.setContext(servletContext);
		map.setRequest(servletRequest);
		map.setResponse(servletResponse);
		
		Class<?>[] types = method.getParameterTypes();
		Object[] params = null;
		// get parameters
		if(types.length > 0) {
			params = new Object[types.length];
		}
		
		Annotation[][] parameters = method.getParameterAnnotations();
		for(int i=0; i<parameters.length; i++) {
			Class<?> type = types[i];
			Annotation annotation = null;
			if(parameters[i].length > 0 ) {
				for(Annotation an: parameters[i]) {
					if(an.annotationType().isAssignableFrom(Param.class)) {
						annotation = an;
						break;
					}
				}
				
			}
			params[i] = map.map(method.getClass().getName(),method.getName(),type, annotation);	
		}
		return params;
	}
	
	private Method findMethod(String name) {
		Method method = MethodUtil.getMethodByName(target.getClass(), name);
		if(method == null) {
			throw new SherpaActionNotFoundException("no method found ["+name+"] in class ["+target.getClass().getCanonicalName()+"]");
		}
		return method;
	}
	
	static <T> T[] append(T[] arr, T element) {
	    final int N = arr.length;
	    arr = Arrays.copyOf(arr, N + 1);
	    arr[N] = element;
	    return arr;
	}
	
	protected boolean hasRole(SessionToken token, String role) {
		return this.getSherpaContext().getSessionTokenService().hasRole(token.getUserid(), token.getToken(), role);
	}
	
	private JsonProvider getJsonProvider() {
		return this.getSherpaContext().getSherpaSettings().jsonProvider();
	}
	
	public void authenticate() {
		String userid = servletRequest.getParameter("userid");
		String password = servletRequest.getParameter("password");
		try {
			Authentication authentication = new Authentication(getSherpaContext());
			SessionToken token = authentication.authenticate(userid, password);
			
			// load the sherpa admin user
			if(this.hasRole(token, this.getSherpaContext().getSherpaSettings().sherpaAdmin())) {
				String[] roles = token.getRoles();
				token.setRoles(append(roles, "SHERPA_ADMIN"));
			}
			log(msg("authenticated"), userid, "*****");
			
			if (this.getSherpaContext().getSherpaSettings().jsonpSupport() && this.getCallback() != null) {
				servletResponse.setContentType("text/javascript");
			    JsonUtil.mapJsonp(this.getResponseOutputStream(), this.getJsonProvider(), token, this.getCallback());	
			} else {
			  JsonUtil.map(this.getResponseOutputStream(), this.getJsonProvider(), token);
			}    
		} catch (SherpaInvalidUsernamePassword e) {
			JsonUtil.error("Authentication Error Invalid Credentials", this.getJsonProvider(), this.getResponseOutputStream());
			log(msg("invalid authentication"), userid, "*****");
		}
	}
	
	public void run() {
		// see if there's a json call back function specified
		
		if(isAuthRequest(servletRequest)) {
			this.authenticate();
			return;
		} else {
			sessionStatus = this.getSherpaContext().getSessionTokenService().isActive(getToken(), getUserId());
		}
		
		if(target == null) {
			throw new SherpaRuntimeException("@Endpoint not found initialized");
		}
		
		Method method = this.findMethod(action);
		ContentType type = ContentType.JSON;
		if(method.isAnnotationPresent(Action.class)) {
			type = method.getAnnotation(Action.class).contentType();
		}
		
		servletResponse.setContentType(type.type);
		try {
			this.validateMethod(method);
			if (this.getSherpaContext().getSherpaSettings().jsonpSupport() && this.getCallback() != null) {
				servletResponse.setContentType("text/javascript");
				JsonUtil.mapJsonp(this.getResponseOutputStream(), this.getJsonProvider(), this.invokeMethod(method), this.getCallback());	
			} else {
				JsonUtil.map(this.getResponseOutputStream(), this.getJsonProvider(), this.invokeMethod(method));
			}
		} catch (SherpaRuntimeException e) {
			JsonUtil.error(e, this.getJsonProvider(), this.getResponseOutputStream());
			throw e;
		}
		
	}
	
	private String getUserId() {
		String userid = servletRequest.getHeader("userid");
		if(userid == null) {
			userid = servletRequest.getParameter("userid");
		}
		this.userid = userid;
		return userid;
	}
	
	private String getToken() {
		String token = servletRequest.getHeader("token");
		if(token == null) {
			token = servletRequest.getParameter("token");
		}
		this.token = token;
		return token;
	}
	
	private void log(String action, String email, String token) {
		LOG.info(msg(String.format("Executed - %s,%s,%s ", action, email, token)));
	}
	
	private OutputStream getResponseOutputStream() {
		try {
			return servletResponse.getOutputStream();
		} catch (IOException e) {
			return null;
		}
	}
	
	public ServletContext getServletContext() {
		return servletContext;
	}

	public void setServletContext(ServletContext servletContext) {
		this.servletContext = servletContext;
	}

	private SherpaContext getSherpaContext() {
		return SherpaContext.getSherpaContext(servletContext);
	}
	
	private String getCallback() {
		return this.servletRequest.getParameter("callback");
	}
	
}
