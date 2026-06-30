package com.vetsoft.ccms.netty.repos;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import com.vetsoft.ccms.netty.parser.BaseUtil;
import com.vetsoft.ccms.netty.pojo.AllEventData;
import com.vetsoft.ccms.netty.pojo.ChunkDataHolder;
import com.vetsoft.ccms.netty.pojo.DCUConfiguration;
import com.vetsoft.ccms.netty.pojo.DCUSysConfData;
import com.vetsoft.ccms.netty.pojo.Event;
import com.vetsoft.ccms.netty.pojo.HandShake;
import com.vetsoft.ccms.netty.pojo.IOPojo;
import com.vetsoft.ccms.netty.pojo.InstantEventData;
import com.vetsoft.ccms.netty.pojo.InstantMeterData;
import com.vetsoft.ccms.netty.pojo.MeterData;
import com.vetsoft.ccms.netty.pojo.NodeConfData;
import com.vetsoft.ccms.netty.pojo.ScheduleConfData;
import com.vetsoft.ccms.netty.pojo.SinglePhaseMeterData;

@Repository
public class DeviceRequestDataRepository {

	static final Logger logger = LoggerFactory
			.getLogger(DeviceRequestDataRepository.class);
	@Autowired
	private MongoTemplate mongoTemplate;

	public List<HandShake> getAllHandShake() {
		if(logger.isDebugEnabled())
			logger.debug("Get ALL request Recived");
		return mongoTemplate.findAll(HandShake.class);
	}

	public HandShake addOrUpdateHandShake(HandShake user) {
		if(logger.isDebugEnabled())
			logger.debug("==== ADD HAND SHAKE =============== +++++++++++++++++");
		// mongoTemplate.save(user);

		Query query = new Query();
		query.addCriteria(Criteria.where("_id").is(user.getId()));

		Update update = new Update();
		update.set("id", user.getId());
		// update.set("gateway_identifier", user.getGateway_identifier());
		update.set("protocol_version", user.getProtocol_version());
		update.set("flag", user.getFlag());
		update.set("dsn", user.getDsn());
		update.set("command_identifier", user.getCommand_identifire());
		update.set("payload_length", user.getPayload_length());
		update.set("gateway_serial_number", user.getGateway_serial_number());
		update.set("gateway_firmware_type", user.getGateway_firmware_type());
		update.set("storage_detection", user.getStorage_detection());
		update.set("battery_voltage", user.getBattery_voltage());
		update.set("asset_mode", user.getAsset_mode());
		update.set("asset_mode_timestamp", user.getAsset_mode_timestamp());
		update.set("imei_length", user.getImei_length());
		update.set("imei", user.getImei());
		update.set("gsm_version_len", user.getGsm_version_len());
		update.set("gsm_version", user.getGsm_version());
		update.set("mobile_len", user.getMobile_len());
		update.set("mobile_no", user.getMobile_no());
		update.set("imsi_number", user.getImsi_number());
		update.set("iccid_number", user.getIccid_number());
	
		update.set("crc", user.getCrc());
		update.set("csq", user.getCsq());
		update.set("hs_time_stamp", user.getHs_time_stamp());
		update.set("device_time_yymmddhhmm", BaseUtil.getCurrentYYMMDD());
		mongoTemplate.upsert(query, update, HandShake.class);

		HandShake handShake = mongoTemplate.findOne(query, HandShake.class);
	

		return handShake;
	}

	public HandShake getByDeviceSerialNumber(String serial_number) {
		
		Query query = new Query();
		query.addCriteria(Criteria.where("_id").is(serial_number));

		HandShake obj = mongoTemplate.findOne(query, HandShake.class);
		return obj;
	}

	public List<Event> getAllEvents() {
		return mongoTemplate.findAll(Event.class);
	}

	public Event addOrUpdateEvent(Event user) {
		if(logger.isDebugEnabled())
			logger.debug("==== ADD EVENT ===============" + user);
		mongoTemplate.save(user);
		
		updateDeviceEventsStatusOnHandshakeCollection(user);
		return user;
	}

	

