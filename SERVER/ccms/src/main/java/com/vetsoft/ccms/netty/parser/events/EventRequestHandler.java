package com.vetsoft.ccms.netty.parser.events;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vetsoft.ccms.MainBootApp;
import com.vetsoft.ccms.netty.cfg.ALLAlertsNotifications;
import com.vetsoft.ccms.netty.cfg.EventIdInformation;
import com.vetsoft.ccms.netty.parser.BaseUtil;
import com.vetsoft.ccms.netty.parser.hs.HandShakeParser;
import com.vetsoft.ccms.netty.pojo.CommonHeader;
import com.vetsoft.ccms.netty.pojo.Event;
import com.vetsoft.ccms.netty.pojo.EventStatusRequest;
import com.vetsoft.ccms.netty.repos.DeviceRequestDataRepository;

public class EventRequestHandler extends BaseUtil{

	private static final Logger LOG = LoggerFactory.getLogger(HandShakeParser.class);
	
	
	public static EventStatusRequest handleEventRequest(String buffer, EventStatusRequest obj) {
		
		DeviceRequestDataRepository repos_context = MainBootApp.context.getBean(DeviceRequestDataRepository.class);
		
		try {
			
			String sTextAsHex = buffer;
			int index = 28;
			CommonHeader obj_common = BaseUtil.getCommonHeader(sTextAsHex);
		//	System.out.println("Payload length : "+ obj_common.getPayload_length());
		
			int event_id = 0;
			if(obj_common.getFlag() == 1){ // gateway serial number present
				String device_serial_number =  convertHexToString(sTextAsHex.substring(index, (index + 32))); // 16 byte of data
				//System.out.println(device_serial_number);
				obj.setDevice_serial_number(device_serial_number);
				index += 32;
			} 
			
			
			
			int transection_id = Integer.parseInt(sTextAsHex.substring(index, (index + 8)), 16); // Transection id 4 byte
			index +=8;
			obj.setTransaction_id(transection_id);
			
			int file_type = Integer.parseInt(sTextAsHex.substring(index, (index + 2)), 16); // 1 byte
			index += 2;
			obj.setFile_type(file_type);
			
			
			
			if (LOG.isDebugEnabled()) 
				LOG.debug("EVENT DATA REQUEST");
				
				int file_version = Integer.parseInt(
						sTextAsHex.substring(index, (index + 4)), 16); // 2 byte
				index += 4;
				obj.setFile_version(file_version);

				int total_file_size = Integer.parseInt(
						sTextAsHex.substring(index, (index + 8)), 16); // 4 byte
				index += 8;
				obj.setTotal_file_size(total_file_size);

				if (LOG.isDebugEnabled()) 
					LOG.debug("TOTAL FILE SIZE : "+total_file_size + " INDEX : "+ index);
				
				int chunk_offset = Integer.parseInt(
						sTextAsHex.substring(index, (index + 8)), 16); // 4 byte
				index += 8;
				obj.setChunk_offset(chunk_offset);

				int chunk_len = Integer.parseInt(
						sTextAsHex.substring(index, (index + 4)), 16); // 4 byte
				index += 4;
				obj.setChunk_length(chunk_len);

				if(chunk_len != 0) {
					
					if(chunk_len == 12){ // single event data it will be 12 byte so we are considering the same
						//55AA55AA0101000009F70805002D31393033485933503343303032313136000000030F00020000000C00000000000C5CCD1D660001C35B003F000082
						EventsParser.processSingleEventRequest(index, sTextAsHex, obj.getDevice_serial_number(), repos_context);
					} else {
					
						if(total_file_size < 256){
							
							EventRequestProcessor.processAndParseDeviceData(sTextAsHex.substring((index)), repos_context, obj.getDevice_serial_number());
						
						} else { // its Chunk Data, event data is coming in Chunks
							if (LOG.isDebugEnabled()) 
								LOG.debug("PROCESSING AS CHUNK DATA");
							new EventChunkDataHandler().processEventChunkData(sTextAsHex, obj_common, index, obj, repos_context);
							
						}
					}
				} else {
					if (LOG.isDebugEnabled()) 
						LOG.debug("EMPTY REQUEST");
				}
			
			
		}catch(Exception e){
			LOG.error(e.getMessage());
			LOG.error("Exception : "+e.getStackTrace());
		}
		return obj;
		
	}

	public static String getResponseBuffer(String buffer, CommonHeader obj, EventStatusRequest event_status_request) {
		StringBuffer response = new StringBuffer();
		response.append(buffer.substring(0, 12)); // start packet , Protoclo version , Flag
		response.append(BaseUtil.getDeviceSerialNumberFromDecimalToHexWithPadding(obj.getGateway_identifier())); // Device Identifier 
		response.append(buffer.substring(20, 22)); // DSN 
		if(LOG.isDebugEnabled())
			LOG.debug("DSN : "+ buffer.substring(20, 22));
		response.append("00"); // Command identifier
		
		StringBuffer sb = new StringBuffer();
		
		
		int index = 28;
		
		if(obj.getFlag() == 1){ // gateway serial number present
			if(LOG.isDebugEnabled())
				LOG.debug("INCLUDING DEVICE Serial Number : "+buffer.substring(index, (index + 32)) );
			sb.append(buffer.substring(index, (index + 32)));
			index += 32;
		} else {
			if(LOG.isDebugEnabled())
				LOG.debug("SKIPPING DEVICE SERIAL NUMBER ");
		}
		
		sb.append("00"); // response flag need to confirm
		sb.append(buffer.substring(index, (index + 8)));
		index +=8;
		
		sb.append(buffer.substring(index, (index + 2)));
		index +=2;
		
		sb.append(buffer.substring(index, (index + 4)));
		index +=4;
		index += 8; // skip total file size
		sb.append(buffer.substring(index, (index + 8)));
		index +=8;
		sb.append(buffer.substring(index, (index + 4)));
		index +=4;
		
	
		String len =  Integer.toHexString(sb.toString().length()/2);
		
		//System.out.println("LEN : "+ String.format("%04d" ,String.valueOf(len)) + " len "+ len);
		String payload_len = String.format("%0"+ (4 - len.length() )+"d%s",0 ,len);
		//System.out.println("LEN : "+ len + " PAY LEN : "+ payload_len);
		response.append(payload_len);
		response.append(sb.toString());
		
		String resp_buff = response.toString() ;
		if(LOG.isDebugEnabled())
			LOG.debug("BUFFER  " +resp_buff);
		String CRC = Integer.toHexString(calculateCRC(convertHexToString(resp_buff).toCharArray(), convertHexToString(resp_buff).length()));
		if(LOG.isDebugEnabled())
			LOG.debug("CRC : "+ CRC);
	
		return (resp_buff + StringUtils.leftPad("" + CRC, 2, "0"));
	}
	
	
}
