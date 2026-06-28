package com.vnetsoft.ccms.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;

import com.vnetsoft.ccms.dao.file.FileDao;
import com.vnetsoft.ccms.pojo.HandShake;
import com.vnetsoft.ccms.pojo.MeterData;
import com.vnetsoft.ccms.pojo.SinglePhaseMeterData;
import com.vnetsoft.ccms.pojo.ui.MeterDataUI;
import com.vnetsoft.ccms.services.DCUServices;
import com.vnetsoft.ccms.util.DateUtils;
	

@Controller
@RequestMapping("/meter")
public class MeterDataControler {

	
	@Autowired
	DCUServices meter_data_services;
	
	static final Logger logger = Logger.getLogger(MeterDataControler.class);


	@RequestMapping(value = "/meter_data_list", method = RequestMethod.GET)
	public @ResponseBody List<MeterDataUI> getAllMeterData() {


		if(logger.isDebugEnabled()) {
			 logger.debug("GET ALL MeterData REQUEST RECIVED");
		}
		List<SinglePhaseMeterData> userList = null;
		List<MeterDataUI> update_meter_data = new ArrayList<MeterDataUI>();
		try {
			userList = meter_data_services.getMeterDataListForCurrentDate();
			/*if(logger.isDebugEnabled()) {
				 logger.debug(userList);
			}*/
			for(SinglePhaseMeterData tmp : userList){
				MeterDataUI ui_obj = new MeterDataUI();
				
				String utc_date  = DateUtils.getEpochTimeFromSeconds(tmp.getTime_stamp());
				ui_obj.setUtc_date(utc_date);
				ui_obj.setDcu_id(tmp.getDcu_id());
				ui_obj.setConsumption(tmp.getCumulative_active_energy());
				ui_obj.setR_phase_voltage(tmp.getInstantaneous_voltage());
				ui_obj.setCurrent_line_1(tmp.getInstantaneous_current());
				ui_obj.setPf_1(tmp.getInstantaneous_power_factor());
				ui_obj.setKwh_total(tmp.getInstantaneous_power());
				
				HandShake hand_shake = meter_data_services.getHandShakeByID(tmp.getDcu_id());
				ui_obj.setDcu_name(hand_shake.getName());
				
				
				
				update_meter_data.add(ui_obj);
			}
		} catch (Exception e) {
			System.out.println("Exception : " + e);
			e.printStackTrace();
		}

		return update_meter_data;
	}
	
	
	

	
	@RequestMapping(value = "/meter_data_by_id/{id}", method = RequestMethod.GET)
	public @ResponseBody MeterDataUI getMeterDataByID(
			@PathVariable("id") String id) {
		
		MeterDataUI ui_obj = new MeterDataUI();
		
		SinglePhaseMeterData tmp = null;
		System.out.println("GET BY METER ID : " + id);
		try {
			tmp = meter_data_services.getByMeterDataID(id);
			System.out.println(tmp);
			
			String utc_date  = DateUtils.getEpochTimeFromSeconds(tmp.getTime_stamp());
			ui_obj.setUtc_date(utc_date);
			ui_obj.setDcu_id(tmp.getDcu_id());
			ui_obj.setConsumption(tmp.getCumulative_active_energy());
			ui_obj.setR_phase_voltage(tmp.getInstantaneous_voltage());
			ui_obj.setCurrent_line_1(tmp.getInstantaneous_current());
			ui_obj.setPf_1(tmp.getInstantaneous_power_factor());
			ui_obj.setKwh_total(tmp.getInstantaneous_power());
			
			HandShake hand_shake = meter_data_services.getHandShakeByID(tmp.getDcu_id());
			ui_obj.setDcu_name(hand_shake.getName());
			
			return ui_obj;
		} catch (Exception e) {
			e.getStackTrace();
		}
		return ui_obj;
	}
	
	
	
	@RequestMapping(value = "/meter_data_between_date", method = RequestMethod.GET)
	public @ResponseBody List<MeterDataUI> getMeterDataBetweenDates(
			@RequestParam("id") String id, @RequestParam("start_date") String start_date, @RequestParam("end_date") String end_date) {

		List<MeterDataUI> ui_obj_list = null;
		
		try {
			ui_obj_list = FileDao.getByMeterDataBetweenDate(id, start_date, end_date);
		} catch (Exception e) {
			e.getStackTrace();
		}
		return ui_obj_list;
	}

	
	
	
	
	
	
	
	
	
	
	
	
	
	public static void main(String[] args) {
		
		System.out.println(getCurrentYYMMDD());
	}
	
	
	
	private static String getCurrentYYMMDD() {

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());

		String yymmdd = 
				String.format("%02d", calendar.get(Calendar.DATE)) + "/"
				+ String.format("%02d", calendar.get(Calendar.MONTH) + 1)+ "/"
				+ String.valueOf(calendar.get(Calendar.YEAR)) ;
		return yymmdd;
	}
	
	/*
	 * DOWNLOAD API's
	 * 
	 */
	

	@RequestMapping(value = "/export_history")
	public void exportHistory(HttpServletResponse response,
			@RequestParam(value = "id") String id,
			@RequestParam(value = "start_date") String start_date,
			@RequestParam(value = "end_date") String end_date) throws IOException {
 
	
		List<MeterDataUI> meter_data_list = new ArrayList<MeterDataUI>();
	
		try {

			meter_data_list = FileDao.getByMeterDataBetweenDate(id, start_date, end_date);
		
			if (logger.isDebugEnabled()) {
				logger.debug(meter_data_list);
			}

		} catch (Exception e) {
			logger.error(e);
		}

		String csvFileName = "CrossFields_Report_"+id+" _ "+start_date+" _ "+end_date+".csv";
		 
        response.setContentType("text/csv");
        
        String headerKey = "Content-Disposition";
        String headerValue = String.format("attachment; filename=\"%s\"",
                csvFileName);
        response.setHeader(headerKey, headerValue);
        response.setHeader("x-filename", csvFileName);
       
        ICsvBeanWriter csvWriter = new CsvBeanWriter(response.getWriter(),
                CsvPreference.STANDARD_PREFERENCE);
        
  
        String[] header_user_frendly = { "Name ", "Date Time ", "R-Phase Voltage(V) ",
                "R-Phase Current(A) ", "Power Factor (pf) ", "R-Phase Power (kw) ", "Consumption (kwh)"};
       
        String[] header = {"dcu_id", "utc_date", "r_phase_voltage",
                "current_line_1", "pf_1", "kwh_total", "kwh_total"};
       
        csvWriter.writeHeader(header_user_frendly);
       
       
        for (MeterDataUI tmp : meter_data_list) {
        	
            csvWriter.write(tmp, header);
        }
        csvWriter.close();	 
	}
	

}
