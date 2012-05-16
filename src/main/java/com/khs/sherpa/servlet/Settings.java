package com.khs.sherpa.servlet;

import static com.khs.sherpa.util.Defaults.*;

public class Settings {
	
	public String dateFormat = DATE_FORMAT;
	public String dateTimeFormat = DATE_TIME_FORMAT;
	public long sessionTimeout = SESSION_TIMEOUT;
	public boolean endPointAuthentication = ENDPOINT_AUTHENTICATION;
	public String endpointPackage = null;
	public boolean activityLogging = ACTIVITY_LOG;
	
}
