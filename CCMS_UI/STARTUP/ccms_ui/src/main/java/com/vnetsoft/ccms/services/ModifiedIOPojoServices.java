package com.vnetsoft.ccms.services;

import java.util.List;

import com.vnetsoft.ccms.pojo.ModifiedIOPojo;

public interface ModifiedIOPojoServices {

	List<ModifiedIOPojo> getByDcuAndDateRange(String dcuId, String startDate, String endDate) throws Exception;
}
