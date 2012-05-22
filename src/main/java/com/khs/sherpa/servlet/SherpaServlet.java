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

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.khs.sherpa.endpoint.SherpaEndpoint;
import com.khs.sherpa.json.service.JSONService;
import com.khs.sherpa.json.service.SessionStatus;
import com.khs.sherpa.util.SettingsLoader;

public class SherpaServlet extends HttpServlet {

//	private Logger LOG = Logger.getLogger(SherpaServlet.class.getName());
	private static final long serialVersionUID = 4345668988238038540L;	
	private JSONService service = new JSONService();
	private Settings settings = new Settings();

	private void doService(HttpServletRequest request, HttpServletResponse response) throws Exception {
		SessionStatus sessionStatus = null;
		
		SherpaRequest sherpa = new SherpaRequest();
		sherpa.setService(service);
		sherpa.setSettings(settings);
		sherpa.setSessionStatus(sessionStatus);
		sherpa.loadRequest(request, response);
	
		sherpa.setTarget(ReflectionCache.getObject(sherpa.getEndpoint()));
		sherpa.run();
	}
	
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			doService(request, response);
		} catch (Exception e) {
			throw new ServletException(e);
		}
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		try {
			doService(request, response);
		} catch (Exception e) {
			throw new ServletException(e);
		}
	}

//	private void executeSherpaCommand(HttpServletRequest request,HttpServletResponse response,String action) {	
//		try {
//			adminAuthenticate(request, response);
//			
//			if (action.equals(SESSION_ACTION)) {
//				this.service.map(response.getOutputStream(), this.service.getTokenService().sessions());		
//			} else if (action.equals(DEACTIVATE_USER_ACTION)) {
//				String deactivateId = request.getParameter("deactivate");
//				if (deactivateId == null) {
//					this.service.error("deactivate parameter not specified", response.getOutputStream());
//				}
//				this.service.getTokenService().deactivateUser(deactivateId);
//				this.service.info("user id->"+deactivateId+" has been deactivated",response.getOutputStream());			
//			}
//			
//		} catch (ServletException e) {
//			error("Executing sherpa command");
//            throw new RuntimeException(e);
//		} catch (IOException e) {
//			error("Executing sherpa command");
//			throw new RuntimeException(e);
//		}
//		
//	}
//
	
	@Override
	public void init() throws ServletException {

		String configFile = "sherpa.properties";
		if(getInitParameter("sherpaConfigPath") != null) {
			configFile = getInitParameter("sherpaConfigPath");
		}
		
		SettingsLoader loader = new SettingsLoader(configFile);
		
		// loading service
		service.setUserService(loader.userService());
		service.setTokenService(loader.tokenService());
		service.setActivityService(loader.activityService());
		
		// loading settings
		settings.endpointPackage = loader.endpoint();
		settings.sessionTimeout = loader.timeout();
		settings.dateFormat = loader.dateFormat();
		settings.dateTimeFormat = loader.dateTimeFormat();
		settings.activityLogging = loader.logging();
		settings.encode = loader.encoding();
		settings.sherpaAdmin = loader.sherpaAdmin();
		
//		// initialize endpoints
		EndpointScanner scanner = new EndpointScanner();
		scanner.classPathScan(this.settings.endpointPackage);
		
		// hard code sherpa endpoint
		ReflectionCache.addObject("sherpa", SherpaEndpoint.class);
	}
}
