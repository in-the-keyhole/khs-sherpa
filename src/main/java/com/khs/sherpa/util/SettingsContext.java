package com.khs.sherpa.util;

import com.khs.sherpa.servlet.Settings;

public class SettingsContext {

	private static Settings settings;
	
	public void setSettings(Settings settings) {
		SettingsContext.settings = settings;
	}
	
	public static Settings getSettings() {
		return settings;
	}
	
}
