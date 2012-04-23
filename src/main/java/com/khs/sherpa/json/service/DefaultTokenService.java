package com.khs.sherpa.json.service;

import java.util.HashMap;
import java.util.Map;

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

public class DefaultTokenService implements SessionTokenService {

	public Map<String, SessionToken> tokens = new HashMap<String, SessionToken>();

	public String newToken(String userid) {
		return "" + System.currentTimeMillis();
	}

	public SessionStatus isActive(String userid, String tokenId) {

		SessionToken token = tokens.get(tokenId);
		if (token == null) {
			return SessionStatus.NOT_AUTHENTICATED;
		}

		if (token.getToken().equals(tokenId)) {

			// validate timeout
			long current = System.currentTimeMillis();
			if (token.getTimeout() > 0 && current - token.getLastActive() > token.getTimeout()) {
				return SessionStatus.TIMED_OUT;
			}
		}

		return SessionStatus.AUTHENTICATED;
	}

	public void activate(String userid, SessionToken token) {
		tokens.put(token.getToken(), token);
	}

	public void deactivate(String token) {
		tokens.remove(token);
	}

}
