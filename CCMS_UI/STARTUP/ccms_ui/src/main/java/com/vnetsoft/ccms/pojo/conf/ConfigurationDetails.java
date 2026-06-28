package com.vnetsoft.ccms.pojo.conf;

import org.springframework.data.annotation.Id;

public class ConfigurationDetails {


	private String device_serial_number;
	
	private String conf_type; // SYS_CONF, NODE_CONF, SCHEDULER_CONF

	private long request_sent_time;
	
	private String status;

	private long last_sync_time;

	public String getDevice_serial_number() {
		return device_serial_number;
	}

	public void setDevice_serial_number(String device_serial_number) {
		this.device_serial_number = device_serial_number;
	}

	public String getConf_type() {
		return conf_type;
	}

	public void setConf_type(String conf_type) {
		this.conf_type = conf_type;
	}

	public long getRequest_sent_time() {
		return request_sent_time;
	}

	public void setRequest_sent_time(long request_sent_time) {
		this.request_sent_time = request_sent_time;
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

	
}
