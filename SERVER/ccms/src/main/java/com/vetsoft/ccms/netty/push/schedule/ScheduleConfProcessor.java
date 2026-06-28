package com.vetsoft.ccms.netty.push.schedule;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vetsoft.ccms.netty.parser.BaseUtil;
import com.vetsoft.ccms.netty.pojo.DeviceDownloadInitRequest;
import com.vetsoft.ccms.netty.pojo.DownloadContentRequest;
import com.vetsoft.ccms.netty.pojo.SchedulerConfiguration;

public class ScheduleConfProcessor {

	public static Logger LOG =  LoggerFactory.getLogger(ScheduleConfProcessor.class);
	
	public static String getSchedulerConfZeroByteResponse(
			DownloadContentRequest obj) {


		
		StringBuffer sb = new StringBuffer();
		
		try {
			
			sb.append("55AA55AA")
			.append(obj.protocol_version)
			.append(obj.flag)
			.append(obj.dcu_identifier)
			.append(obj.dsn)
			.append("00")// command identifier
			.append("001C") // pay load len need to update
			.append(obj.duc_serial_no)
			.append("00")
			.append(obj.file_type)
			.append(obj.file_identifier)
			.append("000000AB") // chunk off set
			.append("0000")// 850/2 byte
						;
			String buffer= sb.toString();
			String CRC = Integer.toHexString(BaseUtil.calculateCRC(BaseUtil.convertHexToString(buffer.toString()).toCharArray(), BaseUtil.convertHexToString(buffer.toString()).length()));
			
			sb.append(StringUtils.leftPad("" + CRC, 2, "0"));
		} catch (Exception e) {
			LOG.error("Exception while making buffer : "+e.getMessage());
		}
		return sb.toString();
	
	
	}
	
	public static String getSchedulerConfResponse(
			DownloadContentRequest obj, String conf_buff) {

		/*SchedulerConfiguration tmp_obj = new SchedulerConfiguration();
		conf_buff = getSchedulerConfBuffer(tmp_obj);
		*/
		StringBuffer sb = new StringBuffer();
		int chunk_len = conf_buff.length() / 2 ;
		try {
			
			sb.append("55AA55AA")
			.append(obj.protocol_version)
			.append(obj.flag)
			.append(obj.dcu_identifier)
			.append(obj.dsn)
			.append("00")// command identifier
			//.append("00C7") // pay load len need to update
			.append(StringUtils.leftPad("" + Integer.toHexString(chunk_len + 28), 4, "0"))
			.append(obj.duc_serial_no)
			.append("00")
			.append(obj.file_type)
			.append(obj.file_identifier)
			.append("00000000") // chunk off set
			.append(StringUtils.leftPad("" + Integer.toHexString(chunk_len), 4, "0"))// 850/2 byte
			.append(conf_buff)
			;
			String buffer= sb.toString();
			String CRC = Integer.toHexString(BaseUtil.calculateCRC(BaseUtil.convertHexToString(buffer.toString()).toCharArray(), BaseUtil.convertHexToString(buffer.toString()).length()));
			
			sb.append(StringUtils.leftPad("" + CRC, 2, "0"));
		} catch (Exception e) {
			LOG.error("Exception while making buffer : "+e.getMessage());
		}
		return sb.toString();
	
	}

	public static String getSchedulerConfigurationFileInitResponse(DeviceDownloadInitRequest obj) {
	
	StringBuffer sb = new StringBuffer();

	try {
		
		sb.append("55AA55AA")
		.append(obj.protocol_version)
		.append(obj.flag)
		.append(obj.dcu_identifier)
		.append(obj.dsn)
		.append("00")// command identifier
		.append("001C") // pay load len
		.append(obj.duc_serial_no)
		.append("00")
		.append(obj.file_type)
		.append(obj.file_identifier)
		//.append(obj.file_identifier)
		//.append(StringUtils.leftPad("" + Integer.toHexString(total_file_size), 8, "0"))
		.append(StringUtils.leftPad("" + "AB", 8, "0"))// change as per no of scheduler currently hard coded for 2 action AB
		.append("0200")
		;
		String buffer= sb.toString();
		String CRC = Integer.toHexString(BaseUtil.calculateCRC(BaseUtil.convertHexToString(buffer.toString()).toCharArray(), BaseUtil.convertHexToString(buffer.toString()).length()));
		
		sb.append(StringUtils.leftPad("" + CRC, 2, "0"));
	} catch (Exception e) {
		LOG.error("Exception while making buffer : "+e.getMessage());
	}
	return sb.toString();
}
	
private static String getSchedulerConfBuffer(com.vetsoft.ccms.netty.pojo.SchedulerConfiguration obj) {
		
		StringBuffer sb = new StringBuffer();
		
		long end_date = 1592035785;
		
		try {
			
			String meter_schedule_buff = getMeterScheduleConfBuffer(3,30, System.currentTimeMillis(), 
					end_date, 3, 0, 300, 900, 1073, 100);
			
			sb.append(meter_schedule_buff);
			String on = getMeterScheduleConfBuffer(1,0, System.currentTimeMillis(), 
					end_date, 2, 60, 300, 39600, 810, 1);
			sb.append(on);
			String off = getMeterScheduleConfBuffer(1,1, System.currentTimeMillis(), 
					end_date, 2, 60, 300, 39600, 810, 1);
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
		String ed_hex = Long.toHexString(end_date);
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
