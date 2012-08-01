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

	public static void mapJsonp(OutputStream out, JsonProvider provider, Object object,String jsonpCallback) {
		try {
			    out.write( (jsonpCallback+"(").getBytes());
			    JsonUtil.map(out, provider, object);
				out.write(");".getBytes());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void map(OutputStream out, JsonProvider provider, Object object) {
		try {	
			  out.write(provider.toJson(object).getBytes());			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void error(String msg, JsonProvider provider, OutputStream out) {
		map(out, provider, new Result("ERROR", msg));
	}
	
	public static void info(String msg, JsonProvider provider, OutputStream out) {
		map(out, provider, new Result("INFO", msg));
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

		map(out, provider, new Result("ERROR", error));
	}

	public static void message(String message, JsonProvider provider, OutputStream out) {
		map(out, provider, new Result("SUCCESS", message));
	}

	public static void validation(String message, JsonProvider provider, OutputStream out) {
		map(out, provider, new Result("VALIDATION", message));
	}
}
