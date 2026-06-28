package com.vetsoft.ccms.netty.parser.events;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vetsoft.ccms.netty.cfg.ALLAlertsNotifications;
import com.vetsoft.ccms.netty.cfg.Constants;
import com.vetsoft.ccms.netty.parser.BaseUtil;
import com.vetsoft.ccms.netty.pojo.AllEventData;
import com.vetsoft.ccms.netty.pojo.Event;
import com.vetsoft.ccms.netty.pojo.IOPojo;
import com.vetsoft.ccms.netty.pojo.InstantEventData;
import com.vetsoft.ccms.netty.pojo.MeterData;
import com.vetsoft.ccms.netty.repos.DeviceRequestDataRepository;



public class EventsParser {

	private static final Logger LOG = LoggerFactory
			.getLogger(EventsParser.class);
	
	/*
	 * This method will take only io event data and returns the IO event object
	 *  
	 */
	public IOPojo ioEventBufferParser(String io_event_data) {
		
		int index = 0; 
		
		IOPojo io_obj = new IOPojo();
		
		try {
			
			int status = Integer.parseInt(io_event_data.substring(index, (index + 2)), 16); // 4 byte
			index += 2;
			//io_obj.setStatus(status);
			
			int opration_type =  Integer.parseInt(io_event_data.substring(index, (index + 2)), 16);   //Operation type(01= ON/OFF)
			index += 2;
			io_obj.setOpration_type(opration_type);
			/*if(opration_type == 1){
				io_obj.setOpration_type("ON/OFF");
			} else if(opration_type == 2){
				io_obj.setOpration_type("DIM");
			} else {
				io_obj.setOpration_type("UNKNOW-"+opration_type);
			}*/
			
			int opration_value =  Integer.parseInt(io_event_data.substring(index, (index + 4)), 16);  //Operation value  (00=OFF, 01=ON)
			index += 4;
			io_obj.setOpration_value(opration_value);
		/*	if(opration_value == 0){
				io_obj.setOpration_value("OFF");
			} else if(opration_value == 1){
				io_obj.setOpration_value("ON");
			} else if(opration_value == 2){
				io_obj.setOpration_value("DIM");
			} else {
				io_obj.setOpration_value("UNKNOW-"+opration_value);
			}
			*/
			int opration_resone =  Integer.parseInt(io_event_data.substring(index, (index + 2)), 16); 
			index += 2;
			io_obj.setOpration_resone(opration_resone);
			/* IO_CNTRL_FROM_UI,    0. IO is controlled from user screen.  
			 * IO_CNTRL_FROM_SCH,  1. IO is controlled from schedule.   
			 * IO_CNTRL_FROM_SMS,    2. IO is controlled from sms. */
/*
			if(opration_resone == 0){
				io_obj.setOpration_resone("IO_CNTRL_FROM_UI");
			} else if(opration_resone == 1){
				io_obj.setOpration_resone("IO_CNTRL_FROM_SCH");
			}else if(opration_resone == 2){
				io_obj.setOpration_resone("IO_CNTRL_FROM_SMS");
			} else {
				io_obj.setOpration_resone("IO_CNTRL_FROM_UNKOWN-"+ opration_resone);
			}*/
			
			int phase_reson =  Integer.parseInt(io_event_data.substring(index, (index + 2)), 16); 
			index += 2; ////phase reson  1, 1,2,3 (parese and ignore as we are going with single phase)
			//io_obj.setPhase_reson(phase_reson);
			
			if (LOG.isDebugEnabled()) 
				LOG.debug(io_obj.toString());
		} catch (Exception e) {
			LOG.error(e.getMessage());
			LOG.error("Exception while parssing IO packet : "+e.getStackTrace());
			
		}
		return io_obj;
	}
	
	
	
	
	public AllEventData allEventBufferParser(String io_event_data) {
		if (LOG.isDebugEnabled()) 
			LOG.debug("EVENT DATA : "+ io_event_data);
		
		AllEventData obj = new AllEventData();
		
		int index = 0;
		try {
			
			for(int j = 1; j < 34; j++){
							
							int event_status =  Integer.parseInt(io_event_data.substring(index, (index + 2)), 16); 
							index +=2;
							if (LOG.isDebugEnabled()) 
								LOG.debug("EVENT [ " + ALLAlertsNotifications.allAllertsNotificationSubparrsing.get(j) + "]  STATUS [ " + event_status + " ]");
							
							obj = setAllEventStatus(obj, j, event_status);
							
			}
			
			index +=2; // 	1 byte-Total relay count
			
			// (1-ON, 0-OFF)
			int io_r_relay = Integer.parseInt(io_event_data.substring(index, (index + 2)), 16);
			index +=2;
			obj.setIo_r_relay(io_r_relay);
			
			int io_y_relay = Integer.parseInt(io_event_data.substring(index, (index + 2)), 16);
			index +=2;
			obj.setIo_y_relay(io_y_relay);
			
			int io_b_relay = Integer.parseInt(io_event_data.substring(index, (index + 2)), 16);
			index +=2;
			obj.setIo_b_relay(io_b_relay);
			
			int io_all_relay = Integer.parseInt(io_event_data.substring(index, (index + 2)), 16);
			index +=2;
			obj.setIo_all_relay(io_all_relay);
			
			if (LOG.isDebugEnabled()) 
				LOG.debug("ALL LERTS  : " + obj.toString());
			
			return obj;
		} catch (Exception e) {
			LOG.error(e.getMessage());
			LOG.error("Exception : : "+e.getStackTrace());
		}
		
		return null;
	}
	
	
	private AllEventData setAllEventStatus(AllEventData obj, int j,
			int event_status) {
		
		
		try {
			
			if(j == 1){
				obj.setRed_phase_no_output(event_status);
			}else if(j == 2){
				obj.setYellow_phase_no_output(event_status);
			}else if(j == 3){
				obj.setBlue_phase_no_output(event_status);
			}else if(j == 4){
				obj.setRed_threshold_cross_i_high(event_status);
			}else if(j == 5){
				obj.setYellow_threshold_cross_i_high(event_status);
			}else if(j == 6){
				obj.setBlue_threshold_cross_i_high(event_status);
			}else if(j == 7){
				obj.setRed_threshold_cross_i(event_status);
			}else if(j == 8){
				obj.setYellow_threshold_cross_i(event_status);
			}else if(j == 9){
				obj.setBlue_threshold_cross_i(event_status);
			}else if(j == 10){
				obj.setRed_threshold_cross_v_low(event_status);
			}else if(j == 11){
				obj.setYellow_threshold_cross_v_low(event_status);
			}else if(j == 12){
				obj.setBlue_threshold_cross_v_low(event_status);
			}else if(j == 13){
				obj.setRed_threshold_cross_v(event_status);
			}else if(j == 14){
				obj.setYellow_threshold_cross_v(event_status);
			}else if(j == 15){
				obj.setBlue_threshold_cross_v(event_status);
			}else if(j == 16){
				obj.setRed_mains_supply(event_status);
			}else if(j == 17){
				obj.setYellow_mains_supply(event_status);
			}else if(j == 18){
				obj.setBlue_mains_supply(event_status);
			}else if(j == 19){
				obj.setMcb_trip(event_status);
			}else if(j == 20){
				obj.setRed_cntrct_fail(event_status);
			}else if(j == 21){
				obj.setYellow_cntrct_fail(event_status);
			}else if(j == 22){
				obj.setBlue_cntrct_fail(event_status);
			}else if(j == 23){
				obj.setDoor_alert(event_status);
			}else if(j == 24){
				obj.setContractor_failure(event_status);
			}else if(j == 25){
				obj.setYellow_mcb_trip(event_status);
			}else if(j == 26){
				obj.setBlue_mcb_trip(event_status);
			}else if(j == 27){
				obj.setCommon_mcb_trip(event_status);
			}else if(j == 28){
				obj.setAuto_manual(event_status);
			}else if(j == 29){
				obj.setR_surge_prtctr_trip(event_status);
			}else if(j == 30){
				obj.setY_surge_prtctr_trip(event_status);
			}else if(j == 31){
				obj.setB_surge_prtctr_trip(event_status);
			}else if(j == 32){
				obj.setCommon_surge_prtctr_trip(event_status);
			}else if(j == 33){
				obj.setRtu_mains(event_status);
			}
			
			else {
				if (LOG.isDebugEnabled()) 
					LOG.debug("UNKNOW ALERT : " + event_status);
			}
			
		} catch (Exception e) {
			LOG.error(e.getMessage());
			LOG.error("Exception : : "+e.getStackTrace());
		}
		return obj;
	}




