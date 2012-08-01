package com.khs.sherpa;

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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.ServletContext;

import com.khs.sherpa.json.service.ActivityService;
import com.khs.sherpa.json.service.SessionTokenService;
import com.khs.sherpa.json.service.UserService;
import com.khs.sherpa.parser.ParamParser;

public class SherpaContext {

	private static final String SHERPA_APPLICATION_CONTEXT_ATTRIBUTE = SherpaContext.class.getName() + ".CONTEXT";
	
	private SherpaSettings sherpaSettings;
	
	private Set<String> endpiontPaths = new HashSet<String>();
	
	// Sherpa Services
	private ActivityService activityService;
	private UserService userService;
	private SessionTokenService sessionTokenService;
	
	private List<ParamParser<?>> parser;
	
	public final List<ParamParser<?>> getParser() {
		return parser;
	}

	public final Set<String> getEndpiontPaths() {
		return endpiontPaths;
	}

	public void addEnpointPath(String path) {
		this.endpiontPaths.add(path);
	}

	public void setParser(List<ParamParser<?>> parser) {
		this.parser = parser;
	}

	public SherpaSettings getSherpaSettings() {
		return sherpaSettings;
	}

	public ActivityService getActivityService() {
		if(activityService == null) {
			activityService = getSherpaSettings().activityService();
		}
		return activityService;
	}

	public UserService getUserService() {
		if(userService == null) {
			userService = getSherpaSettings().userService();
		}
		return userService;
	}

	public SessionTokenService getSessionTokenService() {
		if(sessionTokenService == null) {
			sessionTokenService = getSherpaSettings().tokenService();
		}
		return sessionTokenService;
	}

	public void setSherpaSettings(SherpaSettings sherpaSettings) {
		this.sherpaSettings = sherpaSettings;
	}

	public SherpaContext(ServletContext servletContext) {
		servletContext.setAttribute(SHERPA_APPLICATION_CONTEXT_ATTRIBUTE, this);
	}
	
	public static final SherpaContext getSherpaContext(ServletContext servletContext) {
		return (SherpaContext) servletContext.getAttribute(SHERPA_APPLICATION_CONTEXT_ATTRIBUTE);
	}
}
