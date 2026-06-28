package com.vnetsoft.ccms.scheduler;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.apache.log4j.Logger;

import com.vnetsoft.ccms.constants.EventIdInformation;
import com.vnetsoft.ccms.pojo.Event;
import com.vnetsoft.ccms.pojo.IOPojo;
import com.vnetsoft.ccms.pojo.IOUIObject;
import com.vnetsoft.ccms.pojo.SinglePhaseMeterData;

public class FileLogger {


	static final Logger logger = Logger.getLogger(FileLogger.class);

	
	private static final String METER_DATA_FILE = "meter_data.csv";
	private static final String EVENT_DATA_FILE = "event_data.csv";
	private static final String IO_DATA_FILE = "io_data.csv";
	private static final String IO_UI_DATA_FILE = "io_ui_data.csv";

	public static boolean logMeterData(SinglePhaseMeterData obj, String file_name, String device_name) {
		
		StringBuffer sb = new StringBuffer();
		
		try {
			sb.append(obj.getDcu_id()).append(",")
			.append(device_name).append(",")
			.append(getDataString(obj.getTime_stamp())).append(",")
			.append("").append(",")
			.append("").append(",")
			.append("").append(",")
			.append(obj.getInstantaneous_voltage()).append(",")
			.append(obj.getInstantaneous_current()).append(",")
			.append(obj.getInstantaneous_power()).append(",")
			.append(obj.getInstantaneous_power_factor()).append(",")
			.append(obj.getCumulative_active_energy()).append(",")
			.append(obj.getCt_reverse_tamper_status()).append(",")
			.append(obj.getEarth_load_tamper_status()).append(",")
			.append(obj.getCover_open_tamper_status()).append(",")
			.append(obj.getMagnetic_influence_tamper_status()).append(",")
			.append(obj.getSingle_wire_tamper_status())
		/*	.append(obj.getPf_2()).append(",")
			.append(obj.getPf_3()).append(",")
			.append(obj.getActive_power()).append(",")
			.append(obj.getApparent_power()).append(",")
			.append(obj.getReactive_power()).append(",")
			*/
			;
			if(appendToFile(sb.toString(), file_name +FileSystemManager.FILE_SPRATER + METER_DATA_FILE))
				return true;
		} catch (Exception e) {
			logger.error("Exception : "+e.getMessage());
			logger.error(e.getStackTrace());
		}
		//if appending is success then only return true
		return false;
	}

	private static String getDataString(long time_stamp) {
		String formatted = null;
		
		try {
			if (String.valueOf(time_stamp).length() > 10) {
				time_stamp = Long.parseLong(String.valueOf(time_stamp)
						.substring(0, 10));
			}
		} catch (Exception e) {
			logger.error("Exception : "+e.getMessage());
			logger.error(e.getStackTrace());
		}
		
		try {
			
			Date date = new Date(time_stamp * 1000L);
	        DateFormat format = new SimpleDateFormat("dd MMM yyyy HH:mm");
	        format.setTimeZone(TimeZone.getTimeZone("Asia/Kolkata"));
	         formatted = format.format(date);
	        
		} catch (Exception e) {
			logger.error("Exception : "+e.getMessage());
			logger.error(e.getStackTrace());
		}
		return formatted;
	}
	
	/*private static String getHour(long time_stamp) {
		String formatted = null;
		
		try {
			if (String.valueOf(time_stamp).length() > 10) {
				time_stamp = Long.parseLong(String.valueOf(time_stamp)
						.substring(0, 10));
			}
		} catch (Exception e) {
			System.out.println("Exception while converting time : "+ e.getMessage());
		}
		
		try {
			
			Date date = new Date(time_stamp * 1000L);
			Calendar cal = Calendar.getInstance();
			cal.setTime(date);
			System.out.println(cal.get(Calendar.HOUR));
			System.out.println(cal.get(Calendar.MINUTE));
		} catch (Exception e) {
			// TODO: handle exception
		}
		return formatted;
	}
	public static void main(String[] args) {
		getHour(1559104396L);
	}*/
	
