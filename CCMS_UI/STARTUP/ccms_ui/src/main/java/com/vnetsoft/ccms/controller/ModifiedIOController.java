package com.vnetsoft.ccms.controller;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;

import com.vnetsoft.ccms.pojo.ModifiedIOPojo;
import com.vnetsoft.ccms.services.ModifiedIOPojoServices;

@Controller
@RequestMapping("/modified_io")
public class ModifiedIOController {

	@Autowired
	private ModifiedIOPojoServices modifiedIOServices;

	@RequestMapping(value = "/modified_io_list", method = RequestMethod.GET)
	public @ResponseBody List<ModifiedIOPojo> getModifiedIOList(
			@RequestParam("id") String id,
			@RequestParam("start_date") String startDate,
			@RequestParam("end_date") String endDate) throws Exception {
		return modifiedIOServices.getByDcuAndDateRange(id, startDate, endDate);
	}

	@RequestMapping(value = "/export", method = RequestMethod.GET)
	public void exportModifiedIO(HttpServletResponse response,
			@RequestParam("id") String id,
			@RequestParam("start_date") String startDate,
			@RequestParam("end_date") String endDate) throws Exception {
		List<ModifiedIOPojo> rows = modifiedIOServices.getByDcuAndDateRange(id, startDate, endDate);
		String filename = "Light_Status_" + id + "_" + startDate + "_" + endDate + ".csv";
		response.setContentType("text/csv");
		response.setHeader("Content-Disposition", "attachment; filename=\"" + filename + "\"");
		response.setHeader("x-filename", filename);

		ICsvBeanWriter writer = new CsvBeanWriter(response.getWriter(), CsvPreference.STANDARD_PREFERENCE);
		try {
			writer.writeHeader("DCU", "Node", "Date", "Operation Type", "Operation Value", "Operation Reason");
			for (ModifiedIOPojo row : rows) {
				writer.write(row, new String[] { "dcu_id", "node", "date", "operation_type", "operation_value", "operation_reason" });
			}
		} finally {
			writer.close();
		}
	}
}
