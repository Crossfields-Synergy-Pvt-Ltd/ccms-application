package com.vnetsoft.ccms.controller;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;

import com.vnetsoft.ccms.pojo.DCUConfiguration;
import com.vnetsoft.ccms.pojo.HandShake;
import com.vnetsoft.ccms.pojo.Node;
import com.vnetsoft.ccms.pojo.SchedulerConfiguration;
import com.vnetsoft.ccms.pojo.Status;
import com.vnetsoft.ccms.services.DCUServices;
import com.vnetsoft.ccms.services.NodeServices;

@Controller
@RequestMapping("/device_conf")
public class DeviceConfigurationController {

	@Autowired
	DCUServices userServices;

	@Autowired
	NodeServices nodeServices;
	
	static final Logger logger = Logger.getLogger(DeviceConfigurationController.class);

	@Value("${ccms.server.host}")
	private String serverHost;
	
	@RequestMapping(value = "/sync_dcu_configuration", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Status syncDCUConfigurations(@RequestBody DCUConfiguration obj) {

		try {
			
			if(logger.isDebugEnabled()) {
				 logger.debug(obj);
			}
			
			String tmp = String.valueOf(System.currentTimeMillis());
	    	int tt = Integer.parseInt(tmp.substring(0, 10));
	    	
	    	String file_type = Integer.toHexString(tt);
	    	
			obj.setFile_identifier(file_type);
		//	obj.setStatus("0"); // pending
			userServices.addDCUConfiguration(obj);
			
			HandShake hand_shake = userServices.getHandShakeByID(obj.getDcu_id());
			StringBuilder sb = new StringBuilder();
			
			sb.append( "http://"+serverHost+":8102/user/push/sys_conf?dcu_id=")
			.append( obj.getDcu_id()+ "&buffer=" )
			.append(getInformationExchangePacket(hand_shake.getGateway_identifier(), file_type))
			.append("&dcu_identifier="+hand_shake.getGateway_identifier())
			.append("&node_data=")
			.append("&file_idetifier="+file_type)
			;
			//final String uri = "http://control.msg91.com/api/sendotp.php?otp_length=&authkey=&message=&sender=&mobile=";
			final String uri = sb.toString();
			System.out.println(uri);
			try {
		    RestTemplate restTemplate = new RestTemplate();
		    String result = restTemplate.getForObject(uri, String.class);
		    System.out.println(result);
			}catch(Exception e){
				System.out.println("Exception while pushing sys sync : "+ e.getMessage());
			}
		    
		    
		    
			return new Status(200, "Success");
		} catch (Exception e) {
			return new Status(0, e.toString());
		}
	}
	
	
	@RequestMapping(value = "sync_node_conf/{id}", method = RequestMethod.GET)
	public @ResponseBody Status syncNodeConfigurations(@PathVariable("id") String id) {
		
		if(logger.isDebugEnabled()) {
			 logger.debug("NODE CONFIGURATION SYNC REQUEST FOR DEVICE : "+ id);
		}
		List<Node> node_list =  null;
		try {
			
			HandShake hand_shake = userServices.getHandShakeByID(id);
			
			node_list = nodeServices.getEntityList();
		
			StringBuffer sb = new StringBuffer();
			
			for(Node node : node_list){
				sb.append(getNodeHexData(node));
				System.out.println("NODE : "+ node);
				System.out.println("HEX : "+ getNodeHexData(node));
			}
			
			System.out.println("NODE DATA : " + sb.toString());
			
			long UTC_date = (System.currentTimeMillis()/1000);
			String node_file_id = Long.toHexString(UTC_date);
			
			String dcu_identifier = StringUtils.leftPad("" + Integer.toHexString(hand_shake.getGateway_identifier()), 8, "0");
			
			pushNodeConfData(id, dcu_identifier, node_file_id, sb.toString());
			
		}catch(Exception e){
			
		}
		return new Status(200, "success");
	}
	
	
	

	@RequestMapping(value = "sync_schduler_conf", method = RequestMethod.GET)
	public @ResponseBody Status syncSchedulerConfigurations(@RequestParam("id") String id) {
		
		if(logger.isDebugEnabled()) {
			 logger.debug("SCHEDULER CONFIGURATION SYNC REQUEST FOR DEVICE : "+ id);
		}
	
		SchedulerConfiguration obj = null;
		
		try {
			HandShake hand_shake = userServices.getHandShakeByID(id);
			
			//long scheduler_id = 1561101289928L;
			obj = userServices.getSchedulerConfigurationById("schedule_1");
			System.out.println(obj);
			
			String buffer = getSchedulerConfBuffer(obj);
			System.out.println(buffer);
			System.out.println(obj);
			
			System.out.println(getRefranceTime(obj.getHandle_1_time()));
			System.out.println(getValidTillTime(obj.getValid_till()));
			
			
			
			long UTC_date = (System.currentTimeMillis()/1000);
			String node_file_id = Long.toHexString(UTC_date);
			
			String dcu_identifier = StringUtils.leftPad("" + Integer.toHexString(hand_shake.getGateway_identifier()), 8, "0");
		
			pushSchedulerConfData(id, dcu_identifier, node_file_id, buffer.toString());
			
			
		}catch(Exception e){
			
		}
		return new Status(200, "success");
	}
	
	@RequestMapping(value = "lights_on", method = RequestMethod.GET)
	public @ResponseBody Status turnOnLights(@RequestParam("device_serial_number") String device_serial_number, 
			@RequestParam("device_identifier") String device_identifier) {
		
		//http://204.93.160.139:8102/user/push/manuval_on?dcu_serial_number=1905HY1P1C009534&dcu_identifier=2043
		
		try {
			
			
			StringBuilder sb = new StringBuilder();
			
			sb.append( "http://"+serverHost+":8102/user/push/manuval_on?dcu_serial_number=")
			.append(device_serial_number+ "&dcu_identifier=" + device_identifier );
			
			final String uri = sb.toString();
			System.out.println(uri);
			try {
			    RestTemplate restTemplate = new RestTemplate();
			    String result = restTemplate.getForObject(uri, String.class);
			    System.out.println(result);
			}catch(Exception e){
				System.out.println("Exception while pushing sys sync : "+ e.getMessage());
			}
			
		} catch (Exception e) {
			logger.error(e.getMessage());
			logger.error(""+e.getStackTrace());
		}
		return new Status(200, "success");
	}
	
	
	
	@RequestMapping(value = "lights_off", method = RequestMethod.GET)
	public @ResponseBody Status turnOffLights(@RequestParam("device_serial_number") String device_serial_number, 
			@RequestParam("device_identifier") String device_identifier) {
		
		//http://204.93.160.139:8102/user/push/manuval_on?dcu_serial_number=1905HY1P1C009534&dcu_identifier=2043
		
		try {
			
			
			StringBuilder sb = new StringBuilder();
			
			sb.append( "http://"+serverHost+":8102/user/push/manuval_off?dcu_serial_number=")
			.append(device_serial_number+ "&dcu_identifier=" + device_identifier );
			
			final String uri = sb.toString();
			System.out.println(uri);
			try {
			    RestTemplate restTemplate = new RestTemplate();
			    String result = restTemplate.getForObject(uri, String.class);
			    System.out.println(result);
			}catch(Exception e){
				System.out.println("Exception while pushing sys sync : "+ e.getMessage());
			}
			
		} catch (Exception e) {
			logger.error(e.getMessage());
			logger.error(""+e.getStackTrace());
		}
		return new Status(200, "success");
	}
	
	
	private void pushSchedulerConfData(String id, String dcu_identifier,
			String node_file_id, String node_data) {
		
		StringBuilder sb = new StringBuilder();
		sb.append( "http://"+serverHost+":8102/user/push/sync_scheduler_conf?dcu_id=")
				.append(id+ "&dcu_identifier=")
				.append(dcu_identifier+ "&node_data=")
				.append(node_data+ "&file_idetifier="+node_file_id)
		;
		System.out.println(sb.toString());
		//final String uri = "http://control.msg91.com/api/sendotp.php?otp_length=&authkey=&message=&sender=&mobile=";
		final String uri = sb.toString();
		System.out.println(uri);
		
		try {
		    RestTemplate restTemplate = new RestTemplate();
		    String result = restTemplate.getForObject(uri, String.class);
		    System.out.println(result);
		}catch(Exception e){
			System.out.println("Exception while pushing sys sync : "+ e.getMessage());
		}
	}


	private static long getValidTillTime(String valid_till) throws ParseException {
		
		String myDate = valid_till.split("T")[0].trim();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date date = sdf.parse(myDate);
		long millis = date.getTime();
		return millis/1000;
	}


	
	private static  String getUTCHourMin(String hour_min) {
		String ret_hour_min = hour_min;
		try {
			 String strDateFormat = "dd-MM-yyyy HH:mm:ss";
			  DateFormat dateFormat_1 = new SimpleDateFormat(strDateFormat);
			    
			Date date =dateFormat_1.parse("27-06-2019 "+ hour_min +":00");
			SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
			dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
			
			//date = dateFormat.parse("27-06-2019 17:49:00");
			System.out.println("UTC Time is: " + dateFormat.format(date));
			Calendar utc_date = dateFormat.getCalendar();
			//System.out.println(utc_date.get(Calendar.HOUR));
			//System.out.println(utc_date.get(Calendar.MINUTE));
			ret_hour_min = (utc_date.get(Calendar.HOUR) + ":"+ utc_date.get(Calendar.MINUTE));
		} catch (Exception e) {
			System.out.println("Exception : "+ e.getMessage());
		}
		return ret_hour_min;
	}
	
	public static int getUTCSeconds(String time_satmp) {
		int hour_in_sec = 3600;
		int ref_time = 0;
		try {
			String tmp[] = time_satmp.split(":");
			ref_time = (Integer.parseInt(tmp[0]) * hour_in_sec)
					+ (Integer.parseInt(tmp[1]) * 60);
			ref_time -=19800;
			
			if(ref_time < 0){
				ref_time += 86400;
			}
			
			System.out.println("REFRANCE TIME : [ " + time_satmp + " ] "
					+ ref_time);
		} catch (Exception e) {
			System.out.println("EXCEPTION WHILE CONVERTING REFRANCE TIME : "
					+ e.getMessage());
		}
		return ref_time;

	}
	
	
	private static int getRefranceTime(String handle_1_time) {
		int hour_in_sec = 3600;
		int ref_time = 0;
		try {
			String tmp[] = handle_1_time.split(":");
			ref_time = (Integer.parseInt(tmp[0]) * hour_in_sec) + (Integer.parseInt(tmp[1]) * 60);

			/*
			 * As per document Tip: Reference time = Converted UTC ON/OFF time
			 * into seconds. For ex: If OFF time of schedule is 6.00 a.m. then
			 * Reference time = 21600 (6 a.m. in terms of seconds in IST format)
			 * + (-19800) (UTC seconds) = 1800 seconds
			 */
		/*	if(ref_time >= 19800){
				ref_time -= 19800;
			} else {
				ref_time = 19800 - ref_time;
			}*/
			logger.debug("REFRANCE TIME : [ "+ handle_1_time + " ] "+ ref_time);
		} catch (Exception e) {
			System.out.println("EXCEPTION WHILE CONVERTING REFRANCE TIME : "+ e.getMessage());
		}
		return ref_time;
	}


	private void pushNodeConfData(String id, String dcu_identifier,
			String node_file_id, String node_data) {
		
		StringBuilder sb = new StringBuilder();
		sb.append( "http://"+serverHost+":8102/user/push/sync_node_conf?dcu_id=")
				.append(id+ "&dcu_identifier=")
				.append(dcu_identifier+ "&node_data=")
				.append(node_data+ "&file_idetifier="+node_file_id)
		;
		System.out.println(sb.toString());
		//final String uri = "http://control.msg91.com/api/sendotp.php?otp_length=&authkey=&message=&sender=&mobile=";
		final String uri = sb.toString();
		System.out.println(uri);
		
		try {
		    RestTemplate restTemplate = new RestTemplate();
		    String result = restTemplate.getForObject(uri, String.class);
		    System.out.println(result);
		}catch(Exception e){
			System.out.println("Exception while pushing sys sync : "+ e.getMessage());
		}
		
	}


	private String getNodeHexData(Node node) {
		StringBuffer sb = new StringBuffer();
		try {
			
			sb.append(StringUtils.leftPad(Integer.toHexString(Integer.parseInt(node.getNodeid())), 8, "0"))
			.append(StringUtils.leftPad(Integer.toHexString(Integer.parseInt(node.getType())), 2, "0"))
			.append(StringUtils.leftPad(Integer.toHexString(Integer.parseInt(node.getSubtype())), 2, "0"))
			.append(StringUtils.leftPad(Long.toHexString(Long.parseLong(node.getReserved())), 8, "0"))
			.append(StringUtils.leftPad(Integer.toHexString(Integer.parseInt(node.getSlaveid())), 2, "0"))
			.append(StringUtils.leftPad(Integer.toHexString(Integer.parseInt(node.getIo_channel_1())), 2, "0"))
			.append(StringUtils.leftPad(Integer.toHexString(Integer.parseInt(node.getIo_channel_2())), 2, "0"))
			//.append(StringUtils.leftPad(Integer.toHexString(Integer.parseInt(node.getIo_channel_3())), 2, "0"))
			
			;
		} catch (Exception e) {
			System.out.println("Exception ");
		}
		return sb.toString();
	}


	
	
	
	
	
private String getInformationExchangePacket(int i, String dcu_conf_file_type) {
		
		StringBuffer sb = new StringBuffer();
		
		try {
			
			sb.append("55AA55AA")
			.append("01")
			.append("00")
			.append(StringUtils.leftPad("" + Integer.toHexString(i),8, "0"))
			.append("3E")
			.append("02")
			.append("0006") // pay load len
			.append("01")
			/*.append("03")
			.append("57E4D904")*/
			.append("01")
			.append(dcu_conf_file_type)
			;
		} catch (Exception e) {
			System.out.println("Exception while making init command :"+ e.getMessage());
		}
		return sb.toString();
	}


private static String getSchedulerConfBuffer(SchedulerConfiguration obj) {
		
		StringBuffer sb = new StringBuffer();
		
		long end_date = 1870237380;
		
		try {
			
			/*String meter_schedule_buff = getMeterScheduleConfBuffer(3,30, System.currentTimeMillis(), 
					end_date, 3, 0, 900, 900, 1073, 100);
			
			sb.append(meter_schedule_buff);
				
			if(!obj.getHandle_1_time().equals("undefined")){
				String on = getMeterScheduleConfBuffer(1,0, System.currentTimeMillis(), 
						end_date, 2, 21600, 60, 39600, 810, 1);
				sb.append(on);
			} else if(!obj.getHandle_2_time().equals("undefined")) {
				String off = getMeterScheduleConfBuffer(1,1, System.currentTimeMillis(), 
						end_date, 2, 64800, 60, 39600, 810, 1);
				sb.append(off);
			}*/
			
			int meter_pooling_interval = Integer.parseInt(obj.getData_interval_collection());
			
			end_date = getValidTillTime(obj.getValid_till());
			/*
			 * system not supporting validity time .. so we are setting 01/19/1970
			 */
			//end_date = 1590863;
			System.out.println("METER POLLING INTERVAL : "+ meter_pooling_interval + " END DATE : "+ end_date);
			int alerts_pooling_interval = Integer.parseInt(obj.getFault_detection());
			
			String meter_schedule_buff = getMeterScheduleConfBuffer(3,30, System.currentTimeMillis(), 
					end_date, 3, 0,meter_pooling_interval , 900, 1073, 100);
			
			sb.append(meter_schedule_buff);
				
			if(!obj.getHandle_0_time().equals("undefined")){
				int referance_time = getUTCSeconds(obj.getHandle_0_time());
				System.out.println("REF TIME : "+ referance_time + " ALETR P I "+ alerts_pooling_interval + " STATUS : "+obj.getHandle_0_status());
				if(obj.getHandle_0_status().equalsIgnoreCase("ON")){
					String on = getMeterScheduleConfBuffer(1,1, System.currentTimeMillis(), 
							end_date, 2, referance_time, alerts_pooling_interval, 39600, 810, 1);
					sb.append(on);
				} else {
					String off = getMeterScheduleConfBuffer(1,0, System.currentTimeMillis(), 
							end_date, 2, referance_time, alerts_pooling_interval, 39600, 810, 1);
					sb.append(off);
				}
			} 
			if(!obj.getHandle_1_time().equals("undefined")){
				int referance_time = getUTCSeconds(obj.getHandle_1_time());
				System.out.println("REF TIME : "+ referance_time + " ALETR P I "+ alerts_pooling_interval + " STATUS : "+obj.getHandle_1_status());
				
				if(obj.getHandle_1_status().equalsIgnoreCase("ON")){
					String on = getMeterScheduleConfBuffer(1,1, System.currentTimeMillis(), 
							end_date, 2, referance_time, alerts_pooling_interval, 39600, 810, 1);
					sb.append(on);
				} else {
					String off = getMeterScheduleConfBuffer(1,0, System.currentTimeMillis(), 
							end_date, 2, referance_time, alerts_pooling_interval, 39600, 810, 1);
					sb.append(off);
				}
			} 
			
			if(!obj.getHandle_2_time().equals("undefined")){
				int referance_time = getUTCSeconds(obj.getHandle_2_time());
				System.out.println("REF TIME : "+ referance_time + " ALETR P I "+ alerts_pooling_interval + " STATUS : "+obj.getHandle_2_status());
				
				if(obj.getHandle_2_status().equalsIgnoreCase("ON")){
					String on = getMeterScheduleConfBuffer(1,1, System.currentTimeMillis(), 
							end_date, 2, referance_time, alerts_pooling_interval, 39600, 810, 1);
					sb.append(on);
				} else {
					String off = getMeterScheduleConfBuffer(1,0, System.currentTimeMillis(), 
							end_date, 2, referance_time, alerts_pooling_interval, 39600, 810, 1);
					sb.append(off);
				}
			} 
			
			if(!obj.getHandle_3_time().equals("undefined")){
				int referance_time = getUTCSeconds(obj.getHandle_3_time());
				System.out.println("REF TIME : "+ referance_time + " ALETR P I "+ alerts_pooling_interval + " STATUS : "+obj.getHandle_3_status());
				
				if(obj.getHandle_3_status().equalsIgnoreCase("ON")){
					String on = getMeterScheduleConfBuffer(1,1, System.currentTimeMillis(), 
							end_date, 2, referance_time, alerts_pooling_interval, 39600, 810, 1);
					sb.append(on);
				} else {
					String off = getMeterScheduleConfBuffer(1,0, System.currentTimeMillis(), 
							end_date, 2, referance_time, alerts_pooling_interval, 39600, 810, 1);
					sb.append(off);
				}
			} 
			
			if(!obj.getHandle_4_time().equals("undefined")){
				int referance_time = getUTCSeconds(obj.getHandle_4_time());
				System.out.println("REF TIME : "+ referance_time + " ALETR P I "+ alerts_pooling_interval + " STATUS : "+obj.getHandle_4_status());
				
				if(obj.getHandle_4_status().equalsIgnoreCase("ON")){
					String on = getMeterScheduleConfBuffer(1,1, System.currentTimeMillis(), 
							end_date, 2, referance_time, alerts_pooling_interval, 39600, 810, 1);
					sb.append(on);
				} else {
					String off = getMeterScheduleConfBuffer(1,0, System.currentTimeMillis(), 
							end_date, 2, referance_time, alerts_pooling_interval, 39600, 810, 1);
					sb.append(off);
				}
			} 
			System.out.println(sb);
		} catch (Exception e) {
			System.out.println("Exception : "+ e.getMessage());
		}
		return sb.toString();
	}


	private static String getMeterScheduleConfBuffer(int action_type, int action_value,  long start_date, 
			long end_date, int frequency,
			int refrance_time,  int pooling_interval,	int validity_time, int opcode, int node_ids
			) {
		
		StringBuffer sb = new StringBuffer();
		String sd_hex = "00000000"; //Long.toHexString(start_date/1000);
		String ed_hex = Long.toHexString(end_date);
		try {
			sb
			.append(StringUtils.leftPad("" +  Integer.toHexString(1), 8, "0"))	// scheduler id
			.append(StringUtils.leftPad("" +  Integer.toHexString(action_type), 2, "0")) // action type 3 fixed for meter
			.append(StringUtils.leftPad("" +  Integer.toHexString(action_value), 4, "0"))	
			.append(StringUtils.leftPad("" +  sd_hex, 8, "0"))	
			.append(StringUtils.leftPad("" +  ed_hex, 8, "0"))	
			.append(StringUtils.leftPad("" +  Integer.toHexString(frequency), 2, "0"))	// frequency
			.append(StringUtils.leftPad("" +  Integer.toHexString(1), 2, "0")) // priorty 
			.append(StringUtils.leftPad("" +  Integer.toHexString(refrance_time), 8, "0"))
			.append(StringUtils.leftPad("" +  Integer.toHexString(pooling_interval), 4, "0"))
				
			.append(StringUtils.leftPad("" +  Integer.toHexString(validity_time), 4, "0"))	
			.append(StringUtils.leftPad("" +  Integer.toHexString(opcode), 4, "0"))	
			
			.append(StringUtils.leftPad("" +  Integer.toHexString(32767), 4, "0"))	// sun set
			.append(StringUtils.leftPad("" +  Integer.toHexString(32767), 4, "0"))	// sun raise
			
			.append(StringUtils.leftPad("" +  Integer.toHexString(0), 2, "0")) // group count
			.append(StringUtils.leftPad("" +  Integer.toHexString(0), 40, "0"))// group id
			.append(StringUtils.leftPad("" +  Integer.toHexString(1), 2, "0")) // node count
			.append(StringUtils.leftPad("" +  Integer.toHexString(node_ids), 8, "0"))
			.append(StringUtils.leftPad("" +  Integer.toHexString(0), 72, "0"))
			;
		} catch (Exception e) {
			System.out.println("Exception : "+ e.getMessage());
		}
		return sb.toString();
	}
	
	
}
