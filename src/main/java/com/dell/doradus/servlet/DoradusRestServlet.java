package com.dell.doradus.servlet;

import javax.servlet.annotation.WebServlet;

import com.dell.doradus.service.rest.RESTServlet;

@WebServlet(name = "DoradusRestServlet", urlPatterns = {"/*"})
public class DoradusRestServlet extends RESTServlet {
	private static final long serialVersionUID = 4815487822036229036L;
}
