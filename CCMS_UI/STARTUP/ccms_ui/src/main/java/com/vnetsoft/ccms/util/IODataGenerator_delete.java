package com.vnetsoft.ccms.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class IODataGenerator_delete {

	
	public static void main(String[] args) {
		List<IOStatusTmpPojo_delete> obj_list = getMeterData("E:/CCMS_DATA/1905HY3P3C000904/2019/05/28/io_data.csv");
		System.out.println(obj_list);
	}
	
	/*public static void main(String[] args) {
		
		System.out.println("hello Word");
	}*/
	
	

	public static List<IOStatusTmpPojo_delete> getMeterData(String file_name) {
		
		List<IOStatusTmpPojo_delete> meter_data = new ArrayList<IOStatusTmpPojo_delete>();
		
		
		 BufferedReader fileBufferReader = null;
		try {
			
			fileBufferReader = new BufferedReader(new FileReader(file_name));
			String buffer;
			while ((buffer = fileBufferReader.readLine()) != null) {
				IOStatusTmpPojo_delete ui_obj = new IOStatusTmpPojo_delete();
				
				 System.out.println(buffer);
				 if(buffer.length() > 20){
				 String tmp[] = buffer.split(",");
				 ui_obj.setDate(tmp[2].trim());
					ui_obj.setDcu_id(tmp[1].trim());
					ui_obj.setHour(Integer.parseInt(tmp[3].trim()));
					ui_obj.setMin(Integer.parseInt(tmp[4].trim()));
					ui_obj.setStatus(tmp[5]);
					ui_obj.setOpration_resone(Integer.parseInt(tmp[6].trim()));
					
					meter_data.add(ui_obj);
				 }
			}

		} catch (Exception e) {
			System.out.println("Exception : "+ e.getMessage());
		}finally{
			try {
				fileBufferReader.close();
			} catch (Exception e) {
				System.out.println("Eceception : "+ e.getMessage());
				e.printStackTrace();
			}
		}
		
		return meter_data;
		//System.out.println(meter_data);
	}
}
