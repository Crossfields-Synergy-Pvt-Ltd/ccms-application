package com.vnetsoft.ccms.pojo;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "dcu_system_conf_details")
public class DCUConfiguration {

	@Id
	private String dcu_id; // serial number
	
	private String overwrite_flash;
	private String retry_count;
	private String retry_interval;
	private String apn_name;
	private String apn_user_name;
	private String apn_password;
	private String frequency_band;
	private String meter_polling_interval;
	private String Primary_server_ip;
	private String primary_server_port;
	private String primary_dns;
	private String secondary_server_ip;
	private String secondary_server_port;
	private String secondary_dns;
	private int pan_id;
	private String rf_frequency;
	private String tx_power;
	private String rf_idle_timeout;
	private String rf_retry_count;
	private String mod_value;
	private String stop_bit_1;
	private String baud_rate_1;
	private String Parity_1;
	private String data_bits_1;
	private String serial_retry_count_1;
	private String serial_retry_interval_1;
	private String stop_bits_2;
	private String baud_rate_2;
	private String parity_2;
	private String data_bits_2;
	private String serial_retry_count_2;
	private String voltage_max;
	private String voltage_min;
	private String current_min;
	private String current_max;
	private String phase_2_voltage_min;
	private String phase_2_voltage_max;
	private String phase_2_current_min;
	private String phase_2_current_max;
	private String phase_3_voltage_min;
	private String phase_3_voltage_max;
	private String phase_3_current_min;
	private String phase_3_current_max;
	private String repetative_alert_timer;
	private String retry_count_for_io_recovery;
	private String max_time_for_io_recovery_after_max_retry_count;
	private String power_off_gsm_off;
	private String power_off_gsm_on;
	private int number_1;
	private int number_2;
	private int number_3;
	private int number_4;
	private int number_5;
	private String file_identifier;

	private String status;
	


	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getFile_identifier() {
		return file_identifier;
	}

	public void setFile_identifier(String file_identifier) {
		this.file_identifier = file_identifier;
	}

	private String serial_retry_interval_2;

	private String retry_intravl_for_io_recovery;
	private String hop_count;
	private String change_interval;

	public String getSerial_retry_interval_2() {
		return serial_retry_interval_2;
	}

	public void setSerial_retry_interval_2(String serial_retry_interval_2) {
		this.serial_retry_interval_2 = serial_retry_interval_2;
	}

	public String getRetry_intravl_for_io_recovery() {
		return retry_intravl_for_io_recovery;
	}

	public void setRetry_intravl_for_io_recovery(
			String retry_intravl_for_io_recovery) {
		this.retry_intravl_for_io_recovery = retry_intravl_for_io_recovery;
	}

	public String getHop_count() {
		return hop_count;
	}

	public void setHop_count(String hop_count) {
		this.hop_count = hop_count;
	}

	public String getChange_interval() {
		return change_interval;
	}

	public void setChange_interval(String change_interval) {
		this.change_interval = change_interval;
	}

	public String getDcu_id() {
		return dcu_id;
	}

	public void setDcu_id(String dcu_id) {
		this.dcu_id = dcu_id;
	}

	public String getOverwrite_flash() {
		return overwrite_flash;
	}

	public void setOverwrite_flash(String overwrite_flash) {
		this.overwrite_flash = overwrite_flash;
	}

	public String getRetry_count() {
		return retry_count;
	}

	public void setRetry_count(String retry_count) {
		this.retry_count = retry_count;
	}

	public String getRetry_interval() {
		return retry_interval;
	}

	public void setRetry_interval(String retry_interval) {
		this.retry_interval = retry_interval;
	}

	public String getApn_name() {
		return apn_name;
	}

	public void setApn_name(String apn_name) {
		this.apn_name = apn_name;
	}

	public String getApn_user_name() {
		return apn_user_name;
	}

	public void setApn_user_name(String apn_user_name) {
		this.apn_user_name = apn_user_name;
	}

	public String getApn_password() {
		return apn_password;
	}

