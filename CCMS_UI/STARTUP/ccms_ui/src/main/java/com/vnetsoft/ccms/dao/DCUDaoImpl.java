package com.vnetsoft.ccms.dao;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import com.vnetsoft.ccms.pojo.DCU;
import com.vnetsoft.ccms.pojo.DCUConfiguration;
import com.vnetsoft.ccms.pojo.Event;
import com.vnetsoft.ccms.pojo.HandShake;
import com.vnetsoft.ccms.pojo.HerarchyDetails;
import com.vnetsoft.ccms.pojo.IOPojo;
import com.vnetsoft.ccms.pojo.DeviceMeterConfigurations;
import com.vnetsoft.ccms.pojo.MeterData;
import com.vnetsoft.ccms.pojo.MonitorControlCount;
import com.vnetsoft.ccms.pojo.SchedulerConfiguration;
import com.vnetsoft.ccms.pojo.SinglePhaseMeterData;
import com.vnetsoft.ccms.pojo.server.InstantEventData;
import com.vnetsoft.ccms.pojo.server.InstantMeterData;

public class DCUDaoImpl implements DCUDao {

	@Autowired
	private MongoTemplate mongoTemplate;

	static final Logger logger = Logger.getLogger(DCUDaoImpl.class);

	public MongoTemplate getMongoTemplate() {
		return mongoTemplate;
	}

	@Autowired
	SessionFactory sessionFactory;

	Session session = null;
	Transaction tx = null;

	@Override
	public boolean addDCU(DCU user) throws Exception {
		mongoTemplate.save(user, "dcu_details");
		return true;
	}

	@Override
	public DCU getDCUById(String id) throws Exception {
		Query query = new Query();
		query.addCriteria(Criteria.where("_id").is(id));

		List<DCU> list = mongoTemplate.find(query, DCU.class);

		return list.get(0);
	}

	@Override
	public List<DCU> getDCUList() throws Exception {
		return mongoTemplate.findAll(DCU.class);
	}

	@Override
	public boolean deleteDCU(String id) throws Exception {
		Query query = new Query();
		query.addCriteria(Criteria.where("_id").is(id));
		mongoTemplate.remove(query, HandShake.class);

		return true;
	}

	@Override
	public List<HandShake> getHandShakeList() throws Exception {
		return mongoTemplate.findAll(HandShake.class);
	}

	@Override
	public List<Event> getEventList() throws Exception {

		Query query = new Query();
		query.with(
				new Sort(Sort.Direction.ASC, "yymmdd"));
		List<Event> list = mongoTemplate.find(query, Event.class);
	//	List<Event> reverse = reverseList(list);
		return list;
	}
	
	
	@Override
	public List<Event> getEventListByDate(String id, String start_date, String end_date) throws Exception {

		//int start_date = getCurrentYYMMDD();
		Query query = new Query();
		query.addCriteria(Criteria.where("gateway_serial_number").is(id));
		query.addCriteria(
				Criteria.where("yymmdd").gte(Integer.parseInt(start_date)).lte(Integer.parseInt(end_date))).with(
				new Sort(Sort.Direction.ASC, "yymmdd"));
		List<Event> list = mongoTemplate.find(query, Event.class);
	//	List<Event> reverse = reverseList(list);
		return list;
	}
	

	@Override
	public boolean addHandShake(HandShake user) throws Exception {
		mongoTemplate.save(user, "handshake_info");
		return true;
	}

	@Override
	public boolean addMeter(DeviceMeterConfigurations user) throws Exception {
		mongoTemplate.save(user, "meter_configurations");
		return true;
	}

	@Override
	public List<DeviceMeterConfigurations>  getByMeterID(String dcu_serial_number) throws Exception {
		Query query = new Query();
		query.addCriteria(Criteria.where("_id").is(dcu_serial_number));

		List<DeviceMeterConfigurations> list = mongoTemplate.find(query, DeviceMeterConfigurations.class);

		return list;
	}

	@Override
	public boolean addDCUConfiguration(DCUConfiguration user) throws Exception {
		mongoTemplate.save(user, "dcu_system_conf_details");
		return true;
	}

