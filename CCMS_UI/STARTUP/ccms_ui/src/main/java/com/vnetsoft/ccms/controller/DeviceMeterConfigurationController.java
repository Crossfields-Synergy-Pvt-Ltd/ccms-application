package com.vnetsoft.ccms.controller;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.vnetsoft.ccms.pojo.DeviceMeterConfigurations;
import com.vnetsoft.ccms.pojo.Status;
import com.vnetsoft.ccms.services.DCUServices;

@Controller
@RequestMapping("/meter_conf")
public class DeviceMeterConfigurationController {

	@Autowired
	DCUServices userServices;

	static final Logger logger = Logger.getLogger(DCUController.class);
	
	

	@RequestMapping(value = "/create_meter", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Status add(@RequestBody DeviceMeterConfigurations obj) {

		try {
			
			
			if(logger.isDebugEnabled()) {
				 logger.debug(obj);
			}
			
			userServices.addMeter(obj);
			return new Status(200, "Success");
		} catch (Exception e) {
			return new Status(0, e.toString());
		}
	}
	
	

	@RequestMapping(value = "/meter_list_by_id/{dcu_serial_number}", method = RequestMethod.POST)
	public @ResponseBody List<DeviceMeterConfigurations> getDCUConfByID(@PathVariable("dcu_serial_number") String dcu_serial_number) {
		List<DeviceMeterConfigurations> obj = null;

		try {
			obj  = userServices.getByMeterID(dcu_serial_number);
			return obj;
		} catch (Exception e) {
			e.getStackTrace();
		}
		return obj;
	}
	
	@RequestMapping(value = "delete_meter/{id}", method = RequestMethod.DELETE)
	public @ResponseBody Status delete(@PathVariable("id") String id) {
		

		try {
			userServices.deleteDeviceMeterConfiguration(id);
			return new Status(1, "Employee deleted Successfully !");
		} catch (Exception e) {
			return new Status(0, e.toString());
		}

	}
}
