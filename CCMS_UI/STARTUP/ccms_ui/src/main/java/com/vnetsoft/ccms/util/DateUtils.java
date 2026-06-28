package com.vnetsoft.ccms.util;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;


public class DateUtils {

	public static String getEpochTimeFromSeconds(long seconds) {

		String formattedDtm = null;
		try {

			seconds = Long.valueOf(String.valueOf(seconds).substring(0, 10));
			
			final DateTimeFormatter formatter = DateTimeFormatter
					.ofPattern("dd MMM yy HH:mm");

			formattedDtm = Instant.ofEpochSecond(seconds)
					.atZone(ZoneId.of("Asia/Kolkata")).format(formatter);
		} catch (Exception e) {
			System.out.println("Exception while convertting last seen date : "
					+ e.getMessage());
		}
		return formattedDtm;
	}

	public static String getLastSeenTimeForMonitorControl(long seconds) {

		String formattedDtm = null;
		try {
			seconds = Long.valueOf(String.valueOf(seconds).substring(0, 10));
			
			
			final DateTimeFormatter formatter = DateTimeFormatter
					.ofPattern("dd MMM yy HH:mm");

			formattedDtm = Instant.ofEpochSecond(seconds)
					.atZone(ZoneId.of("Asia/Kolkata")).format(formatter);

		} catch (Exception e) {
			System.out.println("Exception while convertting last seen date : "
					+ e.getMessage());
		}
		return formattedDtm;
	}

	public static void main(String[] args) {
		/*System.out.println(getLastSeenTimeForMonitorControl(1558081800));

		final DateTimeFormatter formatter = DateTimeFormatter
				.ofPattern("yyyy-MM-dd HH:mm:ss");

		final long unixTime = 1558081800;
		final String formattedDtm = Instant.ofEpochSecond(unixTime)
				.atZone(ZoneId.of("Asia/Kolkata")).format(formatter);

		System.out.println(formattedDtm);*/

	}
}
