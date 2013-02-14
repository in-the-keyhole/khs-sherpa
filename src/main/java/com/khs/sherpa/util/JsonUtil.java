package com.khs.sherpa.util;

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
import java.io.OutputStream;

import com.khs.sherpa.exception.SherpaRuntimeException;
import com.khs.sherpa.json.service.JsonProvider;

public class JsonUtil {

	public static void mapJsonp(Object object, JsonProvider provider, OutputStream out, String jsonpCallback) {
		try {
			    out.write( (jsonpCallback+"(").getBytes());
			    JsonUtil.map(object, provider, out);
				out.write(");".getBytes());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void map(Object object, JsonProvider provider, OutputStream out, String callback) {
		if (callback != null) {
			JsonUtil.mapJsonp(object, provider, out, callback);
		} else {
			JsonUtil.map(object, provider, out);
		}
	}
	
	public static void map(Object object, JsonProvider provider, OutputStream out) {
		try {	
			if(object != null) {
			  out.write(provider.toJson(object).getBytes());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void error(String msg, JsonProvider provider, OutputStream out, String callback) {
		if (callback != null) {
			JsonUtil.mapJsonp(new Result("ERROR", msg), provider, out, callback);
		} else {
			JsonUtil.map(new Result("ERROR", msg), provider, out);
		}
	}
	
	public static void error(String msg, JsonProvider provider, OutputStream out) {
		map(new Result("ERROR", msg), provider, out);
	}
	
	public static void info(String msg, JsonProvider provider, OutputStream out) {
		map(new Result("INFO", msg), provider, out);
	}

	public static void error(SherpaRuntimeException ex, JsonProvider provider, OutputStream out) {
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

		map(new Result("ERROR", error), provider, out);
	}

	public static void message(String message, JsonProvider provider, OutputStream out) {
		map(new Result("SUCCESS", message), provider, out);
	}

	public static void validation(String message, JsonProvider provider, OutputStream out) {
		map(new Result("VALIDATION", message), provider, out);
	}
}
