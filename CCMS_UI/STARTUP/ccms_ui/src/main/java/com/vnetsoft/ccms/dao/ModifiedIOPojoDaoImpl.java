package com.vnetsoft.ccms.dao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import com.vnetsoft.ccms.pojo.IOPojo;

public class ModifiedIOPojoDaoImpl implements ModifiedIOPojoDao {

	@Autowired
	private MongoTemplate mongoTemplate;

	@Override
	public List<IOPojo> findByDcuAndDateRange(String dcuId, int startDate, int endDate) throws Exception {
		Query query = new Query();
		query.addCriteria(Criteria.where("dcu_id").is(dcuId));
		query.addCriteria(Criteria.where("yymmdd").gte(startDate).lte(endDate));
		query.with(new Sort(Sort.Direction.ASC, "yymmdd", "id"));
		return mongoTemplate.find(query, IOPojo.class);
	}
}
