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
			//��֤�벻��ȷ
			//request.setAttribute("error_msg", "��֤�벻��ȷ");
			//request.getRequestDispatcher("/register").forward(request, response);
			//response.sendRedirect(request.getContextPath()+"/register");
			response.setContentType("text/html;charset=UTF-8");
			response.getWriter().write("{\"error_msg\":\"��֤�벻��ȷ\"}");
			return;
		}
		Map<String, String[]> properties = request.getParameterMap();
		User user = new User();
		try {
			//�Լ�ָ��һ������ת��������Stringת��Date��
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
		//private int state;//�Ƿ񼤻�
		user.setState(0);
		//private String code;//������
		String activeCode = CommonsUtils.getUUID();
		user.setCode(activeCode);
		
		//System.out.println(user);
		
		UserService service = (UserService) BeanFactory.getBean("userService");
		boolean isRegisterSuccess = service.regist(user);
		//�Ƿ�ע��ɹ�
		if (isRegisterSuccess) {
			//���ͼ����ʼ�
			String emailMsg = "��ϲ��ע��ɹ���������������ӽ��м����˻�"
					+ "<a href='http://localhost:8080/"+request.getContextPath()+"/active?activeCode="+activeCode+"'>"
							+ "http://localhost:8080/"+request.getContextPath()+"/active?activeCode="+activeCode+"</a>";
			try {
				MailUtils.sendMail(user.getEmail(), emailMsg);
			} catch (MessagingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//��ת��ע��ɹ�ҳ��
			response.addHeader("REDIRECT", "REDIRECT");//����ajax�����ض��� 
			response.addHeader("CONTEXTPATH", request.getContextPath()+"/jsp/registsuccess.jsp");//�ض����ַ 
			//response.sendRedirect();
		} else {
			//��ת��ʧ�ܵ���ʾҳ��
			response.addHeader("REDIRECT", "REDIRECT");//����ajax�����ض��� 
			response.addHeader("CONTEXTPATH", request.getContextPath()+"/jsp/registfail.jsp");//�ض����ַ 
			//response.sendRedirect(request.getContextPath()+"/jsp/registfail.jsp");
		}
		
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}

}