	public static boolean appendToFile(String data, String file_name) {
		if(touchFile(file_name)){

		BufferedWriter writer = null;
		try {
				writer = new BufferedWriter(
			                                new FileWriter(file_name, true)  //Set true for append mode
			                            ); 
			    writer.newLine();   //Add new line
			    writer.write(data);
			    writer.close();
			    return true;
		} catch (Exception e) {
			logger.error("Exception : "+e.getMessage());
			logger.error(e.getStackTrace());
		}finally{
			try {
			if(writer != null)
				writer.close();
			}catch(Exception e1){
				logger.error("Exception : "+e1.getMessage());
				logger.error(e1.getStackTrace());
			}
		}}
		return false; 
	}
	
	public static boolean touchFile(String file_name) {

		try {
			File file = new File(file_name);

			if (file.createNewFile()) {
				//System.out.println("File has been created.");
				return true;
			} else {
				//System.out.println("ERROR :: IO FILE ALREADY EXIST SKIPPING OVER RAIED.");
				return true;
			}
		
		} catch (Exception e) {
			logger.error("Exception : "+e.getMessage());
			logger.error(e.getStackTrace());
		} finally {

		}
		return false;
	}

	public static boolean logEventData(Event obj, String base_dir,
			String device_name) {
		
		if(obj.getEvent_id() == 1073) // meter data event just skip as we are having meter data it self
			return true;
		
		StringBuffer sb = new StringBuffer();

		try {
			sb.append(obj.getGateway_serial_number()).append(",")
			.append(device_name).append(",")
			.append(getDataString(obj.getTime_stamp())).append(",")
			.append(obj.getEvent_id()).append(",")
			.append(EventIdInformation.eventInformation.get(obj.getEvent_id())).append(",")
			.append(obj.getTime_stamp()).append(",")
				.append(obj.getNode_identifier()).append(",")
			;
			if(appendToFile(sb.toString(), base_dir +FileSystemManager.FILE_SPRATER + EVENT_DATA_FILE))
				return true;
		} catch (Exception e) {
			logger.error("Exception : "+e.getMessage());
			logger.error(e.getStackTrace());
		}
		//if appending is success then only return true
		return false;
	}

	public static boolean logIOData(IOPojo obj, String base_dir,
			String device_name) {
StringBuffer sb = new StringBuffer();

		try {
			String date = getDataString(obj.getId());
			String tmp[] = date.split(" ")[3].split(":");
			sb.append(obj.getDcu_id()).append(",")
			.append(device_name).append(",")
			.append(date).append(",")
			.append(tmp[0]).append(",")
			.append(tmp[1]).append(",")
			.append(obj.getOpration_type()).append(",");
			
			if(obj.getOpration_value() == 0)
				sb.append("OFF").append(",");
			else if(obj.getOpration_value() == 1)
				sb.append("ON").append(",");
			else if(obj.getOpration_value() == 1)
				sb.append("DIM").append(",");
			else
				sb.append("UNKNOW-"+obj.getOpration_value()).append(",");
			
			sb.append(obj.getOpration_resone()).append(",");
				
			
			sb.append(obj.getNode_id()).append(",")
				.append(obj.getId()).append(",")
			;
			//System.out.println(sb.toString());
			if(appendToFile(sb.toString(), base_dir +FileSystemManager.FILE_SPRATER + IO_DATA_FILE))
				return true;
		} catch (Exception e) {
			logger.error("Exception : "+e.getMessage());
			logger.error(e.getStackTrace());
		}
		//if appending is success then only return true
		return false;
	}

	public static void logIOUIData(String file) {
		List<IOUIObject> ui_io_obj_list = IODataGenerator.getIOUIObjects(file+FileSystemManager.FILE_SPRATER + IO_DATA_FILE);
		//System.out.println("=================IO DATA==============");
		//System.out.println(ui_io_obj_list);
		for(IOUIObject tmp : ui_io_obj_list){
			StringBuffer sb = new StringBuffer();
			sb.append(tmp.getDcu_id()).append(",")
			
			.append(tmp.getDate()).append(",")
			.append(tmp.getOn_hours()).append(",")
			.append(tmp.getOff_hours()).append(",")
			.append(tmp.getOn_hour_min()).append(",")
			.append(tmp.getOff_hour_min()).append(",")
			.append(tmp.getCumulative_on_hour()).append(",")
			.append(tmp.getCumulative_off_hour()).append(",")
			
			;
			
			appendToFile(sb.toString(), file +FileSystemManager.FILE_SPRATER + IO_UI_DATA_FILE);
		}
	}
	
}
