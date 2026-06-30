package com.vnetsoft.ccms.dao;

import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import com.vnetsoft.ccms.pojo.HandShake;
import com.vnetsoft.ccms.pojo.MonitorControlCount;
import com.vnetsoft.ccms.pojo.server.InstantMeterData;

public class DashBoardDaoImpl implements DashBoardDao {

	@Autowired
	private MongoTemplate mongoTemplate;

	static final Logger logger = Logger.getLogger(DashBoardDaoImpl.class);

	public MongoTemplate getMongoTemplate() {
		return mongoTemplate;
	}

	@Autowired
	SessionFactory sessionFactory;

	Session session = null;
	Transaction tx = null;

	
	@Override
	public MonitorControlCount getDahsBoardCountstats(String district,
			String mandal, String gp) throws Exception {
	
		MonitorControlCount obj = new MonitorControlCount();
		
		Query query = new Query();
		 if(district.equals("5_districts")){ 
			query.addCriteria(Criteria.where("district").in("YSR Kadapa", "Kurnool", "Prakasam", "Srikakulam", "West Godavari"));
		} else if(district.equals("ALL")){ 
				;
		}else if(gp.equals("ALL") && mandal.equals("ALL")){
			query.addCriteria(Criteria.where("district").is(district));
		} else if(gp.equals("ALL")){
			query.addCriteria(Criteria.where("district").is(district))
			.addCriteria(Criteria.where("mandal").is(mandal));
		}else {
		
			query.addCriteria(Criteria.where("district").is(district))
			.addCriteria(Criteria.where("mandal").is(mandal))
			.addCriteria(Criteria.where("gp").is(gp));
		}
		List<HandShake> list = mongoTemplate.find(query, HandShake.class);
		
		
		 long total_devices = 0,  total_lights_connected = 0,  mcb_trip_count = 0,  cnt_failure = 0, 
			main_supply_off = 0,  door_open = 0,  spd_failure = 0,  no_out_put = 0,  manual_mode = 0,
			on_count = 0,  off_count = 0 ,  good_gprs = 0,  poor_gprs = 0,  light_off = 0,  light_on = 0,
		offline_ccms = 0 ,  online_ccms = 0,  total_connected_load = 0;
		 
		 long ccms_on = 0, ccms_off= 0;// IO on off
		double active_load = 0.0;
		
		 total_devices = list.size();
		 
		
		 for(HandShake tmp : list){
			 try {
				
				 if(tmp.getMcb_trip() == 1)
					 mcb_trip_count ++;
				 
				 if(tmp.getManual_mode_status() == 1)
					 manual_mode++;
				 
				 if(tmp.getLight_status() == 1) {
					 light_on += tmp.getNo_of_lights();
					 ccms_on++;
				 }  else {
					 light_off += tmp.getNo_of_lights();
					 ccms_off++;
				 }
				 if(tmp.getDoor_status() == 1)
					 door_open++;
				 
				 if(tmp.getCsq() > 15)
					 good_gprs++;
				 else 
					 poor_gprs++;
				 
				 if(tmp.getSpd_status() == 1)
					 spd_failure++;
				 
				 if(tmp.getMain_supply_status() == 1)
					 main_supply_off++;
				 
				 total_connected_load +=tmp.getConnected_load();
				 total_lights_connected += tmp.getNo_of_lights();
				 
				 try {
					 long hs = Long.valueOf(tmp.getHs_time_stamp()) ;
					 if((System.currentTimeMillis() - hs) > 7200000){
						 offline_ccms++;
					 } else {
						 online_ccms++;
					 }
				} catch (Exception e) {
					 offline_ccms++;
				}
				 
				try {
					active_load += getConectedActiveLoad(tmp.getGateway_serial_number());
				} catch (Exception e) {
					// TODO: handle exception
				}
				 
			} catch (Exception e) {
				// TODO: handle exception
			}
		 }
		 
		 
		 obj.total_devices = total_devices;
		 obj.total_lights_connected = total_lights_connected;
		 obj.mcb_trip_count = mcb_trip_count;
		 obj.cnt_failure = cnt_failure;
		 obj.main_supply_off = main_supply_off;
		 obj.door_open = door_open;
		 
		 obj.spd_failure = spd_failure;
		 obj.no_out_put = no_out_put;
		 obj.manual_mode = manual_mode;
		 obj.good_gprs = good_gprs;
		 obj.poor_gprs = poor_gprs;
		 obj.light_off = light_off;
		 obj.light_on = light_on;
		 obj.offline_ccms =offline_ccms;
		 obj.online_ccms = online_ccms;
		 obj.total_connected_load = total_connected_load;
		 obj.ccms_on = ccms_on;
		 obj.ccms_off = ccms_off;
		 obj.active_load = active_load;
		/*obj.setTotal_devices(getTotalDevice(district,mandal, gp));
		obj.setMcb_trip_count(getMCBTripCount(district,mandal, gp));
		obj.setManual_mode(getManuvalModeCount(district,mandal, gp));
		obj.setLight_off(getLightsOffCount(district,mandal, gp));
	
		obj.setLight_on(getLightOnCount(district,mandal, gp));
		obj.setDoor_open(getDorrOpenCount(district,mandal, gp));
		obj.setGood_gprs(getGoodGprsCount(district,mandal, gp));
		obj.setMain_supply_off(getMainSupplyStatus(district,mandal, gp));
		obj.setSpd_failure(getSpdFailureCount(district,mandal, gp));
		obj.setPoor_gprs(obj.getTotal_devices() - obj.getGood_gprs());
		
		obj.setTotal_connected_load(getTotalConectedLoad(district,mandal, gp));*/
		
		
		return obj;
	}


