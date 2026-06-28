package com.vetsoft.ccms.netty.pojo;

public class EventStatusRequest {

	private int protocol_version;
	private int flag;
	private int gateway_identifier;
	private int dsn;
	private int command_identifire;
	private int payload_length;
	
	private String device_serial_number;
	
	private int transaction_id;
	private int file_type;
	private int file_version;
	private int total_file_size;
	private int chunk_offset;
	private int chunk_length;
	private int timestamp; //   need to change to long
	private long  node_identifier;
	private int event_identifier;
	private int event_data_length;
	private int status;
	private int operation_type;
	private int operation_value;
	private int reason;
	private int crc;
	
	public int getProtocol_version() {
		return protocol_version;
	}
	public void setProtocol_version(int protocol_version) {
		this.protocol_version = protocol_version;
	}
	public int getFlag() {
		return flag;
	}
	public void setFlag(int flag) {
		this.flag = flag;
	}
	public int getGateway_identifier() {
		return gateway_identifier;
	}
	public void setGateway_identifier(int gateway_identifier) {
		this.gateway_identifier = gateway_identifier;
	}
	public int getDsn() {
		return dsn;
	}
	public void setDsn(int dsn) {
		this.dsn = dsn;
	}
	public int getCommand_identifire() {
		return command_identifire;
	}
	public void setCommand_identifire(int command_identifire) {
		this.command_identifire = command_identifire;
	}
	public int getPayload_length() {
		return payload_length;
	}
	public void setPayload_length(int payload_length) {
		this.payload_length = payload_length;
	}
	public int getTransaction_id() {
		return transaction_id;
	}
	public void setTransaction_id(int transaction_id) {
		this.transaction_id = transaction_id;
	}
	public int getFile_type() {
		return file_type;
	}
	public void setFile_type(int file_type) {
		this.file_type = file_type;
	}
	public int getFile_version() {
		return file_version;
	}
	public void setFile_version(int file_version) {
		this.file_version = file_version;
	}
	public int getTotal_file_size() {
		return total_file_size;
	}
	public void setTotal_file_size(int total_file_size) {
		this.total_file_size = total_file_size;
	}
	public int getChunk_offset() {
		return chunk_offset;
	}
	public void setChunk_offset(int chunk_offset) {
		this.chunk_offset = chunk_offset;
	}
	public int getChunk_length() {
		return chunk_length;
	}
	public void setChunk_length(int chunk_length) {
		this.chunk_length = chunk_length;
	}
	public int getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(int timestamp) {
		this.timestamp = timestamp;
	}
	public long getNode_identifier() {
		return node_identifier;
	}
	public void setNode_identifier(long node_identifier) {
		this.node_identifier = node_identifier;
	}
	public int getEvent_identifier() {
		return event_identifier;
	}
	public void setEvent_identifier(int event_identifier) {
		this.event_identifier = event_identifier;
	}
	public int getEvent_data_length() {
		return event_data_length;
	}
	public void setEvent_data_length(int event_data_length) {
		this.event_data_length = event_data_length;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public int getOperation_type() {
		return operation_type;
	}
	public void setOperation_type(int operation_type) {
		this.operation_type = operation_type;
	}
	public int getOperation_value() {
		return operation_value;
	}
	public void setOperation_value(int operation_value) {
		this.operation_value = operation_value;
	}
	public int getReason() {
		return reason;
	}
	public void setReason(int reason) {
		this.reason = reason;
	}
	public int getCrc() {
		return crc;
	}
	public void setCrc(int crc) {
		this.crc = crc;
	}
	public String getDevice_serial_number() {
		return device_serial_number;
	}
	public void setDevice_serial_number(String device_serial_number) {
		this.device_serial_number = device_serial_number;
	}
	@Override
	public String toString() {
		return "EventStatusRequest [protocol_version=" + protocol_version
				+ ", flag=" + flag + ", gateway_identifier="
				+ gateway_identifier + ", dsn=" + dsn + ", command_identifire="
				+ command_identifire + ", payload_length=" + payload_length
				+ ", device_serial_number=" + device_serial_number
				+ ", transaction_id=" + transaction_id + ", file_type="
				+ file_type + ", file_version=" + file_version
				+ ", total_file_size=" + total_file_size + ", chunk_offset="
				+ chunk_offset + ", chunk_length=" + chunk_length
				+ ", timestamp=" + timestamp + ", node_identifier="
				+ node_identifier + ", event_identifier=" + event_identifier
				+ ", event_data_length=" + event_data_length + ", status="
				+ status + ", operation_type=" + operation_type
				+ ", operation_value=" + operation_value + ", reason=" + reason
				+ ", crc=" + crc + "]";
	}
	
	
	
	
}
