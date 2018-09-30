package com.shop.web.servlet;

import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.Gson;
import com.shop.domain.Cart;
import com.shop.domain.CartItem;
import com.shop.domain.Category;
import com.shop.domain.Order;
import com.shop.domain.OrderItem;
import com.shop.domain.PageBean;
import com.shop.domain.Product;
import com.shop.domain.User;
import com.shop.service.ProductService;
import com.shop.service.impl.ProductServiceImpl;
import com.shop.utils.BeanFactory;
import com.shop.utils.CommonsUtils;

/**
 * Servlet implementation class ProductServlet
 */
public class ProductServlet extends BaseServlet {
	private static final long serialVersionUID = 1L;

	public void index(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		ProductService service = (ProductService) BeanFactory.getBean("productService");
		// ������Ʒ
		List<Product> hotList = service.findHotProductList();
		// ������Ʒ
		List<Product> newList = service.findNewProductList();

		req.setAttribute("hotProductList", hotList);
		req.setAttribute("newProductList", newList);

		req.getRequestDispatcher("/jsp/index.jsp").forward(req, resp);
	}

	public void categoryList(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		ProductService service = (ProductService) BeanFactory.getBean("productService");
		List<Category> categories = service.findAllCategory();
		Gson gson = new Gson();
		String json = gson.toJson(categories);
		resp.setContentType("text/html;charset=UTF-8");
		resp.getWriter().write(json);
	}

