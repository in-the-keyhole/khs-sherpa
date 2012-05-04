khsSherpa
==========

Remote java object JSON data framework

About
-----
Turn Java application servers into a remote JSON data access mechanism for mobile and HTML 5/Java Script applications. 

This lightweight server side framework allows Java classes contained inside a JEE application sever
to become JSON endpoints that can be consumed via HTTP by native mobile devices or HTML/Javascript clients. 

Many MVC frameworks exist, but khsSherpa is intended to allow access to server side java objects with HTTP/and JSON. It 
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
   
Not using Maven: include following jars in lib class path

    khs-sherpa-x.x.x.jar
	jackson-mapper-asl-1.8.2.jar
	jackson-core-asi-1.8.2.jar
	

Applying to JEE App Server
--------------------------
Add the khsSherpa framework jar to your classpath/maven dependency list and add the 
SherpaServlet to the WEB-INF/web.xml as shown below. 

    <servlet>	
  		<servlet-name>sherpa</servlet-name>
		<display-name>sherpa</display-name>
		<servlet-class>com.khs.sherpa.servlet.SherpaServlet</servlet-class>	
	</servlet>
	<servlet-mapping>
		<servlet-name>sherpa</servlet-name>
		<url-pattern>/sherpa</url-pattern>
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

The @Param annotation is used to specify request parameters for an endpoint method. 

### Get/Post URL to access the TestService.helloWorld() java method is formatted in this manner 

	http://<server>/<webapp>/sherpa?endpoint=TestService&action=helloWorld
	     
### Get/Post URL to access the TestService.add(x,y) java method is formatted in this manner

	http://<server>/<webapp>/sherpa?endpoint=TestService&action=add&x_value=100&y_value=200
  
Configuring khsSherpa
---------------------
Define a sherpa.properties file in your webapp classpath. The only required entry is 
the endpoint.package entry, which tells sherpa where to find Java Endpoints. 


    ##khsSherpa server properties

    #package where endpoints are located
    endpoint.package=com.khs.example.endpoints

Test Fixture
------------
A testing jsp, test-fixture.jsp has been created that will allow testing of khsSherpa endpoints, copy this 
file into your web contents web app directory, access the test-fixture.jsp with a browser and you will be able to invoke @Endpoint 
methods and view JSON results.  

Steps for testing json endpoints from a web app:

	create a java webapp project
	
	copy test-fixure.jsp into web content folder
	
	define a java class and annotate with @Endpoint, see above example
	
	define endpoint package name in sherpa.properties file
	
	start your webapp
	
	open text-fixture.jsp with this url 
	
		http://<server>/<webapp>/test-fixture.jsp
		
	Fill in endpoint simple class name, method name and parameter names and submit	


Authentication
--------------
Endpoints can be configured to require configuration by setting the authentication attribute to true 
on the @Endpoint annotation. Authenticated endpoints can only be invoked if a valid user id and token
id is supplied. Valid token ids are obtained by invoking the framework authenticate action with valid 
credentials. 

An example authentication request URL is shown below. 

	http://<server>/<webapp>/sherpa?endpoint=authenticate&userid=dpitt@keyholesoftware.com&password=password
         
If valid credentials, the following JSON token object will be returned. 

	{
	    "token": "1336103738643",
	    "timeout": 0,
	    "active": true,
	    "userid": "dpitt@keyholesoftware.com",
	    "lastActive": 1336103738643
	}	

The token id and userid values are supplied as parameters to @Endpoint method calls.
Authenticated URL's with token parameters will look like this...

	http://<server>/<webapp>/sherpa?endpoint=TestService&action=helloWord&userid=dpitt@keyholesoftware.com&token=1336103738643

The default authentication mechanism denies all credentials. Since various authentication mechanisms exist,
the framework supplies an interface, com.khs.sherpa.json.service.UserService. Concrete UserService implementations
are registered by defining the entry below in the sherpa.properties file.  

	## Authentication implementation
	user.service = com.example.LDAPUserService

Session Timeout
---------------

An authenticated user token will also define a timeout period in milliseconds. 0 indicates a session will never timeout
For timeout values greater than zero, the framework requires a new authentication token to be obtained through the 
authentication mechanism in order to continue to access an authenticated end point. Session timeouts can be set with 
the sherpa.properties file entry below. 

	## Session timeout (ms), default is 0 
	session.timeout=900000 

   

  
  