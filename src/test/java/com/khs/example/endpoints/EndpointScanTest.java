package com.khs.example.endpoints;

import java.io.File;
import java.lang.reflect.Method;

import junit.framework.TestCase;

import org.junit.Test;

import com.khs.sherpa.servlet.EndpointScanner;

public class EndpointScanTest {

	@Test
	public void test() {
		//fail("Not yet implemented");
		
		System.out.println(System.getProperty("java.class.path"));
		EndpointScanner scanner = new EndpointScanner();
		scanner.classPathScan("com.khs.example.endpoints");
		//scanner.
		//scanDir("/Users/dpitt/git/khs-sherpa/target/test-classes");	
		
	}
	


private void scanDir(String dir)
{
File root=new File(dir);
String [] files=root.list();
int i=0;
File file;

for(i=0;i<files.length;i++) {
file=new File(dir,files[i]);

if(file.isDirectory()==true) {
dir+=File.separator+files[i];
scanDir(dir);
continue;
}

if(files[i].endsWith(".class")==true)
scanClass(dir,

files[i].substring(0,files[i].lastIndexOf('.')));
}
}

private void scanClass(String dir, String klass)
{
	Class bClass;
	Class rClass;
	Package bPackage;
	Method method;
	
	
	System.out.println(dir+"-"+klass);

try {
	rClass=Class.forName(getrClassName(dir,klass));
	
	bClass=rClass.getSuperclass();
	bPackage=bClass.getPackage();


if(bClass.getName().equals("sdc.unittest.TestCase" )==true) {
  
	method=rClass.getMethod("init",null);

//TestCases[MaxTests++]=(TestCase)
//method.invoke("init",
//null);
//}
   } } catch(Exception e) {
//e.printStackTrace();
  //System.exit(1);
//}
	}
}

private String getrClassName(String dir,String klass)
{
return
	(dir.substring(dir.lastIndexOf(File.separator+File .separator)
	+2)).replace(File.separatorChar,'.')+"."+klass;
}


private TestCase [] TestCases = new TestCase[CaseLimit];
private int MaxTests;
private static int CaseLimit=1024;
}





