JPA in OpenShift Quickstart
===============================

This is a Red Hat OpenShift sample containing a Tomcat that 
connects to Doradus as a Service.

Running on OpenShift
----------------------------

Create an account at https://www.openshift.com

Create Tomcat application 

    rhc app create testapp jbossews-2.0

Add this upstream repo

    cd testapp
    git remote add upstream https://github.com/TraDuong1/tomcat-doradus-app
    git pull -s recursive -X theirs upstream master

Set the Doradus environment variables
   
    rhc env set DORADUS_HOST=<DORADUS_HOST> DORADUS_PORT=<DORADUS_PORT>

Then push the repo upstream

    git push

That's it, you can now checkout your application at:

    http://testapp-$yournamespace.$youropenshiftserver/testconnection



