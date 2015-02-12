package com.dell.doradus.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.cassandra.utils.Pair;

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
		doGet(request, response);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		PrintWriter out = response.getWriter();
    	out.println(request.getPathInfo());
        Map<String, String> variableMap = new HashMap<String, String>();
        String query = extractQueryParam(request, variableMap);
        RESTCommand cmd = RESTService.instance().matchCommand(request.getMethod(),
                                                              request.getRequestURI(),
                                                              query,
                                                              variableMap);
        if (cmd == null) {
            throw new NotFoundException("Request does not match a known URI: " + request.getRequestURL());
        }
        Utils.require(cmd != null, "Request does not match a known URI: " + request.getRequestURL());
        out.println("Command: {}" + cmd.toString());
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
	
	   private String extractQueryParam(HttpServletRequest request, Map<String, String> restParams) {
	        String query = request.getQueryString();
	        if (Utils.isEmpty(query)) {
	            return "";
	        }
	        StringBuilder buffer = new StringBuilder(query);
	        
	        // Split query component into decoded, &-separate components.
	        String[] parts = Utils.splitURIQuery(buffer.toString());
	        List<Pair<String, String>> unusedList = new ArrayList<Pair<String,String>>();
	        boolean bRewrite = false;
	        for (String part : parts) {
	            Pair<String, String> param = extractParam(part);
	            switch (param.left.toLowerCase()) {
	            case "api":
	                bRewrite = true;
	                restParams.put("api", param.right);
	                break;
	            case "format":
	                bRewrite = true;
	                if (param.right.equalsIgnoreCase("xml")) {
	                    restParams.put("format", "text/xml");
	                } else if (param.right.equalsIgnoreCase("json")) {
	                    restParams.put("format", "application/json");
	                }
	                break;
	            case "tenant":
	                bRewrite = true;
	                restParams.put("tenant", param.right);
	                break;
	            default:
	                unusedList.add(param);
	            }
	        }
	        
	        // If we extracted any fixed params, rewrite the query parameter.
	        if (bRewrite) {
	            buffer.setLength(0);
	            for (Pair<String, String> pair : unusedList) {
	                if (buffer.length() > 0) {
	                    buffer.append("&");
	                }
	                buffer.append(Utils.urlEncode(pair.left));
	                if (pair.right != null) {
	                    buffer.append("=");
	                    buffer.append(Utils.urlEncode(pair.right));
	                }
	            }
	        }
	        return buffer.toString();
	    }   // extractQueryParam

	    // Split the given k=v (or just k) param into a Pair object.
	    private Pair<String, String> extractParam(String part) {
	        int eqInx = part.indexOf('=');
	        String paramName;
	        String paramValue;
	        if (eqInx < 0) {
	            paramName = part;
	            paramValue = null;
	        } else {
	            paramName = part.substring(0, eqInx);
	            paramValue = part.substring(eqInx + 1);
	        }
	        return Pair.create(paramName, paramValue);
	    }   // extractParam	
}
