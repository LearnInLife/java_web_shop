package com.shop.service;

import java.util.List;

import com.shop.domain.Category;
import com.shop.domain.Order;
import com.shop.domain.PageBean;
import com.shop.domain.Product;


public interface ProductService {

	List<Product> findHotProductList();

	List<Product> findNewProductList();

	List<Category> findAllCategory();

	PageBean<Product> findProductListByCid(String cid, int parseInt, int currentCount);

	List<Product> findProductByPids(String[] split);

	Product findProductByPid(String pid);

	void submitOrder(Order order);

}
