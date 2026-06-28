package com.vnetsoft.ccms.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.bson.Document;

import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.vnetsoft.ccms.pojo.SchedulerConfiguration;

public class SchedulerConfDataGeneratorTest {

	
	public static void main(String[] args) {
		
		   SchedulerConfiguration obj = new SchedulerConfiguration();
		   getSchedulerConfBuffer(obj);
		/*try {
			
			MongoClient mongo = new MongoClient( "localhost" , 27017 );
			 // Accessing the database 
		    MongoDatabase database = mongo.getDatabase("ccms");
	
		    MongoCollection<Document> collection = database.getCollection("scheduler_details");
		       
	        System.out.println("Collection sampleCollection selected successfully"); 

	        // Getting the iterable object 
	        FindIterable<Document> iterDoc = collection.find(); 
	        int i = 1; 

	        // Getting the iterator 
	        Iterator cur = iterDoc.iterator(); 
	      
	        while (cur.hasNext()) {  
	        	  Document doc = (Document) cur.next();
	             SchedulerConfiguration obj = new SchedulerConfiguration();
	             
	              List list = new ArrayList(doc.values());
	              System.out.println(list);
	              String id = String.valueOf(list.get(0));
	              System.out.println("id -- " + id);
	              
	              obj.setScheduleId(Long.parseLong(id));
	             
	              
	              
	             
	         	 obj.setScheduleId(Long.parseLong(id));
            	 System.out.println("OVER WRITE : "+list.get(2));
            	 obj.setSchedules_name(String.valueOf(list.get(2)));
            	 
            	 obj.setValid_till("2022/04/07");
            	 
	        }
    
		      
		} catch (Exception e) {
			// TODO: handle exception
		}*/
	}
	
	
	private static String getSchedulerConfBuffer(SchedulerConfiguration obj) {
		
		StringBuffer sb = new StringBuffer();
		
		long end_date = 1649289600;
		
		try {
			
			String meter_schedule_buff = getMeterScheduleConfBuffer(3,30, System.currentTimeMillis(), 
					end_date, 3, 0, 900, 900, 1061, 100);
			
			sb.append(meter_schedule_buff);
			String on = getMeterScheduleConfBuffer(1,0, System.currentTimeMillis(), 
					end_date, 2, 21600, 60, 39600, 810, 1);
			sb.append(on);
			String off = getMeterScheduleConfBuffer(1,1, System.currentTimeMillis(), 
					end_date, 2, 64800, 60, 39600, 810, 1);
			sb.append(off);
			System.out.println(sb);
		} catch (Exception e) {
			System.out.println("Exception : "+ e.getMessage());
		}
		return sb.toString();
	}


	private static String getMeterScheduleConfBuffer(int action_type, int action_value,  long start_date, 
			long end_date, int frequency,
			int refrance_time,  int pooling_interval,	int validity_time, int opcode, int node_ids
			) {
		
		StringBuffer sb = new StringBuffer();
		String sd_hex = Long.toHexString(start_date/1000);
		String ed_hex = Long.toHexString(end_date/1000);
		try {
			sb
			.append(StringUtils.leftPad("" +  Integer.toHexString(1), 8, "0"))	// scheduler id
			.append(StringUtils.leftPad("" +  Integer.toHexString(action_type), 2, "0")) // action type 3 fixed for meter
			.append(StringUtils.leftPad("" +  Integer.toHexString(action_value), 4, "0"))	
			.append(StringUtils.leftPad("" +  sd_hex, 8, "0"))	
			.append(StringUtils.leftPad("" +  ed_hex, 8, "0"))	
			.append(StringUtils.leftPad("" +  Integer.toHexString(frequency), 2, "0"))	// frequency
			.append(StringUtils.leftPad("" +  Integer.toHexString(1), 2, "0")) // priorty 
			.append(StringUtils.leftPad("" +  Integer.toHexString(refrance_time), 8, "0"))
			.append(StringUtils.leftPad("" +  Integer.toHexString(pooling_interval), 4, "0"))
				
			.append(StringUtils.leftPad("" +  Integer.toHexString(validity_time), 4, "0"))	
			.append(StringUtils.leftPad("" +  Integer.toHexString(opcode), 4, "0"))	
			
			.append(StringUtils.leftPad("" +  Integer.toHexString(32767), 4, "0"))	// sun set
			.append(StringUtils.leftPad("" +  Integer.toHexString(32767), 4, "0"))	// sun raise
			
			.append(StringUtils.leftPad("" +  Integer.toHexString(0), 2, "0")) // group count
			.append(StringUtils.leftPad("" +  Integer.toHexString(0), 40, "0"))// group id
			.append(StringUtils.leftPad("" +  Integer.toHexString(1), 2, "0")) // node count
			.append(StringUtils.leftPad("" +  Integer.toHexString(node_ids), 8, "0"))
			;
		} catch (Exception e) {
			System.out.println("Exception : "+ e.getMessage());
		}
		return sb.toString();
	}
	
}