	private double getConectedActiveLoad(String id) {
		double kwh = 0;
		try {
			Query query = new Query();
			query.addCriteria(Criteria.where("dcu_serial_number").is(id));

			InstantMeterData list = mongoTemplate.findOne(query,
					InstantMeterData.class);

		
	
		
		
			try {
			kwh += Double.parseDouble(list.getKwh_total());
			}catch(Exception e){
				
			}
		
		
		} catch (Exception e) {
			return 0;
		}
		//return mongoTemplate.findAll(InstantMeterData.class);
		return kwh;
	}

/*
	private long getTotalDevice(String district, String mandal, String gp) {
		Query query = new Query();
		
		 if(district.equals("ALL")){ 
			;
		} else if(gp.equals("ALL") && mandal.equals("ALL")){
			query.addCriteria(Criteria.where("district").is(district));
		} else if(gp.equals("ALL")){
			query.addCriteria(Criteria.where("district").is(district))
			.addCriteria(Criteria.where("mandal").is(mandal));
		} else {
			query.addCriteria(Criteria.where("district").is(district))
			.addCriteria(Criteria.where("mandal").is(mandal))
			.addCriteria(Criteria.where("gp").is(gp))
			
			;
		}
		
		return mongoTemplate.count(query, HandShake.class, "handshake_info");
	}


	private long getSpdFailureCount(String district, String mandal, String gp) {
		Query query = new Query();
		query.addCriteria(Criteria.where("district").is(district))
		.addCriteria(Criteria.where("mandal").is(mandal))
		.addCriteria(Criteria.where("gp").is(gp))
		.addCriteria(Criteria.where("spd_status").is(1))
		;
	
		Query query = new Query();
		if(gp.equals("ALL") && mandal.equals("ALL")){
			query.addCriteria(Criteria.where("district").is(district))
			.addCriteria(Criteria.where("spd_status").is(1));
		} else if(gp.equals("ALL")){
			query.addCriteria(Criteria.where("district").is(district))
			.addCriteria(Criteria.where("mandal").is(mandal))
			.addCriteria(Criteria.where("spd_status").is(1));
		} else if(district.equals("ALL")){ 
			query.addCriteria(Criteria.where("spd_status").is(1));
		}else {
			query.addCriteria(Criteria.where("district").is(district))
			.addCriteria(Criteria.where("mandal").is(mandal))
			.addCriteria(Criteria.where("gp").is(gp))
			.addCriteria(Criteria.where("spd_status").is(1))
			;
		}
		
		return  mongoTemplate.count(query, HandShake.class, "handshake_info");
	}


	private long getMainSupplyStatus(String district, String mandal, String gp) {
		Query query = new Query();
		query.addCriteria(Criteria.where("district").is(district))
		.addCriteria(Criteria.where("mandal").is(mandal))
		.addCriteria(Criteria.where("gp").is(gp))
		.addCriteria(Criteria.where("main_supply_status").is(1))
		;
	
		Query query = new Query();
		if(gp.equals("ALL") && mandal.equals("ALL")){
			query.addCriteria(Criteria.where("district").is(district));
		} else if(gp.equals("ALL")){
			query.addCriteria(Criteria.where("district").is(district))
			.addCriteria(Criteria.where("mandal").is(mandal));
		} else if(district.equals("ALL")){ 
			;
		}else {
			query.addCriteria(Criteria.where("district").is(district))
			.addCriteria(Criteria.where("mandal").is(mandal))
			.addCriteria(Criteria.where("gp").is(gp));
			
			;
		}
		query.addCriteria(Criteria.where("main_supply_status").is(1));
		
		return  mongoTemplate.count(query, HandShake.class, "handshake_info");
	}


	private long getGoodGprsCount(String district, String mandal, String gp) {
		Query query = new Query();
		query.addCriteria(Criteria.where("district").is(district))
		.addCriteria(Criteria.where("mandal").is(mandal))
		.addCriteria(Criteria.where("gp").is(gp))
		.addCriteria(Criteria.where("csq").gt(15))
		;
	
		Query query = new Query();
		if(gp.equals("ALL") && mandal.equals("ALL")){
			query.addCriteria(Criteria.where("district").is(district));
		} else if(gp.equals("ALL")){
			query.addCriteria(Criteria.where("district").is(district))
			.addCriteria(Criteria.where("mandal").is(mandal));
		}else if(district.equals("ALL")){ 
			;
		} else {
			query.addCriteria(Criteria.where("district").is(district))
			.addCriteria(Criteria.where("mandal").is(mandal))
			.addCriteria(Criteria.where("gp").is(gp))
			
			;
		}
		query.addCriteria(Criteria.where("csq").gt(15));
		return  mongoTemplate.count(query, HandShake.class, "handshake_info");
	}


	private long getDorrOpenCount(String district, String mandal, String gp) {
		Query query = new Query();
		query.addCriteria(Criteria.where("district").is(district))
		.addCriteria(Criteria.where("mandal").is(mandal))
		.addCriteria(Criteria.where("gp").is(gp))
		.addCriteria(Criteria.where("door_status").is(1))
		;

		Query query = new Query();
		if(gp.equals("ALL") && mandal.equals("ALL")){
			query.addCriteria(Criteria.where("district").is(district));
		} else if(gp.equals("ALL")){
			query.addCriteria(Criteria.where("district").is(district))
			.addCriteria(Criteria.where("mandal").is(mandal));
		}else if(district.equals("ALL")){ 
			;
		} else {
			query.addCriteria(Criteria.where("district").is(district))
			.addCriteria(Criteria.where("mandal").is(mandal))
			.addCriteria(Criteria.where("gp").is(gp))
			
			;
		}
		query.addCriteria(Criteria.where("door_status").is(1));
		return  mongoTemplate.count(query, HandShake.class, "handshake_info");
	}


	private long getLightOnCount(String district, String mandal, String gp) {
		Query query = new Query();
		query.addCriteria(Criteria.where("district").is(district))
		.addCriteria(Criteria.where("mandal").is(mandal))
		.addCriteria(Criteria.where("gp").is(gp))
		.addCriteria(Criteria.where("light_status").is(1))
		;
	
		Query query = new Query();
		if(gp.equals("ALL") && mandal.equals("ALL")){
			query.addCriteria(Criteria.where("district").is(district));
		} else if(gp.equals("ALL")){
			query.addCriteria(Criteria.where("district").is(district))
			.addCriteria(Criteria.where("mandal").is(mandal));
		}else if(district.equals("ALL")){ 
			;
		} else {
			query.addCriteria(Criteria.where("district").is(district))
			.addCriteria(Criteria.where("mandal").is(mandal))
			.addCriteria(Criteria.where("gp").is(gp))
			
			;
		}
		query.addCriteria(Criteria.where("light_status").is(1));
		return  mongoTemplate.count(query, HandShake.class, "handshake_info");
	}


	private long getLightsOffCount(String district, String mandal, String gp) {
		Query query = new Query();
		query.addCriteria(Criteria.where("district").is(district))
		.addCriteria(Criteria.where("mandal").is(mandal))
		.addCriteria(Criteria.where("gp").is(gp))
		.addCriteria(Criteria.where("light_status").is(0))
		;
		Query query = new Query();
		if(gp.equals("ALL") && mandal.equals("ALL")){
			query.addCriteria(Criteria.where("district").is(district));
		} else if(gp.equals("ALL")){
			query.addCriteria(Criteria.where("district").is(district))
			.addCriteria(Criteria.where("mandal").is(mandal));
		} else if(district.equals("ALL")){ 
			;
		}else {
			query.addCriteria(Criteria.where("district").is(district))
			.addCriteria(Criteria.where("mandal").is(mandal))
			.addCriteria(Criteria.where("gp").is(gp))
			
			;
		}
		query.addCriteria(Criteria.where("light_status").is(0));
		return  mongoTemplate.count(query, HandShake.class, "handshake_info");
	
	}


	private long getManuvalModeCount(String district, String mandal, String gp) {

		Query query = new Query();
		query.addCriteria(Criteria.where("district").is(district))
		.addCriteria(Criteria.where("mandal").is(mandal))
		.addCriteria(Criteria.where("gp").is(gp))
		.addCriteria(Criteria.where("manual_mode_status").is(1))
		;
		Query query = new Query();
		if(gp.equals("ALL") && mandal.equals("ALL")){
			query.addCriteria(Criteria.where("district").is(district));
		} else if(gp.equals("ALL")){
			query.addCriteria(Criteria.where("district").is(district))
			.addCriteria(Criteria.where("mandal").is(mandal));
		} else if(district.equals("ALL")){ 
			;
		}else {
			query.addCriteria(Criteria.where("district").is(district))
			.addCriteria(Criteria.where("mandal").is(mandal))
			.addCriteria(Criteria.where("gp").is(gp))
			
			;
		}
		query.addCriteria(Criteria.where("manual_mode_status").is(1));
		return  mongoTemplate.count(query, HandShake.class, "handshake_info");
	
	}


	private long getMCBTripCount(String district, String mandal, String gp) {
		Query query = new Query();
		query.addCriteria(Criteria.where("district").is(district))
		.addCriteria(Criteria.where("mandal").is(mandal))
		.addCriteria(Criteria.where("gp").is(gp))
		.addCriteria(Criteria.where("mcb_trip").is(1))
		;
	
		Query query = new Query();
		if(gp.equals("ALL") && mandal.equals("ALL")){
			query.addCriteria(Criteria.where("district").is(district));
		} else if(gp.equals("ALL")){
			query.addCriteria(Criteria.where("district").is(district))
			.addCriteria(Criteria.where("mandal").is(mandal));
		} else if(district.equals("ALL")){ 
			;
		}else {
			query.addCriteria(Criteria.where("district").is(district))
			.addCriteria(Criteria.where("mandal").is(mandal))
			.addCriteria(Criteria.where("gp").is(gp))
			
			;
		}
		
		query.addCriteria(Criteria.where("mcb_trip").is(1));
		return  mongoTemplate.count(query, HandShake.class, "handshake_info");
	}

*/
	@Override
	public List<HandShake> getMapData(String district, String mandal, String gp)
			throws Exception {
		Query query = new Query();
		 
		if(district.equals("5_districts")){ 
			query.addCriteria(Criteria.where("district").in("YSR Kadapa", "Kurnool", "Prakasam", "Srikakulam", "West Godavari"));
		} else if(district.equals("ALL")){ 
				;
		} else if(gp.equals("ALL") && mandal.equals("ALL")){
			query.addCriteria(Criteria.where("district").is(district));
		} else if(gp.equals("ALL")){
			query.addCriteria(Criteria.where("district").is(district))
			.addCriteria(Criteria.where("mandal").is(mandal));
		} else {
		
			query.addCriteria(Criteria.where("district").is(district))
			.addCriteria(Criteria.where("mandal").is(mandal))
			.addCriteria(Criteria.where("gp").is(gp));
		}
		List<HandShake> list = mongoTemplate.find(query, HandShake.class);

		return list;
	}


