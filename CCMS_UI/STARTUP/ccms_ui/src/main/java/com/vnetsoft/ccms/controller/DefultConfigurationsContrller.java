package com.vnetsoft.ccms.controller;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.vnetsoft.ccms.pojo.DCUConfiguration;
import com.vnetsoft.ccms.pojo.Status;
import com.vnetsoft.ccms.services.DCUServices;

@Controller
@RequestMapping("/conf")
public class DefultConfigurationsContrller {


	@Autowired
	DCUServices userServices;

	static final Logger logger = Logger.getLogger(DefultConfigurationsContrller.class);


	@RequestMapping(value = "/get_dcu_defult_conf", method = RequestMethod.POST)
	public @ResponseBody DCUConfiguration getDefultConf() {
		DCUConfiguration obj = null;

		try {
			obj  = userServices.getDCUConfigurationByID("100");
			return obj;
		} catch (Exception e) {
			e.getStackTrace();
		}
		return obj;
	}
	
	
	
	@RequestMapping(value = "/add_defult_dcu_conf", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Status syncDCUConfigurations(@RequestBody DCUConfiguration obj) {

		try {
			obj.setDcu_id("100");
		
			if(logger.isDebugEnabled()) {
				 logger.debug("UPDATTING DCU DEFULT CONFIGURATION : "+ obj);
			}
			
			userServices.addDCUConfiguration(obj);
			return new Status(200, "Success");
		} catch (Exception e) {
			return new Status(0, e.toString());
		}
	}
	
	
	@RequestMapping(value = "update_dcu_conf/{id}", method = RequestMethod.GET)
	public @ResponseBody Status syncDCUConfigurations(@PathVariable("id") String id) {

		try {
			
			DCUConfiguration obj = userServices.getDCUConfigurationByID("100");
			obj.setDcu_id(id);
			if(logger.isDebugEnabled()) {
				 logger.debug("UPDATTING DCU WITH DEFULT CONFIGURATION : "+ obj);
			}
			
			obj.setDcu_id(id);
			
			String tmp = String.valueOf(System.currentTimeMillis());
	    	int tt = Integer.parseInt(tmp.substring(0, 10));
	    	
	    	String file_type = Integer.toHexString(tt);
	    	
			obj.setFile_identifier(file_type);
			obj.setStatus("Waiting for device ");
			userServices.addDCUConfiguration(obj);
			return new Status(200, "Success");
		} catch (Exception e) {
			return new Status(0, e.toString());
		}
	}
}
