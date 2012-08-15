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

import com.khs.sherpa.context.GenericApplicationContext;
import com.khs.sherpa.exception.SherpaInvalidUsernamePassword;
import com.khs.sherpa.exception.SherpaRuntimeException;
import com.khs.sherpa.servlet.request.DefaultSherpaRequest;

public class SherpaServlet extends HttpServlet {

	private static final long serialVersionUID = 4345668988238038540L;	
	
    private static final Logger LOG = Logger.getLogger(SherpaServlet.class.getName());

	private void doService(HttpServletRequest request, HttpServletResponse response) throws RuntimeException, IOException {
		try {
			
			DefaultSherpaRequest sherpaRequest = new DefaultSherpaRequest();
			sherpaRequest.setApplicationContext(GenericApplicationContext.getApplicationContext(getServletContext()));

			sherpaRequest.doService(request, response);
			
		} catch (SherpaInvalidUsernamePassword e) {
			response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid Username or Password!");
			LOG.log(Level.INFO,msg("INFO "+e.getMessage() ));			
		} catch (SherpaRuntimeException e) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,"Sherpa Error "+e.getMessage());
			LOG.log(Level.SEVERE,msg("ERROR "+e.getMessage() ));
		} catch (Exception e) {
			throw new SherpaRuntimeException(e);
		} finally {
			// do nothing right now
		}
	}
	
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doService(request, response);
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doService(request, response);
	}

	@Override
	protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doService(request, response);
	}

	@Override
	protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doService(request, response);
	}

	@Override
	protected long getLastModified(HttpServletRequest req) {
		return super.getLastModified(req);
	}

	@Override
	protected void doHead(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		super.doHead(req, resp);
	}
	
	@Override
	protected void doOptions(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		super.doOptions(req, resp);
	}

	@Override
	protected void doTrace(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		super.doTrace(req, resp);
	}
	
}
