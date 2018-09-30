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
		//校验用户是否登录----校验session是否存在user对象
		HttpSession session = req.getSession();
		
		//判断用户是否已经登录 未登录下面代码不执行
		User user = (User) session.getAttribute("user");
		if (user == null) {
			//没有登录 重定向到登录页面
			resp.sendRedirect(req.getContextPath()+"/login");
			return;
		}
		
		chain.doFilter(request, response);
	}

	public void init(FilterConfig fConfig) throws ServletException {
	}

}
