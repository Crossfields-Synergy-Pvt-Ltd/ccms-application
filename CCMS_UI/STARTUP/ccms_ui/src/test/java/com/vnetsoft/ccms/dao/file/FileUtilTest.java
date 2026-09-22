package com.vnetsoft.ccms.dao.file;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class FileUtilTest {

	@Test
	public void convertsHourMinuteDurationsToSortableMinutes() {
		assertEquals(125, FileUtil.durationToMinutes("2:05"));
		assertEquals(600, FileUtil.durationToMinutes("10:00"));
	}

	@Test
	public void invalidDurationsSortAtZero() {
		assertEquals(0, FileUtil.durationToMinutes("invalid"));
	}
}
