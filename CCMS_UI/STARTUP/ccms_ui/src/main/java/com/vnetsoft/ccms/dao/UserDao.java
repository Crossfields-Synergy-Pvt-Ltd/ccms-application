package com.vnetsoft.ccms.dao;

import java.util.List;

import com.vnetsoft.ccms.pojo.User;

public interface UserDao {

	public boolean addEntity(User user) throws Exception;
	public User getEntityById(String id) throws Exception;
	public List<User> getEntityList() throws Exception;
	public boolean deleteEntity(String id) throws Exception;
	
}
