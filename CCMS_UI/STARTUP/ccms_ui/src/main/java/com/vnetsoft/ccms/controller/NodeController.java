package com.vnetsoft.ccms.controller;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.vnetsoft.ccms.pojo.Status;
import com.vnetsoft.ccms.pojo.Node;
import com.vnetsoft.ccms.services.NodeServices;

@Controller
@RequestMapping("/node")
public class NodeController {

	@Autowired
	NodeServices nodeServices;

	static final Logger logger = Logger.getLogger(NodeController.class);

	@RequestMapping(value = "/create", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Status add(@RequestBody Node obj,
			@RequestHeader("Authorization") String basicAuth) {

		try {
			if (logger.isDebugEnabled()) {
				logger.debug(obj);
			}
			nodeServices.addEntity(obj);
			return new Status(200, "Success");
		} catch (Exception e) {
			return new Status(0, e.toString());
		}
	}

	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public @ResponseBody List<Node> getAll(
			@RequestHeader("Authorization") String basicAuth) {

		if (logger.isDebugEnabled()) {
			logger.debug("GET ALL REQUEST RECIVED");
		}
		List<Node> userList = null;
		try {
			userList = nodeServices.getEntityList();
			if (logger.isDebugEnabled()) {
				logger.debug(userList);
			}
		} catch (Exception e) {
			System.out.println("Exception : " + e);
			e.printStackTrace();
		}

		return userList;
	}

	@RequestMapping(value = "delete/{id}", method = RequestMethod.DELETE)
	public @ResponseBody Status delete(@PathVariable("id") String id) {

		try {
			nodeServices.deleteEntity(id);
			return new Status(1, "Employee deleted Successfully !");
		} catch (Exception e) {
			return new Status(0, e.toString());
		}

	}

}
