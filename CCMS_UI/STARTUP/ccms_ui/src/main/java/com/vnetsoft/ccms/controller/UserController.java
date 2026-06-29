package com.vnetsoft.ccms.controller;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.vnetsoft.ccms.pojo.Status;
import com.vnetsoft.ccms.pojo.User;
import com.vnetsoft.ccms.services.UserServices;


@Controller
@RequestMapping("/superadmin/user")
public class UserController {

	@Autowired
	UserServices userServices;

	static final Logger logger = Logger.getLogger(UserController.class);

	@RequestMapping(value = "/create", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Status add(@RequestBody User obj) {

		try {
			if(logger.isDebugEnabled()) {
				 logger.debug(obj);
			}
			userServices.addEntity(obj);
			return new Status(200, "Success");
		} catch (Exception e) {
			return new Status(0, e.toString());
		}
	}

	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public @ResponseBody List<User> getAll(
			@RequestHeader("Authorization") String basicAuth) {


		if(logger.isDebugEnabled()) {
			 logger.debug("GET ALL REQUEST RECIVED");
		}
		List<User> userList = null;
		try {
			userList = userServices.getEntityList();
			if(logger.isDebugEnabled()) {
				 logger.debug(userList);
			}
		} catch (Exception e) {
			System.out.println("Exception : " + e);
			e.printStackTrace();
		}

		return userList;
	}

	@RequestMapping(value = "/login", method = RequestMethod.GET)
	public @ResponseBody User login(@RequestParam("name") String name,
			@RequestParam("password") String password) {

		System.out.println("LOGIN REQUEST RECIVED : " + name + " | " + password);

		User user = authenticateHardcoded(name, password);

		if (user == null) {
			user = new User();
			user.setEmail(name);
			user.setStatus("00");
		} else {
			user.setStatus("100");
		}
		user.setPassword("");
		return user;
	}

	private User authenticateHardcoded(String name, String password) {
		if ("admin@example.com".equals(name) && "admin123".equals(password)) {
			return createHardcodedUser("admin@example.com", "Admin", "User", "ADMIN", true);
		}
		if ("user@example.com".equals(name) && "user123".equals(password)) {
			return createHardcodedUser("user@example.com", "Regular", "User", "USER", false);
		}
		return null;
	}

	private User createHardcodedUser(String email, String firstName,
			String lastName, String role, boolean allPrivileges) {
		User u = new User();
		u.setEmail(email);
		u.setFirstName(firstName);
		u.setLastName(lastName);
		u.setFull_name(firstName + " " + lastName);
		u.setRole(role);
		u.setPassword("");
		u.setDist("ALL");
		u.setMondal("ALL");
		u.setGp("ALL");
		u.setMonitor_and_controller(allPrivileges);
		u.setHistory(allPrivileges);
		u.setEvent(allPrivileges);
		u.setSwitching_point_summary(allPrivileges);
		u.setOperational_hour(allPrivileges);
		u.setLight_status(allPrivileges);
		u.setSchedule(allPrivileges);
		u.setSettings(allPrivileges);
		u.setDefault_settings(allPrivileges);
		u.setFilter(allPrivileges);
		u.setNode(allPrivileges);
		u.setDcu(allPrivileges);
		u.setUser(allPrivileges);
		return u;
	}

	/*private String getAuthKey(String name, String password) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH");
		Date date = new Date();
		if(logger.isDebugEnabled()) {
			 logger.debug(dateFormat.format(date)); // 2013/10/15 16:16:39
		}
		String tmp = dateFormat.format(date) + password + "," + name;
		String t = AES.encrypt(tmp, "veera@1234");
		if(logger.isDebugEnabled()) {
			 logger.debug(t);
		}
		return t;
	}
*/
	@RequestMapping(value = "delete/{id}", method = RequestMethod.DELETE)
	public @ResponseBody Status delete(@PathVariable("id") String id) {
		

		try {
			userServices.deleteEntity(id);
			return new Status(1, "Employee deleted Successfully !");
		} catch (Exception e) {
			return new Status(0, e.toString());
		}

	}
/*
	private boolean isValidUser(String basicAuth) {
		if(logger.isDebugEnabled()) {
			 logger.debug(basicAuth);
		}
		
		try {
			String t = AES.decrypt(basicAuth, "veera@1234");
			if(logger.isDebugEnabled()) {
				 logger.debug(t);
			}
			String tmp[] = t.split(",");
			if(logger.isDebugEnabled()) {
				 logger.debug("USER NAME : "+tmp[1]);
			}
			User user = userServices.getEntityById(tmp[1].trim());
		
			if(basicAuth.equals(getAuthKey(user.getName(), user.getPassword()))){
				return true;
			} else {
				return false;
			}
			
		} catch (Exception e) {
			
			e.printStackTrace();
		}
		
		return false;
	}*/
	
	
/*	@RequestMapping(value = "/testurl", method = RequestMethod.POST, consumes = MediaType.ALL_VALUE)
	public @ResponseBody Status add(@RequestBody  String buffer) {

		
		long time = System.currentTimeMillis();
		Date date = new Date(time);
		try {
			String str_time = DateFormatter.formatDateToString(date, "dd MM yyyy hh:mm a",
					"Asia/Kolkata");
			DataLogger.logPostData(str_time + " , " +buffer);
			return new Status(200, "Success");
		} catch (Exception e) {
			return new Status(0, e.toString());
		}
	}
	*/
}