	private void updateDeviceEventsStatusOnHandshakeCollection(Event user) {
		try {

			boolean isRequiredAlert = false;
			int UTC_TO_IST_SEC = 19800;
			
			Query query = new Query();
			query.addCriteria(Criteria.where("_id").is(user.getGateway_serial_number()));

			Update update = new Update();
			update.set("id", user.getGateway_serial_number());
			
			if(user.getEvent_id() == 304){// light off
				update.set("light_status", 0);
				update.set("light_time_stamp", user.getTime_stamp() + UTC_TO_IST_SEC);
				isRequiredAlert = true;
			} else if(user.getEvent_id() == 303){ // lights on
				update.set("light_status", 1);
				update.set("light_time_stamp", user.getTime_stamp() + UTC_TO_IST_SEC);
				isRequiredAlert = true;
			}else if(user.getEvent_id() == 305){ // lights dim
				update.set("light_status", 2);
				update.set("light_time_stamp", user.getTime_stamp() + UTC_TO_IST_SEC);
				isRequiredAlert = true;
			}
			
			
		else if(user.getEvent_id() == 3){ // door open
			update.set("door_status", 1);
			update.set("door_time_stamp", user.getTime_stamp() + UTC_TO_IST_SEC);
			isRequiredAlert = true;
		} else if(user.getEvent_id() == 24){ // door closed
			update.set("door_status", 0);
			update.set("door_time_stamp", user.getTime_stamp() + UTC_TO_IST_SEC);
			isRequiredAlert = true;
		}
			
		else if(user.getEvent_id() == 5){ // AUTO_MANUAL
			update.set("manual_mode_status", 1);
			update.set("manual_mode_time_stamp", user.getTime_stamp() + UTC_TO_IST_SEC);
			isRequiredAlert = true;
		} else if(user.getEvent_id() == 29){ // MAN_MODE
			update.set("manual_mode_status", 0);
			update.set("manual_mode_time_stamp", user.getTime_stamp() + UTC_TO_IST_SEC);
			isRequiredAlert = true;
		}
			
			
			
		else if(user.getEvent_id() == 60){ // Common mcb trip occured
			update.set("mcb_trip", 1);
			update.set("mcb_trip_time_stamp", user.getTime_stamp() + UTC_TO_IST_SEC);
			isRequiredAlert = true;
		} else if(user.getEvent_id() == 63){ // Common mcb trip resolved
			update.set("mcb_trip", 0);
			update.set("mcb_trip_time_stamp", user.getTime_stamp());
			isRequiredAlert = true;
		}
			
			
		else if(user.getEvent_id() == 31){ //:RTU mains off
			update.set("main_supply_status", 1);
			update.set("main_supply_time_stamp", user.getTime_stamp() + UTC_TO_IST_SEC);
			isRequiredAlert = true;
		} else if(user.getEvent_id() == 32){ // :RTU mains on
			update.set("main_supply_status", 0);
			update.set("main_supply_time_stamp", user.getTime_stamp() + UTC_TO_IST_SEC);
			isRequiredAlert = true;
		}
			
			
			
			
		else if((user.getEvent_id() == 4) || (user.getEvent_id() == 18)){ //CONTRACTOR_FAILURE/RED_CNTRCT_FAIL
			update.set("cnt_status", 1);
			update.set("cnt_time_stamp", user.getTime_stamp() + UTC_TO_IST_SEC);
			isRequiredAlert = true;
		} else if((user.getEvent_id() == 25) || (user.getEvent_id() == 26)){ //CNTRCT_FAIL_RESOLVED / RED_CNTRCT_FAIL_RESOLVED
			update.set("cnt_status", 0);
			update.set("cnt_time_stamp", user.getTime_stamp() + UTC_TO_IST_SEC);
			isRequiredAlert = true;
		}
			
			
			
		else if((user.getEvent_id() == 78) || (user.getEvent_id() == 81)){ //UI_EVENT_R_SURGE_PRTCTR_TRIP/UI_EVENT_COMMON_SURGE_PRTCTR_TRIP
			update.set("spd_status", 1);
			update.set("spd_time_stamp", user.getTime_stamp() + UTC_TO_IST_SEC);
			isRequiredAlert = true;
		} else if((user.getEvent_id() == 82) || (user.getEvent_id() == 85)){ //UI_EVENT_R_SURGE_PRTCTR_TRIP_RESOLVED / UI_EVENT_COMMON_SURGE_PRTCTR_TRIP_RESOLVED
			update.set("spd_status", 0);
			update.set("spd_time_stamp", user.getTime_stamp() + UTC_TO_IST_SEC);
			isRequiredAlert = true;
		}
			
			
		else if(user.getEvent_id() == 21){ //RED_PHASE_NO_OUTPUT
			update.set("red_phse_no_output", 1);
			isRequiredAlert = true;
		} else if(user.getEvent_id() == 41){ // RED_PHASE_NO_OUTPUT_RESOLVED
			update.set("red_phse_no_output", 0);
			isRequiredAlert = true;
		}
			
		else if(user.getEvent_id() == 6){ //RED_THRESHOLD_CROSS_V_HIGH
			update.set("high_voltage", 1);
			isRequiredAlert = true;
		} else if(user.getEvent_id() == 44){ // RED_THRESHOLD_CROSS_V_HIGH_RESOLVED
			update.set("high_voltage", 0);
			isRequiredAlert = true;
		}
			
			
		else if(user.getEvent_id() == 8){ //RED_THRESHOLD_CROSS_I_HIGH
			update.set("high_current", 1);
			isRequiredAlert = true;
		} else if(user.getEvent_id() == 45){ // RED_THRESHOLD_CROSS_I_HIGH_RESOLVED
			update.set("high_current", 0);
			isRequiredAlert = true;
		}
			
			
			if(isRequiredAlert) {
				logger.debug("ADDING MAIN ALERT IN HANDSHAKE");
				mongoTemplate.upsert(query, update, HandShake.class);
			
			}
		} catch (Exception e) {
			logger.error("Exception while updatting device handshake event : "+ e.getMessage());
			logger.error(e.getStackTrace().toString());
		}
		
	}