	public MeterData meterBufferParser(String io_event_data) {
		MeterData obj = new MeterData();
		 int index = 0;
		 
		 if (LOG.isDebugEnabled()) 
				LOG.debug("PROCESSING METER OBJECT : "+ io_event_data);
		try {
			
			/*obj.setSlave_id(Integer.parseInt(
					io_event_data.substring(index, (index + 2)), 16));
				ignoring as these fields are not used
			*/
			index += 2;

		/*	obj.setFunction_code(Integer.parseInt(
					io_event_data.substring(index, (index + 2)), 16));
					ignoring as these fields are not used
					*/
			index += 2;
			
			
			/*double tmp = Double.parseDouble(
					io_event_data.substring(index, (index + 8)), 16);*/
			obj.setFrequency(getMeterDecimalData(io_event_data.substring(index, (index + 8)) ,Constants.Frequency_DF));
			index += 8;
			
			index += 8; // skip byte
			index += 8;
			index += 8;
			index += 8; // skip byte
			
			/*tmp = Long.parseLong(
					io_event_data.substring(index, (index + 8)), 16);*/
			obj.setR_phase_voltage(getMeterDecimalData(io_event_data.substring(index, (index + 8)) ,Constants.Voltage_DF));
			index += 8; 
			
			
			/*tmp = Long.parseLong(
					io_event_data.substring(index, (index + 8)), 16);*/
			obj.setY_phase_voltage(getMeterDecimalData(io_event_data.substring(index, (index + 8)) ,Constants.Voltage_DF));
			index += 8; 
			
			
			/*tmp = Long.parseLong(
					io_event_data.substring(index, (index + 8)), 16);*/
			obj.setB_phase_voltage(getMeterDecimalData(io_event_data.substring(index, (index + 8)) ,Constants.Voltage_DF));
			index += 8; 
			
			
			;
		/*	tmp = Long.parseLong(
					io_event_data.substring(index, (index + 8)), 16);
			obj.setEqu_current_3_phase(String.valueOf(tmp/Constants.Current_DF));
			*/
			/*tmp = Long.parseLong(
					io_event_data.substring(index, (index + 8)), 16);*/
			obj.setEqu_current_3_phase(getMeterDecimalData(io_event_data.substring(index, (index + 8)) , Constants.Current_DF));
			
			index += 8; 
			
			
			/*tmp = Long.parseLong(
					io_event_data.substring(index, (index + 8)), 16);
			obj.setCurrent_line_1(String.valueOf(tmp/Constants.Current_DF));
			index += 8; */
			/*tmp = Long.parseLong(
					io_event_data.substring(index, (index + 8)), 16);*/
			obj.setCurrent_line_1(getMeterDecimalData(io_event_data.substring(index, (index + 8)) , Constants.Current_DF));
			index += 8; 
			
			/*tmp = Long.parseLong(
					io_event_data.substring(index, (index + 8)), 16);*/
			obj.setCurrent_line_2(getMeterDecimalData(io_event_data.substring(index, (index + 8)) ,Constants.Current_DF));
			index += 8; 
			
			
			/*tmp = Long.parseLong(
					io_event_data.substring(index, (index + 8)), 16);*/
			obj.setCurrent_line_3(getMeterDecimalData(io_event_data.substring(index, (index + 8)) ,Constants.Current_DF));
			index += 8; 
			
			/*tmp = Long.parseLong(
					io_event_data.substring(index, (index + 8)), 16);*/
			obj.setEqu_power_3_phase(getMeterDecimalData(io_event_data.substring(index, (index + 8)) ,Constants.PowerFactor_DF));
			index += 8; 
			
		/*	tmp = Long.parseLong(
					io_event_data.substring(index, (index + 8)), 16);*/
			obj.setPf_1(getMeterDecimalData(io_event_data.substring(index, (index + 8)) ,Constants.PowerFactor_DF));
			index += 8; 
			
			/*tmp = Long.parseLong(
					io_event_data.substring(index, (index + 8)), 16);*/
			obj.setPf_2(getMeterDecimalData(io_event_data.substring(index, (index + 8)) ,Constants.PowerFactor_DF));
			index += 8; 
			
			
			/*tmp = Long.parseLong(
					io_event_data.substring(index, (index + 8)), 16);*/
			obj.setPf_3(getMeterDecimalData(io_event_data.substring(index, (index + 8)) ,Constants.PowerFactor_DF));
			index += 8; 
			
			/*tmp = Long.parseLong(
					io_event_data.substring(index, (index + 8)), 16);*/
			obj.setActive_power(getMeterDecimalData(io_event_data.substring(index, (index + 8)) ,Constants.ActivePower_DF));
			index += 8; 
			
		/*	tmp = Long.parseLong(
					io_event_data.substring(index, (index + 8)), 16);*/
			obj.setApparent_power(getMeterDecimalData(io_event_data.substring(index, (index + 8)) ,Constants.ApparentPower_DF));
			index += 8; 
			
			
			/*tmp = Long.parseLong(
					io_event_data.substring(index, (index + 8)), 16);*/
			obj.setReactive_power(getMeterDecimalData(io_event_data.substring(index, (index + 8)) ,Constants.ReactivePower_DF));
			index += 8; 
			
			index += 8; // skip byte
			index += 8;
			index += 8;
			index += 8; // skip byte
			
			/*tmp = Long.parseLong(
					io_event_data.substring(index, (index + 8)), 16);*/
			obj.setKwh_total(getMeterDecimalData(io_event_data.substring(index, (index + 8)) ,Constants.KWHTOTAL_DF));
			index += 8; 
			
			
			index += 8; // skip byte
			index += 8; // skip byte
			
			
			
			long tmp = Long.parseLong(
					io_event_data.substring(index, (index + 8)), 16);
			obj.setEarth_tamper(String.valueOf(tmp));
			index += 8; 
			
			tmp = Long.parseLong(
					io_event_data.substring(index, (index + 8)), 16);
			obj.setReverse_tamper(String.valueOf(tmp));
			index += 8; 
			
			
			tmp = Long.parseLong(
					io_event_data.substring(index, (index + 8)), 16);
			obj.setCover_open_tamper(String.valueOf(tmp));
			index += 8; 
			
			/*tmp = Long.parseLong(
					chunk_data.substring(index, (index + 8)), 16);
			obj.setMagnetic_tamper(String.valueOf(tmp));
			index += 8; */
			
			
		} catch (Exception e) {
			
			LOG.error(e.getMessage());
			LOG.error("Exception while parssing meter object : "+e.getStackTrace());
		}
		
		return obj;
	}
	

