package com.vetsoft.ccms.netty.pojo;

public class DeviceDownloadInitRequest {

	public String sof;
	
	public String protocol_version;
	
	public String flag;
	
	public String dcu_identifier;
	
	public String dsn;
	
	public String duc_serial_no;
	
	public String file_type;
	
	public String file_identifier; // file identifier
	
	public String file_version;

	@Override
	public String toString() {
		return "DeviceDownloadInitRequest [sof=" + sof + ", protocol_version="
				+ protocol_version + ", flag=" + flag + ", dcu_identifier="
				+ dcu_identifier + ", dsn=" + dsn + ", duc_serial_no="
				+ duc_serial_no + ", file_type=" + file_type + ", file_id="
				+ file_identifier + ", file_version=" + file_version + "]";
	}

	
}