	public void saveIOChunkData(ChunkDataHolder obj) {
		mongoTemplate.save(obj);
	}
	

	public void saveNodeConfData(NodeConfData obj) {
		mongoTemplate.save(obj);
	}
	
	public void saveNodeConfData(DCUSysConfData obj) {
		mongoTemplate.save(obj);
	}
	public void saveScheduleConfDataData(ScheduleConfData obj) {
		mongoTemplate.save(obj);
	}
	
	public NodeConfData getNodeConfData(String serial_number) {
		
		Query query = new Query();
		query.addCriteria(Criteria.where("_id").is(serial_number));

		NodeConfData obj = mongoTemplate.findOne(query, NodeConfData.class);
		return obj;
	}
	
	
public DCUSysConfData getDCUConfData(String serial_number) {
		
		Query query = new Query();
		query.addCriteria(Criteria.where("_id").is(serial_number));

		DCUSysConfData obj = mongoTemplate.findOne(query, DCUSysConfData.class);
		return obj;
	}


	public ScheduleConfData getScheduleConfData(String serial_number) {
		
		Query query = new Query();
		query.addCriteria(Criteria.where("_id").is(serial_number));

		ScheduleConfData obj = mongoTemplate.findOne(query, ScheduleConfData.class);
		return obj;
	}

	
	public ChunkDataHolder getIOChunkDataById(String gateway_serial_no) {

		ChunkDataHolder obj = null;
		Query query = new Query();
		query.addCriteria(Criteria.where("_id").is(gateway_serial_no));

		obj = mongoTemplate.findOne(query, ChunkDataHolder.class);
		return obj;
	}

	public void saveMeterData(MeterData obj) {
		mongoTemplate.save(obj);
	}
	

