package com.khs.sherpa;

import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

import com.khs.sherpa.json.service.ActivityService;
import com.khs.sherpa.json.service.SessionTokenService;
import com.khs.sherpa.json.service.UserService;
import com.khs.sherpa.parser.ParamParser;

public class SherpaContext {

	private static final String SHERPA_APPLICATION_CONTEXT_ATTRIBUTE = SherpaContext.class.getName() + ".CONTEXT";
	
	private SherpaSettings sherpaSettings;
	
	// Sherpa Services
	private ActivityService activityService;
	private UserService userService;
	private SessionTokenService sessionTokenService;
	
	private List<ParamParser<?>> parser;
	
	public final List<ParamParser<?>> getParser() {
		return parser;
	}

	public void setParser(List<ParamParser<?>> parser) {
		this.parser = parser;
	}

	public SherpaSettings getSherpaSettings() {
		return sherpaSettings;
	}

	public ActivityService getActivityService() {
		if(activityService == null) {
			activityService = getSherpaSettings().activityService();
		}
		return activityService;
	}

	public UserService getUserService() {
		if(userService == null) {
			userService = getSherpaSettings().userService();
		}
		return userService;
	}

	public SessionTokenService getSessionTokenService() {
		if(sessionTokenService == null) {
			sessionTokenService = getSherpaSettings().tokenService();
		}
		return sessionTokenService;
	}

	public void setSherpaSettings(SherpaSettings sherpaSettings) {
		this.sherpaSettings = sherpaSettings;
	}

	public SherpaContext(ServletContext servletContext) {
		servletContext.setAttribute(SHERPA_APPLICATION_CONTEXT_ATTRIBUTE, this);
	}
	
	public static final SherpaContext getSherpaContext(ServletContext servletContext) {
		return (SherpaContext) servletContext.getAttribute(SHERPA_APPLICATION_CONTEXT_ATTRIBUTE);
	}
}
