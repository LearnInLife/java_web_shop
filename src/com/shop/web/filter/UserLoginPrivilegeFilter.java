package com.shop.web.filter;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.shop.domain.User;

/**
 * Servlet Filter implementation class UserLoginPrivilegeFilter
 */
public class UserLoginPrivilegeFilter implements Filter {

    public UserLoginPrivilegeFilter() {
    }

	public void destroy() {
		// TODO Auto-generated method stub
	}

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse resp = (HttpServletResponse) response;
		//У���û��Ƿ��¼----У��session�Ƿ����user����
		HttpSession session = req.getSession();
		
		//�ж��û��Ƿ��Ѿ���¼ δ��¼������벻ִ��
		User user = (User) session.getAttribute("user");
		if (user == null) {
			//û�е�¼ �ض��򵽵�¼ҳ��
			resp.sendRedirect(req.getContextPath()+"/login");
			return;
		}
		
		chain.doFilter(request, response);
	}

	public void init(FilterConfig fConfig) throws ServletException {
	}

}