	public void setApn_password(String apn_password) {
		this.apn_password = apn_password;
	}

	public String getFrequency_band() {
		return frequency_band;
	}

	public void setFrequency_band(String frequency_band) {
		this.frequency_band = frequency_band;
	}

	public String getMeter_polling_interval() {
		return meter_polling_interval;
	}

	public void setMeter_polling_interval(String meter_polling_interval) {
		this.meter_polling_interval = meter_polling_interval;
	}

	public String getPrimary_server_ip() {
		return Primary_server_ip;
	}

	public void setPrimary_server_ip(String primary_server_ip) {
		Primary_server_ip = primary_server_ip;
	}

	public String getPrimary_server_port() {
		return primary_server_port;
	}

	public void setPrimary_server_port(String primary_server_port) {
		this.primary_server_port = primary_server_port;
	}

	public String getPrimary_dns() {
		return primary_dns;
	}

	public void setPrimary_dns(String primary_dns) {
		this.primary_dns = primary_dns;
	}

	public String getSecondary_server_ip() {
		return secondary_server_ip;
	}

	public void setSecondary_server_ip(String secondary_server_ip) {
		this.secondary_server_ip = secondary_server_ip;
	}

	public String getSecondary_server_port() {
		return secondary_server_port;
	}

	public void setSecondary_server_port(String secondary_server_port) {
		this.secondary_server_port = secondary_server_port;
	}

	public String getSecondary_dns() {
		return secondary_dns;
	}

	public void setSecondary_dns(String secondary_dns) {
		this.secondary_dns = secondary_dns;
	}

	public int getPan_id() {
		return pan_id;
	}

	public void setPan_id(int pan_id) {
		this.pan_id = pan_id;
	}

	public String getRf_frequency() {
		return rf_frequency;
	}

	public void setRf_frequency(String rf_frequency) {
		this.rf_frequency = rf_frequency;
	}

	public String getTx_power() {
		return tx_power;
	}

	public void setTx_power(String tx_power) {
		this.tx_power = tx_power;
	}

	public String getRf_idle_timeout() {
		return rf_idle_timeout;
	}

	public void setRf_idle_timeout(String rf_idle_timeout) {
		this.rf_idle_timeout = rf_idle_timeout;
	}

	public String getRf_retry_count() {
		return rf_retry_count;
	}

	public void setRf_retry_count(String rf_retry_count) {
		this.rf_retry_count = rf_retry_count;
	}

	public String getMod_value() {
		return mod_value;
	}

	public void setMod_value(String mod_value) {
		this.mod_value = mod_value;
	}

	public String getStop_bit_1() {
		return stop_bit_1;
	}

	public void setStop_bit_1(String stop_bit_1) {
		this.stop_bit_1 = stop_bit_1;
	}

	public String getBaud_rate_1() {
		return baud_rate_1;
	}

	public void setBaud_rate_1(String baud_rate_1) {
		this.baud_rate_1 = baud_rate_1;
	}

	public String getParity_1() {
		return Parity_1;
	}

	public void setParity_1(String parity_1) {
		Parity_1 = parity_1;
	}

	public String getData_bits_1() {
		return data_bits_1;
	}

	public void setData_bits_1(String data_bits_1) {
		this.data_bits_1 = data_bits_1;
	}

	public String getSerial_retry_count_1() {
		return serial_retry_count_1;
	}

	public void setSerial_retry_count_1(String serial_retry_count_1) {
		this.serial_retry_count_1 = serial_retry_count_1;
	}

	public String getSerial_retry_interval_1() {
		return serial_retry_interval_1;
	}

	public void setSerial_retry_interval_1(String serial_retry_interval_1) {
		this.serial_retry_interval_1 = serial_retry_interval_1;
	}

	public String getStop_bits_2() {
		return stop_bits_2;
	}

	public void setStop_bits_2(String stop_bits_2) {
		this.stop_bits_2 = stop_bits_2;
	}

	public String getBaud_rate_2() {
		return baud_rate_2;
	}

