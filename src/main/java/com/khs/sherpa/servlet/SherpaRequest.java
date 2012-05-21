package com.khs.sherpa.servlet;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import javax.servlet.http.HttpServletRequest;

class SherpaRequest {

	private String endpoint;
	private String action;
	
	private Object target;
	private Object responseData;
	
	private HttpServletRequest servletRequest;
	
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

	public void loadRequest(HttpServletRequest request) {
		this.setEndpoint(request.getParameter("endpoint"));
		this.setAction(request.getParameter("action"));
		this.setServletRequest(request);
	}
	
	public void run() {
		RequestMapper map = new RequestMapper(new Settings());
		Class<?> clazz = target.getClass();
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
								Object result = map.map(clazz.getName(),m.getName(),types[i], servletRequest, annotation);					
								params[i] = result;
								i++;
							}
						}

					}

					try {
						responseData = m.invoke(target, params);
//						this.service.map(response.getOutputStream(), result);
//						if (this.settings.activityLogging) {
//							this.service.getActivityService().logActivity(userid==null?"anonymous":userid,"executed endpoint:"+target.getClass().getName()+" action:"+m.getName());
//						}
						
					} catch (Throwable e) {
//						this.service.error("action " + action + " with  parameter types " + types + " failed", response.getOutputStream());
						throw new RuntimeException(e);
					}
				}
			}
		} catch (Exception e) {
//			this.service.error(e, response.getOutputStream());
			throw new RuntimeException(e);

		}
		if (!found) {
			// can run this method for this endpoint error
//			this.service.error("Action " + action + " not found...", response.getOutputStream());
		}
	}
}
