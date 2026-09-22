package com.vnetsoft.ccms.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import com.vnetsoft.ccms.pojo.IOPojo;
import com.vnetsoft.ccms.util.IOStatusTmpPojo_delete;

public class IOControllerTest {

	@Test
	public void convertsMongoTimestampIntoLocalDateAndTime() {
		IOPojo input = new IOPojo();
		input.setId(1720000000L);
		input.setDcu_id("DCU-1");
		input.setOpration_value(1);

		IOStatusTmpPojo_delete result = IOController.getIODetails(input);

		assertNotNull(result);
		assertEquals("DCU-1", result.getDcu_id());
		assertEquals("03 Jul 2024", result.getDate());
		assertEquals(15, result.getHour());
		assertEquals(16, result.getMin());
	}

	@Test
	public void timestampConversionHasNoSharedPreviousValueState() {
		IOPojo first = new IOPojo();
		first.setId(1720000000L);
		first.setOpration_value(1);
		IOPojo second = new IOPojo();
		second.setId(1720000001L);
		second.setOpration_value(1);

		assertNotNull(IOController.getIODetails(first));
		assertNotNull(IOController.getIODetails(second));
	}
}
