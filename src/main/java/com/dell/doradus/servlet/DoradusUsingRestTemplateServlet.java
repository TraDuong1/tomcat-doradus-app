package com.dell.doradus.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

/*
 * Example to invoke Doradus Rest APIs using RestEasy
 * http://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/web/client/RestTemplate.html
 */
@WebServlet(name = "resttemplate", urlPatterns = {"/resttemplate"})
public class DoradusUsingRestTemplateServlet extends HttpServlet {

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
		return getData(serviceURL, authenticationString, MediaType.APPLICATION_JSON, String.class);		
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
		return postData(serviceURL, authenticationString, NEWAPPLICATION_XML, MediaType.TEXT_XML, null);
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
		return postData(serviceURL, authenticationString, DATA_XML, MediaType.APPLICATION_JSON, null);				
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
		return postData(serviceURL, authenticationString, DATA_XML, MediaType.APPLICATION_JSON, null);	
		
	}	
	private int deleteDataForApplication(String doradusHost,
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
		return deleteData(serviceURL, authenticationString, DATA_XML, MediaType.APPLICATION_JSON, null);	
		
	}	
	private int createNewTenant(String doradusHost, String doradusPort, String authenticationString, String tenant) {
	
		String NEWTENANT_XML = "<tenant name='" + tenant + "'>" +
							   "<users>" +
							   "<user name='Katniss' password='Everdeen'/>" +
							   "</users>" +
							   "</tenant>";
		String serviceURL = "http://" + doradusHost + ":" + doradusPort +"/_tenants";		
		return postData(serviceURL, authenticationString, NEWTENANT_XML, MediaType.TEXT_XML, String.class);
	}

	/**
	 * Post to Doradus
	 * @param serviceURL
	 * @param authenticationString
	 * @param data
	 * @param contentType
	 * @param responseType
	 * @return
	 */
	private <T> int postData(String serviceURL, String authenticationString, T data, MediaType contentType, Class<T> responseType) {
		RestTemplate restTemplate = new RestTemplate();
		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set("Authorization", "Basic " + authenticationString);
		requestHeaders.setContentType(contentType);
		HttpEntity<T> payload = new HttpEntity<T>(data, requestHeaders);  
		ResponseEntity<T> result = restTemplate.exchange(serviceURL, HttpMethod.POST, payload, responseType);
		return result.getStatusCode().value();
	}
	
	/**
	 * Query Doradus
	 * @param serviceURL
	 * @param authenticationString
	 * @param contentType
	 * @param responseType
	 * @return
	 */
	private <T> T getData(String serviceURL, String authenticationString, MediaType contentType, Class<T> responseType) {
		RestTemplate restTemplate = new RestTemplate();
		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set("Authorization", "Basic " + authenticationString);
		requestHeaders.setContentType(contentType);
		HttpEntity<T> payload = new HttpEntity<T>(requestHeaders);  
		ResponseEntity<T> result = restTemplate.exchange(serviceURL, HttpMethod.GET, payload, responseType);
		return result.getBody();
	}
	
	/**
	 * Delete object from Doradus
	 * @param serviceURL
	 * @param authenticationString
	 * @param data
	 * @param contentType
	 * @param responseType
	 * @return
	 */
	private <T> int deleteData(String serviceURL, String authenticationString, T data, MediaType contentType, Class<T> responseType) {
		RestTemplate restTemplate = new RestTemplate();
		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set("Authorization", "Basic " + authenticationString);
			requestHeaders.setContentType(contentType);
			HttpEntity<T> payload = new HttpEntity<T>(data, requestHeaders);  
			ResponseEntity<T> result = restTemplate.exchange(serviceURL, HttpMethod.DELETE, payload, responseType);  
			return result.getStatusCode().value();
	}
  
}
