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
import com.vnetsoft.ccms.util.PasswordHasher;


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
		validateEmail(user.getEmail());
		if (getEntityById(user.getEmail()) != null) {
			throw new IllegalStateException("Email is already registered");
		}
		user.setPassword(PasswordHasher.hash(user.getPassword()));
		mongoTemplate.save(user, "ccms_user_details");
		return true;
	}

	@Override
	public boolean updateEntity(String originalEmail, User user) throws Exception {
		validateEmail(originalEmail);
		validateEmail(user.getEmail());
		User existing = getEntityById(originalEmail);
		if (existing == null) {
			throw new IllegalArgumentException("User does not exist");
		}
		if (!originalEmail.equals(user.getEmail()) && getEntityById(user.getEmail()) != null) {
			throw new IllegalStateException("Email is already registered");
		}
		if (user.getPassword() == null || user.getPassword().trim().isEmpty()) {
			user.setPassword(existing.getPassword());
		} else if (!user.getPassword().startsWith("{PBKDF2}")) {
			user.setPassword(PasswordHasher.hash(user.getPassword()));
		}
		if (!originalEmail.equals(user.getEmail())) {
			Query oldQuery = new Query(Criteria.where("email").is(originalEmail));
			mongoTemplate.remove(oldQuery, User.class);
		}
		mongoTemplate.save(user, "ccms_user_details");
		return true;
	}

	@Override
	public User getEntityById(String id) throws Exception {
		Query query = new Query();
		query.addCriteria(Criteria.where("email").is(id));
	
	
		List<User> list = mongoTemplate.find(query, User.class);
		 
		
		return list.isEmpty() ? null : list.get(0);
	}

	@Override
	public List<User> getEntityList() throws Exception {
		return mongoTemplate.findAll(User.class);
	}

	@Override
	public boolean deleteEntity(String id) throws Exception {
		Query query = new Query();
		query.addCriteria(Criteria.where("email").is(id));
		return mongoTemplate.remove(query, User.class).getN() > 0;
	}

	private void validateEmail(String email) {
		if (email == null || !email.matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")) {
			throw new IllegalArgumentException("A valid email is required");
		}
	}

}
