package com.vnetsoft.ccms.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.vnetsoft.ccms.dao.UserDao;
import com.vnetsoft.ccms.pojo.User;


public class UserServicesImpl implements UserServices {

	@Autowired
	UserDao userDao;
	
	@Override
	public boolean addEntity(User user) throws Exception {
		return userDao.addEntity(user);
	}

	@Override
	public User getEntityById(String id) throws Exception {
		return userDao.getEntityById(id);
	}

	@Override
	public List<User> getEntityList() throws Exception {
		return userDao.getEntityList();
	}

	@Override
	public boolean deleteEntity(String id) throws Exception {
		return userDao.deleteEntity(id);
	}

}
