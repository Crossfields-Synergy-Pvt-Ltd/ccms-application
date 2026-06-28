package com.vnetsoft.ccms.dao.file;

import java.util.List;

import com.vnetsoft.ccms.pojo.EventUiObject;
import com.vnetsoft.ccms.pojo.IOUIObject;
import com.vnetsoft.ccms.pojo.ui.MeterDataUI;

public class FileDao {

	public static List<MeterDataUI> getByMeterDataBetweenDate(String id, String start_date,
			String end_date) {
		List<String> file_names = FileUtil.getDataFilenamesBetweenDate(id, start_date, end_date, "meter_data.csv");
	
		
	/*	List<MeterDataUI> meter_data = FileUtil.getMeterData(file_names);*/
		
		List<MeterDataUI> meter_data = FileUtil.getMeterData(file_names);
		//System.out.println(meter_data);
		
		return meter_data;
	}
	
	
	
	public static List<EventUiObject> getByEventDataBetweenDate(String id, String start_date,
			String end_date) {
		List<String> file_names = FileUtil.getDataFilenamesBetweenDate(id, start_date, end_date, "event_data.csv");
	
		
		List<EventUiObject> event_data = FileUtil.getEventData(file_names);
		//System.out.println(event_data);
		return event_data;
	}
	
	
	
	public static List<IOUIObject> getIODataBetweenDate(String id, String start_date,
			String end_date) {
		List<String> file_names = FileUtil.getDataFilenamesBetweenDate(id, start_date, end_date, "io_ui_data.csv");
	
		
		List<IOUIObject> event_data = FileUtil.getIOUiData(file_names);
		//System.out.println(event_data);
		return event_data;
	}
	
	
}
