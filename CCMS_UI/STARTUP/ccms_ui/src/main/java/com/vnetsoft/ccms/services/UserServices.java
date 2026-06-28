package com.vnetsoft.ccms.services;

import java.util.List;

import com.vnetsoft.ccms.pojo.User;

public interface UserServices {
	public boolean addEntity(User user) throws Exception;
	public User getEntityById(String id) throws Exception;
	public List<User> getEntityList() throws Exception;
	public boolean deleteEntity(String id) throws Exception;
}
