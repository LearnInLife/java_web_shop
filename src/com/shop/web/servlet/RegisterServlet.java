package com.shop.web.servlet;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import javax.mail.MessagingException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.Converter;

import com.shop.domain.User;
import com.shop.service.UserService;
import com.shop.utils.BeanFactory;
import com.shop.utils.CommonsUtils;
import com.shop.utils.MailUtils;

public class RegisterServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String checkCode = request.getParameter("checkcode");
		String checkcode_session = (String) request.getSession().getAttribute("checkcode_session");
		System.out.println(checkCode + ": " +checkcode_session);
		if (checkCode == null || !checkCode.trim().equals(checkcode_session)) {
			//验证码不正确
			//request.setAttribute("error_msg", "验证码不正确");
			//request.getRequestDispatcher("/register").forward(request, response);
			//response.sendRedirect(request.getContextPath()+"/register");
			response.setContentType("text/html;charset=UTF-8");
			response.getWriter().write("{\"error_msg\":\"验证码不正确\"}");
			return;
		}
		Map<String, String[]> properties = request.getParameterMap();
		User user = new User();
		try {
			//自己指定一个类型转换器（将String转成Date）
			ConvertUtils.register(new Converter() {
				
				@Override
				public Object convert(Class clazz, Object value) {
					SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
					Date parse = null;
					try {
						parse = format.parse(value.toString());
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					return parse;
				}
			}, Date.class);
			BeanUtils.populate(user, properties);
		} catch (IllegalAccessException | InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//private String uid;
		user.setUid(CommonsUtils.getUUID());
		//private String telephone;
		user.setTelephone(null);
		//private int state;//是否激活
		user.setState(0);
		//private String code;//激活码
		String activeCode = CommonsUtils.getUUID();
		user.setCode(activeCode);
		
		//System.out.println(user);
		
		UserService service = (UserService) BeanFactory.getBean("userService");
		boolean isRegisterSuccess = service.regist(user);
		//是否注册成功
		if (isRegisterSuccess) {
			//发送激活邮件
			String emailMsg = "恭喜您注册成功，请点击下面的连接进行激活账户"
					+ "<a href='http://localhost:8080/"+request.getContextPath()+"/active?activeCode="+activeCode+"'>"
							+ "http://localhost:8080/"+request.getContextPath()+"/active?activeCode="+activeCode+"</a>";
			try {
				MailUtils.sendMail(user.getEmail(), emailMsg);
			} catch (MessagingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//跳转到注册成功页面
			response.addHeader("REDIRECT", "REDIRECT");//告诉ajax这是重定向 
			response.addHeader("CONTEXTPATH", request.getContextPath()+"/jsp/registsuccess.jsp");//重定向地址 
			//response.sendRedirect();
		} else {
			//跳转到失败的提示页面
			response.addHeader("REDIRECT", "REDIRECT");//告诉ajax这是重定向 
			response.addHeader("CONTEXTPATH", request.getContextPath()+"/jsp/registfail.jsp");//重定向地址 
			//response.sendRedirect(request.getContextPath()+"/jsp/registfail.jsp");
		}
		
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}

}
