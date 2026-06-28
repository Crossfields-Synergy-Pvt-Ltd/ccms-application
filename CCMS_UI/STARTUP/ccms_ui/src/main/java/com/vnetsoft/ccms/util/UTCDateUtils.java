package com.vnetsoft.ccms.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class UTCDateUtils {

	public static void main(String[] args) throws ParseException {
		 
		
		
		String hour_min = "13:00"; 
		
		int min = 00;
		for(int hour = 0; hour < 24; ){
			
			if(min == 60){
				hour++;
				min= 0;
			}
			hour_min = hour + ":"+ min;
			
			//System.out.println(getUTCSeconds(hour_min));
			min+=15;
		
			
		}
		
		long epoch = new java.text.SimpleDateFormat("MM/dd/yyyy HH:mm:ss").parse("27/05/1927 00:00:00").getTime() / 1000;
		
	     System.out.println("time in UTC : " + epoch);
		/*String handle_1_time = getUTCHourMin(hour_min);
		System.out.println(handle_1_time);
		System.out.println(getRefranceTime(handle_1_time));
		 String strDateFormat = "dd-MM-yyyy HH:mm:ss";
		  DateFormat dateFormat_1 = new SimpleDateFormat(strDateFormat);
		    
		Date date =dateFormat_1.parse("27-06-2019 "+ hour_min);
		System.out.println(getLocalToUTCDate(date));*/
	}

	static final String DATEFORMAT = "yyyy-MM-dd HH:mm:ss";

			public static Date GetUTCdatetimeAsDate()
			{
			    //note: doesn't check for null
			    return StringDateToDate(GetUTCdatetimeAsString());
			}

			public static String GetUTCdatetimeAsString()
			{
			    final SimpleDateFormat sdf = new SimpleDateFormat(DATEFORMAT);
			    sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
			    final String utcTime = sdf.format(new Date());

			    return utcTime;
			}

			public static Date StringDateToDate(String StrDate)
			{
			    Date dateToReturn = null;
			    SimpleDateFormat dateFormat = new SimpleDateFormat(DATEFORMAT);

			    try
			    {
			        dateToReturn = (Date)dateFormat.parse(StrDate);
			    }
			    catch (ParseException e)
			    {
			        e.printStackTrace();
			    }

			    return dateToReturn;
			}
			
	public static int getUTCSeconds(String time_satmp) {
		int hour_in_sec = 3600;
		int ref_time = 0;
		
		try {
			String tmp[] = time_satmp.split(":");
			ref_time = (Integer.parseInt(tmp[0]) * hour_in_sec)
					+ (Integer.parseInt(tmp[1]) * 60);
			ref_time -=19800;
			
			if(ref_time < 0){
				ref_time += 86400;
			}
			
			System.out.println("REFRANCE TIME : [ " + time_satmp + " ] "
					+ ref_time);
		} catch (Exception e) {
			System.out.println("EXCEPTION WHILE CONVERTING REFRANCE TIME : "
					+ e.getMessage());
		}
		return ref_time;

	}
	
/*	private static int getRefranceTime(String handle_1_time) {
		int hour_in_sec = 3600;
		int ref_time = 0;
		try {
			String tmp[] = handle_1_time.split(":");
			ref_time = (Integer.parseInt(tmp[0]) * hour_in_sec) + (Integer.parseInt(tmp[1]) * 60);

			
			 * As per document Tip: Reference time = Converted UTC ON/OFF time
			 * into seconds. For ex: If OFF time of schedule is 6.00 a.m. then
			 * Reference time = 21600 (6 a.m. in terms of seconds in IST format)
			 * + (-19800) (UTC seconds) = 1800 seconds
			 
			if(ref_time >= 19800){
				ref_time -= 19800;
			} else {
				ref_time = 19800 - ref_time;
			}
			System.out.println("REFRANCE TIME : [ "+ handle_1_time + " ] "+ ref_time);
		} catch (Exception e) {
			System.out.println("EXCEPTION WHILE CONVERTING REFRANCE TIME : "+ e.getMessage());
		}
		return ref_time;
	}*/
	public static String getLocalToUTCDate(Date date) {
	    Calendar calendar = Calendar.getInstance();
	    calendar.setTime(date);
	    calendar.setTimeZone(TimeZone.getTimeZone("UTC"));
	    Date time = calendar.getTime();
	     SimpleDateFormat outputFmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	    outputFmt.setTimeZone(TimeZone.getTimeZone("UTC"));
	    return outputFmt.format(time);
	}
	
	private static  String getUTCHourMin(String hour_min) {
		String ret_hour_min = hour_min;
		try {
			 String strDateFormat = "dd-MM-yyyy HH:mm:ss";
			  DateFormat dateFormat_1 = new SimpleDateFormat(strDateFormat);
			    
			Date date =dateFormat_1.parse("27-06-2019 "+ hour_min);
			SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
			dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
			
			//date = dateFormat.parse("27-06-2019 17:49:00");
			System.out.println("UTC Time is: " + dateFormat.format(date));
			Calendar utc_date = dateFormat.getCalendar();
			//System.out.println(utc_date.get(Calendar.HOUR));
			//System.out.println(utc_date.get(Calendar.MINUTE));
			ret_hour_min = (utc_date.get(Calendar.HOUR) + ":"+ utc_date.get(Calendar.MINUTE));
		} catch (Exception e) {
			System.out.println("Exception : "+ e.getMessage());
		}
		return ret_hour_min;
	}
	
	
	private static int getRefranceTime(String handle_1_time) {
		int hour_in_sec = 3600;
		int ref_time = 0;
		try {
			String tmp[] = handle_1_time.split(":");
			ref_time = (Integer.parseInt(tmp[0]) * hour_in_sec) + (Integer.parseInt(tmp[1]) * 60);

			/*
			 * As per document Tip: Reference time = Converted UTC ON/OFF time
			 * into seconds. For ex: If OFF time of schedule is 6.00 a.m. then
			 * Reference time = 21600 (6 a.m. in terms of seconds in IST format)
			 * + (-19800) (UTC seconds) = 1800 seconds
			 */
		/*	if(ref_time >= 19800){
				ref_time -= 19800;
			} else {
				ref_time = 19800 - ref_time;
			}*/
			System.out.println("REFRANCE TIME : [ "+ handle_1_time + " ] "+ ref_time);
		} catch (Exception e) {
			System.out.println("EXCEPTION WHILE CONVERTING REFRANCE TIME : "+ e.getMessage());
		}
		return ref_time;
	}
	
	
}
