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
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.cassandra.utils.Pair;

import com.dell.doradus.common.HttpCode;
import com.dell.doradus.common.HttpDefs;
import com.dell.doradus.common.RESTResponse;
import com.dell.doradus.common.Utils;
import com.dell.doradus.core.DoradusServer;
import com.dell.doradus.core.ServerConfig;
import com.dell.doradus.service.db.DBNotAvailableException;
import com.dell.doradus.service.db.DuplicateException;
import com.dell.doradus.service.db.Tenant;
import com.dell.doradus.service.db.UnauthorizedException;
import com.dell.doradus.service.olap.OLAPService;
import com.dell.doradus.service.rest.NotFoundException;
import com.dell.doradus.service.rest.RESTCallback;
import com.dell.doradus.service.rest.RESTCommand;
import com.dell.doradus.service.rest.RESTRequest;
import com.dell.doradus.service.rest.RESTService;
import com.dell.doradus.service.spider.SpiderService;
import com.dell.doradus.service.tenant.TenantService;

@WebServlet(name = "DoradusRestServlet", urlPatterns = {"/*"})
public class DoradusRestServlet extends HttpServlet {
	private static final long serialVersionUID = 4815487822036229036L;
//	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//		PrintWriter out = response.getWriter();
//		out.println(request.getPathInfo());
//		//super.doPost(request, response);
//    }
	   @Override
	    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
       	PrintWriter out = response.getWriter();

