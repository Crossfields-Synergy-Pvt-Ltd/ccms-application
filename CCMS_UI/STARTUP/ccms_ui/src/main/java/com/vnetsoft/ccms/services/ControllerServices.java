package com.vnetsoft.ccms.services;

import java.util.List;

import com.vnetsoft.ccms.pojo.Event;
import com.vnetsoft.ccms.pojo.SchedulerConfiguration;
import com.vnetsoft.ccms.pojo.SystemConfiguration;
import com.vnetsoft.ccms.pojo.conf.ConfigurationDetails;


public interface ControllerServices {

	public boolean addSchedulerConfiguration(SchedulerConfiguration obj) throws Exception;
	public SchedulerConfiguration getSchedulerConfigurationById(String id) throws Exception;
	public List<SchedulerConfiguration> getSchedulerConfigurationList() throws Exception;
	public boolean deleteSchedulerConfiguration(String id) throws Exception;
	
	public boolean addEvent(Event obj) throws Exception;
	public Event getEventById(String id) throws Exception;
	public List<Event> getEventList() throws Exception;
	public boolean deleteEvent(String id) throws Exception;
	
	
	public boolean addSystemConfiguration(SystemConfiguration obj) throws Exception;
	public SystemConfiguration getSystemConfigurationById(String id) throws Exception;
	public List<SystemConfiguration> getSystemConfigurationList() throws Exception;
	public boolean deleteSystemConfiguration(String id) throws Exception;
	
	
	public List<ConfigurationDetails> getConfigurationDetailsList(String device_serial_number) throws Exception;
}