	public void setBaud_rate_2(String baud_rate_2) {
		this.baud_rate_2 = baud_rate_2;
	}

	public String getParity_2() {
		return parity_2;
	}

	public void setParity_2(String parity_2) {
		this.parity_2 = parity_2;
	}

	public String getData_bits_2() {
		return data_bits_2;
	}

	public void setData_bits_2(String data_bits_2) {
		this.data_bits_2 = data_bits_2;
	}

	public String getSerial_retry_count_2() {
		return serial_retry_count_2;
	}

	public void setSerial_retry_count_2(String serial_retry_count_2) {
		this.serial_retry_count_2 = serial_retry_count_2;
	}

	public String getVoltage_max() {
		return voltage_max;
	}

	public void setVoltage_max(String voltage_max) {
		this.voltage_max = voltage_max;
	}

	public String getVoltage_min() {
		return voltage_min;
	}

	public void setVoltage_min(String voltage_min) {
		this.voltage_min = voltage_min;
	}

	public String getCurrent_min() {
		return current_min;
	}

	public void setCurrent_min(String current_min) {
		this.current_min = current_min;
	}

	public String getCurrent_max() {
		return current_max;
	}

	public void setCurrent_max(String current_max) {
		this.current_max = current_max;
	}

	public String getPhase_2_voltage_min() {
		return phase_2_voltage_min;
	}

	public void setPhase_2_voltage_min(String phase_2_voltage_min) {
		this.phase_2_voltage_min = phase_2_voltage_min;
	}

	public String getPhase_2_voltage_max() {
		return phase_2_voltage_max;
	}

	public void setPhase_2_voltage_max(String phase_2_voltage_max) {
		this.phase_2_voltage_max = phase_2_voltage_max;
	}

	public String getPhase_2_current_min() {
		return phase_2_current_min;
	}

	public void setPhase_2_current_min(String phase_2_current_min) {
		this.phase_2_current_min = phase_2_current_min;
	}

	public String getPhase_2_current_max() {
		return phase_2_current_max;
	}

	public void setPhase_2_current_max(String phase_2_current_max) {
		this.phase_2_current_max = phase_2_current_max;
	}

	public String getPhase_3_voltage_min() {
		return phase_3_voltage_min;
	}

	public void setPhase_3_voltage_min(String phase_3_voltage_min) {
		this.phase_3_voltage_min = phase_3_voltage_min;
	}

	public String getPhase_3_voltage_max() {
		return phase_3_voltage_max;
	}

	public void setPhase_3_voltage_max(String phase_3_voltage_max) {
		this.phase_3_voltage_max = phase_3_voltage_max;
	}

	public String getPhase_3_current_min() {
		return phase_3_current_min;
	}

	public void setPhase_3_current_min(String phase_3_current_min) {
		this.phase_3_current_min = phase_3_current_min;
	}

	public String getPhase_3_current_max() {
		return phase_3_current_max;
	}

	public void setPhase_3_current_max(String phase_3_current_max) {
		this.phase_3_current_max = phase_3_current_max;
	}

	public String getRepetative_alert_timer() {
		return repetative_alert_timer;
	}

	public void setRepetative_alert_timer(String repetative_alert_timer) {
		this.repetative_alert_timer = repetative_alert_timer;
	}

	public String getRetry_count_for_io_recovery() {
		return retry_count_for_io_recovery;
	}

	public void setRetry_count_for_io_recovery(
			String retry_count_for_io_recovery) {
		this.retry_count_for_io_recovery = retry_count_for_io_recovery;
	}

	public String getMax_time_for_io_recovery_after_max_retry_count() {
		return max_time_for_io_recovery_after_max_retry_count;
	}

	public void setMax_time_for_io_recovery_after_max_retry_count(
			String max_time_for_io_recovery_after_max_retry_count) {
		this.max_time_for_io_recovery_after_max_retry_count = max_time_for_io_recovery_after_max_retry_count;
	}

	public String getPower_off_gsm_off() {
		return power_off_gsm_off;
	}

