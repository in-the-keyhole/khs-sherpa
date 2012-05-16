package com.khs.sherpa.servlet;

/*
 * Copyright 2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


import java.io.File;
import java.net.URL;
import java.util.logging.Logger;
import static com.khs.sherpa.util.Constants.*;
import static com.khs.sherpa.util.Util.*;
import com.khs.sherpa.annotation.Endpoint;

public class EndpointScanner {

Logger LOG = Logger.getLogger(EndpointScanner.class.getName());

public void classPathScan(String pckg) {

	String separator = File.separator;
	URL url = this.getClass().getResource(separator + "sherpa.properties");
	
	if (url == null) {
		url = this.getClass().getResource("sherpa.properties");
	}
	
	if (url == null) {
		url = this.getClass().getClassLoader().getResource("sherpa.properties");
	}
	
	if (url == null) {
		url = this.getClass().getClassLoader().getResource(separator+"sherpa.properties");
	}
	

	if (url == null) {	
		LOG.severe(errmsg("ERROR sherpa.properties not found in classpath, it is required "));
		throw new RuntimeException(SHERPA_NOT_INITIALIZED);
	}  
	
	char c = separator.charAt(0);
	File file = new File(url.getFile());
	String p = file.getAbsolutePath();
	p = p.replace("sherpa.properties","");
	scanPath(p,p+pckg.replace('.', c),pckg.replace('.', c));
	
}
	
public void scanPackage(String pckg)
{	
	String path = System.getProperty("java.class.path");
	String separator = System.getProperty("path.separator");
	String[] paths = path.split(separator);
	for (String p : paths){
		if (p.indexOf(".jar") < 0 ){
			scanPath(p,p+"/"+pckg.replace('.', File.separatorChar),pckg.replace('.', File.separatorChar));
		}
	}
}	
private void scanPath(String classPath,String dir,String pckg) {	
	
	LOG.info(msg("scanning for @Endpoints in "+dir));
	File root=new File(dir);
	String [] files=root.list();
	if (files == null) {return;}
	int i=0;
	File file;

	for(i=0;i<files.length;i++) {
		file=new File(dir,files[i]);		
	
		// if directory scan it...
		if(file.isDirectory()==true) {
			dir+=File.separator+files[i];
				scanPath(classPath,dir,pckg);
				continue;
		} else {
				
				Class<?> clazz = null;
				try {								
					String qualified = file.getAbsolutePath();
					int end = qualified.indexOf(pckg);
					String className = qualified.substring(end);
					clazz = Class.forName(className.replace(File.separatorChar,'.').replace(".class",""));
					// check for endpoint...
					if (clazz.isAnnotationPresent(Endpoint.class)) {
						LOG.info(msg("@Endpoint found "+clazz.getName()));
						ReflectionCache.put(clazz.getName(),clazz);		
					}
				
				} catch (ClassNotFoundException e) {
					LOG.severe(errmsg("ERROR loading @Endpoint "+file.getName()));
					throw new RuntimeException(SHERPA_NOT_INITIALIZED+e);
				
				}	
		}
     }
   }
		

}