	@Override
	public DCUConfiguration getDCUConfigurationByID(String dcu_serial_number)
			throws Exception {
		try {
			Query query = new Query();
			query.addCriteria(Criteria.where("_id").is(dcu_serial_number));

			List<DCUConfiguration> list = mongoTemplate.find(query,
					DCUConfiguration.class);

			return list.get(0);
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	public SinglePhaseMeterData getByMeterDataID(String id) throws Exception {
		try {
			Query query = new Query();
			query.addCriteria(Criteria.where("_id").is(id));

			List<SinglePhaseMeterData> list = mongoTemplate.find(query, SinglePhaseMeterData.class);

			return list.get(0);
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	public List<SinglePhaseMeterData> getMeterDataListForCurrentDate() throws Exception {

		int start_date = getCurrentYYMMDD();
		Query query = new Query();
		query.addCriteria(
				Criteria.where("yymmdd").gte(start_date).lte(start_date)).with(
				new Sort(Sort.Direction.ASC, "yymmdd"))

		;
		List<SinglePhaseMeterData> list = mongoTemplate.find(query, SinglePhaseMeterData.class);
	
		return list;

	}

	@Override
	public List<DCUConfiguration> getDCUConfigurationList() throws Exception {
		return mongoTemplate.findAll(DCUConfiguration.class);
	}

	@Override
	public InstantMeterData getByInstantMeterDataID(String id) throws Exception {
		try {
			Query query = new Query();
			query.addCriteria(Criteria.where("dcu_serial_number").is(id));

			List<InstantMeterData> list = mongoTemplate.find(query,
					InstantMeterData.class);

			return list.get(0);

		} catch (Exception e) {
			return null;
		}
	}

	@Override
	public List<InstantMeterData> getInstantMeterDataList() throws Exception {
		return mongoTemplate.findAll(InstantMeterData.class);
	}

	@Override
	public List<InstantEventData> getByInstantEventDataD(String id)
			throws Exception {

		try {
			Query query = new Query();
			query.addCriteria(Criteria.where("gateway_serial_number").is(id));

			List<InstantEventData> list = mongoTemplate.find(query,
					InstantEventData.class);

			return list;
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	public List<InstantEventData> getInstantEventDataList() throws Exception {
		return mongoTemplate.findAll(InstantEventData.class);
	}

	@Override
	public List<IOPojo> getIOPojoByID(String id) throws Exception {

		try {
			Query query = new Query();
			query.addCriteria(Criteria.where("dcu_id").is(id));

			List<IOPojo> list = mongoTemplate.find(query,
					IOPojo.class);

			return list;
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	public List<IOPojo> getIOPojoList() throws Exception {
		return mongoTemplate.findAll(IOPojo.class);
	}

	private int getCurrentYYMMDD() {

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());

		int yymmdd = Integer.parseInt(String.valueOf(calendar
				.get(Calendar.YEAR))
				+ String.format("%02d", calendar.get(Calendar.MONTH) + 1)
				+ String.format("%02d", calendar.get(Calendar.DATE)));
		return yymmdd;
	}


	private int getPreviousDayYYMMDD() {
		int MILLIS_IN_DAY = 1000 * 60 * 60 * 24;
		Calendar calendar = Calendar.getInstance();
		//calendar.setTime(new Date((System.currentTimeMillis() - MILLIS_IN_DAY)));
		calendar.setTime(new Date((System.currentTimeMillis())));
		int yymmdd = Integer.parseInt(String.valueOf(calendar.get(Calendar.YEAR)) +  
				String.format("%02d", calendar.get(Calendar.MONTH) + 1) +  
				String.format("%02d", calendar.get(Calendar.DATE)) );
		
		return yymmdd;
	}
	
	private int getPreviousDayForIOYYMMDD() {
		int MILLIS_IN_DAY = 1000 * 60 * 60 * 24;
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date((System.currentTimeMillis() - MILLIS_IN_DAY)));
		calendar.setTime(new Date((System.currentTimeMillis())));
		int yymmdd = Integer.parseInt(String.valueOf(calendar.get(Calendar.YEAR)) +  
				String.format("%02d", calendar.get(Calendar.MONTH) + 1) +  
				String.format("%02d", calendar.get(Calendar.DATE)) );
		
		return yymmdd;
	}

	@Override
	public HandShake getHandShakeByID(String dcu_serial_number)
			throws Exception {
		try {
			Query query = new Query();
			query.addCriteria(Criteria.where("gateway_serial_number").is(dcu_serial_number));

			List<HandShake> list = mongoTemplate.find(query,
					HandShake.class);

			return list.get(0);
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	public List<SinglePhaseMeterData> getByMeterDataBetweenDate(String id, String start_date,
			String end_date) throws Exception {
		List<SinglePhaseMeterData> list = null;
		try {
		Query query = new Query();
			query.addCriteria(Criteria.where("dcu_id").is(id));
			query.addCriteria(
					Criteria.where("yymmdd").gte(Integer.parseInt(start_date)).lte(Integer.parseInt(end_date))).with(
					new Sort(Sort.Direction.ASC, "yymmdd"));
			list = mongoTemplate.find(query, SinglePhaseMeterData.class);
			
		}catch(Exception e){
			System.out.println("Exception while get meter data by date " +e.getMessage());
		}
		return list;
	}

	@Override
	public boolean addHerachy(HerarchyDetails obj) throws Exception {
		mongoTemplate.save(obj, "herarchy_details");
		return true;
	}

	@Override
	public List<HerarchyDetails> getHerarchyDetailsList() throws Exception {
		return mongoTemplate.findAll(HerarchyDetails.class);
	}

	@Override
	public List<String> getMondalByDistrict(String distrtict) throws Exception {
		Set<String> ret_list = new LinkedHashSet<String>();
		
		List<HerarchyDetails> list = new ArrayList<HerarchyDetails>();
		
		try {
			Query query = new Query();
			query.addCriteria(Criteria.where("district").is(distrtict));
			
			list = mongoTemplate.find(query, HerarchyDetails.class);
			
			
			for(HerarchyDetails tmp : list){
				ret_list.add(tmp.getMandal());
			}
			
			  ArrayList<String> listWithoutDuplicates = new ArrayList<String>(ret_list);
		        
		       return listWithoutDuplicates;
		}catch(Exception e){
			System.out.println("Exception while get meter data by date " +e.getMessage());
		}
		return null;
	}

	@Override
	public List<String> getGPByMandal(String mandal) throws Exception {

		Set<String> ret_list = new LinkedHashSet<String>();
		
		List<HerarchyDetails> list = new ArrayList<HerarchyDetails>();
		
		try {
		Query query = new Query();
			query.addCriteria(Criteria.where("mandal").is(mandal));
			
			list = mongoTemplate.find(query, HerarchyDetails.class);
			
			
			for(HerarchyDetails tmp : list){
				ret_list.add(tmp.getGp());
			}
			
		
	        ArrayList<String> listWithoutDuplicates = new ArrayList<String>(ret_list);
	        
	       return listWithoutDuplicates;
		}catch(Exception e){
			System.out.println("Exception while get meter data by date " +e.getMessage());
		}
		return null;
	
	}

	@Override
	public List<String> getVilageByGP(String gp) throws Exception {
		Set<String> ret_list = new LinkedHashSet<String>();		
		List<HerarchyDetails> list = new ArrayList<HerarchyDetails>();
		
		try {
		Query query = new Query();
			query.addCriteria(Criteria.where("gp").is(gp));
			
			list = mongoTemplate.find(query, HerarchyDetails.class);
			
			
			for(HerarchyDetails tmp : list){
				ret_list.add(tmp.getVillage());
			}
			  ArrayList<String> listWithoutDuplicates = new ArrayList<String>(ret_list);
		        
		       return listWithoutDuplicates;
		}catch(Exception e){
			System.out.println("Exception " +e.getMessage());
		}
		return null;
	
	}

	@Override
	public List<MeterData> getByMeterDataByDCUSerialNumber(String id)
			throws Exception {
		Query query = new Query();
		query.addCriteria(Criteria.where("dcu_id").is(id));
		query.addCriteria(
				Criteria.where("yymmdd").lte(getPreviousDayYYMMDD())).with(
				new Sort(Sort.Direction.ASC, "yymmdd"));
		List<MeterData> list = mongoTemplate.find(query, MeterData.class);

		return list;
	}

	@Override
	public boolean deleteMeterDateByID(long id) throws Exception {
		Query query = new Query();
		query.addCriteria(Criteria.where("_id").is(id));
		mongoTemplate.remove(query, SinglePhaseMeterData.class);
		return true;
	}

	@Override
	public List<Event> getByEventDataByDCUSerialNumber(String id)
			throws Exception {
		Query query = new Query();
		query.addCriteria(Criteria.where("gateway_serial_number").is(id));
		query.addCriteria(
				Criteria.where("yymmdd").lte(getPreviousDayYYMMDD())).with(
				new Sort(Sort.Direction.ASC, "yymmdd"));
		List<Event> list = mongoTemplate.find(query, Event.class);

		return list;
	}

	@Override
	public boolean deleteEventDateByID(long id) throws Exception {
		Query query = new Query();
		query.addCriteria(Criteria.where("_id").is(id));
		mongoTemplate.remove(query, Event.class);

		return true;
	}

	@Override
	public List<IOPojo> getByIODataByDCUSerialNumber(String id)
			throws Exception {
		Query query = new Query();
		query.addCriteria(Criteria.where("dcu_id").is(id));
		query.addCriteria(
				Criteria.where("yymmdd").lte(getPreviousDayForIOYYMMDD())).with(
				new Sort(Sort.Direction.ASC, "yymmdd"));
		List<IOPojo> list = mongoTemplate.find(query, IOPojo.class);

		return list;
	}

	@Override
	public boolean deleteIODateByID(long id) throws Exception {
		Query query = new Query();
		query.addCriteria(Criteria.where("_id").is(id));
		mongoTemplate.remove(query, IOPojo.class);

		return true;
	}

	@Override
	public boolean addSchedulerConfiguration(SchedulerConfiguration user)
			throws Exception {
		mongoTemplate.save(user, "scheduler_details");
		return true;
	
	}

	@Override
	public SchedulerConfiguration getSchedulerConfigurationById(String id)
			throws Exception {
		Query query = new Query();
		query.addCriteria(Criteria.where("schedules_name").is(id));

		List<SchedulerConfiguration> list = mongoTemplate.find(query, SchedulerConfiguration.class);

		return list.get(0);
	}

	@Override
	public List<SchedulerConfiguration> getSchedulerConfigurationList()
			throws Exception {
		return mongoTemplate.findAll(SchedulerConfiguration.class);
	}

	@Override
	public boolean deleteSchedulerConfiguration(String id) throws Exception {
		Query query = new Query();
		query.addCriteria(Criteria.where("_id").is(Long.parseLong(id)));
		mongoTemplate.remove(query, SchedulerConfiguration.class);

		return true;
	}

	@Override
	public List<SinglePhaseMeterData> getBySinglePhaseMeterDataByDCUSerialNumber(
			String id) throws Exception {

		Query query = new Query();
		query.addCriteria(Criteria.where("dcu_id").is(id));
		query.addCriteria(
				Criteria.where("yymmdd").lte(getPreviousDayYYMMDD())).with(
				new Sort(Sort.Direction.ASC, "yymmdd"));
		List<SinglePhaseMeterData> list = mongoTemplate.find(query, SinglePhaseMeterData.class);

		return list;
	
	}

	@Override
	public boolean deleteSinglePhaseMeterDateByID(long id) throws Exception {
		Query query = new Query();
		query.addCriteria(Criteria.where("_id").is(id));
		mongoTemplate.remove(query, SinglePhaseMeterData.class);

		return true;
	}

	@Override
	public MonitorControlCount getDahsBoardCountstats(String district, String mandal, String gp) throws Exception {
		
		
		
		return null;
	}

	@Override
	public void deleteDeviceMeterConfiguration(String dcu_serial_number)
			throws Exception {
		Query query = new Query();
		query.addCriteria(Criteria.where("_id").is(dcu_serial_number));
		mongoTemplate.remove(query, DeviceMeterConfigurations.class);

	}
}
