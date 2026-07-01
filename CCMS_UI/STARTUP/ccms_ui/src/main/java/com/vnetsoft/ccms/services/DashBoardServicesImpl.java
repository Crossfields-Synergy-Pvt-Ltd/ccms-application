package com.vnetsoft.ccms.services;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.vnetsoft.ccms.dao.DashBoardDao;
import com.vnetsoft.ccms.pojo.HandShake;
import com.vnetsoft.ccms.pojo.MonitorControlCount;
import com.vnetsoft.ccms.pojo.server.InstantMeterData;

public class DashBoardServicesImpl  implements DashBoardServices{


	@Autowired
	DashBoardDao dashBoardDao;
	
	@Override
	public MonitorControlCount getDahsBoardCountstats(String district,
			String mandal, String gp, Date startDate, Date endDate) throws Exception {
		return dashBoardDao.getDahsBoardCountstats(district, mandal, gp, startDate, endDate);
	}

	@Override
	public List<HandShake> getMapData(String district, String mandal, String gp,
			Date startDate, Date endDate) throws Exception {
		return dashBoardDao.getMapData(district, mandal, gp, startDate, endDate);
	}

	@Override
	public List<HandShake> getAllHandShakeData(String district, String mandal, String gp,
			Date startDate, Date endDate) throws Exception {
		return dashBoardDao.getAllHandShakeData(district, mandal, gp, startDate, endDate);
	}

	@Override
	public InstantMeterData getInstantMeterData(String device_serial_number) throws Exception {
		return dashBoardDao.getInstantMeterData(device_serial_number);
	}

	@Override
	public  List<HandShake> getHandShakeByIDWithFilter(String district, String mandal, String gp, String dcu_name,
			Date startDate, Date endDate) throws Exception {
		return dashBoardDao.getHandShakeByIDWithFilter(district, mandal, gp, dcu_name, startDate, endDate);
	}

	@Override
	public HandShake getHandShakeByID(String id) throws Exception {
		return dashBoardDao.getHandShakeByID(id);
	}

}
