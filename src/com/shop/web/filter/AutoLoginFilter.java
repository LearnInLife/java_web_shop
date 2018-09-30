package com.shop.web.filter;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import com.shop.domain.User;
import com.shop.service.UserService;
import com.shop.utils.BeanFactory;

public class AutoLoginFilter implements Filter {

	public void destroy() {
	}

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		// 强转成HttpServletRequest
		HttpServletRequest req = (HttpServletRequest) request;
		User user = (User)req.getSession().getAttribute("user");
		if (user == null || req.getSession(false) == null) {
			//用戶未登录  自动登录
			String cookie_username = null;
			String cookie_password = null;
			Cookie[] cookies = req.getCookies();
			if (cookies != null) {
				for (Cookie cookie : cookies) {
					if ("autologin".equals(cookie.getName())) {
						String loginStr = cookie.getValue();
						if (loginStr != null) {
							cookie_username = loginStr.split(":")[0];
							cookie_password = loginStr.split(":")[1];
							break;
						}
					}
				}
				
				if (cookie_username != null && cookie_password!=null) {
					UserService service = (UserService) BeanFactory.getBean("userService");
					User loginUser = service.login(cookie_username, cookie_password);
					if (loginUser != null) {
						req.getSession().setAttribute("user", loginUser);
					}
				}
			}
		}

		//无论是否自动登陆,都放行资源
		chain.doFilter(request, response);
	}

	public void init(FilterConfig fConfig) throws ServletException {
	}

}