	public static String getMeterDecimalData(String val, int devision_factor) {
		double d = 0;
		try {
			d = ((double) (Long.parseLong(val, 16)) / (double) devision_factor);
		//	System.out.println(d);

		} catch (Exception e) {
			System.out.println("Exception while converting hex to decimal");
		}

		return String.valueOf(d);
	}
	
public static void processSingleEventRequest(int index, String sTextAsHex, String device_serial_number, DeviceRequestDataRepository repos_context) {
	
	Event event = new Event();
	
	
	try {
		if (LOG.isDebugEnabled()) 
			LOG.debug("SINGLE EVENT REQUEST RECIVED : "+ sTextAsHex);
		
			event.setId(System.currentTimeMillis());
			event.setGateway_serial_number(device_serial_number);
			
			int time_stamp = Integer.parseInt(sTextAsHex.substring(index, (index + 8)), 16); // 4 byte
			index += 8;
			
			event.setTime_stamp(time_stamp); // UTC to IST
			
			long node_id = Long.parseLong(sTextAsHex.substring(index, (index + 8)), 16); // 4 byte
			index += 8;
			event.setNode_identifier(node_id);
			
			int event_id = Integer.parseInt(sTextAsHex.substring(index, (index + 4)), 16); // 4 byte
			index += 4;
		
			event.setEvent_id(event_id);
			
			int event_data = Integer.parseInt(sTextAsHex.substring(index, (index + 4)), 16); // 4 byte
			index += 4;
			//event.setEvent_status(event_data); // Need to Update proper value as per EventIdInformation
			
			if(event_id == 1061){
				if (LOG.isInfoEnabled()) 
					LOG.info("SINGLE EVENT METER DATA IGNORING : " + sTextAsHex);
				
				return;
			}
			event.setYymmdd(BaseUtil.getCurrentYYMMDD());
			if (LOG.isDebugEnabled()) 
				LOG.debug("SINGLE EVENT " + event.toString());
			repos_context.addOrUpdateEvent(event);
		
			InstantEventData inst_event = new InstantEventData();
			inst_event.setGateway_serial_number(device_serial_number);
			inst_event.setTime_stamp(time_stamp);
			inst_event.setEvent_id(event_id);
			inst_event.setEvent_status(event_data);
			inst_event.setNode_identifier(node_id); // add node identifier
			
			if (LOG.isDebugEnabled()) 
				LOG.debug("ADDING INSTANT EVENT : " + inst_event.toString());
			repos_context.addOrUpdateInstantEvent(inst_event);
			
			
	} catch (Exception e) {
		
		LOG.error(e.getMessage());
		LOG.error("Exception while processing single event : "+e.getStackTrace());
	}
	
	
	}




public static void generateEventAndInstantEvent(int event_id,
		String dcu_serial_number, int time_stamp, long node_id, DeviceRequestDataRepository repos_context) {
	try {
	
	InstantEventData inst_event = new InstantEventData();
	inst_event.setGateway_serial_number(dcu_serial_number);
	inst_event.setTime_stamp(time_stamp);
	inst_event.setEvent_id(event_id);
	inst_event.setEvent_status(3333);
	inst_event.setNode_identifier(node_id); // add node identifier
	
	if (LOG.isDebugEnabled()) 
		LOG.debug("ADDING INSTANT EVENT : " + inst_event.toString());
	repos_context.addOrUpdateInstantEvent(inst_event);
	
	
	Event event = new Event();
	event.setId(System.currentTimeMillis()/1000);
	event.setGateway_serial_number(dcu_serial_number);
	
	event.setTime_stamp(time_stamp);
	event.setNode_identifier(node_id);
	event.setEvent_id(event_id);
	event.setYymmdd(BaseUtil.getCurrentYYMMDD());
	//inst_event.setEvent_status(3333);
	
	if (LOG.isDebugEnabled()) 
		LOG.debug("SINGLE EVENT " + event.toString());
	repos_context.addOrUpdateEvent(event);
} catch (Exception e) {
	LOG.error(e.getMessage());
	LOG.error("Exception while processing single event : "+e.getStackTrace());
}
}




public void generateIOEvent(IOPojo io_obj, DeviceRequestDataRepository repos_context) {
	
	try {
		
	

		Event event = new Event();
		event.setId(System.currentTimeMillis()/1000);
		event.setGateway_serial_number(io_obj.getDcu_id());
		
		event.setTime_stamp(Integer.parseInt(String.valueOf(io_obj.getTime_stamp())));
		event.setNode_identifier(io_obj.getNode_id());
		
		if(io_obj.getOpration_value() == 0){
			event.setEvent_id(304); // OFF
		} else if(io_obj.getOpration_value() == 1){
			event.setEvent_id(303); // ON
		} else if(io_obj.getOpration_value() == 2){
			event.setEvent_id(305); // DIM
		} else {
			event.setEvent_id(306); // UNKNOWN
		}
		
		event.setYymmdd(BaseUtil.getCurrentYYMMDD());
		//inst_event.setEvent_status(3333);
		
		if (LOG.isDebugEnabled()) 
			LOG.debug("IO EVENT GENERATED  " + event.toString());
		repos_context.addOrUpdateEvent(event);
	} catch (Exception e) {
		LOG.error(e.getMessage());
		LOG.error("Exception while processing single event : "+e.getStackTrace());
	}
}

	
	
}
