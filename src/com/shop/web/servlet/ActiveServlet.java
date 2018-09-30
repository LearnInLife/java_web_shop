package com.shop.web.servlet;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.shop.service.UserService;
import com.shop.utils.BeanFactory;

public class ActiveServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String activeCode = request.getParameter("activeCode");
		UserService service = (UserService) BeanFactory.getBean("userService");
		service.active(activeCode);
		// Ìø×ªµ½µÇÂ¼Ò³Ãæ
		response.sendRedirect(request.getContextPath() + "/jsp/login.jsp");
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}

}
