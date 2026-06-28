package com.vnetsoft.ccms.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.vnetsoft.ccms.dao.ControllerDao;
import com.vnetsoft.ccms.pojo.Event;
import com.vnetsoft.ccms.pojo.SchedulerConfiguration;
import com.vnetsoft.ccms.pojo.SystemConfiguration;
import com.vnetsoft.ccms.pojo.conf.ConfigurationDetails;

public class ControllerServicesImpl implements ControllerServices {

	@Autowired
	ControllerDao controllerDao;

	@Override
	public boolean addSchedulerConfiguration(SchedulerConfiguration obj)
			throws Exception {
		// TODO Auto-generated method stub
		return controllerDao.addSchedulerConfiguration(obj);
	}

	@Override
	public SchedulerConfiguration getSchedulerConfigurationById(String id)
			throws Exception {
		// TODO Auto-generated method stub
		return controllerDao.getSchedulerConfigurationById(id);
	}

	@Override
	public List<SchedulerConfiguration> getSchedulerConfigurationList()
			throws Exception {
		// TODO Auto-generated method stub
		return controllerDao.getSchedulerConfigurationList();
	}

	@Override
	public boolean deleteSchedulerConfiguration(String id) throws Exception {
		// TODO Auto-generated method stub
		return controllerDao.deleteSchedulerConfiguration(id);
	}

	@Override
	public boolean addEvent(Event obj) throws Exception {
		// TODO Auto-generated method stub
		return controllerDao.addEvent(obj);
	}

	@Override
	public Event getEventById(String id) throws Exception {
		// TODO Auto-generated method stub
		return controllerDao.getEventById(id);
	}

	@Override
	public List<Event> getEventList() throws Exception {
		// TODO Auto-generated method stub
		return controllerDao.getEventList();
	}

	@Override
	public boolean deleteEvent(String id) throws Exception {
		// TODO Auto-generated method stub
		return controllerDao.deleteEvent(id);
	}

	@Override
	public boolean addSystemConfiguration(SystemConfiguration obj)
			throws Exception {
		// TODO Auto-generated method stub
		return controllerDao.addSystemConfiguration(obj);
	}

	@Override
	public SystemConfiguration getSystemConfigurationById(String id)
			throws Exception {
		// TODO Auto-generated method stub
		return controllerDao.getSystemConfigurationById(id);
	}

	@Override
	public List<SystemConfiguration> getSystemConfigurationList()
			throws Exception {
		// TODO Auto-generated method stub
		return controllerDao.getSystemConfigurationList();
	}

	@Override
	public boolean deleteSystemConfiguration(String id) throws Exception {
		// TODO Auto-generated method stub
		return controllerDao.deleteSystemConfiguration(id);
	}

	@Override
	public List<ConfigurationDetails> getConfigurationDetailsList(
			String device_serial_number) throws Exception {
		// TODO Auto-generated method stub
		return controllerDao.getConfigurationDetailsList(device_serial_number);
	}

	
}
