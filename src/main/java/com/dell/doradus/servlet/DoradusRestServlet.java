package com.dell.doradus.servlet;

import java.util.Arrays;

import javax.servlet.ServletConfig;
import javax.servlet.annotation.WebServlet;

import com.dell.doradus.core.DoradusServer;
import com.dell.doradus.service.olap.OLAPService;
import com.dell.doradus.service.rest.RESTService;
import com.dell.doradus.service.rest.RESTServlet;
import com.dell.doradus.service.spider.SpiderService;

@WebServlet(name = "DoradusRestServlet", urlPatterns = {"/*"})
public class DoradusRestServlet extends RESTServlet {
	private static final long serialVersionUID = 4815487822036229036L;
	
	private static final String[] SERVICES = new String[]{
	        SpiderService.class.getName(),
	        OLAPService.class.getName(),
	        RESTService.class.getName()
	};
	 
	@Override
	public void init(ServletConfig config)  {
		final String[] args = new String[] { "-dbhost", "10.228.23.117", "-dbport", "9042", "-restport", "29042", "-dbuser", "SuperDory", "-dbpassword", "Alpha1"};
				DoradusServer.startEmbedded(args, SERVICES);
	}
}
