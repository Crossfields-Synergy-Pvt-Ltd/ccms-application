package com.vetsoft.ccms.dal;

import java.util.List;

import com.vetsoft.ccms.model.User;
import com.vetsoft.ccms.netty.pojo.DCUConfiguration;

public interface UserDAL {

	List<User> getAllUsers();

	User getUserById(String userId);

	User addNewUser(User user);

	Object getAllUserSettings(String userId);

	String getUserSetting(String userId, String key);

	String addUserSetting(String userId, String key, String value);
	
	public DCUConfiguration getDcuConfigurationsByID(String dcu_serial_number) ;
}