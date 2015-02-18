package com.dell.doradus.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@WebServlet(name = "restservlet", urlPatterns = {"/restservet/*"})
public class DoradusRestServlet extends HttpServlet {

	private static final long serialVersionUID = -7075159164600457298L;
	
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		PrintWriter out = response.getWriter();
		out.println("getPathInfo: " + request.getPathInfo());
    	out.println("getRequestURI: "+ request.getRequestURI());        	
    	out.println("getMethod: "+ request.getMethod());         	
    	out.println("getQueryString: "+ request.getQueryString());
       	//super.doGet(request, response);
	}
    
}
