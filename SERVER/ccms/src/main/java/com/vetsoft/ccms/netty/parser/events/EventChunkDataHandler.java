package com.vetsoft.ccms.netty.parser.events;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vetsoft.ccms.netty.pojo.ChunkDataHolder;
import com.vetsoft.ccms.netty.pojo.CommonHeader;
import com.vetsoft.ccms.netty.pojo.EventStatusRequest;
import com.vetsoft.ccms.netty.repos.DeviceRequestDataRepository;

public class EventChunkDataHandler {

	private static final Logger LOG = LoggerFactory
			.getLogger(EventChunkDataHandler.class);
	
	public void processEventChunkData(String sTextAsHex, CommonHeader obj_common,
			int index, EventStatusRequest obj, DeviceRequestDataRepository repos_context) {
		
	
		int chunk_len = obj.getChunk_length();
		
		if (LOG.isDebugEnabled()) 
			LOG.debug("Chunk LEn : "+ chunk_len);
		
		if (chunk_len < 256) {
			
			if (LOG.isDebugEnabled()) {
				LOG.debug("END OF EVENT CHUNK DATA REQUEST CHUNK LEN : " + chunk_len);
			}
			ChunkDataHolder event_chunk_data_holder = repos_context
					.getIOChunkDataById(obj.getDevice_serial_number());
			
			String buffer = sTextAsHex.substring(index, (index +  (chunk_len * 2)));

			if (LOG.isDebugEnabled()) {
				LOG.debug("PROCESSING PACKET EVENT CHUNK DATA : "+ event_chunk_data_holder.getChunk_data()+ buffer );
			}
			
			EventRequestProcessor.processAndParseDeviceData(event_chunk_data_holder.getChunk_data()+ buffer, repos_context, obj.getDevice_serial_number());
			
			event_chunk_data_holder.setGateway_serial_number(obj
					.getDevice_serial_number());
			event_chunk_data_holder.setChunk_data("");
			repos_context.saveIOChunkData(event_chunk_data_holder);
			return;
			
		} else { // need to read some more chunks of file in next packet from
			// device, store in DB and parse data when chunk len becomes
			// zero
			
			
			
			String buffer = sTextAsHex.substring(index, (index + (chunk_len * 2)));

			ChunkDataHolder event_chunk_data_holder = repos_context
					.getIOChunkDataById(obj.getDevice_serial_number());
			
			if(event_chunk_data_holder == null || obj.getChunk_offset() == 0 ){ // in case restart if any data is there that will be flused  
				event_chunk_data_holder = new ChunkDataHolder();
				event_chunk_data_holder.setChunk_data("");
			}
			
			
			event_chunk_data_holder.setGateway_serial_number(obj
					.getDevice_serial_number());
			
			event_chunk_data_holder.setChunk_data(event_chunk_data_holder.getChunk_data()  + buffer);

			if (LOG.isDebugEnabled()) {
				LOG.debug("EVENT CHUNK DATA : " + event_chunk_data_holder);
			}
			
			if(buffer != null && buffer.length() > 5)
				repos_context.saveIOChunkData(event_chunk_data_holder);
			
		}
	}
	

}
