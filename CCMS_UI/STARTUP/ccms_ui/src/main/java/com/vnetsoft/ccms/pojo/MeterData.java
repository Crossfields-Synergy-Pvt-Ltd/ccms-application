package com.vnetsoft.ccms.pojo;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "meter_data")
public class MeterData {

	@Id
	private long id;
	
	private String dcu_id;

	private int slave_id;
	
	private int function_code;

	private long time_stamp;
	private long node_id;
	private String frequency;
	private String r_phase_voltage;
	private String y_phase_voltage;
	private String b_phase_voltage;
	private String equ_current_3_phase;
	private String current_line_1;
	private String current_line_2;
	private String current_line_3;
	private String equ_power_3_phase;
	private String pf_1;
	private String pf_2;
	private String pf_3;
	private String active_power;
	private String apparent_power;
	private String reactive_power;
	private String kwh_total;
	private String earth_tamper;
	private String reverse_tamper;
	private String cover_open_tamper;
	private String magnetic_tamper;
	
	private String utc_date;
	
	private int yymmdd;
	
	
	public String getUtc_date() {
		return utc_date;
	}

	public void setUtc_date(String utc_date) {
		this.utc_date = utc_date;
	}

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

	public String getDcu_id() {
		return dcu_id;
	}

	public void setDcu_id(String dcu_id) {
		this.dcu_id = dcu_id;
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

	public String getY_phase_voltage() {
		return y_phase_voltage;
	}

	public void setY_phase_voltage(String y_phase_voltage) {
		this.y_phase_voltage = y_phase_voltage;
	}

	public String getB_phase_voltage() {
		return b_phase_voltage;
	}

	public void setB_phase_voltage(String b_phase_voltage) {
		this.b_phase_voltage = b_phase_voltage;
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

	public String getCurrent_line_2() {
		return current_line_2;
	}

	public void setCurrent_line_2(String current_line_2) {
		this.current_line_2 = current_line_2;
	}

	public String getCurrent_line_3() {
		return current_line_3;
	}

	public void setCurrent_line_3(String current_line_3) {
		this.current_line_3 = current_line_3;
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

	public String getPf_2() {
		return pf_2;
	}

	public void setPf_2(String pf_2) {
		this.pf_2 = pf_2;
	}

	public String getPf_3() {
		return pf_3;
	}

	public void setPf_3(String pf_3) {
		this.pf_3 = pf_3;
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
	

	@Override
	public String toString() {
		return "MeterData [dcu_id=" + dcu_id + ", slave_id=" + slave_id
				+ ", function_code=" + function_code + ", time_stamp="
				+ time_stamp + ", node_id=" + node_id + ", frequency="
				+ frequency + ", r_phase_voltage=" + r_phase_voltage
				+ ", y_phase_voltage=" + y_phase_voltage + ", b_phase_voltage="
				+ b_phase_voltage + ", equ_current_3_phase="
				+ equ_current_3_phase + ", current_line_1=" + current_line_1
				+ ", current_line_2=" + current_line_2 + ", current_line_3="
				+ current_line_3 + ", equ_power_3_phase=" + equ_power_3_phase
				+ ", pf_1=" + pf_1 + ", pf_2=" + pf_2 + ", pf_3=" + pf_3
				+ ", active_power=" + active_power + ", apparent_power="
				+ apparent_power + ", reactive_power=" + reactive_power
				+ ", kwh_total=" + kwh_total + ", earth_tamper=" + earth_tamper
				+ ", reverse_tamper=" + reverse_tamper + ", cover_open_tamper="
				+ cover_open_tamper + ", magnetic_tamper=" + magnetic_tamper
				+ ", current_md=" + "]";
	}

	

	

}
