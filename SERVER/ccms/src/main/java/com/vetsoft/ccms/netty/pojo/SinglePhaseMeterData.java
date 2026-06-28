package com.vetsoft.ccms.netty.pojo;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "single_phase_meter_data")
public class SinglePhaseMeterData {

	@Id
	private long id;
	
	private String dcu_id;
	private long time_stamp;
	private long node_id;
	private int yymmdd;
	
	private String meter_serial_number;
	private String rtc_date;
	private String rtc_time;
	private String instantaneous_voltage;
	private String instantaneous_current;
	private String instantaneous_power;
	private String instantaneous_power_factor;
	private String cumulative_active_energy;

	private String ct_reverse_tamper_status;
	private String earth_load_tamper_status;
	private String cover_open_tamper_status;
	private String magnetic_influence_tamper_status;
	private String single_wire_tamper_status;
	
	
	public long getTime_stamp() {
		return time_stamp;
	}
	public void setTime_stamp(long time_stamp) {
		this.time_stamp = time_stamp;
	}
	public long getNode_id() {
		return node_id;
	}
	public void setNode_id(long node_id) {
		this.node_id = node_id;
	}
	public int getYymmdd() {
		return yymmdd;
	}
	public void setYymmdd(int yymmdd) {
		this.yymmdd = yymmdd;
	}
	public String getDcu_id() {
		return dcu_id;
	}
	public void setDcu_id(String dcu_id) {
		this.dcu_id = dcu_id;
	}
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getMeter_serial_number() {
		return meter_serial_number;
	}
	public void setMeter_serial_number(String meter_serial_number) {
		this.meter_serial_number = meter_serial_number;
	}
	public String getRtc_date() {
		return rtc_date;
	}
	public void setRtc_date(String rtc_date) {
		this.rtc_date = rtc_date;
	}
	public String getRtc_time() {
		return rtc_time;
	}
	public void setRtc_time(String rtc_time) {
		this.rtc_time = rtc_time;
	}
	public String getInstantaneous_voltage() {
		return instantaneous_voltage;
	}
	public void setInstantaneous_voltage(String instantaneous_voltage) {
		this.instantaneous_voltage = instantaneous_voltage;
	}
	public String getInstantaneous_current() {
		return instantaneous_current;
	}
	public void setInstantaneous_current(String instantaneous_current) {
		this.instantaneous_current = instantaneous_current;
	}
	public String getInstantaneous_power() {
		return instantaneous_power;
	}
	public void setInstantaneous_power(String instantaneous_power) {
		this.instantaneous_power = instantaneous_power;
	}
	public String getInstantaneous_power_factor() {
		return instantaneous_power_factor;
	}
	public void setInstantaneous_power_factor(String instantaneous_power_factor) {
		this.instantaneous_power_factor = instantaneous_power_factor;
	}
	public String getCumulative_active_energy() {
		return cumulative_active_energy;
	}
	public void setCumulative_active_energy(String cumulative_active_energy) {
		this.cumulative_active_energy = cumulative_active_energy;
	}
	public String getCt_reverse_tamper_status() {
		return ct_reverse_tamper_status;
	}
	public void setCt_reverse_tamper_status(String ct_reverse_tamper_status) {
		this.ct_reverse_tamper_status = ct_reverse_tamper_status;
	}
	public String getEarth_load_tamper_status() {
		return earth_load_tamper_status;
	}
	public void setEarth_load_tamper_status(String earth_load_tamper_status) {
		this.earth_load_tamper_status = earth_load_tamper_status;
	}
	public String getCover_open_tamper_status() {
		return cover_open_tamper_status;
	}
	public void setCover_open_tamper_status(String cover_open_tamper_status) {
		this.cover_open_tamper_status = cover_open_tamper_status;
	}
	public String getMagnetic_influence_tamper_status() {
		return magnetic_influence_tamper_status;
	}
	public void setMagnetic_influence_tamper_status(
			String magnetic_influence_tamper_status) {
		this.magnetic_influence_tamper_status = magnetic_influence_tamper_status;
	}
	public String getSingle_wire_tamper_status() {
		return single_wire_tamper_status;
	}
	public void setSingle_wire_tamper_status(String single_wire_tamper_status) {
		this.single_wire_tamper_status = single_wire_tamper_status;
	}
	@Override
	public String toString() {
		return "SinglePhaseMeterData [id=" + id + ", meter_serial_number="
				+ meter_serial_number + ", rtc_date=" + rtc_date
				+ ", rtc_time=" + rtc_time + ", instantaneous_voltage="
				+ instantaneous_voltage + ", instantaneous_current="
				+ instantaneous_current + ", instantaneous_power="
				+ instantaneous_power + ", instantaneous_power_factor="
				+ instantaneous_power_factor + ", cumulative_active_energy="
				+ cumulative_active_energy + ", ct_reverse_tamper_status="
				+ ct_reverse_tamper_status + ", earth_load_tamper_status="
				+ earth_load_tamper_status + ", cover_open_tamper_status="
				+ cover_open_tamper_status
				+ ", magnetic_influence_tamper_status="
				+ magnetic_influence_tamper_status
				+ ", single_wire_tamper_status=" + single_wire_tamper_status
				+ "]";
	}

}
