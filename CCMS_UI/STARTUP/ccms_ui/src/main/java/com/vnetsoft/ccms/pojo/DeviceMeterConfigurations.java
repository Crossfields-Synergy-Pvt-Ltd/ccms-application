package com.vnetsoft.ccms.pojo;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
@Document(collection = "meter_configurations")
public class DeviceMeterConfigurations {

	@Id
	private String gateway_serial_number;
	
	private String date;
	private String name;
	private String serial_number;
	private String installation_date;
	private String type;
	private String rf_id;
	private String slave_id;
	private String sub_type;
	private String red;
	private String yellow;
	private String blue;
	private String initial_reading;
	private String multiplication_factor;
	private String addition_factor;
	private String reset_value;
	private String description;
	private String longitude;
	private String latitude;
	
	
	
	public String getRed() {
		return red;
	}
	public void setRed(String red) {
		this.red = red;
	}
	public String getYellow() {
		return yellow;
	}
	public void setYellow(String yellow) {
		this.yellow = yellow;
	}
	public String getBlue() {
		return blue;
	}
	public void setBlue(String blue) {
		this.blue = blue;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	
	public String getGateway_serial_number() {
		return gateway_serial_number;
	}
	public void setGateway_serial_number(String gateway_serial_number) {
		this.gateway_serial_number = gateway_serial_number;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getSerial_number() {
		return serial_number;
	}
	public void setSerial_number(String serial_number) {
		this.serial_number = serial_number;
	}
	public String getInstallation_date() {
		return installation_date;
	}
	public void setInstallation_date(String installation_date) {
		this.installation_date = installation_date;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getRf_id() {
		return rf_id;
	}
	public void setRf_id(String rf_id) {
		this.rf_id = rf_id;
	}
	public String getSlave_id() {
		return slave_id;
	}
	public void setSlave_id(String slave_id) {
		this.slave_id = slave_id;
	}
	public String getSub_type() {
		return sub_type;
	}
	public void setSub_type(String sub_type) {
		this.sub_type = sub_type;
	}

	public String getInitial_reading() {
		return initial_reading;
	}
	public void setInitial_reading(String initial_reading) {
		this.initial_reading = initial_reading;
	}
	public String getMultiplication_factor() {
		return multiplication_factor;
	}
	public void setMultiplication_factor(String multiplication_factor) {
		this.multiplication_factor = multiplication_factor;
	}
	public String getAddition_factor() {
		return addition_factor;
	}
	public void setAddition_factor(String addition_factor) {
		this.addition_factor = addition_factor;
	}
	public String getReset_value() {
		return reset_value;
	}
	public void setReset_value(String reset_value) {
		this.reset_value = reset_value;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getLongitude() {
		return longitude;
	}
	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}
	public String getLatitude() {
		return latitude;
	}
	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}
	@Override
	public String toString() {
		return "DeviceMeterConfigurations [gateway_serial_number="
				+ gateway_serial_number + ", date=" + date + ", name=" + name
				+ ", serial_number=" + serial_number + ", installation_date="
				+ installation_date + ", type=" + type + ", rf_id=" + rf_id
				+ ", slave_id=" + slave_id + ", sub_type=" + sub_type
				+ ", red=" + red + ", yellow=" + yellow + ", blue=" + blue
				+ ", initial_reading=" + initial_reading
				+ ", multiplication_factor=" + multiplication_factor
				+ ", addition_factor=" + addition_factor + ", reset_value="
				+ reset_value + ", description=" + description + ", longitude="
				+ longitude + ", latitude=" + latitude + "]";
	}
	
}
