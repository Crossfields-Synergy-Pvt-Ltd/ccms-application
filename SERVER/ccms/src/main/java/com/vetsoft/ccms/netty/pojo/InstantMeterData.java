package com.vetsoft.ccms.netty.pojo;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
@Document(collection = "meter_instant_details")
public class InstantMeterData {

	@Id
	private String id;

	private String dcu_serial_number;

	int yymmdd;

	private int slave_id;
	private int function_code;

	private long meter_data_time_stamp;
	private long node_id;
	private String frequency;
	private String r_phase_voltage;
	/*
	 * private String y_phase_voltage; // if 3 phase then only required private
	 * String b_phase_voltage;
	 */
	private String equ_current_3_phase;
	private String current_line_1;
	/*
	 * private String current_line_2;// if 3 phase then only required private
	 * String current_line_3;
	 */
	private String equ_power_3_phase;
	private String pf_1;
	/*
	 * private String pf_2;// if 3 phase then only required private String pf_3;
	 */
	private String active_power;
	private String apparent_power;
	private String reactive_power;
	private String kwh_total;
	private String earth_tamper;
	private String reverse_tamper;
	private String cover_open_tamper;
	private String magnetic_tamper;

	/*
	 * private String current_md; // this info not available in packet but
	 * mentioned in document
	 * 
	 * private String current_date;
	 * 
	 * private String prev_md;
	 * 
	 * private String prev_date;
	 */
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getDcu_serial_number() {
		return dcu_serial_number;
	}

	public void setDcu_serial_number(String dcu_serial_number) {
		this.dcu_serial_number = dcu_serial_number;
	}

	public int getYymmdd() {
		return yymmdd;
	}

	public void setYymmdd(int yymmdd) {
		this.yymmdd = yymmdd;
	}

	public int getSlave_id() {
		return slave_id;
	}

	public void setSlave_id(int slave_id) {
		this.slave_id = slave_id;
	}

	public int getFunction_code() {
		return function_code;
	}

	public void setFunction_code(int function_code) {
		this.function_code = function_code;
	}

	public long getMeter_data_time_stamp() {
		return meter_data_time_stamp;
	}

	public void setMeter_data_time_stamp(long meter_data_time_stamp) {
		this.meter_data_time_stamp = meter_data_time_stamp;
	}

	public long getNode_id() {
		return node_id;
	}

	public void setNode_id(long node_id) {
		this.node_id = node_id;
	}

	public String getFrequency() {
		return frequency;
	}

	public void setFrequency(String frequency) {
		this.frequency = frequency;
	}

	public String getR_phase_voltage() {
		return r_phase_voltage;
	}

	public void setR_phase_voltage(String r_phase_voltage) {
		this.r_phase_voltage = r_phase_voltage;
	}

	public String getEqu_current_3_phase() {
		return equ_current_3_phase;
	}

	public void setEqu_current_3_phase(String equ_current_3_phase) {
		this.equ_current_3_phase = equ_current_3_phase;
	}

	public String getCurrent_line_1() {
		return current_line_1;
	}

	public void setCurrent_line_1(String current_line_1) {
		this.current_line_1 = current_line_1;
	}

	public String getEqu_power_3_phase() {
		return equ_power_3_phase;
	}

	public void setEqu_power_3_phase(String equ_power_3_phase) {
		this.equ_power_3_phase = equ_power_3_phase;
	}

	public String getPf_1() {
		return pf_1;
	}

	public void setPf_1(String pf_1) {
		this.pf_1 = pf_1;
	}

	public String getActive_power() {
		return active_power;
	}

	public void setActive_power(String active_power) {
		this.active_power = active_power;
	}

	public String getApparent_power() {
		return apparent_power;
	}

	public void setApparent_power(String apparent_power) {
		this.apparent_power = apparent_power;
	}

	public String getReactive_power() {
		return reactive_power;
	}

	public void setReactive_power(String reactive_power) {
		this.reactive_power = reactive_power;
	}

	public String getKwh_total() {
		return kwh_total;
	}

	public void setKwh_total(String kwh_total) {
		this.kwh_total = kwh_total;
	}

	public String getEarth_tamper() {
		return earth_tamper;
	}

	public void setEarth_tamper(String earth_tamper) {
		this.earth_tamper = earth_tamper;
	}

	public String getReverse_tamper() {
		return reverse_tamper;
	}

	public void setReverse_tamper(String reverse_tamper) {
		this.reverse_tamper = reverse_tamper;
	}

	public String getCover_open_tamper() {
		return cover_open_tamper;
	}

	public void setCover_open_tamper(String cover_open_tamper) {
		this.cover_open_tamper = cover_open_tamper;
	}

	public String getMagnetic_tamper() {
		return magnetic_tamper;
	}

	public void setMagnetic_tamper(String magnetic_tamper) {
		this.magnetic_tamper = magnetic_tamper;
	}

	@Override
	public String toString() {
		return "InstantMeterData [id=" + id + ", dcu_serial_number="
				+ dcu_serial_number + ", yymmdd=" + yymmdd + ", slave_id="
				+ slave_id + ", function_code=" + function_code
				+ ", meter_data_time_stamp=" + meter_data_time_stamp
				+ ", node_id=" + node_id + ", frequency=" + frequency
				+ ", r_phase_voltage=" + r_phase_voltage
				+ ", equ_current_3_phase=" + equ_current_3_phase
				+ ", current_line_1=" + current_line_1 + ", equ_power_3_phase="
				+ equ_power_3_phase + ", pf_1=" + pf_1 + ", active_power="
				+ active_power + ", apparent_power=" + apparent_power
				+ ", reactive_power=" + reactive_power + ", kwh_total="
				+ kwh_total + ", earth_tamper=" + earth_tamper
				+ ", reverse_tamper=" + reverse_tamper + ", cover_open_tamper="
				+ cover_open_tamper + ", magnetic_tamper=" + magnetic_tamper
				+ "]";
	}

}
