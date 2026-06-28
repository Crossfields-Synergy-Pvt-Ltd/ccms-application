package com.vnetsoft.ccms.dao;

import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import com.vnetsoft.ccms.pojo.Node;



public class NodeDaoImpl implements NodeDao {

	
	@Autowired
	private MongoTemplate mongoTemplate;
	
	static final Logger logger = Logger.getLogger(NodeDaoImpl.class);

	public MongoTemplate getMongoTemplate() {
		return mongoTemplate;
	}
	
	
	@Autowired
	SessionFactory sessionFactory;

	Session session = null;
	Transaction tx = null;


	@Override
	public boolean addEntity(Node user) throws Exception {
		mongoTemplate.save(user, "node_details");
		return true;
	}
	
	@Override
	public Node getEntityById(String id) throws Exception {
		Query query = new Query();
		query.addCriteria(Criteria.where("_id").is(id));
	
	
		List<Node> list = mongoTemplate.find(query, Node.class);
		 
		
		return list.get(0);
	}
	
	
	@Override
	public List<Node> getEntityList() throws Exception {
		Query query = new Query();
		query.with(
				new Sort(Sort.Direction.ASC, "nodeid"));
		
		return mongoTemplate.find(query, Node.class);
	}
	
	@Override
	public boolean deleteEntity(String id) throws Exception {
		Query query = new Query();
		query.addCriteria(Criteria.where("_id").is(id));
		mongoTemplate.remove(query, Node.class);
	 
		return true;	}
	

}
