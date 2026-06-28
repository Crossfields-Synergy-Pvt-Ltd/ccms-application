package com.vnetsoft.ccms.controller;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.vnetsoft.ccms.pojo.DCUInstantData;
import com.vnetsoft.ccms.pojo.HandShake;
import com.vnetsoft.ccms.pojo.MonitorControlCount;
import com.vnetsoft.ccms.pojo.server.InstantMeterData;
import com.vnetsoft.ccms.pojo.ui.MapData;
import com.vnetsoft.ccms.services.DashBoardServices;
import com.vnetsoft.ccms.util.DateUtils;


@Controller
@RequestMapping("/dashboard")
public class MontorController {
	@Autowired
	DashBoardServices dashBpardService;


	static final Logger logger = Logger.getLogger(MontorController.class);
	

	@RequestMapping(value = "/count", method = RequestMethod.GET)
	public @ResponseBody MonitorControlCount getDashBoardCounts(@RequestParam("distrtict") String distrtict,
			@RequestParam("mandal") String mandal,
			@RequestParam("gp") String gp
			) {
	
		if(logger.isDebugEnabled()){
			logger.debug("DIST : "+ distrtict +" MANDAL : "+ mandal + " GP : "+ gp);
		}
		
		try {
			
			return dashBpardService.getDahsBoardCountstats(distrtict, mandal, gp);
		} catch (Exception e) {
			logger.error("Exception : " + e.getMessage());
			logger.error(e.getStackTrace());
		}
		
		return null;
	}
	
	
	@RequestMapping(value = "/map_data", method = RequestMethod.GET)
	public @ResponseBody List<MapData> getDashBoardMapData(@RequestParam("distrtict") String distrtict,
			@RequestParam("mandal") String mandal,
			@RequestParam("gp") String gp
			) {
	
		if(logger.isDebugEnabled()){
			logger.debug("DIST : "+ distrtict +" MANDAL : "+ mandal + " GP : "+ gp);
		}
		
		List<MapData> update_list = new ArrayList<MapData>();
		try {
			List<HandShake> tmp_list = dashBpardService.getMapData(distrtict, mandal, gp);
			
			for(HandShake tmp : tmp_list){
				MapData obj = new MapData();
				obj.lang = tmp.getLang();
				obj.lat = tmp.getLat();
				obj.light_status = tmp.getLight_status();
				obj.mcb_trip = tmp.getMcb_trip();
				obj.high_current = tmp.getHigh_current();
				obj.high_voltage = tmp.getHigh_voltage();
				
				obj.info_details = getMapInfoWindowDetails(tmp);
				update_list.add(obj);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return update_list;
		
	}
	
	
	

/*	@RequestMapping(value = "/instant_data", method = RequestMethod.POST)
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
			
		//	List<HandShake> dcu_list = dashBpardService.getAllHandShakeData();
			
	for(HandShake tmp : dcu_list){
				
				try {
					
					if(tmp.getGateway_serial_number().length() < 4)
						continue;
					
				DCUInstantData dcu_instant_data = new DCUInstantData();
				
				try {
					String hs_last_communication_time  = DateUtils.getLastSeenTimeForMonitorControl(Long.valueOf(tmp.getHs_time_stamp()));
					dcu_instant_data.setLast_communication_time(hs_last_communication_time);
					}catch(Exception e){
						System.out.println("Exception while setting last seen date : "+ e.getMessage());
					}
				
				dcu_instant_data.setDcu_details(tmp);
				InstantMeterData instant_meter_data = dashBpardService.getInstantMeterData(tmp.getGateway_serial_number());
				dcu_instant_data.setMeter_data(instant_meter_data);
				dcu_instant_data.setId(tmp.getGateway_serial_number());
				dcu_instant_data.setDevice_name(tmp.getName());
				dcu_instant_data_list.add(dcu_instant_data);
				
				}catch(Exception e){
					System.out.println("Exception : "+ e.getMessage());
					System.out.println(e.getStackTrace());
				}
				
	}
			
			return dcu_instant_data_list;
		} catch (Exception e) {
			System.out.println("Exception : "+ e.getMessage());
			System.out.println(e.getStackTrace());
			
		}
		
		return null;
	}
	*/
	
	private String getMapInfoWindowDetails(HandShake tmp) {
		
		StringBuffer sb = new StringBuffer();
		
		sb.append("CSA : ").append(tmp.getName()).append("  |   ");
		if(tmp.getLight_status() == 1)
			sb.append("ON");
		else
			sb.append("OFF");
		
		sb.append("<br>");
		sb.append("Lights Connected : ").append(tmp.getNo_of_lights());
		sb.append("<br>");
		sb.append("Active load : ").append(tmp.getConnected_load());
		sb.append("<br>");
		sb.append("Lat, Lang : ").append(tmp.getLat()).append(" , ").append(tmp.getLang());
		sb.append("<br>");
		sb.append("Land Mark : ").append(tmp.getDescription());
		sb.append("<br>");
		sb.append("Device Status : ").append("OKAY");
		sb.append("<br>");
		
		return sb.toString();
	}


	@RequestMapping(value = "/instant_data_id/{id}", method = RequestMethod.GET)
	public @ResponseBody DCUInstantData getDevicesInstantDataByID(@PathVariable("id") String id) {

		DCUInstantData dcu_instant_data = new DCUInstantData();
		
		try {
			
			if(logger.isDebugEnabled()) {
				 logger.debug("INSTANT DATA REQUEST");
			}
			
			HandShake dcu = dashBpardService.getHandShakeByID(id);
			
			try {
				String hs_last_communication_time  = DateUtils.getLastSeenTimeForMonitorControl(Long.valueOf(dcu.getHs_time_stamp()));
				dcu_instant_data.setLast_communication_time(hs_last_communication_time);
				}catch(Exception e){
					System.out.println("Exception while setting last seen date : "+ e.getMessage());
				}
			InstantMeterData instant_meter_data = dashBpardService.getInstantMeterData(dcu.getGateway_serial_number());
			dcu_instant_data.setMeter_data(instant_meter_data);
			dcu_instant_data.setId(dcu.getGateway_serial_number());
			dcu_instant_data.setDevice_name(dcu.getName());
			
			return dcu_instant_data;
		} catch (Exception e) {
			System.out.println("Exception : "+ e.getMessage());
			System.out.println(e.getStackTrace());
			return dcu_instant_data;
		}
	}
	
	

	@RequestMapping(value = "/instant_data_filter", method = RequestMethod.POST)
	public @ResponseBody List<DCUInstantData> getAllDevicesInstantDataByFilter(
			@RequestParam("distrtict") String distrtict,
			@RequestParam("mandal") String mandal,
			@RequestParam("gp") String gp,
			@RequestParam(value = "page", defaultValue = "0") int page,
			@RequestParam(value = "size", defaultValue = "50") int size,
			
			   @RequestHeader(
			  "Authorization") String
			  basicAuth) {
		
		
		System.out.println("BASIC AOUTH : "+ basicAuth);
		List<DCUInstantData> dcu_instant_data_list = new ArrayList<DCUInstantData>();
		
		try {
			
			if(logger.isDebugEnabled()) {
				 logger.debug("INSTANT DATA REQUEST");
			}
			
			List<HandShake> dcu_list = dashBpardService.getAllHandShakeData(distrtict, mandal, gp);
			
			int start = page * size;
			int end = Math.min(start + size, dcu_list.size());
			
			if (start < dcu_list.size()) {
				List<HandShake> paged_list = dcu_list.subList(start, end);
				
				for(HandShake tmp : paged_list){
					
					try {
						
						if(tmp.getGateway_serial_number().length() < 4)
							continue;
						
					DCUInstantData dcu_instant_data = new DCUInstantData();
					
					try {
						String hs_last_communication_time  = DateUtils.getLastSeenTimeForMonitorControl(Long.valueOf(tmp.getHs_time_stamp()));
						dcu_instant_data.setLast_communication_time(hs_last_communication_time);
						}catch(Exception e){
							System.out.println("Exception while setting last seen date : "+ e.getMessage());
						}
					
					dcu_instant_data.setDcu_details(tmp);
					InstantMeterData instant_meter_data = dashBpardService.getInstantMeterData(tmp.getGateway_serial_number());
					dcu_instant_data.setMeter_data(instant_meter_data);
					dcu_instant_data.setId(tmp.getGateway_serial_number());
					dcu_instant_data.setDevice_name(tmp.getName());
					dcu_instant_data_list.add(dcu_instant_data);
					
					}catch(Exception e){
						System.out.println("Exception : "+ e.getMessage());
						System.out.println(e.getStackTrace());
					}
					
				}
			}
			
			return dcu_instant_data_list;
		} catch (Exception e) {
			System.out.println("Exception : "+ e.getMessage());
			System.out.println(e.getStackTrace());
			
		}
		
		return null;
	}
	
}