	public void setPower_off_gsm_off(String power_off_gsm_off) {
		this.power_off_gsm_off = power_off_gsm_off;
	}

	public String getPower_off_gsm_on() {
		return power_off_gsm_on;
	}

	public void setPower_off_gsm_on(String power_off_gsm_on) {
		this.power_off_gsm_on = power_off_gsm_on;
	}

	public int getNumber_1() {
		return number_1;
	}

	public void setNumber_1(int number_1) {
		this.number_1 = number_1;
	}

	public int getNumber_2() {
		return number_2;
	}

	public void setNumber_2(int number_2) {
		this.number_2 = number_2;
	}

	public int getNumber_3() {
		return number_3;
	}

	public void setNumber_3(int number_3) {
		this.number_3 = number_3;
	}

	public int getNumber_4() {
		return number_4;
	}

	public void setNumber_4(int number_4) {
		this.number_4 = number_4;
	}

	public int getNumber_5() {
		return number_5;
	}

	public void setNumber_5(int number_5) {
		this.number_5 = number_5;
	}

	@Override
	public String toString() {
		return "DCUConfiguration [dcu_id=" + dcu_id + ", overwrite_flash="
				+ overwrite_flash + ", retry_count=" + retry_count
				+ ", retry_interval=" + retry_interval + ", apn_name="
				+ apn_name + ", apn_user_name=" + apn_user_name
				+ ", apn_password=" + apn_password + ", frequency_band="
				+ frequency_band + ", meter_polling_interval="
				+ meter_polling_interval + ", Primary_server_ip="
				+ Primary_server_ip + ", primary_server_port="
				+ primary_server_port + ", primary_dns=" + primary_dns
				+ ", secondary_server_ip=" + secondary_server_ip
				+ ", secondary_server_port=" + secondary_server_port
				+ ", secondary_dns=" + secondary_dns + ", pan_id=" + pan_id
				+ ", rf_frequency=" + rf_frequency + ", tx_power=" + tx_power
				+ ", rf_idle_timeout=" + rf_idle_timeout + ", rf_retry_count="
				+ rf_retry_count + ", mod_value=" + mod_value + ", stop_bit_1="
				+ stop_bit_1 + ", baud_rate_1=" + baud_rate_1 + ", Parity_1="
				+ Parity_1 + ", data_bits_1=" + data_bits_1
				+ ", serial_retry_count_1=" + serial_retry_count_1
				+ ", serial_retry_interval_1=" + serial_retry_interval_1
				+ ", stop_bits_2=" + stop_bits_2 + ", baud_rate_2="
				+ baud_rate_2 + ", parity_2=" + parity_2 + ", data_bits_2="
				+ data_bits_2 + ", serial_retry_count_2="
				+ serial_retry_count_2 + ", voltage_max=" + voltage_max
				+ ", voltage_min=" + voltage_min + ", current_min="
				+ current_min + ", current_max=" + current_max
				+ ", phase_2_voltage_min=" + phase_2_voltage_min
				+ ", phase_2_voltage_max=" + phase_2_voltage_max
				+ ", phase_2_current_min=" + phase_2_current_min
				+ ", phase_2_current_max=" + phase_2_current_max
				+ ", phase_3_voltage_min=" + phase_3_voltage_min
				+ ", phase_3_voltage_max=" + phase_3_voltage_max
				+ ", phase_3_current_min=" + phase_3_current_min
				+ ", phase_3_current_max=" + phase_3_current_max
				+ ", repetative_alert_timer=" + repetative_alert_timer
				+ ", retry_count_for_io_recovery="
				+ retry_count_for_io_recovery
				+ ", max_time_for_io_recovery_after_max_retry_count="
				+ max_time_for_io_recovery_after_max_retry_count
				+ ", power_off_gsm_off=" + power_off_gsm_off
				+ ", power_off_gsm_on=" + power_off_gsm_on + ", number_1="
				+ number_1 + ", number_2=" + number_2 + ", number_3="
				+ number_3 + ", number_4=" + number_4 + ", number_5="
				+ number_5 + "]";
	}

}
