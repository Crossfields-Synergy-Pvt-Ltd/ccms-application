package com.vnetsoft.ccms.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.vnetsoft.ccms.dao.DCUDao;
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


public class DCUServicesImpl implements DCUServices {

	@Autowired
	DCUDao dcuDao;
	


	@Override
	public boolean addDCU(DCU user) throws Exception {
		
		return dcuDao.addDCU(user);
	}

	@Override
	public DCU getDCUById(String id) throws Exception {
		
		return dcuDao.getDCUById(id);
	}

	@Override
	public List<DCU> getDCUList() throws Exception {
		
		return dcuDao.getDCUList();
	}

	@Override
	public boolean deleteDCU(String id) throws Exception {
		
		return dcuDao.deleteDCU(id);
	}

	@Override
	public List<HandShake> getHandShakeList() throws Exception {
		return dcuDao.getHandShakeList();
	}

	@Override
	public List<Event> getEventList() throws Exception {
		return dcuDao.getEventList();
	}

	@Override
	public boolean addHandShake(HandShake user) throws Exception {
		
		return dcuDao.addHandShake(user);
	}

	@Override
	public boolean addMeter(DeviceMeterConfigurations user) throws Exception {
		
		return dcuDao.addMeter(user);
	}

	@Override
	public List<DeviceMeterConfigurations>  getByMeterID(String dcu_serial_number) throws Exception {
		
		return dcuDao.getByMeterID(dcu_serial_number);
	}

	@Override
	public boolean addDCUConfiguration(DCUConfiguration user) throws Exception {
		return dcuDao.addDCUConfiguration(user);
	}

	@Override
	public DCUConfiguration getDCUConfigurationByID(String dcu_serial_number)
			throws Exception {
		return dcuDao.getDCUConfigurationByID(dcu_serial_number);
	}

	@Override
	public SinglePhaseMeterData getByMeterDataID(String id) throws Exception {
		
		return dcuDao.getByMeterDataID(id);
	}

	@Override
	public List<SinglePhaseMeterData> getMeterDataListForCurrentDate() throws Exception {
		
		return dcuDao.getMeterDataListForCurrentDate();
	}

	@Override
	public List<DCUConfiguration> getDCUConfigurationList() throws Exception {
		
		return dcuDao.getDCUConfigurationList();
	}

	@Override
	public InstantMeterData getByInstantMeterDataID(String id) throws Exception {
		return dcuDao.getByInstantMeterDataID(id);
	}

	@Override
	public List<InstantMeterData> getInstantMeterDataList() throws Exception {
		return dcuDao.getInstantMeterDataList();
	}

	@Override
	public List<InstantEventData> getByInstantEventDataD(String id) throws Exception {
		return dcuDao.getByInstantEventDataD(id);
	}

	@Override
	public List<InstantEventData> getInstantEventDataList() throws Exception {		
		return dcuDao.getInstantEventDataList();
	}

	@Override
	public List<IOPojo> getIOPojoByID(String id) throws Exception {
		
		return dcuDao.getIOPojoByID(id);
	}

	@Override
	public List<IOPojo> getIOPojoList() throws Exception {
		
		return dcuDao.getIOPojoList();
	}

	@Override
	public HandShake getHandShakeByID(String dcu_serial_number) throws Exception {
		
		return dcuDao.getHandShakeByID( dcu_serial_number);
	}

	@Override
	public List<SinglePhaseMeterData> getByMeterDataBetweenDate(String id, String start_date,
			String end_date) throws Exception {
		
		return dcuDao.getByMeterDataBetweenDate(id, start_date, end_date);
	}

	@Override
	public List<Event> getEventListByDate(String id,String start_date, String end_date)
			throws Exception {
		
		return dcuDao.getEventListByDate(id, start_date, end_date);
	}

	@Override
	public boolean addHierarchy(HierarchyDetails obj) throws Exception {
		
		return dcuDao.addHierarchy(obj);
	}

	@Override
	public List<HierarchyDetails> getHierarchyDetailsList() throws Exception {
		
		return dcuDao.getHierarchyDetailsList();
	}

	@Override
	public List<String> getMandalByDistrict(String district) throws Exception {
		
		return dcuDao.getMandalByDistrict(district);
	}

	@Override
	public List<String> getGPByMandal(String mandal) throws Exception {
		
		return dcuDao.getGPByMandal(mandal);
	}

	@Override
	public List<String> getVilageByGP(String gp) throws Exception {
		
		return dcuDao.getVilageByGP(gp);
	}

	@Override
	public List<MeterData> getByMeterDataByDCUSerialNumber(String id)
			throws Exception {
		
		return dcuDao.getByMeterDataByDCUSerialNumber(id);
	}

	@Override
	public boolean deleteMeterDateByID(long id) throws Exception {
		
		return dcuDao.deleteMeterDateByID(id);
	}

	@Override
	public List<Event> getByEventDataByDCUSerialNumber(String id)
			throws Exception {
		
		return dcuDao.getByEventDataByDCUSerialNumber(id);
	}

	@Override
	public boolean deleteEventDateByID(long id) throws Exception {
		
		return dcuDao.deleteEventDateByID(id);
	}

	@Override
	public List<IOPojo> getByIODataByDCUSerialNumber(String id)
			throws Exception {
		// TODO Auto-generated method stub
		return dcuDao.getByIODataByDCUSerialNumber(id);
	}

	@Override
	public boolean deleteIODateByID(long id) throws Exception {
		// TODO Auto-generated method stub
		return dcuDao.deleteIODateByID(id);
	}

	@Override
	public boolean addSchedulerConfiguration(SchedulerConfiguration user)
			throws Exception {
		// TODO Auto-generated method stub
		return dcuDao.addSchedulerConfiguration(user);
	}

	@Override
	public SchedulerConfiguration getSchedulerConfigurationById(String id)
			throws Exception {
		// TODO Auto-generated method stub
		return dcuDao.getSchedulerConfigurationById(id);
	}

	@Override
	public List<SchedulerConfiguration> getSchedulerConfigurationList()
			throws Exception {
		// TODO Auto-generated method stub
		return dcuDao.getSchedulerConfigurationList();
	}

	@Override
	public boolean deleteSchedulerConfiguration(String id) throws Exception {
		// TODO Auto-generated method stub
		return dcuDao.deleteSchedulerConfiguration(id);
	}

	@Override
	public List<SinglePhaseMeterData> getBySinglePhaseMeterDataByDCUSerialNumber(
			String id) throws Exception {
		// TODO Auto-generated method stub
		return dcuDao.getBySinglePhaseMeterDataByDCUSerialNumber(id);
	}

	@Override
	public boolean deleteSinglePhaseMeterDateByID(long id) throws Exception {
		// TODO Auto-generated method stub
		return dcuDao.deleteSinglePhaseMeterDateByID(id);
	}

	@Override
	public MonitorControlCount getDahsBoardCountstats(String district, String mandal, String gp) throws Exception {
		// TODO Auto-generated method stub
		return dcuDao.getDahsBoardCountstats(district, mandal, gp);
	}

	@Override
	public void deleteDeviceMeterConfiguration(String dcu_serial_number)
			throws Exception {
		dcuDao.deleteDeviceMeterConfiguration(dcu_serial_number);
	}

}
