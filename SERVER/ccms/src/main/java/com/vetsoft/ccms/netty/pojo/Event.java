package com.vetsoft.ccms.netty.pojo;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "event_details")
public class Event {

	@Id
	private long id;
	
	private String gateway_serial_number;
	
	private int time_stamp;
	
	private long node_identifier;
	
	private int event_id;
	
	private int event_status;

	private int yymmdd;
	
	
	public int getYymmdd() {
		return yymmdd;
	}

	public void setYymmdd(int yymmdd) {
		this.yymmdd = yymmdd;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getGateway_serial_number() {
		return gateway_serial_number;
	}

	public void setGateway_serial_number(String gateway_serial_number) {
		this.gateway_serial_number = gateway_serial_number;
	}


	public int getEvent_id() {
		return event_id;
	}

	public void setEvent_id(int event_id) {
		this.event_id = event_id;
	}

	

	
	public int getEvent_status() {
		return event_status;
	}

	public void setEvent_status(int event_status) {
		this.event_status = event_status;
	}

	public int getTime_stamp() {
		return time_stamp;
	}

	public void setTime_stamp(int time_stamp) {
		this.time_stamp = time_stamp;
	}

	public long getNode_identifier() {
		return node_identifier;
	}

	public void setNode_identifier(long node_identifier) {
		this.node_identifier = node_identifier;
	}

	@Override
	public String toString() {
		return "Event [id=" + id + ", gateway_serial_number="
				+ gateway_serial_number + ", time_stamp=" + time_stamp
				+ ", node_identifier=" + node_identifier + ", event_id="
				+ event_id  + "]";
	}

	
}
