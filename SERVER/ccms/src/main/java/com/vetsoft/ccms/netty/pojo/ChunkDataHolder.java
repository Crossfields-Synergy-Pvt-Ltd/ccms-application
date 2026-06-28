package com.vetsoft.ccms.netty.pojo;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "io_chunk_data")
public class ChunkDataHolder {

	@Id
	private String gateway_serial_number;

	private String chunk_data;

	public String getGateway_serial_number() {
		return gateway_serial_number;
	}

	public void setGateway_serial_number(String gateway_serial_number) {
		this.gateway_serial_number = gateway_serial_number;
	}

	public String getChunk_data() {
		return chunk_data;
	}

	public void setChunk_data(String chunk_data) {
		this.chunk_data = chunk_data;
	}

	@Override
	public String toString() {
		return "EventChunkDataHolder [gateway_serial_number="
				+ gateway_serial_number + ", chunk_data=" + chunk_data + "]";
	}

}
