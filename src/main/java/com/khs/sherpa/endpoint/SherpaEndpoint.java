package com.khs.sherpa.endpoint;

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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.annotation.security.DenyAll;
import javax.annotation.security.RolesAllowed;

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
import com.khs.sherpa.exception.SherpaRuntimeException;
import com.khs.sherpa.json.service.SessionTokenService;
import com.khs.sherpa.util.MethodUtil;
import com.khs.sherpa.util.SherpaPredicates;

@Endpoint(value = "Sherpa", authenticated = false)
public class SherpaEndpoint implements ApplicationContextAware {

	private ApplicationContext applicationContext;
	
	@RolesAllowed("SHERPA_ADMIN")
	@Action(mapping = "/sherpa/admin/sessions")
	public Object sessions(SessionTokenService service) {
		return service.sessions();
	}
	
	@RolesAllowed("SHERPA_ADMIN")
	@Action(mapping = "/sherpa/admin/endpoints")
	public Map<String, String> endpoints() {
		Map<String, String> map = new HashMap<String, String>();
		
		for(Entry<String, Object> entry: applicationContext.getEndpointTypes().entrySet()) {
			String name = entry.getValue().getClass().getAnnotation(Endpoint.class).value();
			if(StringUtils.isEmpty(name)) {
				name = entry.getValue().getClass().getSimpleName();
			}
			map.put(name, entry.getValue().getClass().getName());
		}
		return map;
	}
	
	@SuppressWarnings("unchecked")
//	@RolesAllowed("SHERPA_ADMIN")
	@Action(mapping = "/sherpa/admin/describe/{value}")
	public Object describe(@Param("value") String value) {
		
		List<Map<String, Object>> actions = new ArrayList<Map<String,Object>>();
		
		Set<Method> methods = null;
		
		try {
			methods = Reflections.getAllMethods(applicationContext.getType(value),
					Predicates.and(
							Predicates.not(SherpaPredicates.withAssignableFrom(Object.class)),
							ReflectionUtils.withModifier(Modifier.PUBLIC),
							Predicates.not(ReflectionUtils.withModifier(Modifier.ABSTRACT)),
							Predicates.not(SherpaPredicates.withGeneric()))
					);
		} catch (NoSuchManagedBeanExcpetion e) {
			throw new SherpaRuntimeException(e);
		}
		
		for(Method method: methods) {
			
			Map<String, Object> action = new HashMap<String, Object>();
			actions.add(action);
			
			action.put("name", MethodUtil.getMethodName(method));
			
			if(method.isAnnotationPresent(DenyAll.class)) {
				action.put("permission", "DenyAll");
			} else if(method.isAnnotationPresent(RolesAllowed.class)) {
				action.put("permission", "RolesAllowed");
				action.put("roles", method.getAnnotation(RolesAllowed.class).value());
			} else {
				action.put("permission", "PermitAll");
			}
			
			Map<String, String> params = new HashMap<String, String>();
			
			Class<?>[] types = method.getParameterTypes();
			Annotation[][] parameters = method.getParameterAnnotations();
			for(int i=0; i<parameters.length; i++) {
				Class<?> type = types[i];
				Param annotation = null;
				if(parameters[i].length > 0 ) {
					for(Annotation an: parameters[i]) {
						if(an.annotationType().isAssignableFrom(Param.class)) {
							annotation = (Param) an;
							break;
						}
					}
					
				}
				if(annotation != null) {
					params.put(annotation.value(), type.getName());
				}
			}
			
			if(params.size() > 0) {
				action.put("params", params);
			} else {
				action.put("params", null);
			}
			
		}
		
		return actions;
	}

	public void setApplicationContext(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}
}
