package com.vnetsoft.ccms.controller;


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.vnetsoft.ccms.pojo.DCU;
import com.vnetsoft.ccms.pojo.DCUConfiguration;
import com.vnetsoft.ccms.pojo.DCUDropDown;
import com.vnetsoft.ccms.pojo.EventUiObject;
import com.vnetsoft.ccms.pojo.HandShake;
import com.vnetsoft.ccms.pojo.IOPojo;
import com.vnetsoft.ccms.pojo.Status;
import com.vnetsoft.ccms.services.DCUServices;
import com.vnetsoft.ccms.services.DashBoardServices;
import com.vnetsoft.ccms.util.DateUtils;


@Controller
@RequestMapping("/dcu")
public class DCUController {
	@Autowired
	DCUServices userServices;

	@Autowired
	DashBoardServices dashboardService;
	
	static final Logger logger = Logger.getLogger(DCUController.class);

	@RequestMapping(value = "/create", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Status add(@RequestBody HandShake obj) {

		try {
			
			if(logger.isDebugEnabled()) {
				 logger.debug(obj);
			}
			
			userServices.addHandShake(obj);
			return new Status(200, "Success");
		} catch (Exception e) {
			return new Status(0, e.toString());
		}
	}
	
	
	/*@RequestMapping(value = "/create_meter", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Status add(@RequestBody Meter obj) {

		try {
			
			if(logger.isDebugEnabled()) {
				 logger.debug(obj);
			}
			
			userServices.addMeter(obj);
			return new Status(200, "Success");
		} catch (Exception e) {
			return new Status(0, e.toString());
		}
	}*/
	

	

	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public @ResponseBody List<DCU> getAll() {


		if(logger.isDebugEnabled()) {
			 logger.debug("GET ALL REQUEST RECIVED");
		}
		
		List<DCU> userList = null;
		try {
			
			List<HandShake> hand_shake_list = userServices.getHandShakeList();
			userList = userServices.getDCUList();
			if(logger.isDebugEnabled()) {
				 logger.debug(userList);
			}
		
		} catch (Exception e) {
			System.out.println("Exception : " + e);
			e.printStackTrace();
		}

		return userList;
	}

	
	
	@RequestMapping(value = "/handshake_list", method = RequestMethod.GET)
	public @ResponseBody List<HandShake> getAllHandShake(@RequestParam("distrtict") String distrtict,
			@RequestParam("mandal") String mandal,
			@RequestParam("gp") String gp
			) {


		if(logger.isDebugEnabled()) {
			 logger.debug("GET ALL HandShake REQUEST RECIVED");
		}
		List<HandShake> userList = null;
		try {
			//userList = userServices.getHandShakeList();
			userList = dashboardService.getMapData(distrtict, mandal, gp);
			/*if(logger.isDebugEnabled()) {
				 logger.debug(userList);
			}*/
		} catch (Exception e) {
			System.out.println("Exception : " + e);
			e.printStackTrace();
		}

		return userList;
	}

	
	
	@RequestMapping(value = "/handshake_info_by_id", method = RequestMethod.GET)
	public @ResponseBody List<HandShake> getAllHandShake(@RequestParam("distrtict") String distrtict,
			@RequestParam("mandal") String mandal,
			@RequestParam("gp") String gp, 
			@RequestParam("name") String name
			) {


		if(logger.isDebugEnabled()) {
			 logger.debug("GET ALL HandShake REQUEST RECIVED");
		}
		List<HandShake> userList = null;
		try {
			//userList = userServices.getHandShakeList();
			userList = dashboardService.getHandShakeByIDWithFilter(distrtict, mandal, gp, name);
			/*if(logger.isDebugEnabled()) {
				 logger.debug(userList);
			}*/
		} catch (Exception e) {
			System.out.println("Exception : " + e);
			e.printStackTrace();
		}

		return userList;
	}
	

	
	
	
	@RequestMapping(value = "sys_conf_id/{id}", method = RequestMethod.POST)
	public @ResponseBody DCUConfiguration getDCUConfByID(@PathVariable("id") String id) {
		DCUConfiguration obj = null;

		try {
			obj  = userServices.getDCUConfigurationByID(id);
			return obj;
		} catch (Exception e) {
			e.getStackTrace();
		}
		return obj;
	}
	
	
	
	@RequestMapping(value = "delet_dcu_id/{id}", method = RequestMethod.DELETE)
	public @ResponseBody Status deletDCUByID(@PathVariable("id") String id) {
	System.out.println("DCU DELETE REQUEST : "+ id);
		try {
			userServices.deleteDCU(id);
			return new Status(1, "DCU deleted Successfully !");
		} catch (Exception e) {
			e.getStackTrace();
			return new Status(0, e.toString());
		}

	}
	
	
	
	
	/*
	@RequestMapping(value = "/instant_data", method = RequestMethod.POST)
	public @ResponseBody List<DCUInstantData> getAllDevicesInstantData(
			   @RequestHeader(
			  "Authorization") String
			  basicAuth) {

		System.out.println("BASIC AOUTH : "+ basicAuth);
		List<DCUInstantData> dcu_instant_data_list = new ArrayList<DCUInstantData>();
		
		
		try {
			
			if(logger.isDebugEnabled()) {
				 logger.debug("INSTANT DATA REQUEST");
			}
			
			List<HandShake> dcu_list = userServices.getHandShakeList();
			
		
			System.out.println("DCU LIST : "+ dcu_list);
			
			if(logger.isDebugEnabled()) {
				 logger.debug("DCU LIST : "+ dcu_list);
			}
			 List<Integer> selected_events_list = getEventCountListIDs();
			
			for(HandShake tmp : dcu_list){
				
				try {
					
					if(tmp.getGateway_serial_number().length() < 4)
						continue;
					
				DCUInstantData dcu_instant_data = new DCUInstantData();
				
				
				dcu_instant_data.setDcu_details(tmp);
				try {
				String hs_last_communication_time  = DateUtils.getLastSeenTimeForMonitorControl(Long.valueOf(tmp.getHs_time_stamp()));
				dcu_instant_data.setLast_communication_time(hs_last_communication_time);
				}catch(Exception e){
					System.out.println("Exception while setting last seen date : "+ e.getMessage());
				}
				DCUConfiguration dcu_conf = userServices.getDCUConfigurationByID(tmp.getGateway_serial_number());
				
				if(logger.isDebugEnabled()) {
					 logger.debug("DCU CONF : "  + dcu_conf);
				}
				
				
				System.out.println("DCU CONF : "  + dcu_conf);
				if(dcu_conf != null)
					dcu_instant_data.setDcu_configurations(dcu_conf);
				InstantMeterData instant_meter_data = userServices.getByInstantMeterDataID(tmp.getGateway_serial_number());
				
				if(logger.isDebugEnabled()) {
					 logger.debug("DCU instant_meter_data : "  + instant_meter_data);
				}
				
				if(instant_meter_data != null)
					dcu_instant_data.setMeter_data(instant_meter_data);
				
				List<InstantEventData>  instant_event_data = userServices.getByInstantEventDataD(tmp.getGateway_serial_number());
				
				
				
				if(instant_event_data != null) {
					List<InstantEventData>  updated_event_list = new ArrayList<InstantEventData>();
					for( InstantEventData tmp_event_obj : instant_event_data){
						String utc_date  = DateUtils.getEpochTimeFromSeconds(tmp_event_obj.getTime_stamp());
						//String event_data = ALLAlertsNotifications.allAllertsNotificationSubparrsing.get(tmp_event_obj.getEvent_id());
						String event_data = EventIdInformation.eventInformation.get(tmp_event_obj.getEvent_id());
						
						if(event_data == null || event_data.length() < 4)
							continue;
						if(!selected_events_list.contains(tmp_event_obj.getEvent_id()))
							continue;
						
						tmp_event_obj.setGateway_serial_number(utc_date); // insted of g/w serial number updatting with time stamp
						tmp_event_obj.setEvent_status(event_data);
						updated_event_list.add(tmp_event_obj);
					}
					
					if(logger.isDebugEnabled()) {
						 logger.debug("DCU instant_event_data : "  + updated_event_list);
					}
					dcu_instant_data.setEvent_instant_list(updated_event_list);
				}
				dcu_instant_data.setId(tmp.getGateway_serial_number());
				dcu_instant_data.setDevice_name(tmp.getName());
				dcu_instant_data_list.add(dcu_instant_data);
				
				}catch(Exception e){
					System.out.println("Exception : " + e.getMessage());
				}
			}
			
			return dcu_instant_data_list;
		} catch (Exception e) {
			System.out.println("Exception : "+ e.getMessage());
			System.out.println(e.getStackTrace());
			return dcu_instant_data_list;
		}
}
	*/
	
	
	private List<Integer> getEventCountListIDs() {
		List<Integer> supported_event_id = new ArrayList<Integer>();
		supported_event_id.add(18);
		supported_event_id.add(33);
		supported_event_id.add(5); 
		supported_event_id.add(3); 
		supported_event_id.add(2); 
		supported_event_id.add(21); 
		supported_event_id.add(6); 
		supported_event_id.add(7); 
		supported_event_id.add(8); 
		supported_event_id.add(9); 
		supported_event_id.add(29); 
		return supported_event_id;
	}
	
	
	@RequestMapping(value = "/dcu_name_list", method = RequestMethod.GET)
	public @ResponseBody List<DCUDropDown> getAllDcuName(@RequestParam("distrtict") String distrtict,
			@RequestParam("mandal") String mandal,
			@RequestParam("gp") String gp
			) {

		if(logger.isDebugEnabled()){
			logger.debug("DIST : "+ distrtict +" MANDAL : "+ mandal + " GP : "+ gp);
		}
		
		
		List<DCUDropDown> dcu_name_lis = new ArrayList<DCUDropDown>();
		
		if(logger.isDebugEnabled()) {
			 logger.debug("GET ALL DEVICE DROP DOWN ");
		}
		
		List<HandShake> userList = null;
		try {
			userList = dashboardService.getMapData(distrtict, mandal, gp);
			
			for(HandShake tmp : userList){
				
				DCUDropDown obj = new DCUDropDown();
				obj.setGateway_identifier(tmp.getGateway_serial_number());
				obj.setName(tmp.getName());
				dcu_name_lis.add(obj);
			}
		} catch (Exception e) {
			System.out.println("Exception : " + e);
			e.printStackTrace();
		}

		return dcu_name_lis;
		
		
	}
	
	
	
	

	@RequestMapping(value = "/io_list", method = RequestMethod.GET)
	public @ResponseBody List<IOPojo> getAllIOList() {


		if(logger.isDebugEnabled()) {
			 logger.debug("GET ALL HandShake REQUEST RECIVED");
		}
		
		
		List<IOPojo> event_list = null;
		List<IOPojo> updated_event_list = new ArrayList<IOPojo>();
		
		try {
			event_list = userServices.getIOPojoList();
			
			
			DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			
			for(IOPojo tmp : event_list){
				EventUiObject obj = new EventUiObject();
				
				  String utc_date  = DateUtils.getEpochTimeFromSeconds(tmp.getId());
				 
				 updated_event_list.add(tmp);
				 
			}
		} catch (Exception e) {
			System.out.println("Exception : " + e);
			e.printStackTrace();
		}

		return updated_event_list;
	}
	
	
	// 

	
	
	
	
	
	


/*
	@RequestMapping(value = "/instant_data_id/{id}", method = RequestMethod.GET)
	public @ResponseBody DCUInstantData getDevicesInstantDataByID(@PathVariable("id") String id) {

		DCUInstantData dcu_instant_data = new DCUInstantData();
		
		try {
			
			if(logger.isDebugEnabled()) {
				 logger.debug("INSTANT DATA REQUEST");
			}
			
			HandShake dcu = userServices.getHandShakeByID(id);
			
			
			System.out.println("DCU LIST : "+ dcu);
			
			if(logger.isDebugEnabled()) {
				 logger.debug("DCU LIST : "+ dcu);
			}
			
			
			
				
				try {
					
					if(dcu.getGateway_serial_number().length() < 4)
						return null;
					
				
				
				dcu_instant_data.setDcu_details(dcu);
				dcu_instant_data.setLast_communication_time(dcu.getDate());
				DCUConfiguration dcu_conf = userServices.getDCUConfigurationByID(dcu.getGateway_serial_number());
				
				if(logger.isDebugEnabled()) {
					 logger.debug("DCU CONF : "  + dcu_conf);
				}
				
				
				System.out.println("DCU CONF : "  + dcu_conf);
				if(dcu_conf != null)
					dcu_instant_data.setDcu_configurations(dcu_conf);
				InstantMeterData instant_meter_data = userServices.getByInstantMeterDataID(dcu.getGateway_serial_number());
				
				if(logger.isDebugEnabled()) {
					 logger.debug("DCU instant_meter_data : "  + instant_meter_data);
				}
				
				if(instant_meter_data != null)
					dcu_instant_data.setMeter_data(instant_meter_data);
				
				List<InstantEventData>  instant_event_data = userServices.getByInstantEventDataD(dcu.getGateway_serial_number());
				
				
				
				
				if(instant_event_data != null) {
					List<InstantEventData>  updated_event_list = new ArrayList<InstantEventData>();
					for( InstantEventData tmp_event_obj : instant_event_data){
						String utc_date  = DateUtils.getEpochTimeFromSeconds(tmp_event_obj.getTime_stamp());
						String event_data = ALLAlertsNotifications.allAllertsNotificationSubparrsing.get(tmp_event_obj.getEvent_id());
				
						if(event_data == null || event_data.length() < 4)
							continue;
						
						tmp_event_obj.setGateway_serial_number(utc_date); // insted of g/w serial number updatting with time stamp
						tmp_event_obj.setEvent_status(event_data);
						updated_event_list.add(tmp_event_obj);
					}
					
					if(logger.isDebugEnabled()) {
						 logger.debug("DCU instant_event_data : "  + updated_event_list);
					}
					dcu_instant_data.setEvent_instant_list(updated_event_list);
				}
				dcu_instant_data.setId(dcu.getGateway_serial_number());
				dcu_instant_data.setDevice_name(dcu.getName());
				
				}catch(Exception e){
					System.out.println("Exception : " + e.getMessage());
				}
			
			
			return dcu_instant_data;
		} catch (Exception e) {
			System.out.println("Exception : "+ e.getMessage());
			System.out.println(e.getStackTrace());
			return dcu_instant_data;
		}
	}*/
	
}
