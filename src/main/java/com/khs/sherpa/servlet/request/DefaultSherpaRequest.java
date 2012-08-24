package com.khs.sherpa.servlet.request;

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

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import javax.annotation.security.DenyAll;
import javax.annotation.security.RolesAllowed;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.reflections.ReflectionUtils;
import org.reflections.Reflections;

import com.google.common.base.Predicates;
import com.khs.sherpa.annotation.Action;
import com.khs.sherpa.annotation.Endpoint;
import com.khs.sherpa.annotation.Param;
import com.khs.sherpa.context.ApplicationContext;
import com.khs.sherpa.context.ApplicationContextAware;
import com.khs.sherpa.exception.NoSuchManagedBeanExcpetion;
import com.khs.sherpa.exception.SherpaActionNotFoundException;
import com.khs.sherpa.exception.SherpaPermissionExcpetion;
import com.khs.sherpa.exception.SherpaRuntimeException;
import com.khs.sherpa.json.service.Authentication;
import com.khs.sherpa.json.service.JsonProvider;
import com.khs.sherpa.json.service.SessionStatus;
import com.khs.sherpa.json.service.SessionToken;
import com.khs.sherpa.json.service.SessionTokenService;
import com.khs.sherpa.processor.DefaultRequestProcessor;
import com.khs.sherpa.processor.RequestProcessor;
import com.khs.sherpa.processor.RestfulRequestProcessor;
import com.khs.sherpa.servlet.RequestMapper;
import com.khs.sherpa.util.Constants;
import com.khs.sherpa.util.JsonUtil;
import com.khs.sherpa.util.MethodUtil;
import com.khs.sherpa.util.SherpaPredicates;
import com.khs.sherpa.util.Util;

public class DefaultSherpaRequest implements SherpaRequest {

	private Map<String, Object> attributes = new LinkedHashMap<String, Object>();
	private ApplicationContext applicationContext;
	private HttpServletRequest request;
	private RequestProcessor requestProcessor;
	
	public Object getAttribute(String name) {
		return attributes.get(name);
	}

	public void setAttribute(String name, Object object) {
		attributes.put(name, object);
	}
	
