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

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.security.RolesAllowed;

import com.khs.sherpa.annotation.Endpoint;
import com.khs.sherpa.annotation.Param;
import com.khs.sherpa.exception.SherpaEndpointNotFoundException;
import com.khs.sherpa.json.service.SessionTokenService;
import com.khs.sherpa.servlet.ReflectionCache;

@Endpoint(value = "Sherpa", authenticated = true)
public class SherpaEndpoint {

	@RolesAllowed("SHERPA_ADMIN")
	public Object sessions(SessionTokenService service) {
		return service.sessions();
	}
	
	@RolesAllowed("SHERPA_ADMIN")
	public Map<String, String> endpoints() {
		Map<String, String> map = new HashMap<String, String>();
		for(Entry<String, Object> entry: ReflectionCache.getTypeCache().entrySet()) {
			map.put(entry.getKey(), entry.getValue().getClass().getName());
		}
		
		return map;
	}
	
	@RolesAllowed("SHERPA_ADMIN")
	public Object describe(@Param(name ="value") String value) {
		Object object = null;
		try {
			object = ReflectionCache.getObject(value);
		} catch (ClassNotFoundException e) {
			throw new SherpaEndpointNotFoundException("endpoint ["+value+"] not found");
		}
		
		return object.getClass();
	}
}
