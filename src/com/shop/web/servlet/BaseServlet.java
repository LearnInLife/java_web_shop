package com.shop.web.servlet;

import java.io.IOException;
import java.lang.reflect.Method;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@SuppressWarnings("all")
public class BaseServlet extends HttpServlet {

	@Override
	protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		try {
			String method = req.getParameter("method");
			if (method == null || "".equals(method) || method.trim().equals("")) {
				method = "execute";
			}
			
			Class clazz = this.getClass();
			Method md = clazz.getMethod(method, HttpServletRequest.class,HttpServletResponse.class);
			if (md != null)
				md.invoke(this, req,resp);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	protected void execute(HttpServletRequest req, HttpServletResponse resp) {
		
	}
}
