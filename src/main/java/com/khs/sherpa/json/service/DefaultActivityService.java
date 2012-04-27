package com.khs.sherpa.json.service;

import java.util.logging.Logger;

import com.khs.sherpa.servlet.SherpaServlet;

public class DefaultActivityService implements ActivityService {
	
	Logger LOG = Logger.getLogger(DefaultActivityService.class.getName());

	public void logActivity(String userid, String activity) {
	
		LOG.info("sherpa->"+userid+":"+activity);
		
	}

}
