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
		// 热门商品
		List<Product> hotList = service.findHotProductList();
		// 最新商品
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
		// 获得客户端携带名字叫pids的cookie
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
		// 将历史记录的集合放到域中
		req.setAttribute("historyProductList", historyProductList);
		req.getRequestDispatcher("/jsp/product_list.jsp").forward(req, resp);
	}

	public void productInfo(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// 获得当前页
		String currentPage = req.getParameter("currentPage");
		// 获得商品类别
		String cid = req.getParameter("cid");
		// 获得要查询的商品的pid
		String pid = req.getParameter("pid");

		ProductService service = (ProductService) BeanFactory.getBean("productService");
		// 查找商品
		Product product = service.findProductByPid(pid);

		req.setAttribute("product", product);
		req.setAttribute("currentPage", currentPage);
		req.setAttribute("cid", cid);

		// 获得客户端携带cookie---获得名字是pids的cookie
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
			// 没有登录 重定向到登录页面
			resp.sendRedirect(req.getContextPath() + "/login");
			return;
		}
		// 获得要放到购物车的商品的pid
		String pid = req.getParameter("pid");
		// 获得该商品的购买数量
		Integer num = Integer.parseInt(req.getParameter("buyNum"));

		ProductService service = (ProductService) BeanFactory.getBean("productService");
		// 获得product对象
		Product product = service.findProductByPid(pid);
		// 计算小计
		double totalPrice = product.getShop_price() * num;
		// 封装CartItem
		CartItem cartItem = new CartItem();
		cartItem.setProduct(product);
		cartItem.setBuyNum(num);
		cartItem.setSubtotal(totalPrice);

		// 获得购物车---判断是否在session中已经存在购物车
		Cart cart = (Cart) session.getAttribute("cart");
		if (cart == null)
			cart = new Cart();
		// 将购物项放到车中---key是pid
		// 先判断购物车中是否已将包含此购物项了 ----- 判断key是否已经存在
		// 如果购物车中已经存在该商品----将现在买的数量与原有的数量进行相加操作
		Map<String, CartItem> cartItems = cart.getCartItems();
		if (cartItems.containsKey(pid)) {
			CartItem item = cartItems.get(pid);
			int buyNum = item.getBuyNum();
			double subtotal = item.getSubtotal();
			item.setBuyNum(buyNum + num);
			item.setSubtotal(subtotal + (cartItem.getSubtotal()));
		} else {
			// 如果车中没有该商品
			cartItems.put(pid, cartItem);
		}
		// 计算总计
		double total = cart.getTotal() + cartItem.getSubtotal();
		cart.setTotal(total);
		// 将车再次访问session
		session.setAttribute("cart", cart);
		resp.sendRedirect(req.getContextPath() + "/cart");
	}

	public void delProFromCart(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		// 获得要放到购物车的商品的pid
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

		//跳转回cart.jsp
		resp.sendRedirect(req.getContextPath()+"/cart.jsp");
	}
	
	public void submitOrder(HttpServletRequest req, HttpServletResponse resp) {
		HttpSession session = req.getSession();
		User user = (User) session.getAttribute("user");
		Cart cart = (Cart) session.getAttribute("cart");
		//目的：封装好一个Order对象 传递给service层
		Order order = new Order();
		//订单的订单号
		order.setOid(CommonsUtils.getUUID());
		//下单时间
		order.setOrdertime(new Date());
		//该订单的总金额
		order.setTotal(cart.getTotal());
		//订单支付状态 1代表已付款 0代表未付款
		order.setState(0);
		//收货地址
		order.setAddress(null);
		//收货人
		order.setName(null);
		//收货人电话
		order.setTelephone(null);
		//该订单属于哪个用户
		order.setUser(user);
		//该订单中有多少订单项List<OrderItem> orderItems = new ArrayList<OrderItem>();
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
