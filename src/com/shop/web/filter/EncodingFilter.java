package com.shop.web.filter;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

public class EncodingFilter implements Filter {

	public void init(FilterConfig fConfig) throws ServletException {

	}

	public void destroy() {

	}

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		final HttpServletRequest req = (HttpServletRequest) request;
		// ʹ�ö�̬�������ȫ�ֱ���
		HttpServletRequest proxyInstance = (HttpServletRequest) Proxy.newProxyInstance(req.getClass().getClassLoader(),
				req.getClass().getInterfaces(), new InvocationHandler() {

					@Override
					public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
						// ��getParameter����������ǿ
						String name = method.getName();
						if ("getParameter".equals(name)) {
							String invoke = (String) method.invoke(req, args);// ����
							if (invoke != null) {
								invoke = new String(invoke.getBytes("iso8859-1"), "UTF-8");
								return invoke;
							}
						}
						return method.invoke(req, args);
					}
				});
		chain.doFilter(proxyInstance, response);
		// request.setCharacterEncoding("UTF-8");

		// �ڴ���request֮ǰ��request��getParameter����������ǿ
		/*
		 * װ����ģʽ(��װ)
		 * 
		 * 1����ǿ���뱻��ǿ����Ҫʵ��ͳһ�ӿ� 2������ǿ���д��뱻��ǿ���� 3����Ҫ��ǿ�ķ�����д ����Ҫ��ǿ�ķ������ñ���ǿ�����
		 * 
		 */

		// ����ǿ�Ķ���
		// HttpServletRequest req = (HttpServletRequest) request;
		// ��ǿ����
		// EnhanceRequest enhanceRequest = new EnhanceRequest(req);

		// chain.doFilter(enhanceRequest, response);
	}
}

class EnhanceRequest extends HttpServletRequestWrapper {

	private HttpServletRequest request;

	public EnhanceRequest(HttpServletRequest request) {
		super(request);
		this.request = request;
	}

	// ��getParaameter��ǿ
	@Override
	public String getParameter(String name) {
		String parameter = request.getParameter(name);// ����
		try {
			parameter = new String(parameter.getBytes("iso8859-1"), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return parameter;
	}

}