	public void productList(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String cid = req.getParameter("cid");
		String currentPageStr = req.getParameter("currentPage");
		if (currentPageStr == null)
			currentPageStr = "1";
		int currentCount = 12;

		ProductService service = (ProductService) BeanFactory.getBean("productService");

		PageBean<Product> pageBean = service.findProductListByCid(cid, Integer.parseInt(currentPageStr), currentCount);

		req.setAttribute("pageBean", pageBean);
		req.setAttribute("cid", cid);

		List<Product> historyProductList = null;
		// ��ÿͻ���Я�����ֽ�pids��cookie
		Cookie[] cookies = req.getCookies();
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if ("pids".equals(cookie.getName())) {
					String pids = cookie.getValue();
					String[] split = pids.split("-");
					// String[] split = {"1","10","12","3"};
					historyProductList = service.findProductByPids(split);
					break;
				}
			}
		}
		// String[] split = {"1","10","12","3"};
		// historyProductList = service.findProductByPids(split);
		// ����ʷ��¼�ļ��Ϸŵ�����
		req.setAttribute("historyProductList", historyProductList);
		req.getRequestDispatcher("/jsp/product_list.jsp").forward(req, resp);
	}

	public void productInfo(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// ��õ�ǰҳ
		String currentPage = req.getParameter("currentPage");
		// �����Ʒ���
		String cid = req.getParameter("cid");
		// ���Ҫ��ѯ����Ʒ��pid
		String pid = req.getParameter("pid");

		ProductService service = (ProductService) BeanFactory.getBean("productService");
		// ������Ʒ
		Product product = service.findProductByPid(pid);

		req.setAttribute("product", product);
		req.setAttribute("currentPage", currentPage);
		req.setAttribute("cid", cid);

		// ��ÿͻ���Я��cookie---���������pids��cookie
		String pids = pid;
		Cookie[] cookies = req.getCookies();
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if ("pids".equals(cookie.getName())) {
					pids = cookie.getValue();
					String[] split = pids.split("-");
					List<String> aList = Arrays.asList(split);
					LinkedList<String> list = new LinkedList<String>(aList);
					if (list.contains(pid)) {
						list.remove(pid);
						list.addFirst(pid);
					} else {
						if (list.size() > 7) {
							list.remove(list.size() - 1);
						} else {
							list.addFirst(pid);
						}
					}
					pids = StringUtils.join(list, "-");
				}
			}
		}

		Cookie cookie = new Cookie("pids", pids);
		cookie.setPath(req.getContextPath());
		resp.addCookie(cookie);
		req.getRequestDispatcher("/jsp/product_info.jsp").forward(req, resp);
	}

	public void addProductToCart(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		HttpSession session = req.getSession();
		User user = (User) session.getAttribute("user");
		if (user == null) {
			// û�е�¼ �ض��򵽵�¼ҳ��
			resp.sendRedirect(req.getContextPath() + "/login");
			return;
		}
		// ���Ҫ�ŵ����ﳵ����Ʒ��pid
		String pid = req.getParameter("pid");
		// ��ø���Ʒ�Ĺ�������
		Integer num = Integer.parseInt(req.getParameter("buyNum"));

		ProductService service = (ProductService) BeanFactory.getBean("productService");
		// ���product����
		Product product = service.findProductByPid(pid);
		// ����С��
		double totalPrice = product.getShop_price() * num;
		// ��װCartItem
		CartItem cartItem = new CartItem();
		cartItem.setProduct(product);
		cartItem.setBuyNum(num);
		cartItem.setSubtotal(totalPrice);

		// ��ù��ﳵ---�ж��Ƿ���session���Ѿ����ڹ��ﳵ
		Cart cart = (Cart) session.getAttribute("cart");
		if (cart == null)
			cart = new Cart();
		// ��������ŵ�����---key��pid
		// ���жϹ��ﳵ���Ƿ��ѽ������˹������� ----- �ж�key�Ƿ��Ѿ�����
		// ������ﳵ���Ѿ����ڸ���Ʒ----���������������ԭ�е�����������Ӳ���
		Map<String, CartItem> cartItems = cart.getCartItems();
		if (cartItems.containsKey(pid)) {
			CartItem item = cartItems.get(pid);
			int buyNum = item.getBuyNum();
			double subtotal = item.getSubtotal();
			item.setBuyNum(buyNum + num);
			item.setSubtotal(subtotal + (cartItem.getSubtotal()));
		} else {
			// �������û�и���Ʒ
			cartItems.put(pid, cartItem);
		}
		// �����ܼ�
		double total = cart.getTotal() + cartItem.getSubtotal();
		cart.setTotal(total);
		// �����ٴη���session
		session.setAttribute("cart", cart);
		resp.sendRedirect(req.getContextPath() + "/cart");
	}

	public void delProFromCart(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		// ���Ҫ�ŵ����ﳵ����Ʒ��pid
		String pid = req.getParameter("pid");
		HttpSession session = req.getSession();
		Cart cart = (Cart) session.getAttribute("cart");
		
		if (cart.getCartItems().containsKey(pid)) {
			CartItem item = cart.getCartItems().get(pid);
			cart.getCartItems().remove(pid);
			cart.setTotal(cart.getTotal()-item.getSubtotal());
		}
		session.setAttribute("cart", cart);
		resp.sendRedirect(req.getContextPath()+"/cart");
	}

	public void clearCart(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		HttpSession session = req.getSession();
		session.removeAttribute("cart");

		//��ת��cart.jsp
		resp.sendRedirect(req.getContextPath()+"/cart.jsp");
	}
	
	public void submitOrder(HttpServletRequest req, HttpServletResponse resp) {
		HttpSession session = req.getSession();
		User user = (User) session.getAttribute("user");
		Cart cart = (Cart) session.getAttribute("cart");
		//Ŀ�ģ���װ��һ��Order���� ���ݸ�service��
		Order order = new Order();
		//�����Ķ�����
		order.setOid(CommonsUtils.getUUID());
		//�µ�ʱ��
		order.setOrdertime(new Date());
		//�ö������ܽ��
		order.setTotal(cart.getTotal());
		//����֧��״̬ 1�����Ѹ��� 0����δ����
		order.setState(0);
		//�ջ���ַ
		order.setAddress(null);
		//�ջ���
		order.setName(null);
		//�ջ��˵绰
		order.setTelephone(null);
		//�ö��������ĸ��û�
		order.setUser(user);
		//�ö������ж��ٶ�����List<OrderItem> orderItems = new ArrayList<OrderItem>();
		Map<String, CartItem> cartItems = cart.getCartItems();
		for (Map.Entry<String, CartItem> item : cartItems.entrySet()){
			CartItem cartItem = item.getValue();
			OrderItem orderItem = new OrderItem();
			orderItem.setItemid(CommonsUtils.getUUID());
			orderItem.setCount(cartItem.getBuyNum());
			orderItem.setSubtotal(cartItem.getSubtotal());
			orderItem.setProduct(cartItem.getProduct());
			orderItem.setOrder(order);
			
			order.getOrderItems().add(orderItem);
		}
		ProductService service = (ProductService) BeanFactory.getBean("productService");
		service.submitOrder(order);
	}
}
