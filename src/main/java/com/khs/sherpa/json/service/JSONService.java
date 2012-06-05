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

import static com.khs.sherpa.util.Defaults.SESSION_TIMEOUT;
import static com.khs.sherpa.util.Util.msg;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.logging.Logger;

import com.khs.sherpa.annotation.Param;
import com.khs.sherpa.parser.ParamParser;
import com.khs.sherpa.servlet.SherpaServlet;

public class JSONService {

	Logger LOG = Logger.getLogger(SherpaServlet.class.getName());

	class Result {

		private String code;

		private String message;

		Result(String code, String message) {
			this.code = code;
			this.message = message;
		}

		public String getCode() {
			return code;
		}

		public String getMessage() {
			return message;
		}

		public void setCode(String code) {
			this.code = code;
		}

		public void setMessage(String message) {
			this.message = message;
		}
	}

	public long getSessionTimeout() {
		return sessionTimeout;
	}

	public void setSessionTimeout(long sessionTimeout) {
		this.sessionTimeout = sessionTimeout;
	}

	SessionTokenService tokenService = null;

	UserService userService = null;
	
	ActivityService activityService = null;
	
	List<ParamParser<?>> parsers;
	
	JsonProvider jsonProvider = null;
	
	// session timeout in milliseconds, zero indicates no timeout
	long sessionTimeout = SESSION_TIMEOUT;

	public SessionToken authenticate(@Param(name = "userid") String userid, @Param(name = "password") String password) {

		SessionToken token = null;
		
			String[] roles = userService.authenticate(userid, password);
			String tokenId = tokenService.newToken(userid);
			token = new SessionToken();
			token.setRoles(roles);
			token.setToken(tokenId);
			token.setTimeout(sessionTimeout);
			token.setActive(true);
			token.setUserid(userid);
			token.setLastActive(System.currentTimeMillis());
			log("authenticated", userid, "n/a");
			this.activityService.logActivity(token.getUserid(), "authenticated");
	
		return token;

	}

	public void info(String msg,OutputStream out) {
		map(out, new Result("INFO", msg));
	}

	public void error(Exception ex, OutputStream out) {
		String error = ex.getMessage();
		StackTraceElement[] stack = ex.getStackTrace();
		for (StackTraceElement ste : stack) {
			error += "\n" + ste.toString();
		}
		if (ex.getCause() != null) {
			error += "\n *** Exception Cause ***";
			stack = ex.getCause().getStackTrace();
			for (StackTraceElement ste : stack) {
				error += "\n" + ste.toString();
			}
		}

		map(out, new Result("ERROR", error));
	}

	public void error(String msg, OutputStream out) {
		map(out, new Result("ERROR", msg));
	}

	public void mapJsonp(OutputStream out, Object object,String jsonpCallback) {
		try {
			    out.write( (jsonpCallback+"(").getBytes());
				out.write(jsonProvider.toJson(object).getBytes());
				out.write(");".getBytes());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	
	public void map(OutputStream out, Object object) {
		try {	
			  out.write(jsonProvider.toJson(object).getBytes());			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void message(String message, OutputStream out) {
		map(out, new Result("SUCCESS", message));
	}

	public void validation(String message, OutputStream out) {
		map(out, new Result("VALIDATION", message));
	}

	public SessionStatus validToken(String token, String userid) {
		return tokenService.isActive(userid, token);
	}

	private void log(String action, String email, String token) {
		LOG.info(msg(String.format("Executed - %s,%s,%s ", action, email, token)));
	}

	public SessionTokenService getTokenService() {
		return tokenService;
	}

	public void setTokenService(SessionTokenService tokenService) {
		this.tokenService = tokenService;
	}

	public UserService getUserService() {
		return userService;
	}

	public void setUserService(UserService userService) {
		this.userService = userService;
	}
	
	public ActivityService getActivityService() {
		return activityService;
	}

	public void setActivityService(ActivityService activityService) {
		this.activityService = activityService;
	}

	public List<ParamParser<?>> getParsers() {
		return parsers;
	}

	public void setParsers(List<ParamParser<?>> parsers) {
		this.parsers = parsers;
	}

	public JsonProvider getJsonProvider() {
		return jsonProvider;
	}

	public void setJsonProvider(JsonProvider jsonProvider) {
		this.jsonProvider = jsonProvider;
	}
}
