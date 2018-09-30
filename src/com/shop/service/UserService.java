package com.shop.service;

import com.shop.domain.User;

public interface UserService {

	boolean regist(User user);

	void active(String activeCode);

	boolean checkUsername(String username);

	User login(String username, String password);

}