	public void doService(HttpServletRequest request, HttpServletResponse response) {
		this.request = request;
		
		ServletOutputStream output = null;
		JsonProvider jsonProvider = null;
		try {
			output = response.getOutputStream();
			jsonProvider = applicationContext.getManagedBean(JsonProvider.class);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		
		String callback = null;
		if ((Boolean)applicationContext.getAttribute(ApplicationContext.SETTINGS_JSONP)) {
			callback = request.getParameter("callback");
		}
		
		// set the correct Content type
		response.setContentType("application/json");
		if(callback != null) {
			response.setContentType("text/javascript");
		}
		
		try {
			JsonUtil.map(this.proccess(), jsonProvider, output, callback);
		} catch (Exception e) {
			e.printStackTrace();
			JsonUtil.error(e.getMessage(), jsonProvider, output, callback);
		}
	}
	
	@SuppressWarnings("unchecked")
	protected Object proccess() throws SherpaRuntimeException {
		if(!isRestful()) {
			requestProcessor = new DefaultRequestProcessor();
		} else {
			requestProcessor = new RestfulRequestProcessor(applicationContext);
		}
		
		String endpoint = requestProcessor.getEndpoint(request);
		String action = requestProcessor.getAction(request);
		String httpMethod = request.getMethod();
		
		if(StringUtils.isEmpty(endpoint) && action.equals(Constants.AUTHENTICATE_ACTION)) {
			return this.processAuthenication();
		}
		
		Object target = null;
		Set<Method> methods = null;
		
		try {
			this.hasPermission(applicationContext.getType(endpoint), request.getHeader("userid"), request.getHeader("token"));
			
			target = applicationContext.getManagedBean(endpoint);
			
			if(ApplicationContextAware.class.isAssignableFrom(target.getClass())) {
				((ApplicationContextAware)target).setApplicationContext(applicationContext);
			}
			
			methods = Reflections.getAllMethods(applicationContext.getType(endpoint), 
						Predicates.and(
								Predicates.not(SherpaPredicates.withAssignableFrom(Object.class)),
								ReflectionUtils.withModifier(Modifier.PUBLIC),
								Predicates.not(ReflectionUtils.withModifier(Modifier.ABSTRACT)),
								Predicates.not(SherpaPredicates.withGeneric()),
								Predicates.or(
										ReflectionUtils.withName(action),
										Predicates.and(
												ReflectionUtils.withAnnotation(Action.class),
												SherpaPredicates.withActionAnnotationValueEqualTo(action)
											)
									))
						);
			
			if(methods.size() == 0) {
				throw new SherpaActionNotFoundException(action);
			}
			
		} catch (NoSuchManagedBeanExcpetion e) {
			throw new SherpaRuntimeException(e);
		}
		
		return this.processEndpoint(target, methods.toArray(new Method[] {}), httpMethod);
	}
	
	protected Object processEndpoint(Object target, Method[] methods, String httpMethod) {
		Method method = MethodUtil.validateHttpMethods(methods, httpMethod);
		this.hasPermission(method, request.getHeader("userid"), request.getHeader("token"));
		return this.invokeMethod(target, method);  
	}
	
	protected void hasPermission(Class<?> target, String userid, String token) {
		SessionTokenService service = null;
		try {
			service = applicationContext.getManagedBean(SessionTokenService.class);
		} catch (NoSuchManagedBeanExcpetion e) {
			throw new SherpaRuntimeException(e);
		}
		
		// make sure Endpoint Authentication is turned on
		if((Boolean)applicationContext.getAttribute(ApplicationContext.SETTINGS_ENDPOINT_AUTH) == false) {
			return;
		}
		
		// make sure its authenicated
		if(target.getAnnotation(Endpoint.class).authenticated() && !service.isActive(userid, token).equals(SessionStatus.AUTHENTICATED)) {
			throw new SherpaPermissionExcpetion("User status [" + service.isActive(userid, token) + "]");
		}
	}
	
	protected void hasPermission(Method method, String userid, String token) {
		SessionTokenService service = null;
		try {
			service = applicationContext.getManagedBean(SessionTokenService.class);
		} catch (NoSuchManagedBeanExcpetion e) {
			throw new SherpaRuntimeException(e);
		}

		if(method.isAnnotationPresent(DenyAll.class)) {
			throw new SherpaPermissionExcpetion("method ["+method.getName()+"] in class ["+method.getDeclaringClass().getCanonicalName()+"] has `@DenyAll` annotation" );
		}
		
		if(method.isAnnotationPresent(RolesAllowed.class)) {
			boolean fail = true;
			for(String role: method.getAnnotation(RolesAllowed.class).value()) {
				if(service.hasRole(userid, token, role)) {
					fail = false;
				}
			}
			if(fail) {
				throw new SherpaPermissionExcpetion("method ["+method.getName()+"] in class ["+method.getDeclaringClass().getCanonicalName()+"] has `@RolesAllowed` annotation" );
			}
		}
	}
	
	protected Object processAuthenication() throws SherpaRuntimeException {
		String userid = request.getParameter("userid");
		String password = request.getParameter("password");
		try {
			Authentication authentication = new Authentication(applicationContext);
			SessionToken token = authentication.authenticate(userid, password);
			
			boolean hasAdminRole = applicationContext.getManagedBean(SessionTokenService.class)
				.hasRole(token.getUserid(), token.getToken(), (String) applicationContext.getAttribute(ApplicationContext.SETTINGS_ADMIN_USER));
			
			// load the sherpa admin user
			if(hasAdminRole) {
				String[] roles = token.getRoles();
				token.setRoles(Util.append(roles, "SHERPA_ADMIN"));
			}
			return token;
		} catch (NoSuchManagedBeanExcpetion e) {
			throw new SherpaRuntimeException(e);
		}
	}
	
	protected Object invokeMethod(Object target, Method method) {
		try {
			Object obj = method.invoke(target, this.getParams(method));
			return obj;
		} catch (Exception e) {
			throw new SherpaActionNotFoundException("unable to execute method ["+method.getName()+"] in class ["+target.getClass().getCanonicalName()+"]");
		} finally {
			
		}
	}
	
	private Object[] getParams(Method method) {
		RequestMapper map = new RequestMapper();
		map.setApplicationContext(applicationContext);
		map.setRequest(request);
		map.setRequestProcessor(requestProcessor);
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
	
	private boolean isRestful() {
		String path = request.getContextPath();
		if(path.equals("/")) {
			path = request.getServletPath();
		} else {
			path += request.getServletPath();
		}
		return !path.equals(request.getRequestURI());
	}
	
	@Action(disabled = true)
	public void setApplicationContext(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}
	
}
