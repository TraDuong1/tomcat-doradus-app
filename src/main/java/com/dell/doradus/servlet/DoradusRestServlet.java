package com.dell.doradus.servlet;

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dell.doradus.core.DoradusServer;
import com.dell.doradus.service.olap.OLAPService;
import com.dell.doradus.service.rest.RESTServlet;
import com.dell.doradus.service.spider.SpiderService;

/**
 * Servlet implementation class DoradusRestServlet
 */
@WebServlet(name = "DoradusRestServlet", urlPatterns = {"/doradus/*"})
public class DoradusRestServlet extends RESTServlet {
	private static final long serialVersionUID = 1L;
	 private final Logger logger = LoggerFactory.getLogger(getClass().getSimpleName());
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public DoradusRestServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	logger.debug("getPathInfo: " + request.getPathInfo());
    	logger.debug("getRequestURI: "+ request.getRequestURI());        	
    	logger.debug("getMethod: "+ request.getMethod());         	
    	logger.debug("getQueryString: "+ request.getQueryString());
       	super.doGet(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}
	
	@Override
	public void init(ServletConfig config)  {

		final String[] args = new String[] { "-dbhost", "localhost", "-dbport", "9160"};
				DoradusServer.startEmbedded(args, SERVICES);
		logger.debug("Doradus server started... ");	
	}
	
	private static final String[] SERVICES = new String[]{
        SpiderService.class.getName(),
        OLAPService.class.getName()
        //RESTService.class.getName()
};
}
