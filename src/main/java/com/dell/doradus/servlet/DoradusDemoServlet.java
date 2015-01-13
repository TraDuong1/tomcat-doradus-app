package com.dell.doradus.servlet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(name = "testconnection", urlPatterns = {"/testconnection"})
public class DoradusDemoServlet extends HttpServlet {

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
        
        String url = System.getenv("DORADUS_HOST") + ":" + System.getenv("DORADUS_PORT") +"/_applications";
		out.println("invoking url: " + url);
		
		URL obj = new URL(url);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();
		con.setRequestMethod("GET");
		
		BufferedReader in = new BufferedReader(
		        new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer responseBuff = new StringBuffer();
 
		while ((inputLine = in.readLine()) != null) {
			responseBuff.append(inputLine);
		}
		in.close();
 
		//print result
		out.println("Result\n" + responseBuff.toString());
    }
  
}
