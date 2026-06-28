package com.vetsoft.ccms.netty.parser.events;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vetsoft.ccms.netty.cfg.EventIdInformation;
import com.vetsoft.ccms.netty.parser.BaseUtil;
import com.vetsoft.ccms.netty.pojo.AllEventData;
import com.vetsoft.ccms.netty.pojo.Event;
import com.vetsoft.ccms.netty.pojo.IOPojo;
import com.vetsoft.ccms.netty.pojo.InstantEventData;
import com.vetsoft.ccms.netty.pojo.MeterData;
import com.vetsoft.ccms.netty.pojo.SinglePhaseMeterData;
import com.vetsoft.ccms.netty.repos.DeviceRequestDataRepository;

public class EventRequestProcessor {

	private static final Logger LOG = LoggerFactory
			.getLogger(EventRequestProcessor.class);
	public static  void processAndParseDeviceData(String buffer, DeviceRequestDataRepository repos_context, String dcu_serial_number) {
		if (LOG.isDebugEnabled()) 
			LOG.debug("DATA ++++++++++++++ : " + buffer);
		
		EventsParser event_parser = new EventsParser();
		try {
			
			
			int  total_buff_len = buffer.length();
			
			System.out.println(total_buff_len);
			
			// -2 to ignore crc
			for(int index = 0; index < (total_buff_len - 2); ) {
				
				int time_stamp = Integer.parseInt(buffer.substring(index, (index + 8)), 16); // 4 byte
				index += 8;
				
				long node_id = Long.parseLong(buffer.substring(index, (index + 8)), 16); // 4 byte
				index += 8;
				
				int event_id = Integer.parseInt(buffer.substring(index, (index + 4)), 16); // 4 byte
				index += 4;
				
				int event_len = Integer.parseInt(buffer.substring(index, (index + 4)), 16); // 4 byte
				index += 4;
				
				/*System.out.println(time_stamp);
				System.out.println(node_id);
				System.out.println("EVEN ID : " + event_id);
				System.out.println(event_len);
				System.out.println(buffer.substring(index, (index + (event_len * 2))));*/
				 if (LOG.isDebugEnabled()) 
						LOG.debug("EVEN ID : " + event_id + " EVEN LEN : " + event_len  + " BUFFER : "+ buffer.substring(index, (index + (event_len * 2))));
						
				if(event_id == 0) {
					//if(event_len > 4){ // check for proper len condition, event len = 138 means it contains meter data parse the same, after 12 bytes of IO data
					IOPojo io_obj = event_parser.ioEventBufferParser(buffer.substring(index, (index + (event_len * 2))));
					 if (LOG.isDebugEnabled()) 
							LOG.debug(io_obj.toString());
					io_obj.setDcu_id(dcu_serial_number);
					io_obj.setNode_id(node_id);
					io_obj.setTime_stamp(time_stamp);
					io_obj.setId(System.currentTimeMillis()/1000);
					io_obj.setYymmdd(BaseUtil.getCurrentYYMMDD());
					repos_context.saveIOData(io_obj);
					event_parser.generateIOEvent(io_obj, repos_context);
					//}
				} else if(event_id == 87){
					 if (LOG.isDebugEnabled()) 
							LOG.debug("ALL EVENT REQUEST");
					index += 2; 
					
					processAllEventData(buffer.substring(index, (index + (84))), dcu_serial_number, repos_context, event_parser, time_stamp, node_id);
					
				} else if(event_id == 1061){ // Meter Data for OpCode A
					MeterData meter_obj;
					if(event_len > 10) {
						  meter_obj = event_parser.meterBufferParser(buffer.substring(index, (index + (event_len * 2))));
						  meter_obj.setId(System.currentTimeMillis()/1000);
						  meter_obj.setTime_stamp(time_stamp);
						  meter_obj.setDcu_id(dcu_serial_number);
						  meter_obj.setNode_id(node_id);
						  meter_obj.setYymmdd(BaseUtil.getCurrentYYMMDD());
						  repos_context.saveMeterData(meter_obj);
						  //repos_context.saveMeterInstantData(meter_obj);
						  if (LOG.isDebugEnabled()) 
								LOG.debug(meter_obj.toString());
					}
				
				} else if(event_len == 132){ /* for some of the events may contains meter data events like (24, 31, 32, 36, 37, 38) (defult size of meter data is 132 so we are parssing like this) */
					try {
						LOG.debug(EventIdInformation.eventInformation.get(event_id));
						EventsParser.generateEventAndInstantEvent(event_id, dcu_serial_number, time_stamp, node_id, repos_context);
					} catch(Exception e){
						LOG.error(e.getMessage());
						LOG.error("Exception while getting Single Alert : : "+e.getStackTrace());
					}
					/*
					 * here need to check single phase or 3 phase meter 
					 */
					MeterData meter_obj;
					if(event_len > 10) {
						LOG.debug("EVENT AND METER DATA :: "+ buffer.substring(index, (index + (event_len * 2))) );
						  meter_obj = event_parser.meterBufferParser(buffer.substring(index, (index + (event_len * 2))));
						  meter_obj.setId(System.currentTimeMillis()/1000);
						  meter_obj.setDcu_id(dcu_serial_number);
						  meter_obj.setTime_stamp(time_stamp);
						  meter_obj.setNode_id(node_id);
						  meter_obj.setYymmdd(BaseUtil.getCurrentYYMMDD());
						  repos_context.saveMeterData(meter_obj);
						//  repos_context.saveMeterInstantData(meter_obj);
						  if (LOG.isDebugEnabled()) 
								LOG.debug(meter_obj.toString());
					} else {
						LOG.error("UNHANDLED CASE IGNORRING INSAID METER DATA AND EVENT DATA PARSSING");
					}
				}  else if(event_id == 1073){
					if(LOG.isDebugEnabled()) {
						LOG.debug("OPCODE 2 METER DATA "+ buffer.substring(index, (index + (event_len * 2))));
					}
					SinglePhaseMeterData obj = SinglePhaseMeterDataParser.parseSinglePhaseMeterData(buffer.substring(index, (index + (event_len * 2))));
					if(obj != null){
						obj.setId(System.currentTimeMillis()/1000);
						  obj.setDcu_id(dcu_serial_number);
						  obj.setTime_stamp(time_stamp);
						  obj.setNode_id(node_id);
						  obj.setYymmdd(BaseUtil.getCurrentYYMMDD());
						  //	LOG.debug(obj.toString());
							obj.setId(System.currentTimeMillis()/1000);
							repos_context.saveSinglePhaseMeterData(obj);
							
						  repos_context.saveMeterInstantData(obj);
					}
				} else{
					if(event_id < 86){
						try {
							if(EventIdInformation.eventInformation.get(event_id) != null){
								
									
									LOG.debug(EventIdInformation.eventInformation.get(event_id));
									EventsParser.generateEventAndInstantEvent(event_id, dcu_serial_number, time_stamp, node_id, repos_context);
								
							} else {
								//LOG.error("ERROR : UNHANDLED CASE IGNORRING EVENT ID : "+ event_id);
							}
						} catch(Exception e){
							LOG.error(e.getMessage());
							LOG.error("Exception while getting Single Alert : : "+e.getStackTrace());
						}
					} else {
						;//LOG.error("ERROR : UNHANDLED CASE IGNORRING EVENT ID : "+ event_id);
					}
					
				}
				
				if(event_id == 87){
					index += 84;
				} else {
					index += (event_len *2);
				}
			}
			
			
		} catch (Exception e) {
			
			LOG.error(e.getMessage());
			LOG.error("Exception : : "+e.getStackTrace());
		}
	}
	
