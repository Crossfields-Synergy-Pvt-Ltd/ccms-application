package com.vnetsoft.ccms.services;

import java.util.ArrayList;
import java.text.SimpleDateFormat;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.vnetsoft.ccms.dao.ModifiedIOPojoDao;
import com.vnetsoft.ccms.pojo.IOPojo;
import com.vnetsoft.ccms.pojo.ModifiedIOPojo;

public class ModifiedIOPojoServicesImpl implements ModifiedIOPojoServices {

	@Autowired
	private ModifiedIOPojoDao modifiedIODao;

	@Override
	public List<ModifiedIOPojo> getByDcuAndDateRange(String dcuId, String startDate, String endDate) throws Exception {
		int start = parseDate(startDate);
		int end = parseDate(endDate);
		if (start > end) {
			int temporary = start;
			start = end;
			end = temporary;
		}

		List<ModifiedIOPojo> result = new ArrayList<ModifiedIOPojo>();
		for (IOPojo source : modifiedIODao.findByDcuAndDateRange(dcuId, start, end)) {
			ModifiedIOPojo target = new ModifiedIOPojo();
			target.setDcu_id(source.getDcu_id());
			target.setNode(String.valueOf(source.getNode_id()));
			target.setDate(formatDate(source.getYymmdd()));
			target.setOperation_type(source.getOpration_type());
			target.setOperation_value(source.getOpration_value());
			target.setOperation_reason(source.getOpration_resone());
			result.add(target);
		}
		return result;
	}

	public static int parseDate(String value) {
		try {
			if (value == null || !value.matches("\\d{2}/\\d{2}/\\d{4}")) {
				throw new IllegalArgumentException("Dates must use DD/MM/YYYY format");
			}
			SimpleDateFormat parser = new SimpleDateFormat("dd/MM/yyyy");
			parser.setLenient(false);
			return Integer.parseInt(new SimpleDateFormat("yyyyMMdd").format(parser.parse(value)));
		} catch (IllegalArgumentException e) {
			throw e;
		} catch (Exception e) {
			throw new IllegalArgumentException("Invalid date: " + value, e);
		}
	}

	public static String formatDate(int value) throws Exception {
		return new SimpleDateFormat("dd/MM/yyyy").format(new SimpleDateFormat("yyyyMMdd").parse(String.valueOf(value)));
	}
}
