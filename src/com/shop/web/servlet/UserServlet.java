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
		//���洢�ڿͻ��˵�cookieɾ����
		Cookie cookie = new Cookie("autologin", "");
		cookie.setMaxAge(0);
		response.addCookie(cookie);
		
		response.sendRedirect(request.getContextPath()+"/login");
	}

	// �û���¼
	public void login(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String checkCode = request.getParameter("checkcode");
		String checkcode_session = (String) request.getSession().getAttribute("checkcode_session");
		System.out.println(checkCode + ": " + checkcode_session);
		if (checkCode == null || !checkCode.trim().equals(checkcode_session)) {
			// ��֤�벻��ȷ
			request.setAttribute("loginError", "��֤�벻��ȷ");
			request.getRequestDispatcher("/login").forward(request, response);
			return;
		}
		
		HttpSession session = request.getSession();
		
		// ���������û���������
		String username = request.getParameter("username");
		String password = request.getParameter("password");
		// ���û��������봫�ݸ�service��
		UserService service = (UserService) BeanFactory.getBean("userService");
		User user = service.login(username, password);
		// �ж��û��Ƿ��¼�ɹ� user�Ƿ���null
		if (user != null) {
			//��¼�ɹ�
			//***************�ж��û��Ƿ�ѡ���Զ���¼*****************
			String autoLogin = request.getParameter("autoLogin");
			if ("autoLogin".equals(autoLogin)) {
				//Ҫ�Զ���¼
				//�����洢�û�����cookie
				Cookie cookie_username = new Cookie("autologin", user.getUsername()+":"+user.getPassword());
				cookie_username.setMaxAge(10*60);
				cookie_username.setPath(request.getContextPath());
				response.addCookie(cookie_username);
			}
			//***************************************************
			//��user����浽session��
			session.setAttribute("user", user);

			//�ض�����ҳ
			response.sendRedirect(request.getContextPath());
		} else{
			request.setAttribute("loginError", "�û������������");
			request.getRequestDispatcher("/login").forward(request, response);
		}
	}
}
