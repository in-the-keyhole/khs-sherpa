package com.khs.sherpa.util;

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
