package com.khs.sherpa.json.service;


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
	
	public SessionToken authenticate(String userid, String password) {
		String[] roles = this.authenticateUser(userid, password);
		SessionToken token = this.newToken(userid, roles);
		this.activityService.logActivity(token.getUserid(), "authenticated");
		tokenService.activate(userid, token);
		return token;
	}
	
	protected final String[] authenticateUser(String userid, String password) {
		return userService.authenticate(userid, password);
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
