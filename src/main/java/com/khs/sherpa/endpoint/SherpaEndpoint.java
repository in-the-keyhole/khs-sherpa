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

import java.util.Date;

import javax.annotation.security.RolesAllowed;

import com.khs.sherpa.annotation.Endpoint;
import com.khs.sherpa.annotation.Param;
import com.khs.sherpa.json.service.SessionTokenService;

@Endpoint(value = "sherpa", authenticated = true)
public class SherpaEndpoint {

	@RolesAllowed("SHERPA_ADMIN")
	public void sessions(@Param(name ="val") String val, SessionTokenService service, Date date) {
		System.out.println("SESSION" + service);
	}
	
}
