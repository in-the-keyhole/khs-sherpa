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

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.logging.Logger;

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

	private List<ParamParser<?>> parsers;
	
	private JsonProvider jsonProvider = null;
	
	// session timeout in milliseconds, zero indicates no timeout
	long sessionTimeout = SESSION_TIMEOUT;


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
