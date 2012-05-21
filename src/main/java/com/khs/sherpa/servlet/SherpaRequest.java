package com.khs.sherpa.servlet;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import javax.annotation.security.DenyAll;
import javax.annotation.security.RolesAllowed;
import javax.servlet.http.HttpServletRequest;

import com.khs.sherpa.annotation.Endpoint;
import com.khs.sherpa.exception.SherpaActionNotFoundException;
import com.khs.sherpa.exception.SherpaPermissionExcpetion;
import com.khs.sherpa.exception.SherpaRuntimeException;
import com.khs.sherpa.json.service.SessionStatus;

class SherpaRequest {

	private String endpoint;
	private String action;
	
	private Object target;
	private Object responseData;
	
	private HttpServletRequest servletRequest;
	
	private SessionStatus sessionStatus = null;
	
	
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

	public void loadRequest(HttpServletRequest request) {
		this.setEndpoint(request.getParameter("endpoint"));
		this.setAction(request.getParameter("action"));
		this.setServletRequest(request);
	}
	
	private void validateMethod(Method method) throws SherpaRuntimeException {
		if(target.getClass().getAnnotation(Endpoint.class).authenticated()) {
			if(!sessionStatus.equals(SessionStatus.AUTHENTICATED)) {
				throw new SherpaPermissionExcpetion("token is invalid or has expired");
			} else if(method.isAnnotationPresent(DenyAll.class)) {
				throw new SherpaPermissionExcpetion("method ["+method.getName()+"] in class ["+target.getClass().getCanonicalName()+"] has `@DenyAll` annotation" );
			} else if(method.isAnnotationPresent(RolesAllowed.class)) {
				String[] roles = method.getAnnotation(RolesAllowed.class).value(); 
				System.out.println("check roles "+ roles);
				// TODO do a real check
				// throw excpetion
				throw new SherpaPermissionExcpetion("method ["+method.getName()+"] in class ["+target.getClass().getCanonicalName()+"] has `@RolesAllowed` annotation" );
			}
		}
	}
	
	private Object invokeMethod(Method method) {
		try {
			return method.invoke(target, this.getParams(method));
		} catch (Exception e) {
			throw new SherpaActionNotFoundException("unable to execute method ["+method.getName()+"] in class ["+target.getClass().getCanonicalName()+"]");
		}
	}
	
	private Object[] getParams(Method method) {
		RequestMapper map = new RequestMapper(new Settings());
		Class<?>[] types = method.getParameterTypes();
		Object[] params = null;
		// get parameters
		if (types.length > 0) {
			Annotation[][] parameters = method.getParameterAnnotations();
			// Annotation[] annotations = parameters[0];
			params = new Object[types.length];					
			int i = 0;
			for (Annotation[] annotations : parameters) {
				for (Annotation annotation : annotations) {
					Object result = map.map(method.getClass().getName(),method.getName(),types[i], servletRequest, annotation);					
					params[i] = result;
					i++;
				}
			}

		}
		return params;
	}
	
	private Method findMethod(String name) {
		Method[] methods = target.getClass().getMethods();
		for (Method m : methods) {
			if(m.getName().equals(name)) {
				return m;
			}
		}
		throw new SherpaActionNotFoundException("no method found ["+name+"] in class ["+target.getClass().getCanonicalName()+"]");
	}
	
	public void run() {
		
		Method method = this.findMethod(action);

		try {
			this.validateMethod(method);
			this.invokeMethod(method);
		} catch (SherpaRuntimeException e) {
			throw e;
		}
		
	}
}