	@Override
	public List<HandShake> getAllHandShakeData(String district, String mandal, String gp) throws Exception {

		Query query = new Query();
		
		
		 if(district.equals("5_districts")){ 
			query.addCriteria(Criteria.where("district").in("YSR Kadapa", "Kurnool", "Prakasam", "Srikakulam", "West Godavari"));
		} else if(district.equals("ALL")){ 
			;
		}
		 else if(gp.equals("ALL") && mandal.equals("ALL")){
			query.addCriteria(Criteria.where("district").is(district));
		} else if(gp.equals("ALL")){
			query.addCriteria(Criteria.where("district").is(district))
			.addCriteria(Criteria.where("mandal").is(mandal));
		}  else {
		
			query.addCriteria(Criteria.where("district").is(district))
			.addCriteria(Criteria.where("mandal").is(mandal))
			.addCriteria(Criteria.where("gp").is(gp));
		}
		List<HandShake> list = mongoTemplate.find(query, HandShake.class);

		return list;
	
	}


	@Override
	public InstantMeterData getInstantMeterData(
			String device_serial_number) throws Exception {
		Query query = new Query();
		query.addCriteria(Criteria.where("_id").is(device_serial_number));
		InstantMeterData list = mongoTemplate.findOne(query, InstantMeterData.class);

		return list;
	}


