package com.vnetsoft.ccms.controller;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.vnetsoft.ccms.pojo.Event;
import com.vnetsoft.ccms.pojo.SchedulerConfiguration;
import com.vnetsoft.ccms.pojo.Status;
import com.vnetsoft.ccms.pojo.SystemConfiguration;
import com.vnetsoft.ccms.pojo.conf.ConfigurationDetails;
import com.vnetsoft.ccms.services.ControllerServices;

@Controller
@RequestMapping("/cloudsms")
public class StationController {

	@Autowired
	ControllerServices serviceObject;

	

	static final Logger logger = Logger.getLogger(UserController.class);

	@RequestMapping(value = "/sym_create", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Status addSystemConfig(@RequestBody SystemConfiguration obj) {

		try {
			if(logger.isDebugEnabled()) {
				 logger.debug(obj);
			}
			serviceObject.addSystemConfiguration(obj);
			return new Status(200, "Success");
		} catch (Exception e) {
			return new Status(0, e.toString());
		}
	}
	
	
	@RequestMapping(value = "/scheduler_create", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Status addScheduler(@RequestBody SchedulerConfiguration obj) {

		try {
			if(logger.isDebugEnabled()) {
				 logger.debug(obj);
			}
			serviceObject.addSchedulerConfiguration(obj);
			return new Status(200, "Success");
		} catch (Exception e) {
			return new Status(0, e.toString());
		}
	}
	
	
	@RequestMapping(value = "/event_create", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Status addEvent(@RequestBody Event obj) {

		try {
			if(logger.isDebugEnabled()) {
				 logger.debug(obj);
			}
			serviceObject.addEvent(obj);
			return new Status(200, "Success");
		} catch (Exception e) {
			return new Status(0, e.toString());
		}
	}
	

	@RequestMapping(value = "/sym_list", method = RequestMethod.GET)
	public @ResponseBody List<SystemConfiguration> getSystemConfiguration() {
		if(logger.isDebugEnabled()) {
			 logger.debug("GET ALL REQUEST RECIVED");
		}
		List<SystemConfiguration> list_obj = null;
		try {
			list_obj = serviceObject.getSystemConfigurationList();
			if(logger.isDebugEnabled()) {
				 logger.debug(list_obj);
			}
		} catch (Exception e) {
			System.out.println("Exception : " + e);
			e.printStackTrace();
		}
		return list_obj;
	}
	
	
	
	@RequestMapping(value = "/scheduler_list", method = RequestMethod.GET)
	public @ResponseBody List<SchedulerConfiguration> getSchedulerConfiguration() {
		if(logger.isDebugEnabled()) {
			 logger.debug("GET ALL REQUEST RECIVED");
		}
		List<SchedulerConfiguration> list_obj = null;
		try {
			list_obj = serviceObject.getSchedulerConfigurationList();
			if(logger.isDebugEnabled()) {
				 logger.debug(list_obj);
			}
		} catch (Exception e) {
			System.out.println("Exception : " + e);
			e.printStackTrace();
		}
		return list_obj;
	}
	
	@RequestMapping(value = "/event_list", method = RequestMethod.GET)
	public @ResponseBody List<Event> getEventList() {
		if(logger.isDebugEnabled()) {
			 logger.debug("GET ALL REQUEST RECIVED");
		}
		List<Event> list_obj = null;
		try {
			list_obj = serviceObject.getEventList();
			if(logger.isDebugEnabled()) {
				 logger.debug(list_obj);
			}
		} catch (Exception e) {
			System.out.println("Exception : " + e);
			e.printStackTrace();
		}
		return list_obj;
	}
	
	
	@RequestMapping(value = "/conf_details_list", method = RequestMethod.GET)
	public @ResponseBody List<ConfigurationDetails> getConfigurationDetails(@RequestParam("id") String id) {
		if(logger.isDebugEnabled()) {
			 logger.debug("GET ALL CONF DETAILS RECIVED" + id);
		}
		List<ConfigurationDetails> list_obj = null;
		try {
			list_obj = serviceObject.getConfigurationDetailsList(id);
			if(logger.isDebugEnabled()) {
				 logger.debug(list_obj);
			}
		} catch (Exception e) {
			System.out.println("Exception : " + e);
			e.printStackTrace();
		}
		return list_obj;
	}
	
}
