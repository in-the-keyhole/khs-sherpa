package com.khs.sherpa.events;

import com.khs.sherpa.context.ApplicationContext;

public interface MethodEvent extends SherpaEvent {

	public void before(ApplicationContext applicationContext);
	
	public void after(ApplicationContext applicationContext);
	
}
