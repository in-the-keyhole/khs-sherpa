khs-sherpa
==========

Remote java object JSON data framework

About
-----
Turn Java application servers into a remote JSON data access mechanism for mobile and HTML 5/Java Script applications. 

This lightweight server side framework allows Java classes contained inside a JEE application sever
to become JSON endpoints that can be consumed via HTTP by native mobile devices or HTML/Javascript clients. 

Many MVC frameworks exist, but Sherpa is intended to allow access to server side java objects with HTTP/and JSON. It 
also, provides session support for client applications that exist outside of a browser.

Features  
--------
 * Annotation Based Configuration
 * Authentication
 * Session Support 
 * Plug-gable User Activity Logging
 * Works with any JEE application server

Getting Started
---------------
To build it clone then use Maven:

    $ git clone ...
	$ cd khs-sherpa
	$ mvn install

Using Maven: add this dependency in your 'pom.xml' 

    <dependency>
   	 <groupId>com.keyholesoftware</groupId>
   	 <artifactId>khs-sherpa</artifactId>
   	<version>1.0</version>
    </dependency>
   

Applying to JEE App Server
--------------------------
Add the khs-sherpa framework jar to your classpath/maven dependency list and add the 
SherpaServlet to the WEB-INF/web.xml as shown below. 

    <servlet>	
  		<servlet-name>SherpaServlet</servlet-name>
		<display-name>SherpaServlet</display-name>
		<servlet-class>com.khs.sherpa.servlet.SherpaServlet</servlet-class>	
	</servlet>
	<servlet-mapping>
		<servlet-name>SherpaServlet</servlet-name>
		<url-pattern>/SherpaServlet</url-pattern>
	</servlet-mapping>

Endpoint Example
----------------
JSON endpoints are defined by annotation a Java class with the @Endpoint annotation. 
The java endpoint below has two methods that can be called remotely. 

    @Endpoint(authenticated = true)
	public class TestService {
	
	// hello world  method
	public Result helloWorld() {
		return new Result("Hello World");
	}
	
	// add two numbers method
	public Result add(@Param(name="x_value") double x, @Param(name="y_value") double y) {
		return new Result(x + y);
	}
		
		class Result {	
			public Result(Object o) {
			result = o;
			}
			public Object result;		
		}
	}

The @Param annotation is used to specify request paramters for an endpoint method. 

# URL to access the TestService.helloWorld() java method is formatted in this manner 

	http://<server>/<webapp>/SherpaServlet?endpoint=TestService&action=helloWorld
	     
# URL to access the TestService.add(x,y) java method is formatted in this manner

	http://<server>/<webapp>/SherpaServlet?endpoint=TestService&action=add&x_value=100&y_value=200

  
Configuring Sherpa
------------------
Define a sherpa.properties file in your webapps classpath. The only required entry is 
the endpoint.package entry, which tells sherpa where to find Java Endpoints. 


    ##Sherpa server properties

    #package where endpoints are located
    endpoint.package=com.khs.example.endpoints

Test Fixture
------------
A testing jsp, test-fixture.jsp has been created that will allow testing of sherpa endpoints, copy this 
file into your web contents web app directory, access the test-fixture.jsp with a browser and you will be able to invoke @Endpoing 
methods and view JSON results.  












   

  
  