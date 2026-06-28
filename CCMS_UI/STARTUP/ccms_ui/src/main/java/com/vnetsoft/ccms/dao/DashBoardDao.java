package com.vnetsoft.ccms.dao;

import java.util.List;

import com.vnetsoft.ccms.pojo.HandShake;
import com.vnetsoft.ccms.pojo.MonitorControlCount;
import com.vnetsoft.ccms.pojo.server.InstantMeterData;

public interface DashBoardDao {

	
	public MonitorControlCount getDahsBoardCountstats(String district, String mandal, String gp) throws Exception;
	

	public List<HandShake> getMapData(String distrtict, String mandal, String gp)  throws Exception;
	
	public List<HandShake> getAllHandShakeData(String distrtict, String mandal, String gp)  throws Exception;
	
	public InstantMeterData getInstantMeterData(String device_serial_number)  throws Exception;
	
	public HandShake getHandShakeByID(String dcu_serial_number)  throws Exception;
	
	public List<HandShake> getHandShakeByIDWithFilter(String distrtict, String mandal, String gp, String dcu_name)  throws Exception;
}
