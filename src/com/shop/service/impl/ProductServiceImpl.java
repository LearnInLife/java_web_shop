package com.shop.service.impl;

import java.sql.SQLException;
import java.util.List;

import com.shop.dao.ProductDao;
import com.shop.domain.Category;
import com.shop.domain.Order;
import com.shop.domain.PageBean;
import com.shop.domain.Product;
import com.shop.service.ProductService;
import com.shop.utils.DataSourceUtils;

public class ProductServiceImpl implements ProductService{

	@Override
	public List<Product> findHotProductList() {
		ProductDao dao = new ProductDao();
		List<Product> hot = null;
		try {
			hot = dao.findHotProductList();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return hot;
	}

	@Override
	public List<Product> findNewProductList() {
		ProductDao dao = new ProductDao();
		List<Product> hot = null;
		try {
			hot = dao.findNewProductList();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return hot;
	}

	@Override
	public List<Category> findAllCategory() {
		ProductDao dao = new ProductDao();
		List<Category> categories = null;
		try {
			categories = dao.findAllCategory();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return categories;
	}

	@Override
	public PageBean<Product> findProductListByCid(String cid, int currentPage, int currentCount) {
		ProductDao dao = new ProductDao();
		//��װһ��PageBean ����web��
		PageBean<Product> bean = new PageBean<>();
		bean.setCurrentPage(currentPage);
		bean.setCurrentCount(currentCount);
		//3����װ������
		int totalCount=0;
		try {
			totalCount = dao.getCount(cid);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		bean.setTotalCount(totalCount);
		//4����װ��ҳ��
		int totalPage = (int) Math.ceil(1.0*totalCount/currentCount);
		bean.setTotalPage(totalPage);
		//5����ǰҳ��ʾ������
		int index = (currentPage-1)*currentCount;
		List<Product> list = null;
		try {
			list = dao.findProductListByCid(cid,index,currentCount);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		bean.setList(list);
		return bean;
	}

	@Override
	public List<Product> findProductByPids(String[] split) {
		ProductDao dao = new ProductDao();
		List<Product> history = null;
		try {
			history = dao.findProductByPids(split);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return history;
	}

	@Override
	public Product findProductByPid(String pid) {
		ProductDao dao = new ProductDao();
		Product product = null;
		try {
			product = dao.findProductByPid(pid);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return product;
	}

	@Override
	public void submitOrder(Order order) {
		ProductDao dao = new ProductDao();
		//1����������
		try {
			DataSourceUtils.startTransaction();
			//2������dao�洢order�����ݵķ���
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}

}
