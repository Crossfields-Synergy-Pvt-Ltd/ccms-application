package com.vnetsoft.ccms.controller;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.vnetsoft.ccms.pojo.Status;
import com.vnetsoft.ccms.pojo.User;
import com.vnetsoft.ccms.services.UserServices;
import com.vnetsoft.ccms.util.PasswordHasher;


@Controller
@RequestMapping("/superadmin/user")
public class UserController {

	@Autowired
	UserServices userServices;

	static final Logger logger = Logger.getLogger(UserController.class);

	@RequestMapping(value = "/create", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Status> add(@RequestBody User obj) {

		try {
			validate(obj, true);
			if(logger.isDebugEnabled()) {
				 logger.debug("Creating user " + obj.getEmail());
			}
			userServices.addEntity(obj);
			return ResponseEntity.ok(new Status(200, "Success"));
		} catch (Exception e) {
			return errorResponse(e);
		}
	}

	@RequestMapping(value = "/update/{email}", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Status> update(@PathVariable("email") String email, @RequestBody User obj) {
		try {
			validate(obj, false);
			userServices.updateEntity(email, obj);
			return ResponseEntity.ok(new Status(200, "Success"));
		} catch (Exception e) {
			return errorResponse(e);
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
			for (User user : userList) {
				user.setPassword("");
			}
			if(logger.isDebugEnabled()) {
				 logger.debug(userList);
			}
		} catch (Exception e) {
			System.out.println("Exception : " + e);
			e.printStackTrace();
		}

		return userList;
	}

	@RequestMapping(value = "/login", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<User> login(@RequestBody LoginRequest request) {
		if (request == null || request.name == null || request.password == null) {
			return new ResponseEntity<User>(HttpStatus.BAD_REQUEST);
		}
		User user = null;
		if (request.name != null && request.password != null) {
			try {
				user = userServices.getEntityById(request.name);
				if (user != null && PasswordHasher.matches(request.password, user.getPassword())) {
					if (PasswordHasher.isLegacy(user.getPassword())) {
						user.setPassword(PasswordHasher.hash(request.password));
						userServices.updateEntity(request.name, user);
					}
				user.setStatus("100");
				user.setPassword("");
				return ResponseEntity.ok(user);
				}
			} catch (Exception e) {
				logger.warn("User authentication failed", e);
			}
		}
		User failed = new User();
		failed.setEmail(request == null ? null : request.name);
		failed.setStatus("00");
		failed.setPassword("");
		return ResponseEntity.ok(failed);
	}

	public static class LoginRequest {
		public String name;
		public String password;
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
	public ResponseEntity<Status> delete(@PathVariable("id") String id) {
		

		try {
			userServices.deleteEntity(id);
			return ResponseEntity.ok(new Status(1, "Employee deleted Successfully !"));
		} catch (Exception e) {
			return errorResponse(e);
		}

	}

	private void validate(User user, boolean passwordRequired) {
		if (user == null || isBlank(user.getEmail()) || !user.getEmail().matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")) {
			throw new IllegalArgumentException("A valid email is required");
		}
		if (isBlank(user.getFirstName()) || isBlank(user.getLastName()) || isBlank(user.getRole())) {
			throw new IllegalArgumentException("First name, last name and role are required");
		}
		if (passwordRequired && isBlank(user.getPassword())) {
			throw new IllegalArgumentException("Password is required");
		}
	}

	private boolean isBlank(String value) {
		return value == null || value.trim().isEmpty();
	}

	private ResponseEntity<Status> errorResponse(Exception e) {
		HttpStatus status = e instanceof IllegalStateException ? HttpStatus.CONFLICT : HttpStatus.BAD_REQUEST;
		return new ResponseEntity<Status>(new Status(0, e.getMessage()), status);
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