	@Override
	public List<HandShake> getHandShakeByIDWithFilter(String district, String mandal, String gp, String dcu_name) throws Exception {

		try {
			
			Query query = new Query();
			
			
			 if(district.equals("5_districts")){ 
				query.addCriteria(Criteria.where("district").in("YSR Kadapa", "Kurnool", "Prakasam", "Srikakulam", "West Godavari"));
			} else if(district.equals("ALL")){ 
				;
			}
			 else if(gp.equals("ALL") && mandal.equals("ALL")){
				query.addCriteria(Criteria.where("district").is(district));
			} else if(gp.equals("ALL")){
				query.addCriteria(Criteria.where("district").is(district))
				.addCriteria(Criteria.where("mandal").is(mandal));
			}  else {
			
				query.addCriteria(Criteria.where("district").is(district))
				.addCriteria(Criteria.where("mandal").is(mandal))
				.addCriteria(Criteria.where("gp").is(gp));
			}
			query.addCriteria(Criteria.where("name").regex(dcu_name+ '*'));

			List<HandShake> list = mongoTemplate.find(query,
					HandShake.class);

			return list;
		} catch (Exception e) {
			return null;
		}
	
	}


	@Override
	public HandShake getHandShakeByID(String dcu_serial_number)
			throws Exception {
		Query query = new Query();
		query.addCriteria(Criteria.where("gateway_serial_number").is(dcu_serial_number));

		HandShake list = mongoTemplate.findOne(query,
				HandShake.class);
		return list;
	}

}
