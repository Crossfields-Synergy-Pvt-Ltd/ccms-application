package com.vetsoft.ccms.netty.pojo;

public class DownloadContentRequest {

	public String sof;
	
	public String protocol_version;
	
	public String flag;
	
	public String dcu_identifier;
	
	public String dsn;
	
	public String duc_serial_no;
	
	public String file_type;
	
	public String file_identifier; // file identifier
	
	public String file_offset;

	public int chunk_len;

	@Override
	public String toString() {
		return "DownloadContentRequest [sof=" + sof + ", protocol_version="
				+ protocol_version + ", flag=" + flag + ", dcu_identifier="
				+ dcu_identifier + ", dsn=" + dsn + ", duc_serial_no="
				+ duc_serial_no + ", file_type=" + file_type
				+ ", file_identifier=" + file_identifier + ", file_offset="
				+ file_offset + ", chunk_len=" + chunk_len + "]";
	}
	
}
