package com.dell.doradus.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.ClientRequestFilter;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

import org.springframework.http.MediaType;

/*
 * Example to invoke Doradus Rest APIs using RestEasy
 * http://docs.jboss.org/resteasy/docs/2.0.0.GA/userguide/html/RESTEasy_Client_Framework.html
 */
@WebServlet(name = "resteasy", urlPatterns = {"/resteasy"})
public class DoradusUsingRestEasyServlet extends HttpServlet {

	private static final long serialVersionUID = -7075159164600457298L;
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        handleCmd(response);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        handleCmd(response);
    }
    
    protected void handleCmd(HttpServletResponse response) throws IOException, ServletException {
        PrintWriter out = response.getWriter();
        out.println("Doradus DB outside OpenShift: ");
        out.println("DORADUS_HOST: " + System.getenv("DORADUS_HOST"));
        out.println("DORADUS_PORT: " + System.getenv("DORADUS_PORT"));
        
		String doradusHost = System.getenv("DORADUS_HOST");
		String doradusPort = System.getenv("DORADUS_PORT");
		String authenticationString = "U3VwZXJEb3J5OkFscGhhMQ==";
		String tenant = "Hello" + System.currentTimeMillis();		
		String application = "TestApplication";
		String table = "Person";
			
		//CRUD operations
		out.println("\ncalling createNewTenant service for tenant: " + tenant);
		int statusCode = createNewTenant(doradusHost, doradusPort, authenticationString, tenant); 	
		out.println("status code: " + statusCode +"\n");

		out.println("calling createNewApplication service for tenant: " + tenant +", application: " + application +", table: " + table);
		statusCode = createNewApplicationForTenant(doradusHost, doradusPort, authenticationString, tenant, application, table);  
		out.println("status code: " + statusCode +"\n");
		
		out.println("calling createDataForApplication service for tenant: " + tenant +", application: " + application +", table: " + table);		
		createDataForApplication(doradusHost, doradusPort, authenticationString, tenant, application, table);
		out.println("status code: " + statusCode +"\n");

		out.println("calling retrieveDataForApplication service for tenant: " + tenant +", application: " + application +", table: " + table);				
		String result = retrieveDataForApplication(doradusHost, doradusPort, authenticationString, tenant, application, table);
		out.println("status code: " + statusCode );
		out.println("result: " + result +"\n");
		
		out.println("calling updateDataForApplication service for tenant: " + tenant +", application: " + application +", table: " + table);						
		updateDataForApplication(doradusHost, doradusPort, authenticationString, tenant, application, table);
		out.println("status code: " + statusCode +"\n");

		out.println("calling deleteDataForApplication service for tenant: " + tenant +", application: " + application +", table: " + table);								
		deleteDataForApplication(doradusHost, doradusPort, authenticationString, tenant, application, table);
		out.println("status code: " + statusCode +"\n");
	}


	private String retrieveDataForApplication(String doradusHost,
			String doradusPort, String authenticationString, String tenant, String application, String table) {
			
		String serviceURL = "http://" + doradusHost + ":" + doradusPort + "/" + application + "/" + table + "/_query?q=*&tenant="+tenant;
		return getDataUsingRestEasy(serviceURL, authenticationString, MediaType.APPLICATION_JSON, String.class);		
	}


	private int createNewApplicationForTenant(String doradusHost, String doradusPort, String authenticationString, String tenant, String application, String table) {
		String NEWAPPLICATION_XML =
			        "<application name='" + application +"'>" +
			            "<key>Test1</key>" +
			            "<options>" +
			                "<option name='StorageService'>SpiderService</option>" +
			            "</options>" +
			            "<tables>" +
			                "<table name='" + table + "'>" +
			                    "<fields>" +
			                        "<field name='Name' type='text'/>" +
		                        "<field name='Age' type='integer'/>" +
			                    "</fields>" +
			                "</table>" +
			            "</tables>" +
			        "</application>";
		String serviceURL = "http://" + doradusHost + ":" + doradusPort + "/_applications?tenant="+tenant;
		return postDataUsingRestEasy(serviceURL, authenticationString, NEWAPPLICATION_XML, MediaType.TEXT_XML, null);
	}

	private int createDataForApplication(String doradusHost,
			String doradusPort, String authenticationString, String tenant, String application, String table) {
		String DATA_XML = (
			       "{'batch': {" +
			               "'docs': [" +
			                   "{'doc': {" +
			                       "'_ID': 'Person1'," +
			                       "'Name': 'John'," +
			                       "'Age': 24" +
			                   "}}," +
			                   "{'doc': {" +
			                       "'_ID': 'Person2'," +
			                       "'Name': 'Mary'," +
			                       "'Age': 24" +
			                   "}}," +
			               "]" +
			           "}}").replace('\'', '"');
		String serviceURL = "http://" + doradusHost + ":" + doradusPort + "/" + application + "/" + table + "?tenant="+tenant;
		return postDataUsingRestEasy(serviceURL, authenticationString, DATA_XML, MediaType.APPLICATION_JSON, null);				
	}
	
	private int updateDataForApplication(String doradusHost,
			String doradusPort, String authenticationString, String tenant,
			String application, String table) {
		String DATA_XML = (
			       "{'batch': {" +
			               "'docs': [" +
			                   "{'doc': {" +
			                       "'_ID': 'Person1'," +
			                       "'Name': 'Johnny'," +
			                       "'Age': 24" +
			                   "}}," +
			                   "{'doc': {" +
			                       "'_ID': 'Person2'," +
			                       "'Name': 'Mary'," +
			                       "'Age': 34" +
			                   "}}," +
			               "]" +
			           "}}").replace('\'', '"');
		String serviceURL = "http://" + doradusHost + ":" + doradusPort + "/" + application + "/" + table + "?tenant="+tenant;
		return postDataUsingRestEasy(serviceURL, authenticationString, DATA_XML, MediaType.APPLICATION_JSON, null);	
		
	}	
	
	private String deleteDataForApplication(String doradusHost,
			String doradusPort, String authenticationString, String tenant,
			String application, String table) {
		String DATA_XML = (
			       "{'batch': {" +
			               "'docs': [" +
			                   "{'doc': {" +
			                       "'_ID': 'Person2'" +
			                   "}}," +
			               "]" +
			           "}}").replace('\'', '"');
		String serviceURL = "http://" + doradusHost + ":" + doradusPort + "/" + application + "/" + table + "?tenant="+tenant;
		return deleteDataUsingRestEasy(serviceURL, authenticationString, DATA_XML, MediaType.APPLICATION_JSON, null);	
		
	}	
	private int createNewTenant(String doradusHost, String doradusPort, String authenticationString, String tenant) {
	
		String NEWTENANT_XML = "<tenant name='" + tenant + "'>" +
							   "<users>" +
							   "<user name='Katniss' password='Everdeen'/>" +
							   "</users>" +
							   "</tenant>";
		String serviceURL = "http://" + doradusHost + ":" + doradusPort +"/_tenants";
		return postDataUsingRestEasy(serviceURL, authenticationString, NEWTENANT_XML, MediaType.TEXT_XML, String.class);
	}

	private <T> int postDataUsingRestEasy(String serviceURL, final String authenticationString, final T data, final MediaType contentType, final Class<T> responseType) {		
		
		//create a Rest Client instance with request headers
		Client client = ClientBuilder.newClient().register(new ClientRequestFilter() {
			@Override
			public void filter(
					javax.ws.rs.client.ClientRequestContext requestContext)
					throws IOException {
				MultivaluedMap<String, Object> requestHeaders = requestContext.getHeaders();
				requestHeaders.add("Authorization", "Basic " + authenticationString);
				requestHeaders.add("Content-Type", contentType.toString());					
			}
	
			
		});

		Entity<T> entity = Entity.entity(data, javax.ws.rs.core.MediaType.valueOf(contentType.toString()));
		    
		//invoke Doradus Post API
		Response response = client.target(serviceURL).request().post(entity);
		
		return verifyStatus(response);				
	}


	
	private <T> T getDataUsingRestEasy(final String serviceURL, final String authenticationString, final MediaType contentType, final Class<T> responseType) {
		
		//create a Rest Client instance with request headers
		Client client = ClientBuilder.newClient().register(new ClientRequestFilter() {
			@Override
			public void filter(
					javax.ws.rs.client.ClientRequestContext requestContext)
					throws IOException {
				MultivaluedMap<String, Object> requestHeaders = requestContext.getHeaders();
				requestHeaders.add("Authorization", "Basic " + authenticationString);
				requestHeaders.add("Content-Type", contentType.toString());					
			}
	
			
		});
		
		//invoke Doradus GET API
		Response response = client.target(serviceURL).request().get();
		
		verifyStatus(response);	
		
		//retrieve response data 
		T result = response.readEntity(responseType);
		response.close();
		
		return result;
	}
	
	private <T> T deleteDataUsingRestEasy(final String serviceURL, final String authenticationString, final T data, final MediaType contentType, Class<T> responseType) {
		
		//create a Rest Client instance with request headers
		Client client = ClientBuilder.newClient().register(new ClientRequestFilter() {
			@Override
			public void filter(
					javax.ws.rs.client.ClientRequestContext requestContext)
					throws IOException {
				MultivaluedMap<String, Object> requestHeaders = requestContext.getHeaders();
				requestHeaders.add("Authorization", "Basic " + authenticationString);
				requestHeaders.add("Content-Type", contentType.toString());	
				requestContext.setEntity(data);
			}
	
			
		});
		
		//invoke Doradus DELETE API
		Response response = client.target(serviceURL).request().delete();
		verifyStatus(response);		
		
		//retrieve response data 
		T result = response.readEntity(responseType);
		response.close();
		
		return result;
	}
	
	private int verifyStatus(Response response) {
		int status = response.getStatus();
		if (status != 200 && status != 201) {
			throw new RuntimeException("Failed : HTTP error code : " + response.getStatus());
		}
		return status;
	}
  
}
