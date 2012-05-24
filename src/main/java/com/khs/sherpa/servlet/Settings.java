package com.khs.sherpa.servlet;

import static com.khs.sherpa.util.Defaults.ACTIVITY_LOG;
import static com.khs.sherpa.util.Defaults.DATE_FORMAT;
import static com.khs.sherpa.util.Defaults.DATE_TIME_FORMAT;
import static com.khs.sherpa.util.Defaults.ENDPOINT_AUTHENTICATION;
import static com.khs.sherpa.util.Defaults.SESSION_TIMEOUT;
import static com.khs.sherpa.util.Defaults.SHERPA_ADMIN;

import com.khs.sherpa.json.service.JsonProvider;
import com.khs.sherpa.util.Defaults;

public class Settings {
	
	public String dateFormat = DATE_FORMAT;
	public String dateTimeFormat = DATE_TIME_FORMAT;
	public long sessionTimeout = SESSION_TIMEOUT;
	public boolean endPointAuthentication = ENDPOINT_AUTHENTICATION;
	public String endpointPackage = null;
	public boolean activityLogging = ACTIVITY_LOG;
	public String encode = null;
	public String sherpaAdmin = SHERPA_ADMIN;
	public Class<? extends JsonProvider> jsonProvider = Defaults.JSON_PROVIDER; 
	
}
