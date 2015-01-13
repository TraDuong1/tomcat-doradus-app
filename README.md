JPA in OpenShift Quickstart
===============================

This is a Red Hat OpenShift sample containing a Tomcat that 
connects to Doradus as a Service.

Running on OpenShift
----------------------------

Create an account at https://www.openshift.com

Create Tomcat application 

    rhc app create myapp jbossews-2.0

Add this upstream repo

    cd myapp
    git remote add upstream https://github.com/TraDuong1/jpa-openshift-quickstart
    git pull -s recursive -X theirs upstream master

Modify the persistence.xml with the correct DB credentials for each DB from

    rhc show-app myapp

Then push the repo upstream

    git push

That's it, you can now checkout your application at:

    http://myapp-$yournamespace.$youropenshiftserver/testconnection



