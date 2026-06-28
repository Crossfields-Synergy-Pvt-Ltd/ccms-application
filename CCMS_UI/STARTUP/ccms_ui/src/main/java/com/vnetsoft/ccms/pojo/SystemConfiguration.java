package com.vnetsoft.ccms.pojo;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "system_conf_details")
public class SystemConfiguration {/*

	@Id
	private String gateway_identifier;
	
	private int overwrite_data_on_storage_full;
	private int server_communication_retry_count;
	private int server_communication_retry_interval;
	private String gprs_frequency_band;
	private String gprs_apn_name;
	private String gprs_apn_user_name;
	private String gprs_apn_password;
	private String server_primary_url_ip;
	private String server_secondary_url_ip;
	private int server_port_number;
	private int server_secondary_port;
	private String primary_dns;
	private String secondary_dns;
	private int uart_1_stop_bits;
	private int uart_1_baud_rate;
	private int uart_1_parity;
	private int uart_1_data_bits;
	private int uart_1_retry_count;
	private int uart_1_retry_interval;
	private int uart_2_stop_bits;
	private int uart_2_baud_rate;
	private int uart_2_parity;
	private int uart_2_data_bits;
	private int uart_2_retry_count;
	private int uart_2_retry_interval;
	
	public String getGateway_identifier() {
		return gateway_identifier;
	}
	public void setGateway_identifier(String gateway_identifier) {
		this.gateway_identifier = gateway_identifier;
	}
	public int getOverwrite_data_on_storage_full() {
		return overwrite_data_on_storage_full;
	}
	public void setOverwrite_data_on_storage_full(int overwrite_data_on_storage_full) {
		this.overwrite_data_on_storage_full = overwrite_data_on_storage_full;
	}
	public int getServer_communication_retry_count() {
		return server_communication_retry_count;
	}
	public void setServer_communication_retry_count(
			int server_communication_retry_count) {
		this.server_communication_retry_count = server_communication_retry_count;
	}
	public int getServer_communication_retry_interval() {
		return server_communication_retry_interval;
	}
	public void setServer_communication_retry_interval(
			int server_communication_retry_interval) {
		this.server_communication_retry_interval = server_communication_retry_interval;
	}
	public String getGprs_frequency_band() {
		return gprs_frequency_band;
	}
	public void setGprs_frequency_band(String gprs_frequency_band) {
		this.gprs_frequency_band = gprs_frequency_band;
	}
	public String getGprs_apn_name() {
		return gprs_apn_name;
	}
	public void setGprs_apn_name(String gprs_apn_name) {
		this.gprs_apn_name = gprs_apn_name;
	}
	public String getGprs_apn_user_name() {
		return gprs_apn_user_name;
	}
	public void setGprs_apn_user_name(String gprs_apn_user_name) {
		this.gprs_apn_user_name = gprs_apn_user_name;
	}
	public String getGprs_apn_password() {
		return gprs_apn_password;
	}
	public void setGprs_apn_password(String gprs_apn_password) {
		this.gprs_apn_password = gprs_apn_password;
	}
	public String getServer_primary_url_ip() {
		return server_primary_url_ip;
	}
	public void setServer_primary_url_ip(String server_primary_url_ip) {
		this.server_primary_url_ip = server_primary_url_ip;
	}
	public String getServer_secondary_url_ip() {
		return server_secondary_url_ip;
	}
	public void setServer_secondary_url_ip(String server_secondary_url_ip) {
		this.server_secondary_url_ip = server_secondary_url_ip;
	}
	public int getServer_port_number() {
		return server_port_number;
	}
	public void setServer_port_number(int server_port_number) {
		this.server_port_number = server_port_number;
	}
	public int getServer_secondary_port() {
		return server_secondary_port;
	}
	public void setServer_secondary_port(int server_secondary_port) {
		this.server_secondary_port = server_secondary_port;
	}
	public String getPrimary_dns() {
		return primary_dns;
	}
	public void setPrimary_dns(String primary_dns) {
		this.primary_dns = primary_dns;
	}
	public String getSecondary_dns() {
		return secondary_dns;
	}
	public void setSecondary_dns(String secondary_dns) {
		this.secondary_dns = secondary_dns;
	}
	public int getUart_1_stop_bits() {
		return uart_1_stop_bits;
	}
	public void setUart_1_stop_bits(int uart_1_stop_bits) {
		this.uart_1_stop_bits = uart_1_stop_bits;
	}
	public int getUart_1_baud_rate() {
		return uart_1_baud_rate;
	}
	public void setUart_1_baud_rate(int uart_1_baud_rate) {
		this.uart_1_baud_rate = uart_1_baud_rate;
	}
	public int getUart_1_parity() {
		return uart_1_parity;
	}
	public void setUart_1_parity(int uart_1_parity) {
		this.uart_1_parity = uart_1_parity;
	}
	public int getUart_1_data_bits() {
		return uart_1_data_bits;
	}
	public void setUart_1_data_bits(int uart_1_data_bits) {
		this.uart_1_data_bits = uart_1_data_bits;
	}
	public int getUart_1_retry_count() {
		return uart_1_retry_count;
	}
	public void setUart_1_retry_count(int uart_1_retry_count) {
		this.uart_1_retry_count = uart_1_retry_count;
	}
	public int getUart_1_retry_interval() {
		return uart_1_retry_interval;
	}
	public void setUart_1_retry_interval(int uart_1_retry_interval) {
		this.uart_1_retry_interval = uart_1_retry_interval;
	}
	public int getUart_2_stop_bits() {
		return uart_2_stop_bits;
	}
	public void setUart_2_stop_bits(int uart_2_stop_bits) {
		this.uart_2_stop_bits = uart_2_stop_bits;
	}
	public int getUart_2_baud_rate() {
		return uart_2_baud_rate;
	}
	public void setUart_2_baud_rate(int uart_2_baud_rate) {
		this.uart_2_baud_rate = uart_2_baud_rate;
	}
	public int getUart_2_parity() {
		return uart_2_parity;
	}
	public void setUart_2_parity(int uart_2_parity) {
		this.uart_2_parity = uart_2_parity;
	}
	public int getUart_2_data_bits() {
		return uart_2_data_bits;
	}
	public void setUart_2_data_bits(int uart_2_data_bits) {
		this.uart_2_data_bits = uart_2_data_bits;
	}
	public int getUart_2_retry_count() {
		return uart_2_retry_count;
	}
	public void setUart_2_retry_count(int uart_2_retry_count) {
		this.uart_2_retry_count = uart_2_retry_count;
	}
	public int getUart_2_retry_interval() {
		return uart_2_retry_interval;
	}
	public void setUart_2_retry_interval(int uart_2_retry_interval) {
		this.uart_2_retry_interval = uart_2_retry_interval;
	}
	@Override
	public String toString() {
		return "SystemConfiguration [gateway_identifier=" + gateway_identifier
				+ ", overwrite_data_on_storage_full="
				+ overwrite_data_on_storage_full
				+ ", server_communication_retry_count="
				+ server_communication_retry_count
				+ ", server_communication_retry_interval="
				+ server_communication_retry_interval
				+ ", gprs_frequency_band=" + gprs_frequency_band
				+ ", gprs_apn_name=" + gprs_apn_name + ", gprs_apn_user_name="
				+ gprs_apn_user_name + ", gprs_apn_password="
				+ gprs_apn_password + ", server_primary_url_ip="
				+ server_primary_url_ip + ", server_secondary_url_ip="
				+ server_secondary_url_ip + ", server_port_number="
				+ server_port_number + ", server_secondary_port="
				+ server_secondary_port + ", primary_dns=" + primary_dns
				+ ", secondary_dns=" + secondary_dns + ", uart_1_stop_bits="
				+ uart_1_stop_bits + ", uart_1_baud_rate=" + uart_1_baud_rate
				+ ", uart_1_parity=" + uart_1_parity + ", uart_1_data_bits="
				+ uart_1_data_bits + ", uart_1_retry_count="
				+ uart_1_retry_count + ", uart_1_retry_interval="
				+ uart_1_retry_interval + ", uart_2_stop_bits="
				+ uart_2_stop_bits + ", uart_2_baud_rate=" + uart_2_baud_rate
				+ ", uart_2_parity=" + uart_2_parity + ", uart_2_data_bits="
				+ uart_2_data_bits + ", uart_2_retry_count="
				+ uart_2_retry_count + ", uart_2_retry_interval="
				+ uart_2_retry_interval + "]";
	}

*/}
