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

import com.vnetsoft.ccms.pojo.User;


public class UserDaoImpl implements UserDao {

	
	@Autowired
	private MongoTemplate mongoTemplate;
	
	static final Logger logger = Logger.getLogger(UserDaoImpl.class);

	public MongoTemplate getMongoTemplate() {
		return mongoTemplate;
	}
	
	
	@Autowired
	SessionFactory sessionFactory;

	Session session = null;
	Transaction tx = null;

	@Override
	public boolean addEntity(User user) throws Exception {
		mongoTemplate.save(user, "ccms_user_details");
		return true;
	}

	@Override
	public User getEntityById(String id) throws Exception {
		Query query = new Query();
		query.addCriteria(Criteria.where("email").is(id));
	
	
		List<User> list = mongoTemplate.find(query, User.class);
		 
		
		return list.get(0);
	}

	@Override
	public List<User> getEntityList() throws Exception {
		return mongoTemplate.findAll(User.class);
	}

	@Override
	public boolean deleteEntity(String id) throws Exception {
		Query query = new Query();
		query.addCriteria(Criteria.where("name").is(id));
		mongoTemplate.remove(query, User.class);
	 
		return true;
	}

}
