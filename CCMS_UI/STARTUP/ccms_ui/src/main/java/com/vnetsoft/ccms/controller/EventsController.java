package com.vnetsoft.ccms.controller;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;

import com.vnetsoft.ccms.constants.EventIdInformation;
import com.vnetsoft.ccms.dao.file.FileDao;
import com.vnetsoft.ccms.pojo.Event;
import com.vnetsoft.ccms.pojo.EventUiObject;
import com.vnetsoft.ccms.pojo.server.InstantEventData;
import com.vnetsoft.ccms.pojo.ui.EventUICount;
import com.vnetsoft.ccms.pojo.ui.MeterDataUI;
import com.vnetsoft.ccms.services.DCUServices;
import com.vnetsoft.ccms.util.DateUtils;

@Controller
@RequestMapping("/events")
public class EventsController {

	@Autowired
	DCUServices userServices;

	static final Logger logger = Logger.getLogger(EventsController.class);
	
	
	@RequestMapping(value = "/event_list", method = RequestMethod.GET)
	public @ResponseBody List<EventUiObject> getAllEvents() {


		/*if(logger.isDebugEnabled()) {
			 logger.debug("GET ALL HandShake REQUEST RECIVED");
		}
		
		
		List<InstantEventData> event_list = null;
		List<EventUiObject> updated_event_list = new ArrayList<EventUiObject>();
		
		try {
			event_list = userServices.getInstantEventDataList();
			
			if(logger.isDebugEnabled()) {
				 logger.debug(event_list);
			}
			
			DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			
			for(InstantEventData tmp : event_list){
				EventUiObject obj = new EventUiObject();
				
				  String utc_date  = DateUtils.getEpochTimeFromSeconds(tmp.getTime_stamp());
				  
				 obj.setTime_stamp(utc_date);
				 obj.setEvent_id(tmp.getEvent_id());
				 obj.setEvent_data(EventIdInformation.eventInformation.get(tmp.getEvent_id()));
				 obj.setGateway_serial_number(tmp.getGateway_serial_number());
				 obj.setNode_identifier(tmp.getNode_identifier());
				 updated_event_list.add(obj);
				 
			}
		} catch (Exception e) {
			System.out.println("Exception : " + e);
			e.printStackTrace();
		}

		return updated_event_list;*/
		return null;
	}
	
	
	@RequestMapping(value = "/event_counts", method = RequestMethod.GET)
	public @ResponseBody List<EventUICount> getAllEventsCounts() {

		
		List<Integer> supported_event_id = getEventCountListIDs();
		
		
		
		
		if(logger.isDebugEnabled()) {
			 logger.debug("GET EVENT COUNT REQUEST RECIVED");
		}
		
		List<InstantEventData> event_list = null;
		Map<Integer, EventUICount> updated_event_list = new HashMap<Integer, EventUICount>();
		
		try {
			
			event_list = userServices.getInstantEventDataList();
			
			System.out.println(event_list.size());
			for(InstantEventData tmp : event_list){
				
				if(!supported_event_id.contains(tmp.getEvent_id()))
					continue;
				
				 if(updated_event_list.containsKey(tmp.getEvent_id())) {
					 
					 EventUICount exist_obj = updated_event_list.get(tmp.getEvent_id());
					 int count = exist_obj.getCount();
					 
					 exist_obj.setCount(count + 1);
				 
					 updated_event_list.put(tmp.getEvent_id(), exist_obj);
				 
				 } else {
					 EventUICount obj = new EventUICount();
					 obj.setEvent_id(tmp.getEvent_id());
					 obj.setCount(1);
					 updated_event_list.put(tmp.getEvent_id(), obj);
				 }
				 
			}
		} catch (Exception e) {
			System.out.println("Exception : " + e);
			e.printStackTrace();
		}
		Set<Integer> keys = updated_event_list.keySet();
		
		List<EventUICount> return_list = new ArrayList<EventUICount>();
		
		for(Integer key : keys){
			return_list.add(updated_event_list.get(key));
		}
		System.out.println(return_list);
		return return_list;
	}
	
	

	private List<Integer> getEventCountListIDs() {
		List<Integer> supported_event_id = new ArrayList<Integer>();
		supported_event_id.add(5);// auto manual mode
		supported_event_id.add(4);//cnt failure
		supported_event_id.add(3); // door open
		supported_event_id.add(18); // red cnt failure
		supported_event_id.add(31); // 31 RTU_MAINS_OFF
		supported_event_id.add(33); // 33 RED RTU_MAINS_OFF
		supported_event_id.add(60); // COMMON_MCB_TRIP_OCCURED
		supported_event_id.add(78); //UI_EVENT_R_SURGE_PRTCTR_TRIP
		supported_event_id.add(81); //UI_EVENT_COMMON_SURGE_PRTCTR_TRIP
		supported_event_id.add(21); //RED_PHASE_NO_OUTPUT
		return supported_event_id;
	}
	
	

	@RequestMapping(value = "/events_between_date", method = RequestMethod.GET)
	public @ResponseBody List<EventUiObject> getEventsBetweenDates(
			@RequestParam("id") String id, @RequestParam("start_date") String start_date, @RequestParam("end_date") String end_date) {

		List<EventUiObject> ui_obj_list = null;
		try {
			ui_obj_list = FileDao.getByEventDataBetweenDate(id, start_date, end_date);
			return ui_obj_list;
		} catch (Exception e) {
			e.getStackTrace();
		}
		return ui_obj_list;
	}

	
	

	/*
	 * DOWNLOAD API's
	 * 
	 */
	

	@RequestMapping(value = "/export_events")
	public void exportHistory(HttpServletResponse response,
			@RequestParam(value = "id") String id,
			@RequestParam(value = "start_date") String start_date,
			@RequestParam(value = "end_date") String end_date) throws IOException {
 
	
		List<EventUiObject> meter_data_list = new ArrayList<EventUiObject>();
	
		try {

			meter_data_list = FileDao.getByEventDataBetweenDate(id, start_date, end_date);
		
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
        
  
        String[] header_user_frendly = {  "Name ", "Date Time ", "Event "};
       
        String[] header = {  "gateway_serial_number", "time_stamp", "event_data"};
       
        csvWriter.writeHeader(header_user_frendly);
        
        for (EventUiObject tmp : meter_data_list) {
        	
            csvWriter.write(tmp, header);
        }
        csvWriter.close();	 
	}
}
