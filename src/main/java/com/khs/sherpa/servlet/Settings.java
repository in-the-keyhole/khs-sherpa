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

import static com.khs.sherpa.util.Defaults.ACTIVITY_LOG;
import static com.khs.sherpa.util.Defaults.DATE_FORMAT;
import static com.khs.sherpa.util.Defaults.DATE_TIME_FORMAT;
import static com.khs.sherpa.util.Defaults.ENDPOINT_AUTHENTICATION;
import static com.khs.sherpa.util.Defaults.SESSION_TIMEOUT;
import static com.khs.sherpa.util.Defaults.SHERPA_ADMIN;

import com.khs.sherpa.json.service.JsonProvider;
import com.khs.sherpa.util.Defaults;

public class Settings {
	
	public String dateFormat = DATE_FORMAT;
	public String dateTimeFormat = DATE_TIME_FORMAT;
	public long sessionTimeout = SESSION_TIMEOUT;
	public boolean endPointAuthentication = ENDPOINT_AUTHENTICATION;
	public String endpointPackage = null;
	public boolean activityLogging = ACTIVITY_LOG;
	public String encode = null;
	public String sherpaAdmin = SHERPA_ADMIN;
	public Class<? extends JsonProvider> jsonProvider = Defaults.JSON_PROVIDER; 
	
}
