package com.dell.doradus.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.dell.doradus.common.Utils;
import com.dell.doradus.core.DoradusServer;
import com.dell.doradus.service.olap.OLAPService;
import com.dell.doradus.service.rest.NotFoundException;
import com.dell.doradus.service.rest.RESTCommand;
import com.dell.doradus.service.rest.RESTService;
import com.dell.doradus.service.rest.RESTServlet;
import com.dell.doradus.service.spider.SpiderService;

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
       	out.println("getRequestURI: "+ request.getRequestURI());        	
       	out.println("getMethod: "+ request.getMethod());         	
       	out.println("query: "+ request.getQueryString());
        Map<String, String> variableMap = new HashMap<String, String>();
        try {
        String query = request.getQueryString();
        RESTCommand cmd = RESTService.instance().matchCommand(request.getMethod(),
                request.getRequestURI(),
                query,
                variableMap);
       		out.println("cmd: "+  cmd.toString());
        } catch(Throwable e) {
        	StringBuilder sb = new StringBuilder();
            for (StackTraceElement element : e.getStackTrace()) {
                sb.append(element.toString());
                sb.append("\n");
            }
        	out.println("exception: "+  sb.toString());
        }
    	//super.doGet(request, response);
    	
   }	
	private static final String[] SERVICES = new String[]{
	        SpiderService.class.getName(),
	        OLAPService.class.getName()
	        //RESTService.class.getName()
	};
	 
	@Override
	public void init(ServletConfig config)  {
		final String[] args = new String[] { "-dbhost", "10.228.23.117", "-dbport", "9042", "-dbuser", "SuperDory", "-dbpassword", "Alpha1"};
				DoradusServer.startEmbedded(args, SERVICES);
	}
}
