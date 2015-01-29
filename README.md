Sample Tomcat app that uses Doradus Service
===========================================

This is a sample Openshift Tomcat app that connects to and does CRUD operations on an external Doradus. 

Running on OpenShift
----------------------------

Create an account at https://www.openshift.com

Create Tomcat application 

    rhc app create testapp jbossews-2.0 -g dev-small

Add this upstream repo

    cd testapp
    git remote add upstream https://github.com/TraDuong1/tomcat-doradus-app
    git pull -s recursive -X theirs upstream master

Set the Doradus environment variables
   
    rhc env set DORADUS_HOST=<DORADUS_HOST> DORADUS_PORT=<DORADUS_PORT>

Then push the repo upstream

    git push

There are 2 implementations using REST client APIs for testing

    * Using JBoss RestEasy REST client API by adding this maven dependency in your maven project

	<dependency>
	    	<groupId>org.jboss.resteasy</groupId>
	    	<artifactId>resteasy-client</artifactId>
	    	<version>3.0.10.Final</version>
	</dependency>

    Verify
    http://testapp-$yournamespace.$youropenshiftserver/resteasy

    * Using Spring RestTemplate REST client API by adding this maven dependency in your maven project
	
	<dependency>
		<groupId>org.springframework</groupId>
		<artifactId>spring-web</artifactId>
		<version>3.0.2.RELEASE</version>
	</dependency>
	
    Verify
    http://testapp-$yournamespace.$youropenshiftserver/resttemplate

