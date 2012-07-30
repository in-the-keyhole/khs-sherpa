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

import static com.khs.sherpa.util.Util.msg;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.khs.sherpa.exception.SherpaRuntimeException;
import com.khs.sherpa.json.service.SessionStatus;

public class SherpaServlet extends HttpServlet {

    private Logger LOG = Logger.getLogger(SherpaServlet.class.getName());
	private static final long serialVersionUID = 4345668988238038540L;	

	private void doService(HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		SessionStatus sessionStatus = null;
		
		SherpaRequest sherpa = new SherpaRequest();
		sherpa.setServletContext(getServletContext());
		sherpa.setSessionStatus(sessionStatus);
		sherpa.loadRequest(request, response);
	
		sherpa.setTarget(ReflectionCache.getObject(sherpa.getEndpoint()));
		sherpa.run();
	}
	
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			doService(request, response);
		} catch (SherpaRuntimeException e) {	
			response.sendError(500,"Sherpa Error "+e.getMessage());
			LOG.log(Level.SEVERE,msg("ERROR "+e.getMessage() ));
		}
		catch (Exception e) {	
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
		} catch (SherpaRuntimeException e) {	
			response.sendError(500,"Sherpa Error "+e.getMessage());
			LOG.log(Level.SEVERE,msg(e.getMessage() ));
			e.printStackTrace();
		}
		catch (Exception e) {	
			throw new ServletException(e);
		}
	}

//	@Override
//	public void init() throws ServletException {
//
//		System.out.println(SherpaContext.getSherpaContext(this.getServletContext()));
//		
//		String configFile = "sherpa.properties";
//		if(getInitParameter("sherpaConfigPath") != null) {
//			configFile = getInitParameter("sherpaConfigPath");
//		}
//		
//		SherpaSettings loader = new SherpaSettings(configFile);
//		
//		// loading service
////		service.setUserService(loader.userService());
////		service.setTokenService(loader.tokenService());
////		service.setActivityService(loader.activityService());
//		
//		// loading settings
//		Settings settings = new Settings();
//		settings.endpointPackage = loader.endpoint();
//		settings.sessionTimeout = loader.timeout();
//		settings.dateFormat = loader.dateFormat();
//		settings.dateTimeFormat = loader.dateTimeFormat();
//		settings.activityLogging = loader.logging();
//		settings.encode = loader.encoding();
//		settings.sherpaAdmin = loader.sherpaAdmin();
//		settings.jsonpSupport = loader.jsonpSupport();
//		SettingsContext context = new SettingsContext();
//		context.setSettings(settings);
//		
//		JsonProvider jsonProvider = loader.jsonProvider();
//		
//		// initialize parsers
//		List<ParamParser<?>> parsers = new ArrayList<ParamParser<?>>();
//		parsers.add(new StringParamParser());
//		parsers.add(new IntegerParamParser());
//		parsers.add(new DoubleParamPaser());
//		parsers.add(new FloatParamParser());
//		parsers.add(new BooleanParamParser());
//		parsers.add(new DateParamParser());
//		parsers.add(new CalendarParamParser());
//		
//		JsonParamParser jsonParamParser = new JsonParamParser();
//		jsonParamParser.setJsonProvider(jsonProvider);
//		parsers.add(jsonParamParser);
//
//		service.setJsonProvider(jsonProvider);
//		service.setParsers(parsers);
//		
////		// initialize endpoints
//		EndpointScanner scanner = new EndpointScanner();
//		scanner.classPathScan(settings.endpointPackage);
//		
//		// hard code sherpa endpoint
//		ReflectionCache.addObject(SherpaEndpoint.class.getName(), SherpaEndpoint.class);
//	}
}
