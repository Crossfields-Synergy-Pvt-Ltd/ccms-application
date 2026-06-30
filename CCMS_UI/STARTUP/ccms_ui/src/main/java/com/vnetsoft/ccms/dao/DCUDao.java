package com.vnetsoft.ccms.dao;

import java.util.List;

import com.vnetsoft.ccms.pojo.DCU;
import com.vnetsoft.ccms.pojo.DCUConfiguration;
import com.vnetsoft.ccms.pojo.Event;
import com.vnetsoft.ccms.pojo.HandShake;
import com.vnetsoft.ccms.pojo.HierarchyDetails;
import com.vnetsoft.ccms.pojo.IOPojo;
import com.vnetsoft.ccms.pojo.DeviceMeterConfigurations;
import com.vnetsoft.ccms.pojo.MeterData;
import com.vnetsoft.ccms.pojo.MonitorControlCount;
import com.vnetsoft.ccms.pojo.SchedulerConfiguration;
import com.vnetsoft.ccms.pojo.SinglePhaseMeterData;
import com.vnetsoft.ccms.pojo.server.InstantEventData;
import com.vnetsoft.ccms.pojo.server.InstantMeterData;

public interface DCUDao {

	public boolean addDCU(DCU user) throws Exception;
	public DCU getDCUById(String id) throws Exception;
	public List<DCU> getDCUList() throws Exception;
	public boolean deleteDCU(String id) throws Exception;

	public boolean addHandShake(HandShake user) throws Exception;
	
	public List<HandShake> getHandShakeList() throws Exception;
	
	public HandShake getHandShakeByID(String dcu_serial_number) throws Exception;
	
	public List<Event> getEventList() throws Exception;
	
	
	public boolean addMeter(DeviceMeterConfigurations user) throws Exception;
	public List<DeviceMeterConfigurations>  getByMeterID(String dcu_serial_number) throws Exception;
	public void deleteDeviceMeterConfiguration(String dcu_serial_number) throws Exception;
	
	
	public boolean addDCUConfiguration(DCUConfiguration user) throws Exception;
	
	public DCUConfiguration getDCUConfigurationByID(String dcu_serial_number) throws Exception;

	public SinglePhaseMeterData getByMeterDataID(String id)  throws Exception;
	
	public List<SinglePhaseMeterData> getMeterDataListForCurrentDate()  throws Exception;
	
	public List<SinglePhaseMeterData> getByMeterDataBetweenDate(String id, String start_date,
			String end_date)   throws Exception;
	
	public List<DCUConfiguration> getDCUConfigurationList()  throws Exception;
	
	public InstantMeterData getByInstantMeterDataID(String id)  throws Exception;
	
	public List<InstantMeterData> getInstantMeterDataList()  throws Exception;
	
	
	public List<InstantEventData> getByInstantEventDataD(String id)  throws Exception;
	
	public List<InstantEventData> getInstantEventDataList()  throws Exception;
	
	
	public List<IOPojo> getIOPojoByID(String id)  throws Exception;
	
	public List<IOPojo> getIOPojoList()  throws Exception;
	public List<Event> getEventListByDate(String id,String start_date, String end_date)
			throws Exception;
	
	
	
	public boolean addHierarchy(HierarchyDetails obj) throws Exception;
	public List<HierarchyDetails> getHierarchyDetailsList()  throws Exception;
	public List<String> getMandalByDistrict(String district) throws Exception;
	public List<String> getGPByMandal(String mandal) throws Exception;
	public List<String> getVilageByGP(String gp) throws Exception;
	
	
	
	/*
	 * File System related 
	 */
	public List<MeterData>  getByMeterDataByDCUSerialNumber(String id)  throws Exception;
	public boolean deleteMeterDateByID(long id) throws Exception;
	
	
	public List<Event>  getByEventDataByDCUSerialNumber(String id)  throws Exception;
	public boolean deleteEventDateByID(long id) throws Exception;
	
	public List<IOPojo>  getByIODataByDCUSerialNumber(String id)  throws Exception;
	public boolean deleteIODateByID(long id) throws Exception;

	
	

	public boolean addSchedulerConfiguration(SchedulerConfiguration user) throws Exception;
	public SchedulerConfiguration getSchedulerConfigurationById(String id) throws Exception;
	public List<SchedulerConfiguration> getSchedulerConfigurationList() throws Exception;
	public boolean deleteSchedulerConfiguration(String id) throws Exception;

	
	public List<SinglePhaseMeterData>  getBySinglePhaseMeterDataByDCUSerialNumber(String id)  throws Exception;
	public boolean deleteSinglePhaseMeterDateByID(long id) throws Exception;
	
	
	
	public MonitorControlCount getDahsBoardCountstats(String district, String mandal, String gp) throws Exception;
}
