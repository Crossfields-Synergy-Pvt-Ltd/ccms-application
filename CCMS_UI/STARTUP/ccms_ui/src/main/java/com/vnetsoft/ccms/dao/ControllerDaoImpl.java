package com.vnetsoft.ccms.dao;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import com.vnetsoft.ccms.pojo.Event;
import com.vnetsoft.ccms.pojo.SchedulerConfiguration;
import com.vnetsoft.ccms.pojo.SystemConfiguration;
import com.vnetsoft.ccms.pojo.conf.ConfigurationDetails;
import com.vnetsoft.ccms.pojo.conf.DCUSysConfData;
import com.vnetsoft.ccms.pojo.conf.NodeConfData;
import com.vnetsoft.ccms.pojo.conf.ScheduleConfData;

public class ControllerDaoImpl implements ControllerDao {

	
	@Autowired
	private MongoTemplate mongoTemplate;
	
	static final Logger logger = Logger.getLogger(ControllerDaoImpl.class);

	public MongoTemplate getMongoTemplate() {
		return mongoTemplate;
	}
	
	
	@Autowired
	SessionFactory sessionFactory;

	Session session = null;
	Transaction tx = null;

	@Override
	public boolean addSchedulerConfiguration(SchedulerConfiguration obj)
			throws Exception {
		mongoTemplate.save(obj, "scheduler_details");
		return true;
	}
	@Override
	public SchedulerConfiguration getSchedulerConfigurationById(String id)
			throws Exception {
		Query query = new Query();
		query.addCriteria(Criteria.where("_id").is(id));
	
	
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
		query.addCriteria(Criteria.where("_id").is(id));
		mongoTemplate.remove(query, SchedulerConfiguration.class);
	 
		return true;
	}
	@Override
	public boolean addEvent(Event obj) throws Exception {
		mongoTemplate.save(obj, "event_info");
		return true;
	}
	@Override
	public Event getEventById(String id) throws Exception {
		Query query = new Query();
		query.addCriteria(Criteria.where("_id").is(id));
	
	
		List<Event> list = mongoTemplate.find(query, Event.class);
		 
		
		return list.get(0);
	}
	@Override
	public List<Event> getEventList() throws Exception {
		return mongoTemplate.findAll(Event.class);
	}
	@Override
	public boolean deleteEvent(String id) throws Exception {
		Query query = new Query();
		query.addCriteria(Criteria.where("_id").is(id));
		mongoTemplate.remove(query, Event.class);
	 
		return true;
	}
	@Override
	public boolean addSystemConfiguration(SystemConfiguration obj)
			throws Exception {
		mongoTemplate.save(obj, "system_conf_details");
		return true;
	}
	@Override
	public SystemConfiguration getSystemConfigurationById(String id)
			throws Exception {
		Query query = new Query();
		query.addCriteria(Criteria.where("_id").is(id));
	
	
		List<SystemConfiguration> list = mongoTemplate.find(query, SystemConfiguration.class);
		 
		
		return list.get(0);
	}
	@Override
	public List<SystemConfiguration> getSystemConfigurationList()
			throws Exception {
		return mongoTemplate.findAll(SystemConfiguration.class);
	}
	@Override
	public boolean deleteSystemConfiguration(String id) throws Exception {
		Query query = new Query();
		query.addCriteria(Criteria.where("_id").is(id));
		mongoTemplate.remove(query, SystemConfiguration.class);
	 
		return true;
	}
	
	@Override
	public List<ConfigurationDetails> getConfigurationDetailsList(
			String device_serial_number) throws Exception {
		
		try {
			
			List<ConfigurationDetails> ret_list = new ArrayList<ConfigurationDetails>();
			
			Query query = new Query();
			query.addCriteria(Criteria.where("_id").is(device_serial_number));
		
		
			DCUSysConfData dcu_obj = mongoTemplate.findOne(query, DCUSysConfData.class);
			 
			NodeConfData node_obj = mongoTemplate.findOne(query, NodeConfData.class);
			ScheduleConfData schedule_obj = mongoTemplate.findOne(query, ScheduleConfData.class);
			
			ConfigurationDetails tmp = new ConfigurationDetails();
			
			if(dcu_obj != null){
				tmp.setConf_type("SYS CONF");
				tmp.setLast_sync_time(dcu_obj.getLast_sync_time());
				tmp.setStatus(dcu_obj.getStatus());
				tmp.setRequest_sent_time(dcu_obj.getRequest_sent_time());
				ret_list.add(tmp);
			}
			
			

			ConfigurationDetails tmp_1 = new ConfigurationDetails();
			
			if(node_obj != null){
				tmp_1.setConf_type("NODE CONF");
				tmp_1.setLast_sync_time(node_obj.getLast_sync_time());
				tmp_1.setStatus(node_obj.getStatus());
				tmp_1.setRequest_sent_time(node_obj.getRequest_sent_time());
				ret_list.add(tmp_1);
			}
			
			
			ConfigurationDetails tmp_2 = new ConfigurationDetails();
			
			if(schedule_obj != null){
				tmp_2.setConf_type("SCHEDULER CONF");
				tmp_2.setLast_sync_time(schedule_obj.getLast_sync_time());
				tmp_2.setStatus(schedule_obj.getStatus());
				tmp_2.setRequest_sent_time(schedule_obj.getRequest_sent_time());
				ret_list.add(tmp_2);
			}
			
			return ret_list;
			
		} catch (Exception e) {
			System.out.println("Exception : "+ e.getMessage());
		}
		return null;
	}

	
	
	

}
