package com.vnetsoft.ccms.scheduler;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import org.apache.log4j.Logger;

public class FileSystemManager {


	static final Logger logger = Logger.getLogger(FileSystemManager.class);

	
	static final String FILE_SPRATER = "/";
	public static String BASE_PATH = "/home/data/dontdelete/";
	//public static String BASE_PATH = "E:/CCMS_DATA";
	public static  String touchPath(String dcu_serial_number, long time_stamp) {
		
		String file_name = getFileName(dcu_serial_number, time_stamp);
		File dir_path = null;
		try {
			  dir_path = new File(file_name);
			 
			  if (!dir_path.exists()) {
		            if (dir_path.mkdirs()) {
		                System.out.println("Multiple directories are created!");
		            }
		        }
			  
		} catch (Exception e) {
			logger.error("Exception : "+e.getMessage());
			logger.error(e.getStackTrace());
		}finally{
			
		}
		
		return file_name;
	}

	private static String getFileName(String dcu_serial_number, long time_stamp) {

		try {
			if (String.valueOf(time_stamp).length() > 10) {
				time_stamp = Long.parseLong(String.valueOf(time_stamp)
						.substring(0, 10));
			}
		} catch (Exception e) {
			logger.error("Exception : "+e.getMessage());
			logger.error(e.getStackTrace());
		}
		StringBuffer sb = new StringBuffer();
		System.out.println("TIME STAMP : " + time_stamp);
		try {
			Date date = new Date(time_stamp * 1000L);
			DateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
			format.setTimeZone(TimeZone.getTimeZone("Asia/Kolkata"));
			String formatted = format.format(date);

			formatted = formatted.substring(0, 11);
			String tmp[] = formatted.split("/");

			sb.append(BASE_PATH).append(FILE_SPRATER)
					.append(tmp[2].trim()).append(FILE_SPRATER)
					.append(tmp[1].trim()).append(FILE_SPRATER)
					.append(dcu_serial_number).append(FILE_SPRATER)
					.append(tmp[0].trim());
		} catch (Exception e) {
			logger.error("Exception : "+e.getMessage());
			logger.error(e.getStackTrace());
		}
		System.out.println(sb.toString());
		return sb.toString();
	}
	
	public static void main(String[] args) {
		//System.out.println(getFileName("1905HY3P3C000904", 1558502100L));
		
		Date date = new Date(1558502100L * 1000L);
        DateFormat format = new SimpleDateFormat("dd MMM yyyy HH:mm");
        format.setTimeZone(TimeZone.getTimeZone("Asia/Kolkata"));
        String formatted = format.format(date);
        System.out.println(formatted);
	}
}
