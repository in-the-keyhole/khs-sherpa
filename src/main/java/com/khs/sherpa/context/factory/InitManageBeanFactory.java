package com.khs.sherpa.context.factory;

import javax.servlet.ServletContext;

import com.khs.sherpa.SherpaSettings;

public interface InitManageBeanFactory {

	public void loadManagedBeans(String path);
	public void init(SherpaSettings settings, ServletContext context);
	
}
