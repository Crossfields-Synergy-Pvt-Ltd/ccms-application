package com.vetsoft.ccms.netty.pojo;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "dcu_conf_data")
public class DCUSysConfData {
	@Id
	private String device_serial_number;
	
	private String file_identifier;
	
	private String device_identifier;
	
	private long request_sent_time;
	
	private String data;

	private String status;

	private long last_sync_time;

	public String getDevice_serial_number() {
		return device_serial_number;
	}

	public void setDevice_serial_number(String device_serial_number) {
		this.device_serial_number = device_serial_number;
	}

	public String getFile_identifier() {
		return file_identifier;
	}

	public void setFile_identifier(String file_identifier) {
		this.file_identifier = file_identifier;
	}

	public String getDevice_identifier() {
		return device_identifier;
	}

	public void setDevice_identifier(String device_identifier) {
		this.device_identifier = device_identifier;
	}

	public long getRequest_sent_time() {
		return request_sent_time;
	}

	public void setRequest_sent_time(long request_sent_time) {
		this.request_sent_time = request_sent_time;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public long getLast_sync_time() {
		return last_sync_time;
	}

	public void setLast_sync_time(long last_sync_time) {
		this.last_sync_time = last_sync_time;
	}

	@Override
	public String toString() {
		return "NodeConfData [device_serial_number=" + device_serial_number
				+ ", file_identifier=" + file_identifier
				+ ", device_identifier=" + device_identifier
				+ ", request_sent_time=" + request_sent_time + ", data=" + data
				+ ", status=" + status + ", last_sync_time=" + last_sync_time
				+ "]";
	}
	
	
	
}
