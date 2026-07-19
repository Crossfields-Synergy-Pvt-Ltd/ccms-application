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
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;

import com.vnetsoft.ccms.pojo.HandShake;
import com.vnetsoft.ccms.pojo.SchedulerConfiguration;
import com.vnetsoft.ccms.pojo.Status;
import com.vnetsoft.ccms.services.DCUServices;

@Controller
@RequestMapping("/scheduler")
public class SchedulerController {

	@Autowired
	DCUServices userServices;

	static final Logger logger = Logger.getLogger(SchedulerController.class);

	@RequestMapping(value = "/create", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Status add(@RequestBody SchedulerConfiguration obj,
			@RequestHeader("Authorization") String basicAuth) {

		try {
			if(logger.isDebugEnabled()) {
				 logger.debug(obj);
			}
			if(obj.getScheduleId() < 1)
				obj.setScheduleId(System.currentTimeMillis());
			
			userServices.addSchedulerConfiguration(obj);
			triggerScheduleSync(obj.getSchedules_name());
			return new Status(200, "Success");
		} catch (Exception e) {
			return new Status(0, e.toString());
		}
	}
	
	private void triggerScheduleSync(String schedulesName) {
		try {
			List<HandShake> dcus = userServices.findHandShakeBySchedulesName(schedulesName);
			RestTemplate restTemplate = new RestTemplate();
			for (HandShake hs : dcus) {
				String uri = "http://localhost:8080/device_conf/sync_schduler_conf?id=" + hs.getGateway_serial_number();
				restTemplate.getForObject(uri, String.class);
			}
		} catch (Exception e) {
			logger.error("Auto-sync schedule config failed: " + e.getMessage());
		}
	}
	
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public @ResponseBody List<SchedulerConfiguration> getAll() {


		if(logger.isDebugEnabled()) {
			 logger.debug("GET ALL REQUEST RECIVED");
		}
		List<SchedulerConfiguration> userList = null;
		try {
			userList = userServices.getSchedulerConfigurationList();
			if(logger.isDebugEnabled()) {
				 logger.debug(userList);
			}
		} catch (Exception e) {
			System.out.println("Exception : " + e);
			e.printStackTrace();
		}

		return userList;
	}

	@RequestMapping(value = "delete/{id}", method = RequestMethod.DELETE)
	public @ResponseBody Status delete(@PathVariable("id") String id) {
		

		try {
			userServices.deleteSchedulerConfiguration(id);
			return new Status(1, "Employee deleted Successfully !");
		} catch (Exception e) {
			return new Status(0, e.toString());
		}

	}
}