	private static void processAllEventData(String buffer,
			String dcu_serial_number, DeviceRequestDataRepository repos_context, EventsParser event_parser, int time_stamp, long node_id) {
		try {
			
			AllEventData db_obj = repos_context.getAllEventDataById(dcu_serial_number);
			
			AllEventData device_obj = event_parser.allEventBufferParser(buffer);
			device_obj.setDcu_id(dcu_serial_number);
			device_obj.setTime_stamp(time_stamp);
		
			repos_context.saveAllEvntDataData(device_obj);
			if(db_obj != null){
				if(db_obj.getRed_phase_no_output() != device_obj.getRed_phase_no_output()){
					genrateEvent(device_obj, 1, device_obj.getRed_phase_no_output(), repos_context, node_id);
					genrateInstantEvent(device_obj, 1, device_obj.getRed_threshold_cross_v(), repos_context, node_id);
				}
				
				/*if(db_obj.getYellow_phase_no_output() != device_obj.getYellow_phase_no_output()){
					genrateEvent(device_obj, 1, device_obj.getYellow_phase_no_output(), repos_context, node_id);
				}
				
				
				if(db_obj.getBlue_phase_no_output() != device_obj.getBlue_phase_no_output()){
					genrateEvent(device_obj, 1, device_obj.getBlue_phase_no_output(), repos_context, node_id);
				}
				*/
				if(db_obj.getRed_threshold_cross_i_high() != device_obj.getRed_threshold_cross_i_high()){
					genrateEvent(device_obj, 7, device_obj.getRed_threshold_cross_i_high(), repos_context, node_id);
				}
				
				/*if(db_obj.getYellow_threshold_cross_i_high() != device_obj.getYellow_threshold_cross_i_high()){
					genrateEvent(device_obj, 1, device_obj.getYellow_threshold_cross_i_high(), repos_context, node_id);
				}
				
				
				if(db_obj.getBlue_threshold_cross_i_high() != device_obj.getBlue_threshold_cross_i_high()){
					genrateEvent(device_obj, 1, device_obj.getBlue_threshold_cross_i_high(), repos_context, node_id);
				}
				*/
				
				if(db_obj.getRed_threshold_cross_i() != device_obj.getRed_threshold_cross_i()){
					genrateEvent(device_obj, 9, device_obj.getRed_threshold_cross_i(), repos_context, node_id);
				}
				
				/*if(db_obj.getYellow_threshold_cross_i() != device_obj.getYellow_threshold_cross_i()){
					genrateEvent(device_obj, 1, device_obj.getYellow_threshold_cross_i(), repos_context, node_id);
				}
				
				if(db_obj.getBlue_threshold_cross_i() != device_obj.getBlue_threshold_cross_i()){
					genrateEvent(device_obj, 1, device_obj.getBlue_threshold_cross_i(), repos_context, node_id);
				}*/
				
				if(db_obj.getRed_threshold_cross_v_low() != device_obj.getRed_threshold_cross_v_low()){
					genrateEvent(device_obj, 10, device_obj.getRed_threshold_cross_v_low(), repos_context, node_id);
				}
				
			/*	if(db_obj.getYellow_threshold_cross_v_low() != device_obj.getYellow_threshold_cross_v_low()){
					genrateEvent(device_obj, 1, device_obj.getYellow_threshold_cross_v_low(), repos_context, node_id);
				}
				
				if(db_obj.getBlue_threshold_cross_v_low() != device_obj.getBlue_threshold_cross_v_low()){
					genrateEvent(device_obj, 1, device_obj.getBlue_threshold_cross_v_low(), repos_context, node_id);
				}*/
				
				if(db_obj.getRed_threshold_cross_v() != device_obj.getRed_threshold_cross_v()){
					genrateEvent(device_obj, 13, device_obj.getRed_threshold_cross_v(), repos_context, node_id);
					
				}
				
				/*if(db_obj.getYellow_threshold_cross_v() != device_obj.getYellow_threshold_cross_v()){
					genrateEvent(device_obj, 1, device_obj.getYellow_threshold_cross_v(), repos_context, node_id);
				}
				
				if(db_obj.getBlue_threshold_cross_v() != device_obj.getBlue_threshold_cross_v()){
					genrateEvent(device_obj, 1, device_obj.getBlue_threshold_cross_v(), repos_context, node_id);
				}*/
				
				if(db_obj.getRed_mains_supply() != device_obj.getRed_mains_supply()){
					genrateEvent(device_obj, 16, device_obj.getRed_mains_supply(), repos_context, node_id);
					genrateInstantEvent(device_obj, 16, device_obj.getRed_threshold_cross_v(), repos_context, node_id);
				}
				
				/*if(db_obj.getYellow_mains_supply() != device_obj.getYellow_mains_supply()){
					genrateEvent(device_obj, 1, device_obj.getYellow_mains_supply(), repos_context, node_id);
				}
				
				if(db_obj.getBlue_mains_supply() != device_obj.getBlue_mains_supply()){
					genrateEvent(device_obj, 1, device_obj.getBlue_mains_supply(), repos_context, node_id);
				}*/
				
				if(db_obj.getMcb_trip() != device_obj.getMcb_trip()){
					genrateEvent(device_obj, 19, device_obj.getMcb_trip(), repos_context, node_id);
					genrateInstantEvent(device_obj, 19, device_obj.getRed_threshold_cross_v(), repos_context, node_id);
				}
				
				if(db_obj.getRed_cntrct_fail() != device_obj.getRed_cntrct_fail()){
					genrateEvent(device_obj, 20, device_obj.getRed_cntrct_fail(), repos_context, node_id);
					genrateInstantEvent(device_obj, 20, device_obj.getRed_threshold_cross_v(), repos_context, node_id);
				}
				
				/*if(db_obj.getYellow_cntrct_fail() != device_obj.getYellow_cntrct_fail()){
					genrateEvent(device_obj, 1, device_obj.getYellow_cntrct_fail(), repos_context, node_id);
				}
				
				if(db_obj.getBlue_cntrct_fail() != device_obj.getBlue_cntrct_fail()){
					genrateEvent(device_obj, 1, device_obj.getBlue_cntrct_fail(), repos_context, node_id);
				}*/
				
				if(db_obj.getDoor_alert() != device_obj.getDoor_alert()){
					genrateEvent(device_obj, 23, device_obj.getDoor_alert(), repos_context, node_id);
					genrateInstantEvent(device_obj, 23, device_obj.getRed_threshold_cross_v(), repos_context, node_id);
				}
				
				if(db_obj.getContractor_failure() != device_obj.getContractor_failure()){
					genrateEvent(device_obj, 24, device_obj.getContractor_failure(), repos_context, node_id);
					genrateInstantEvent(device_obj, 24, device_obj.getRed_threshold_cross_v(), repos_context, node_id);
				}
				
				
				
			/*	if(db_obj.getYellow_mcb_trip() != device_obj.getYellow_mcb_trip()){
					genrateEvent(device_obj, 1, device_obj.getYellow_mcb_trip(), repos_context, node_id);
				}
				
				if(db_obj.getBlue_mcb_trip() != device_obj.getBlue_mcb_trip()){
					genrateEvent(device_obj, 1, device_obj.getBlue_mcb_trip(), repos_context, node_id);
				}*/
				
				
				if(db_obj.getCommon_mcb_trip() != device_obj.getCommon_mcb_trip()){
					genrateEvent(device_obj, 27, device_obj.getCommon_mcb_trip(), repos_context, node_id);
					genrateInstantEvent(device_obj, 27, device_obj.getRed_threshold_cross_v(), repos_context, node_id);
				}
				
				if(db_obj.getAuto_manual() != device_obj.getAuto_manual()){
					genrateEvent(device_obj, 28, device_obj.getAuto_manual(), repos_context, node_id);
					genrateInstantEvent(device_obj, 28, device_obj.getRed_threshold_cross_v(), repos_context, node_id);
				}
				
				if(db_obj.getR_surge_prtctr_trip() != device_obj.getR_surge_prtctr_trip()){
					genrateEvent(device_obj, 29, device_obj.getR_surge_prtctr_trip(), repos_context, node_id);
					genrateInstantEvent(device_obj, 29, device_obj.getRed_threshold_cross_v(), repos_context, node_id);
				}
				
			/*	if(db_obj.getY_surge_prtctr_trip() != device_obj.getY_surge_prtctr_trip()){
					genrateEvent(device_obj, 1, device_obj.getY_surge_prtctr_trip(), repos_context, node_id);
				}
				
				if(db_obj.getB_surge_prtctr_trip() != device_obj.getB_surge_prtctr_trip()){
					genrateEvent(device_obj, 1, device_obj.getB_surge_prtctr_trip(), repos_context, node_id);
				}
				*/
				if(db_obj.getCommon_surge_prtctr_trip() != device_obj.getCommon_surge_prtctr_trip()){
					genrateEvent(device_obj, 32, device_obj.getCommon_surge_prtctr_trip(), repos_context, node_id);
					genrateInstantEvent(device_obj, 32, device_obj.getRed_threshold_cross_v(), repos_context, node_id);
				}
				
				if(db_obj.getRtu_mains() != device_obj.getRtu_mains()){
					genrateEvent(device_obj, 33, device_obj.getRtu_mains(), repos_context, node_id);
					genrateInstantEvent(device_obj, 33, device_obj.getRed_threshold_cross_v(), repos_context, node_id);
				}
			}
		} catch (Exception e) {
			LOG.error(e.getMessage());
			LOG.error("Exception : : "+e.getStackTrace());
		}
		
	}

