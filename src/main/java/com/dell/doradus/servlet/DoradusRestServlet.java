package com.dell.doradus.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.dell.doradus.service.rest.RESTServlet;

@WebServlet(name = "DoradusRestServlet", urlPatterns = {"/*"})
public class DoradusRestServlet extends RESTServlet {
	private static final long serialVersionUID = 4815487822036229036L;
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		PrintWriter out = response.getWriter();
		out.println(request.getPathInfo());
		//super.doPost(request, response);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		PrintWriter out = response.getWriter();
    	out.println(request.getPathInfo());
    	//super.doGet(request, response);
    	
   }	
//	private static final String[] SERVICES = new String[]{
//	        SpiderService.class.getName(),
//	        OLAPService.class.getName()
//	        //RESTService.class.getName()
//	};
//	 
//	@Override
//	public void init(ServletConfig config)  {
//		final String[] args = new String[] { "-dbhost", "10.228.23.117", "-dbport", "9042", "-restport", "57631", "-dbuser", "SuperDory", "-dbpassword", "Alpha1"};
//				DoradusServer.startEmbedded(args, SERVICES);
//	}
}