		   try {
	            //long startNano = System.nanoTime();
	            //RESTService.instance().onNewrequest();
	
	            // Extract the query component of the request, if any, but move api, format,
	            // and tenant parameters to the variable map if present. This removes them
	            // from the URI pattern for matching.
	            Map<String, String> variableMap = new HashMap<String, String>();
	            String query = extractQueryParam(request, variableMap);
	        	out.println("getRequestURI: "+ request.getRequestURI());        
	        	out.println("getMethod: "+ request.getMethod());         	
	        	out.println("query: "+ request.getQueryString());	            
	            
	            RESTCommand cmd = RESTService.instance().matchCommand(request.getMethod(),
	                                                                  request.getRequestURI(),
	                                                                  query,
	                                                                  variableMap);
	            if (cmd == null) {
	                throw new NotFoundException("Request does not match a known URI: " + request.getRequestURL());
	            }
	            Utils.require(cmd != null, "Request does not match a known URI: " + request.getRequestURL());
	            //m_logger.debug("Command: {}", cmd.toString());
	            
	            Tenant tenant = getTenant(cmd, request, variableMap);
	            RESTRequest restRequest = new RESTRequest(tenant, request, variableMap);
	            RESTCallback callback = cmd.getNewCallback(restRequest);
	            RESTResponse restResponse = callback.invoke();
	            
	            if(restResponse.getCode().getCode() >= 300) {
	                //RESTService.instance().onRequestRejected(restResponse.getCode().toString());
	            } else {
	                //RESTService.instance().onRequestSuccess(startNano);
	            }
	            sendResponse(response, restResponse);
	        } catch (IllegalArgumentException e) {
	            // 400 Bad Request
	            RESTResponse restResponse = new RESTResponse(HttpCode.BAD_REQUEST, e.getMessage());
	            //RESTService.instance().onRequestRejected(restResponse.getCode().toString());
	            sendResponse(response, restResponse);
	        } catch (NotFoundException e) {
	            // 404 Not Found
	            RESTResponse restResponse = new RESTResponse(HttpCode.NOT_FOUND, e.getMessage());
	            //RESTService.instance().onRequestRejected(restResponse.getCode().toString());
	            sendResponse(response, restResponse);
	        } catch (DBNotAvailableException e) {
	            // 503 Service Unavailable
	            RESTResponse restResponse = new RESTResponse(HttpCode.SERVICE_UNAVAILABLE, e.getMessage());
	            //RESTService.instance().onRequestRejected(restResponse.getCode().toString());
	            sendResponse(response, restResponse);
	        } catch (UnauthorizedException e) {
	            // 401 Unauthorized
	            RESTResponse restResponse = new RESTResponse(HttpCode.UNAUTHORIZED, e.getMessage());
	            //RESTService.instance().onRequestRejected(restResponse.getCode().toString());
	            sendResponse(response, restResponse);
	        } catch (DuplicateException e) {
	            // 409 Conflict
	            RESTResponse restResponse = new RESTResponse(HttpCode.CONFLICT, e.getMessage());
	            //RESTService.instance().onRequestRejected(restResponse.getCode().toString());
	            sendResponse(response, restResponse);
	        } catch (Throwable e) {
	            // 500 Internal Error: include a stack trace and report in log.
	            //m_logger.error("Unexpected exception handling request", e);
	            String stackTrace = Utils.getStackTrace(e);
	            out.println("stackTrace: "+  stackTrace);
	            RESTResponse restResponse = new RESTResponse(HttpCode.INTERNAL_ERROR, stackTrace);
	            //RESTService.instance().onRequestFailed(e);
	            sendResponse(response, restResponse);
	        }
	    }    // goGet
	    
	    /**
	     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	     */
	    @Override
	    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	        doGet(request, response);
	    }    // doPost
	    
	    /**
	     * @see HttpServlet#doPut(HttpServletRequest request, HttpServletResponse response)
	     */
	    @Override
	    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	        doGet(request, response);
	    }    // doPut
	    
	    /**
	     * @see HttpServlet#doDelete(HttpServletRequest request, HttpServletResponse response)
	     */
	    @Override
	    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	        doGet(request, response);
	    }    // doDelete
	    
	    //----- Private methods

	    // Decide the Tenant context for this command and multi-tenant configuration options.
	    private Tenant getTenant(RESTCommand cmd, HttpServletRequest request, Map<String, String> variableMap) {
	        Tenant tenant = null;
	        String tenantName = variableMap.get("tenant");
	        String authorizationHeader = request.getHeader("Authorization");
	        if (ServerConfig.getInstance().multitenant_mode) {
	            if (cmd.isSystemCommand()) {
	                tenant = TenantService.instance().validateSystemUser(authorizationHeader);
	            } else {
	                if (Utils.isEmpty(tenantName)) {
	                    Utils.require(!ServerConfig.getInstance().disable_default_keyspace,
	                                  "'tenant' parameter is required for this command");
	                    tenant = TenantService.instance().getDefaultTenant();
	                } else {
	                    tenant = TenantService.instance().validateTenant(tenantName, authorizationHeader);
	                }
	            }
	        } else {
	            tenant = TenantService.instance().getDefaultTenant();
	        }
	        return tenant;
	    }   // getTenant
	    
	    // Send the given response, which includes a response code and optionally a body
	    // and/or additional response headers. If the body is non-empty, we automatically add
	    // the Content-Length and a Content-Type of Text/plain.
	    private void sendResponse(HttpServletResponse servletResponse, RESTResponse restResponse) throws IOException {
	        servletResponse.setStatus(restResponse.getCode().getCode());
	        
	        Map<String, String> responseHeaders = restResponse.getHeaders();
	        if (responseHeaders != null) {
	            for (Map.Entry<String, String> mapEntry : responseHeaders.entrySet()) {
	                if (mapEntry.getKey().equalsIgnoreCase(HttpDefs.CONTENT_TYPE)) {
	                    servletResponse.setContentType(mapEntry.getValue());
	                } else {
	                    servletResponse.setHeader(mapEntry.getKey(), mapEntry.getValue());
	                }
	            }
	        }
	        
	        byte[] bodyBytes = restResponse.getBodyBytes();
	        int bodyLen = bodyBytes == null ? 0 : bodyBytes.length;
	        servletResponse.setContentLength(bodyLen);
	        
	        if (bodyLen > 0 && servletResponse.getContentType() == null) {
	            servletResponse.setContentType("text/plain");
	        }
	        
	        if (bodyLen > 0) {
	            servletResponse.getOutputStream().write(restResponse.getBodyBytes());
	        }
	    }   // sendResponse
	    
	    // Extract and return the query component of the given request, but move "api=x",
	    // "format=y", and "tenant=z", if present, to rest parameters.
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
	    
//    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//		PrintWriter out = response.getWriter();
//    	out.println(request.getPathInfo());
//       	out.println("getRequestURI: "+ request.getRequestURI());        	
//       	out.println("getMethod: "+ request.getMethod());         	
//       	out.println("query: "+ request.getQueryString());
//        Map<String, String> variableMap = new HashMap<String, String>();
//        try {
//        String query = request.getQueryString();
//        RESTCommand cmd = RESTService.instance().matchCommand(request.getMethod(),
//                request.getRequestURI(),
//                query,
//                variableMap);      
//       		out.println("cmd: "+  cmd.toString());
//        } catch(Throwable e) {
//        	StringBuilder sb = new StringBuilder();
//            for (StackTraceElement element : e.getStackTrace()) {
//                sb.append(element.toString());
//                sb.append("\n");
//            }
//          	out.println("exception: "+  e.toString());
//        	out.println("stacktrace: "+  sb.toString());
//        }
//    	//super.doGet(request, response);
//    	
//   }	
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
