package com.vnetsoft.ccms.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.vnetsoft.ccms.dao.NodeDao;
import com.vnetsoft.ccms.pojo.Node;

public class NodeServicesImpl implements NodeServices {

	@Autowired
	NodeDao nodeDao;

	@Override
	public boolean addEntity(Node user) throws Exception {
		return nodeDao.addEntity(user);
	}

	@Override
	public Node getEntityById(String id) throws Exception {
		return nodeDao.getEntityById(id);
	}

	@Override
	public List<Node> getEntityList() throws Exception {
		return nodeDao.getEntityList();
	}

	@Override
	public boolean deleteEntity(String id) throws Exception {
		return nodeDao.deleteEntity(id);
	}

}