	private static void genrateEvent(AllEventData device_obj, int event_id, int event_status, DeviceRequestDataRepository repos_context, long node_id) {
		try {
			
			if(LOG.isDebugEnabled())
				LOG.debug("GENERATTING EVENT ");
			Event event = new Event();
			event.setGateway_serial_number(device_obj.getDcu_id());
			event.setTime_stamp(device_obj.getTime_stamp());
			event.setEvent_id(event_id);
			event.setEvent_status(event_status);
			event.setNode_identifier(node_id); // add node identifier
			event.setId(System.currentTimeMillis()/1000);
			event.setYymmdd(BaseUtil.getCurrentYYMMDD());
			if (LOG.isDebugEnabled()) 
				LOG.debug(event.toString());
			repos_context.addOrUpdateEvent(event);
		
		} catch (Exception e) {
			
			LOG.error(e.getMessage());
			LOG.error("Exception while adding event : "+e.getStackTrace());
		}
	}
	
	
	private static void genrateInstantEvent(AllEventData device_obj, int event_id, int event_status, DeviceRequestDataRepository repos_context, long node_id) {
		try {
			
			if(LOG.isDebugEnabled())
				LOG.debug("GENERATTING EVENT ");
			InstantEventData event = new InstantEventData();
			event.setGateway_serial_number(device_obj.getDcu_id());
			event.setTime_stamp(device_obj.getTime_stamp());
			event.setEvent_id(event_id);
			event.setEvent_status(event_status);
			event.setNode_identifier(node_id); // add node identifier
			
			if (LOG.isDebugEnabled()) 
				LOG.debug(event.toString());
			repos_context.addOrUpdateInstantEvent(event);
		
		} catch (Exception e) {
			//System.out.println("Exception while adding event : "+ e.getMessage());
			LOG.error(e.getMessage());
			LOG.error("Exception while adding Instant event : "+e.getStackTrace());
		}
	}
	
	
}
