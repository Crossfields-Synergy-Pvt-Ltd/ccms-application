package com.vnetsoft.ccms.dao;

import java.util.List;

import com.vnetsoft.ccms.pojo.IOPojo;

public interface ModifiedIOPojoDao {

	List<IOPojo> findByDcuAndDateRange(String dcuId, int startDate, int endDate) throws Exception;
}
