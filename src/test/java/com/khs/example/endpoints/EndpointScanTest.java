package com.khs.example.endpoints;


import org.junit.Test;

import com.khs.sherpa.servlet.EndpointScanner;

public class EndpointScanTest {

	@Test
	public void test() {
				
		EndpointScanner scanner = new EndpointScanner();
		scanner.classPathScan("com.khs.example.endpoints");
		
	}
	

}


