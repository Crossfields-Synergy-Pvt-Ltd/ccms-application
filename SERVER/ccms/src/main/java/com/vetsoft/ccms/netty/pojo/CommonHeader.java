package com.vetsoft.ccms.netty.pojo;

public class CommonHeader {

	private String sof ;
	private int protocol_version;
	private int flag;
	private int gateway_identifier;
	private int dsn;
	private int command_identifier;
	private int payload_length;

	public String getSof() {
		return sof;
	}
	public void setSof(String sof) {
		this.sof = sof;
	}
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
	public int getCommand_identifier() {
		return command_identifier;
	}
	public void setCommand_identifier(int command_identifier) {
		this.command_identifier = command_identifier;
	}
	public int getPayload_length() {
		return payload_length;
	}
	public void setPayload_length(int payload_length) {
		this.payload_length = payload_length;
	}
	@Override
	public String toString() {
		return "CommonHeader [sof=" + sof + ", protocol_version="
				+ protocol_version + ", flag=" + flag + ", gateway_identifier="
				+ gateway_identifier + ", dsn=" + dsn + ", command_identifier="
				+ command_identifier + ", payload_length=" + payload_length
				+ "]";
	}

	
}
