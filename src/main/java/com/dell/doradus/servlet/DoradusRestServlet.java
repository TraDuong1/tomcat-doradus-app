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
@WebServlet(name = "restservlet", urlPatterns = {"/restservet/*"})
public class DoradusRestServlet extends HttpServlet {

	private static final long serialVersionUID = -7075159164600457298L;
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		PrintWriter out = response.getWriter();
		out.println("getPathInfo: " + request.getPathInfo());
    	out.println("getRequestURI: "+ request.getRequestURI());        	
    	out.println("getMethod: "+ request.getMethod());         	
    	out.println("getQueryString: "+ request.getQueryString());
       	super.doGet(request, response);
	}
    
}
