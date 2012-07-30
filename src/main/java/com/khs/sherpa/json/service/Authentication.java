package com.khs.sherpa.json.service;


public class Authentication {

//	private static final Logger LOG = Logger.getLogger(SherpaServlet.class.getName());
	
	private UserService userService;
	private SessionTokenService tokenService;
	private ActivityService activityService;
	
	public SessionToken authenticate(String userid, String password) {
		String[] roles = this.authenticateUser(userid, password);
		SessionToken token = this.newToken(userid, roles);
		this.activityService.logActivity(token.getUserid(), "authenticated");
		tokenService.activate(userid, token);
		return token;
	}
	
	protected final String[] authenticateUser(String userid, String password) {
		return userService.authenticate(userid, password);
	}
	
	protected final SessionToken newToken(String userid, String[] roles) {
		SessionToken token =  new SessionToken();
		token.setRoles(roles);
		token.setToken(tokenService.newToken(userid));
		token.setTimeout(0);
		token.setActive(true);
		token.setUserid(userid);
		token.setLastActive(System.currentTimeMillis());
		return token;
	}
	
}
