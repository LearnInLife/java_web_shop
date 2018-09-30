package com.shop.service.impl;

import java.sql.SQLException;

import com.shop.dao.UserDao;
import com.shop.domain.User;
import com.shop.service.UserService;

public class UserServiceImp implements UserService {

	@Override
	public boolean regist(User user) {
		UserDao dao = new UserDao();
		int row = 0;
		try {
			row = dao.regist(user);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return row > 0;
	}

	@Override
	public void active(String activeCode) {
		UserDao dao = new UserDao();
		try {
			dao.active(activeCode);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public boolean checkUsername(String username) {
		UserDao dao = new UserDao();
		Long isExist = 0l;
		try {
			isExist = dao.checkUsername(username);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return isExist>0;
	}

	@Override
	public User login(String username, String password) {
		UserDao dao = new UserDao();
		User user = null;
		try {
			user = dao.login(username,password);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return user;
	}

}