	public void saveSinglePhaseMeterData(SinglePhaseMeterData obj) {
		mongoTemplate.save(obj);
	}

	public void saveIOData(IOPojo obj) {
		try {
			mongoTemplate.save(obj);
		} catch (Exception e) {

		}
		
		/*
		 * generatting instant event ON/OFF for monitor and control
		 */
		try {
			InstantEventData event = new InstantEventData();
			event.setGateway_serial_number(obj.getDcu_id());
			event.setTime_stamp(Integer.parseInt(String.valueOf(obj.getId())));
			if(obj.getOpration_value() == 0){
				event.setEvent_id(303);
			}else{
				event.setEvent_id(303);
			}
			addOrUpdateInstantEvent(event);
		} catch (Exception e) {

		}
	}

	public void saveAllEvntDataData(AllEventData all_event_obj) {
		try {
			mongoTemplate.save(all_event_obj);
		} catch (Exception e) {

		}
	}
	
	public AllEventData getAllEventDataById(String gateway_serial_no) {

		AllEventData obj = null;
		Query query = new Query();
		query.addCriteria(Criteria.where("_id").is(gateway_serial_no));

		obj = mongoTemplate.findOne(query, AllEventData.class);
		return obj;
	}

	
	
	public void saveMeterInstantData(SinglePhaseMeterData obj) {
		InstantMeterData insts_obj = new InstantMeterData();
		insts_obj.setId(obj.getDcu_id());
		
	/*	insts_obj.setDcu_serial_number(obj.getDcu_id());*/
		/*insts_obj.setSlave_id(obj.getSlave_id());
		insts_obj.setFunction_code(obj.getFunction_code());*/
		insts_obj.setMeter_data_time_stamp(obj.getTime_stamp());
		insts_obj.setNode_id(obj.getNode_id());
		//insts_obj.setFrequency(obj.get);
		insts_obj.setR_phase_voltage(obj.getInstantaneous_voltage());
	//	insts_obj.setEqu_current_3_phase(obj.getInstantaneous_current());
		insts_obj.setCurrent_line_1(obj.getInstantaneous_current());
		//insts_obj.setEqu_power_3_phase(obj.getEqu_power_3_phase());
		insts_obj.setPf_1(obj.getInstantaneous_power_factor());
		insts_obj.setActive_power(obj.getCumulative_active_energy());
		//insts_obj.setApparent_power(obj.get);
		//insts_obj.setReactive_power(obj.getReactive_power());
		insts_obj.setKwh_total(obj.getInstantaneous_power());
	//	insts_obj.setEarth_tamper(obj.get);
//		insts_obj.setReverse_tamper(obj.getReverse_tamper());
	//	insts_obj.setCover_open_tamper(obj.getCover_open_tamper());
		//insts_obj.setMagnetic_tamper(obj.getMagnetic_tamper());
		
		if(logger.isDebugEnabled()){
			logger.debug("METER INSTANCE DATA : "+ insts_obj);
		}
		mongoTemplate.save(insts_obj);
	}

	public void addOrUpdateInstantEvent(InstantEventData event) {
		try {
			mongoTemplate.save(event);
		} catch (Exception e) {

		}
	}

	public DCUConfiguration getDCUConfigurationByIdentifier(String gateway_identifier) {
		
		DCUConfiguration obj = null;
		Query query = new Query();
		query.addCriteria(Criteria.where("_id").is(gateway_identifier));

		obj = mongoTemplate.findOne(query, DCUConfiguration.class);
		return obj;
	}
	
	
	public HandShake getByDeviceidentifier(String dcu_identifier) {
		
		Query query = new Query();
		query.addCriteria(Criteria.where("name").is(dcu_identifier));

		HandShake obj = mongoTemplate.findOne(query, HandShake.class);
		return obj;
	}

	public void saveDCUConfData(DCUSysConfData obj) {
		mongoTemplate.save(obj);
	}

	public void saveSchedulerConfData(ScheduleConfData obj) {
		mongoTemplate.save(obj);
	}
}
