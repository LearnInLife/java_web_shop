package com.shop.dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;

import com.shop.domain.Category;
import com.shop.domain.PageBean;
import com.shop.domain.Product;
import com.shop.utils.DataSourceUtils;
import com.sun.org.apache.bcel.internal.generic.NEW;

public class ProductDao {

	public List<Product> findHotProductList() throws SQLException {
		QueryRunner runner = new QueryRunner(DataSourceUtils.getDataSource());
		String sql = "select * from product where is_hot=? limit ?,?";
		List<Product> query = runner.query(sql, new BeanListHandler<Product>(Product.class),1,0,9);
		return query;
	}

	public List<Product> findNewProductList() throws SQLException {
		QueryRunner runner = new QueryRunner(DataSourceUtils.getDataSource());
		String sql = "select * from product order by pdate desc limit ?,?";
		List<Product> query = runner.query(sql, new BeanListHandler<Product>(Product.class),0,9);
		return query;
	}

	public List<Category> findAllCategory() throws SQLException {
		QueryRunner runner = new QueryRunner(DataSourceUtils.getDataSource());
		String sql = "select * from category";
		List<Category> query = runner.query(sql, new BeanListHandler<Category>(Category.class));
		return query;
	}

	public List<Product> findProductListByCid(String cid, int index, int currentCount) throws SQLException {
		QueryRunner runner = new QueryRunner(DataSourceUtils.getDataSource());
		String sql = "select * from product where cid=? limit ?,?";
		List<Product> list = runner.query(sql, new BeanListHandler<Product>(Product.class),cid,index,currentCount);
		return list;
	}

	public int getCount(String cid) throws SQLException {
		QueryRunner runner = new QueryRunner(DataSourceUtils.getDataSource());
		String sql = "select count(*) from product where cid=?";
		Long query = (Long) runner.query(sql, new ScalarHandler(),cid);
		return query.intValue();
	}

	public List<Product> findProductByPids(String[] split) throws SQLException {
		QueryRunner runner = new QueryRunner(DataSourceUtils.getDataSource());
		StringBuffer sql = new StringBuffer("select * from product where pid in(");
		List params = new ArrayList();
		for (int i=0;i<split.length;i++) {
			if (i == split.length-1) {
				sql.append("?");
			} else{
				sql.append("?,");
			}
			params.add(Integer.parseInt(split[i]));
		}
		sql.append(")");
		List<Product> list = runner.query(sql.toString(), new BeanListHandler<Product>(Product.class) , params.toArray());
		return list;
	}

	public Product findProductByPid(String pid) throws SQLException {
		QueryRunner runner = new QueryRunner(DataSourceUtils.getDataSource());
		String sql = "select * from product where pid=?";
		Product product = runner.query(sql, new BeanHandler<Product>(Product.class),pid);
		return product;
	}

}
