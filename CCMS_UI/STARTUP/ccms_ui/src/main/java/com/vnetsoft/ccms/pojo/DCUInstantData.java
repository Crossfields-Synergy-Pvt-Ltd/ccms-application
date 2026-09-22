package com.vnetsoft.ccms.pojo;

import org.springframework.data.annotation.Id;

import com.vnetsoft.ccms.pojo.server.InstantMeterData;

public class DCUInstantData {

	@Id
	private String id;
	private String device_name;

	private String last_communication_time;
	
	InstantMeterData meter_data;
	/*
	List<InstantEventData> event_instant_list = new ArrayList<InstantEventData>();
	*/
	HandShake dcu_details;
	DCUConfiguration dcu_configurations;
	SchedulerConfiguration schedule_configuration;
	public DCUInstantData() {
	
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public InstantMeterData getMeter_data() {
		return meter_data;
	}

	public void setMeter_data(InstantMeterData meter_data) {
		this.meter_data = meter_data;
	}

	


	public HandShake getDcu_details() {
		return dcu_details;
	}

	public void setDcu_details(HandShake dcu_details) {
		this.dcu_details = dcu_details;
	}

	public DCUConfiguration getDcu_configurations() {
		return dcu_configurations;
	}

	public void setDcu_configurations(DCUConfiguration dcu_configurations) {
		this.dcu_configurations = dcu_configurations;
	}

	public SchedulerConfiguration getSchedule_configuration() {
		return schedule_configuration;
	}

	public void setSchedule_configuration(SchedulerConfiguration schedule_configuration) {
		this.schedule_configuration = schedule_configuration;
	}

	
	public String getDevice_name() {
		return device_name;
	}

	public void setDevice_name(String device_name) {
		this.device_name = device_name;
	}

	public String getLast_communication_time() {
		return last_communication_time;
	}

	public void setLast_communication_time(String last_communication_time) {
		this.last_communication_time = last_communication_time;
	}

}
