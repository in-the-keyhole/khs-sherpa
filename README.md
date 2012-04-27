khs-sherpa
==========

Remote java object JSON data framework

About
=====
Turn Java application servers into a remote JSON data access mechanism for mobile and HTML 5/Java Script applications. 

This lightweight server side framework allows Java classes contained inside a JEE application sever
to become JSON endpoints that can be consumed via HTTP by native mobile devices or HTML/Javascript clients. 

Many MVC frameworks exist, but Sherpa is intended to allow access to server side java objects with HTTP/and JSON. It 
also, provides session support for client applications that exist outside of a browser.

Features include 
================
Annotation Based Configuration
Authentication
Session Support 
Plug-gable User Activity Logging
Works with any JEE application server

Getting Started
================
To build it clone then use Maven:

  $ git clone ...
	$ cd khs-sherpa
	$ mvn install

Using Maven: add this dependency in your 'pom.xml' 

   <dependency>
   	<groupId>com.keyholesoftware</groupId>
   	<artifactId>khs-sherpa</artifactId>
   	<version>1.0-SNAPSHOT</version>
   </dependency>
   
Note: This dependency is publicly available via NEXUS Central OSS repository   
   

  
  