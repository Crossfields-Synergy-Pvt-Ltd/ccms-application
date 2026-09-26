package com.vnetsoft.ccms.controller;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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
import com.vnetsoft.ccms.pojo.DCUConfiguration;
import com.vnetsoft.ccms.pojo.HandShake;
import com.vnetsoft.ccms.pojo.MonitorControlCount;
import com.vnetsoft.ccms.pojo.SchedulerConfiguration;
import com.vnetsoft.ccms.pojo.server.InstantMeterData;
import com.vnetsoft.ccms.pojo.ui.MapData;
import com.vnetsoft.ccms.services.DashBoardServices;
import com.vnetsoft.ccms.services.DCUServices;
import com.vnetsoft.ccms.util.DateUtils;


@Controller
@RequestMapping("/dashboard")
public class MonitorController {
	@Autowired
	DashBoardServices dashBpardService;

	@Autowired
	DCUServices dcuServices;


	static final Logger logger = Logger.getLogger(MonitorController.class);
	

	@RequestMapping(value = "/count", method = RequestMethod.GET)
	public @ResponseBody MonitorControlCount getDashBoardCounts(@RequestParam("district") String district,
			@RequestParam("mandal") String mandal,
			@RequestParam("gp") String gp,
			@RequestParam(value = "village", defaultValue = "ALL") String village,
			@RequestParam(value = "start_date", required = false) String startDateStr,
			@RequestParam(value = "end_date", required = false) String endDateStr,
			@RequestParam(value = "search", required = false) String search
			) {
	
		if(logger.isDebugEnabled()){
			logger.debug("DIST : "+ district +" MANDAL : "+ mandal + " GP : "+ gp);
		}
		
		try {
			Date startDate = parseDateParam(startDateStr, false);
			Date endDate = parseDateParam(endDateStr, true);
			return dashBpardService.getDahsBoardCountstats(district, mandal, gp, village, startDate, endDate, search);
		} catch (Exception e) {
			logger.error("Exception : " + e.getMessage());
			logger.error(e.getStackTrace());
		}
		
		return null;
	}
	
	
	@RequestMapping(value = "/map_data", method = RequestMethod.GET)
	public @ResponseBody List<MapData> getDashBoardMapData(@RequestParam("district") String district,
			@RequestParam("mandal") String mandal,
			@RequestParam("gp") String gp,
			@RequestParam(value = "start_date", required = false) String startDateStr,
			@RequestParam(value = "end_date", required = false) String endDateStr
			) {
	
		if(logger.isDebugEnabled()){
			logger.debug("DIST : "+ district +" MANDAL : "+ mandal + " GP : "+ gp);
		}
		
		List<MapData> update_list = new ArrayList<MapData>();
		try {
			Date startDate = parseDateParam(startDateStr, false);
			Date endDate = parseDateParam(endDateStr, true);
			List<HandShake> tmp_list = dashBpardService.getMapData(district, mandal, gp, startDate, endDate);
			
			for(HandShake tmp : tmp_list){
				MapData obj = new MapData();
				obj.id = tmp.getGateway_serial_number();
				obj.name = tmp.getName();
				obj.lang = tmp.getLang();
				obj.lat = tmp.getLat();
				obj.light_status = tmp.getLight_status();
				obj.mcb_trip = tmp.getMcb_trip();
				obj.high_current = tmp.getHigh_current();
				obj.high_voltage = tmp.getHigh_voltage();
				obj.manual_mode_status = tmp.getManual_mode_status();
				obj.no_of_lights = tmp.getNo_of_lights();
				obj.connected_load = tmp.getConnected_load();
				obj.offline = isOffline(tmp.getHs_time_stamp());
				
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
	
	private boolean isOffline(String timestamp) {
		try {
			return (System.currentTimeMillis() - Long.valueOf(timestamp)) > 7200000;
		} catch (Exception e) {
			return true;
		}
	}

	private String escapeHtml(String value) {
		if (value == null) return "";
		return value.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;")
				.replace("\"", "&quot;").replace("'", "&#39;");
	}

	private String getMapInfoWindowDetails(HandShake tmp) {
		
		StringBuffer sb = new StringBuffer();
		
		sb.append("CSA : ").append(escapeHtml(tmp.getName())).append("  |   ");
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
		sb.append("Land Mark : ").append(escapeHtml(tmp.getDescription()));
		sb.append("<br>");
		sb.append("Device Status : ").append(isOffline(tmp.getHs_time_stamp()) ? "OFFLINE" : "ONLINE");
		sb.append("<br>");
		
		return sb.toString();
	}


	private Date parseDateParam(String dateStr, boolean exclusiveEnd) {
		if (dateStr == null || dateStr.isEmpty()) return null;
		try {
			Date date = new SimpleDateFormat("yyyy-MM-dd").parse(dateStr);
			if (exclusiveEnd) {
				Calendar calendar = Calendar.getInstance();
				calendar.setTime(date);
				calendar.add(Calendar.DATE, 1);
				return calendar.getTime();
			}
			return date;
		} catch (Exception e) {
			logger.warn("Failed to parse date: " + dateStr);
			return null;
		}
	}

	@RequestMapping(value = "/instant_data_id/{id}", method = RequestMethod.GET)
	public @ResponseBody DCUInstantData getDevicesInstantDataByID(@PathVariable("id") String id) {

		DCUInstantData dcu_instant_data = new DCUInstantData();
		
		try {
			
			if(logger.isDebugEnabled()) {
				 logger.debug("INSTANT DATA REQUEST");
			}
			
				HandShake dcu = dashBpardService.getHandShakeByID(id);
				if (dcu == null) {
					return dcu_instant_data;
				}
				dcu_instant_data.setDcu_details(dcu);
			
			try {
				String hs_last_communication_time  = DateUtils.getLastSeenTimeForMonitorControl(Long.valueOf(dcu.getHs_time_stamp()));
				dcu_instant_data.setLast_communication_time(hs_last_communication_time);
				}catch(Exception e){
					System.out.println("Exception while setting last seen date : "+ e.getMessage());
				}
				InstantMeterData instant_meter_data = dashBpardService.getInstantMeterData(dcu.getGateway_serial_number());
				dcu_instant_data.setMeter_data(instant_meter_data);
				if (dcuServices != null) {
					DCUConfiguration dcuConfiguration = dcuServices.getDCUConfigurationByID(dcu.getGateway_serial_number());
					dcu_instant_data.setDcu_configurations(dcuConfiguration);
					SchedulerConfiguration scheduleConfiguration = dcuServices.getSchedulerConfigurationById(dcu.getSchedules_name());
					dcu_instant_data.setSchedule_configuration(scheduleConfiguration);
				}
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
			@RequestParam("district") String district,
			@RequestParam("mandal") String mandal,
			@RequestParam("gp") String gp,
			@RequestParam(value = "village", defaultValue = "ALL") String village,
			@RequestParam(value = "start_date", required = false) String startDateStr,
			@RequestParam(value = "end_date", required = false) String endDateStr,
			@RequestParam(value = "page", defaultValue = "0") int page,
			@RequestParam(value = "size", defaultValue = "50") int size,
			@RequestParam(value = "search", required = false) String search,
			@RequestHeader("Authorization") String basicAuth) {
		List<DCUInstantData> result = new ArrayList<DCUInstantData>();
		try {
			if (page < 0 || size <= 0) return result;
			Date startDate = parseDateParam(startDateStr, false);
			Date endDate = parseDateParam(endDateStr, true);
			List<HandShake> dcuList = dashBpardService.getAllHandShakeData(district, mandal, gp, village, startDate, endDate, search, page, size);
			for (HandShake tmp : dcuList) {
				try {
					if (tmp.getGateway_serial_number() == null || tmp.getGateway_serial_number().length() < 4) continue;
					DCUInstantData data = new DCUInstantData();
					try {
						data.setLast_communication_time(DateUtils.getLastSeenTimeForMonitorControl(Long.valueOf(tmp.getHs_time_stamp())));
					} catch (Exception e) {
						System.out.println("Exception while setting last seen date : " + e.getMessage());
					}
					data.setDcu_details(tmp);
					data.setMeter_data(dashBpardService.getInstantMeterData(tmp.getGateway_serial_number()));
					data.setId(tmp.getGateway_serial_number());
					data.setDevice_name(tmp.getName());
					result.add(data);
				} catch (Exception e) {
					System.out.println("Exception : " + e.getMessage());
				}
			}
			return result;
        } catch (Exception e) {
            logger.error("Unable to load monitor data", e);
            throw new RuntimeException("Unable to load monitor data", e);
        }
	}
	
}
