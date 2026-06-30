package com.vnetsoft.ccms.services;

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
			String mandal, String gp) throws Exception {
		return dashBoardDao.getDahsBoardCountstats(district, mandal, gp);
	}

	@Override
	public List<HandShake> getMapData(String district, String mandal, String gp)
			throws Exception {
		// TODO Auto-generated method stub
		return dashBoardDao.getMapData(district, mandal, gp);
	}

	@Override
	public List<HandShake> getAllHandShakeData(String district, String mandal, String gp) throws Exception {
		// TODO Auto-generated method stub
		return dashBoardDao.getAllHandShakeData(district, mandal, gp);
	}

	@Override
	public InstantMeterData getInstantMeterData(String device_serial_number) throws Exception {
		// TODO Auto-generated method stub
		return dashBoardDao.getInstantMeterData(device_serial_number);
	}

	@Override
	public  List<HandShake> getHandShakeByIDWithFilter(String district, String mandal, String gp, String dcu_name) throws Exception {
		// TODO Auto-generated method stub
		return dashBoardDao.getHandShakeByIDWithFilter(district, mandal, gp, dcu_name);
	}

	@Override
	public HandShake getHandShakeByID(String id) throws Exception {
		// TODO Auto-generated method stub
		return dashBoardDao.getHandShakeByID(id);
	}

}
