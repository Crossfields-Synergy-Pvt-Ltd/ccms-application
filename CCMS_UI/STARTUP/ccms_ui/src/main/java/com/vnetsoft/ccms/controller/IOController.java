package com.vnetsoft.ccms.controller;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.vnetsoft.ccms.dao.file.FileDao;
import com.vnetsoft.ccms.pojo.IOPojo;
import com.vnetsoft.ccms.pojo.IOUIObject;
import com.vnetsoft.ccms.pojo.ui.MeterDataUI;
import com.vnetsoft.ccms.services.DCUServices;
import com.vnetsoft.ccms.util.IOStatusTmpPojo_delete;
import com.vnetsoft.ccms.util.IOUIObject_delete;


@Controller
@RequestMapping("/io")
public class IOController {

	
	@Autowired
	DCUServices meter_data_services;
	
	static final Logger logger = Logger.getLogger(IOController.class);

	static int previous_Op_Val = 0;

	@RequestMapping(value = "/get_io_details/{id}", method = RequestMethod.GET)
	public @ResponseBody List<IOUIObject_delete> getAllMeterData(
			@PathVariable("id") String id) {


		if(logger.isDebugEnabled()) {
			 logger.debug("GET ALL MeterData REQUEST RECIVED");
		}
		List<IOPojo> io_list = null;
		List<IOStatusTmpPojo_delete> tmp_io_list = new ArrayList<IOStatusTmpPojo_delete>();
		List<IOUIObject_delete> io_data = new ArrayList<IOUIObject_delete>();
		
		try {
			io_list = meter_data_services.getIOPojoByID(id);
			
			
			for(IOPojo tmp : io_list) {
				IOStatusTmpPojo_delete obj = getIODetails(tmp);
				if(obj != null)
					tmp_io_list.add(obj);
			}
			
			System.out.println(tmp_io_list);
			
		
			IOStatusTmpPojo_delete prv = null;
			
			for(IOStatusTmpPojo_delete tmp : tmp_io_list){
				if(prv == null){
					prv = tmp;
					continue;
				}
				
				IOUIObject_delete obj = new IOUIObject_delete();
				obj.setDate(tmp.getDate());
				if(tmp.getOpration_value() == 0){
					obj.setOn_hour_min(tmp.getHour() + ":"+ tmp.getMin());
					obj.setOn_status("ON");
					
					obj.setOff_hour_min(prv.getHour() + ":"+ prv.getMin());
					obj.setOff_status("OFF");
					
				} else if(tmp.getOpration_value() == 1){
					obj.setOn_hour_min(prv.getHour() + ":"+ prv.getMin());
					obj.setOn_status("ON");
					
					obj.setOff_hour_min(tmp.getHour() + ":"+ tmp.getMin());
					obj.setOff_status("OFF");
				}  else if(tmp.getOpration_value() == 2){
					obj.setOn_hour_min(prv.getHour() + ":"+ prv.getMin());
					if(tmp.getOpration_value() == 0)
						obj.setOn_status("ON");
					if(tmp.getOpration_value() == 0)
						obj.setOn_status("OFF");
					obj.setOn_hour_min(tmp.getHour() + ":"+ tmp.getMin());
					obj.setOn_status("DIM");
				} else {
					System.out.println("Unknow Oprtion value : "+ tmp.getOpration_value());
				}
				obj.setDcu_id(id);
				io_data.add(obj);
				prv = tmp;
			}
			
			
		} catch (Exception e) {
			System.out.println("Exception : " + e);
			e.printStackTrace();
		}

		return io_data;
	}
	

	public static IOStatusTmpPojo_delete getIODetails(IOPojo obj) {

	/*	final DateTimeFormatter formatter = DateTimeFormatter
				.ofPattern("dd MMM yyyy");
		//"yyyy-MM-dd HH:mm:ss"
		long seconds = Long.valueOf(String.valueOf(obj.getId()).substring(0, 10));
		
		final String formattedDtm = Instant.ofEpochSecond(seconds)
				.atZone(ZoneId.of("Asia/Kolkata")).format(formatter);

		int hour = Instant.ofEpochSecond(seconds)
				.atZone(ZoneId.of("Asia/Kolkata")).getHour();
		int min = Instant.ofEpochSecond(seconds)
				.atZone(ZoneId.of("Asia/Kolkata")).getMinute();*/
		/*String status = null;
		if(obj.getOpration_value() == 0)
			status = "OFF";
		else 
			status = "ON";*/
		if(obj.getOpration_value() == previous_Op_Val)
			{
			previous_Op_Val = obj.getOpration_value();
				return null;
			}else {
				previous_Op_Val = obj.getOpration_value();
				//System.out.println(obj.getOpration_value());
			}
		
		//System.out.println(formattedDtm  + " 		"  + hour + ":"+ min + " 		 "+ status);
		
		IOStatusTmpPojo_delete tmp_obj = new IOStatusTmpPojo_delete();
		/*tmp_obj.setDate(formattedDtm);
		tmp_obj.setHour(hour);
		tmp_obj.setMin(min);*/
		tmp_obj.setOpration_value(obj.getOpration_value());
		
		return tmp_obj;
	}
	
	
	@RequestMapping(value = "/io_data_between_date", method = RequestMethod.GET)
	public @ResponseBody List<IOUIObject> getMeterDataBetweenDates(
			@RequestParam("id") String id, @RequestParam("start_date") String start_date, @RequestParam("end_date") String end_date) {

		List<IOUIObject> ui_obj_list = null;
		
		try {
			ui_obj_list = FileDao.getIODataBetweenDate(id, start_date, end_date);
		} catch (Exception e) {
			e.getStackTrace();
		}
		return ui_obj_list;
	}
	
}
