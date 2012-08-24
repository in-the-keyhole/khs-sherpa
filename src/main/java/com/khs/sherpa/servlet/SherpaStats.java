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

import javax.servlet.ServletRequest;

public class SherpaStats {

	private static final String SHERPA_REQUEST_PARAM = "sherpa_request_param";
	private static final String SHERPA_METHOD_PARAM = "sherpa_method_param";
	
	public static void startRequest(ServletRequest request) {
		request.setAttribute(SHERPA_REQUEST_PARAM, System.currentTimeMillis());
	}
	
	public static void endRequest(ServletRequest request) {
		long start = (Long) request.getAttribute(SHERPA_REQUEST_PARAM);
		long end = System.currentTimeMillis();
		System.out.print("Request: start [" + start + "] : end [" + end + "] ");
		System.out.print("seconds ["+seconds(start, end)+"] ");
		System.out.println("millis ["+(end-start)+"] ");
	}
	
	public static void startMethod(ServletRequest request) {
		request.setAttribute(SHERPA_METHOD_PARAM, System.currentTimeMillis());
	}
	
	public static void endMethod(ServletRequest request) {
		long start = (Long) request.getAttribute(SHERPA_METHOD_PARAM);
		long end = System.currentTimeMillis();
		System.out.print("Method: start [" + start + "] : end [" + end + "] ");
		System.out.print("seconds ["+seconds(start, end)+"] ");
		System.out.println("millis ["+(end-start)+"] ");
	}
	
	public static long seconds(long start, long end) {
		return ((end - start) / 1000);
	}
	
}
