package com.shop.web.servlet;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.shop.domain.User;
import com.shop.service.UserService;
import com.shop.utils.BeanFactory;

public class UserServlet extends BaseServlet {
	private static final long serialVersionUID = 1L;
	
	public void loginout(HttpServletRequest request, HttpServletResponse response) throws IOException {
		HttpSession session = request.getSession();
		session.removeAttribute("user");
		//将存储在客户端的cookie删除掉
		Cookie cookie = new Cookie("autologin", "");
		cookie.setMaxAge(0);
		response.addCookie(cookie);
		
		response.sendRedirect(request.getContextPath()+"/login");
	}

	// 用户登录
	public void login(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String checkCode = request.getParameter("checkcode");
		String checkcode_session = (String) request.getSession().getAttribute("checkcode_session");
		System.out.println(checkCode + ": " + checkcode_session);
		if (checkCode == null || !checkCode.trim().equals(checkcode_session)) {
			// 验证码不正确
			request.setAttribute("loginError", "验证码不正确");
			request.getRequestDispatcher("/login").forward(request, response);
			return;
		}
		
		HttpSession session = request.getSession();
		
		// 获得输入的用户名和密码
		String username = request.getParameter("username");
		String password = request.getParameter("password");
		// 将用户名和密码传递给service层
		UserService service = (UserService) BeanFactory.getBean("userService");
		User user = service.login(username, password);
		// 判断用户是否登录成功 user是否是null
		if (user != null) {
			//登录成功
			//***************判断用户是否勾选了自动登录*****************
			String autoLogin = request.getParameter("autoLogin");
			if ("autoLogin".equals(autoLogin)) {
				//要自动登录
				//创建存储用户名的cookie
				Cookie cookie_username = new Cookie("autologin", user.getUsername()+":"+user.getPassword());
				cookie_username.setMaxAge(10*60);
				cookie_username.setPath(request.getContextPath());
				response.addCookie(cookie_username);
			}
			//***************************************************
			//将user对象存到session中
			session.setAttribute("user", user);

			//重定向到首页
			response.sendRedirect(request.getContextPath());
		} else{
			request.setAttribute("loginError", "用户名或密码错误");
			request.getRequestDispatcher("/login").forward(request, response);
		}
	}
}
