package com.khs.sherpa.endpoint;

import javax.annotation.security.RolesAllowed;

import com.khs.sherpa.annotation.Endpoint;

@Endpoint(value = "sherpa", authenticated = true)
public class SherpaEndpoint {

	@RolesAllowed("SHERPA_ADMIN")
	public void sessions() {
		
	}
	
}
