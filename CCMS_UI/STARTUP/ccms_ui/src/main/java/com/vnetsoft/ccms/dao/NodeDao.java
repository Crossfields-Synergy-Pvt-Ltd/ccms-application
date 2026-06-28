package com.vnetsoft.ccms.dao;

import java.util.List;

import com.vnetsoft.ccms.pojo.Node;


public interface NodeDao {

	public boolean addEntity(Node user) throws Exception;
	public Node getEntityById(String id) throws Exception;
	public List<Node> getEntityList() throws Exception;
	public boolean deleteEntity(String id) throws Exception;
	
}
