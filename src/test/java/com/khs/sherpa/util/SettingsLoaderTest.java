package com.khs.sherpa.util;

import junit.framework.Assert;

import org.junit.Test;



public class SettingsLoaderTest {

	@Test
	public void testSettingsLoaderWithBreakInPath(){
		SettingsLoader loader = new SettingsLoader();
		
		String testPath = loader.decodeInputConfigFilePath("Program Files");
		
		Assert.assertTrue(testPath!=null);
		Assert.assertTrue (testPath.equals("Program Files"));
	}

	@Test
	public void testSetingsLoaderWithNoBreakInPath (){
		SettingsLoader loader = new SettingsLoader();
		
		String testPath = loader.decodeInputConfigFilePath("ProgramFiles");
		
		Assert.assertTrue(testPath!=null);
		Assert.assertTrue (testPath.equals("ProgramFiles"));
	}
	
}
