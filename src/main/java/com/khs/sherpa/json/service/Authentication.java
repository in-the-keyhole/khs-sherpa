package com.khs.sherpa.json.service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.khs.sherpa.context.ApplicationContext;
import com.khs.sherpa.exception.NoSuchManagedBeanExcpetion;


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
public class Authentication {

//	private static final Logger LOG = Logger.getLogger(SherpaServlet.class.getName());
	
	private UserService userService;
	private SessionTokenService tokenService;
	private ActivityService activityService;
	
	
	public Authentication(ApplicationContext context) throws NoSuchManagedBeanExcpetion {
		
		userService = context.getManagedBean(UserService.class);
		tokenService = context.getManagedBean(SessionTokenService.class);
		activityService = context.getManagedBean(ActivityService.class);
	}

	public SessionToken authenticate(String userid, String password, HttpServletRequest request, HttpServletResponse response) {
		String[] roles = this.authenticateUser(userid, password, request, response);
		SessionToken token = this.newToken(userid, roles);
		this.activityService.logActivity(token.getUserid(), "authenticated");
		tokenService.activate(userid, token);
		return token;
	}
	
	protected final String[] authenticateUser(String userid, String password, HttpServletRequest request, HttpServletResponse response) {
		return userService.authenticate(userid, password, request, response);
	}
	
	protected final SessionToken newToken(String userid, String[] roles) {
		SessionToken token =  new SessionToken();
		token.setRoles(roles);
		token.setToken(tokenService.newToken(userid));
		token.setTimeout(0);
		token.setActive(true);
		token.setUserid(userid);
		token.setLastActive(System.currentTimeMillis());
		return token;
	}
	
}
