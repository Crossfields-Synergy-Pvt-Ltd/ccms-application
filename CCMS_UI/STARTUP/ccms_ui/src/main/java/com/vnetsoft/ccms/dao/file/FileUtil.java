package com.vnetsoft.ccms.dao.file;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import com.vnetsoft.ccms.pojo.EventUiObject;
import com.vnetsoft.ccms.pojo.IOUIObject;
import com.vnetsoft.ccms.pojo.ui.MeterDataUI;

public class FileUtil {
	
	static final Logger logger = Logger.getLogger(FileUtil.class);


	static final String FILE_SPRATER = "/";
	public static String BASE_PATH = "/home/data/dontdelete/";
	//public static String BASE_PATH = "E:/CCMS_DATA";
	public static List<String> getDataFilenamesBetweenDate(String id, String start_date,
			String end_date, String file_prefix) {

		List<String> file_names = new ArrayList<String>();
		
		String pattern = "dd/MM/yyyy";
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);

		Date date;
		try {

			
			date = simpleDateFormat.parse(start_date);
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(date);
			for (int day = 0; day < 30; day++) {

				calendar = Calendar.getInstance();
				calendar.setTime(date);
				calendar.add(Calendar.DAY_OF_MONTH, day);

				String newDate = simpleDateFormat.format(calendar.getTime());
			
				
			
				
				String file_name = BASE_PATH + FILE_SPRATER 
						
						+ calendar.get(Calendar.YEAR) + FILE_SPRATER 
						+String.format("%02d",(calendar.get(Calendar.MONTH) + 1))+FILE_SPRATER
						+ id + FILE_SPRATER 
						+String.format("%02d",calendar.get(Calendar.DATE))+ FILE_SPRATER 
						+ file_prefix;
				file_names.add(file_name);
				
				if(newDate.equalsIgnoreCase(end_date)){
					break;
				}
			}
		} catch (ParseException e) {
			logger.error("Exception : " + e.getMessage());
			logger.error(e.getStackTrace());
		}
		return file_names;
	}

	public static void main(String[] args) {
		String end_date = "11/06/2019", start_date = "11/06/2019", id = "1905HY3P3C000904";
		List<String> files = getDataFilenamesBetweenDate(id, start_date, end_date, "meter_data.csv");
		//System.out.println(files);
		/*List<MeterDataUI> meter_data = FileUtil.getMeterData(files);
		System.out.println(meter_data);*/
	}

	
	static List<MeterDataUI> getMeterData(List<String> file_names) {
		
		List<MeterDataUI> meter_data = new ArrayList<MeterDataUI>();
		
		//System.out.println(file_names);
		for(String file_name : file_names){
			//System.out.println("READING FILE : "+ file_name);
		 BufferedReader fileBufferReader = null;
		try {
			
			if (!new File(file_name).exists()) {
			//	System.out.println("file not exist : "+ file_name);
				continue;
			}
			fileBufferReader = new BufferedReader(new FileReader(file_name));
			String buffer;
			while ((buffer = fileBufferReader.readLine()) != null) {
				
				try {
				MeterDataUI ui_obj = new MeterDataUI();
				
				 System.out.println(buffer);
				 if(buffer.length() >= 10){
				 String tmp[] = buffer.split(",");
				 	System.out.println("SIZE : "+ tmp.length);
				 	ui_obj.setUtc_date(tmp[2]);
					ui_obj.setDcu_id(tmp[1]);
					ui_obj.setConsumption(tmp[10]);
					ui_obj.setR_phase_voltage(tmp[6]);
					ui_obj.setCurrent_line_1(tmp[7]);
					ui_obj.setPf_1(tmp[9]);
					ui_obj.setKwh_total(tmp[10]);
					ui_obj.setDcu_name(tmp[1]);
					meter_data.add(ui_obj);
				 }
				}catch(Exception e){
					logger.error("Exception : " + e.getMessage());
				}
			}

		} catch (Exception e) {
			logger.error("Exception : " + e.getMessage());
			logger.error(e.getStackTrace());
		}finally{
			try {
				if(fileBufferReader != null)
					fileBufferReader.close();
			} catch (Exception e) {
				System.out.println("Eceception : "+ e.getMessage());
				e.printStackTrace();
			}
		}
		}
		
		System.out.println("DATA SIZE : " +meter_data.size());
		return meter_data;
		//
	}

	public static List<EventUiObject> getEventData(List<String> file_names) {
List<EventUiObject> event_data = new ArrayList<EventUiObject>();
		
		for(String file_name : file_names){
		 BufferedReader fileBufferReader = null;
		 
		try {
		
			if (!new File(file_name).exists()) 
				continue;
			
			fileBufferReader = new BufferedReader(new FileReader(file_name));
			String buffer;
			while ((buffer = fileBufferReader.readLine()) != null) {
				EventUiObject ui_obj = new EventUiObject();
				
				// System.out.println(buffer);
				 if(buffer.length() >= 5){
				 String tmp[] = buffer.split(",");
				 ui_obj.setTime_stamp(tmp[2]);
				 //ui_obj.setEvent_id(tmp[3]);
				 ui_obj.setEvent_data(tmp[4]);
				 ui_obj.setGateway_serial_number(tmp[1]);
				 //ui_obj.setNode_identifier(tmp[5]);
				 if(!tmp[4].equals("null"))
					 event_data.add(ui_obj);
				 
				 }
			}

		} catch (Exception e) {
			logger.error("Exception : " + e.getMessage());
			logger.error(e.getStackTrace());
		} finally{
			try {
				if(fileBufferReader != null)
					fileBufferReader.close();
			} catch (Exception e) {
			
			}
		}
		}
		return event_data;
	}

	public static List<IOUIObject> getIOUiData(List<String> file_names) {

List<IOUIObject> io_data = new ArrayList<IOUIObject>();
		
		for(String file_name : file_names){
		 BufferedReader fileBufferReader = null;
		try {
			
			if (!new File(file_name).exists()) 
				continue;
			
			fileBufferReader = new BufferedReader(new FileReader(file_name));
			String buffer;
			while ((buffer = fileBufferReader.readLine()) != null) {
				IOUIObject ui_obj = new IOUIObject();
				
				// System.out.println(buffer);
				 if(buffer.length() > 20){
				 String tmp[] = buffer.split(",");
				 ui_obj.setDcu_id(tmp[0]);
				 ui_obj.setDate(tmp[1]);
				 ui_obj.setOn_hours(tmp[2]);
				 ui_obj.setOff_hours(tmp[3]);
				 ui_obj.setOn_hour_min(tmp[4]);
				 ui_obj.setOff_hour_min(tmp[5]);
				 ui_obj.setCumulative_on_hour(tmp[6]);
				 ui_obj.setCumulative_off_hour(tmp[7]);
				
				io_data.add(ui_obj);
				 
				 }
			}

		} catch (Exception e) {
			logger.error("Exception : " + e.getMessage());
			logger.error(e.getStackTrace());
		}finally{
			try {
				fileBufferReader.close();
			} catch (Exception e) {
				
			}
		}
		}
		return io_data;
	
	}

}
