package com.vetsoft.ccms.netty.push.node;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vetsoft.ccms.netty.parser.BaseUtil;
import com.vetsoft.ccms.netty.pojo.DeviceDownloadInitRequest;
import com.vetsoft.ccms.netty.pojo.DownloadContentRequest;

public class NodeConfProcessor {

	public static Logger LOG =  LoggerFactory.getLogger(NodeConfProcessor.class);
	
	public static String getNodeConfResponse(DownloadContentRequest obj, String conf_buff) {
		conf_buff = getNodeConfData();
		StringBuffer sb = new StringBuffer();
		
		try {
			
			sb.append("55AA55AA")
			.append(obj.protocol_version)
			.append(obj.flag)
			.append(obj.dcu_identifier)
			.append(obj.dsn)
			.append("00")// command identifier
			.append("0077") // pay load len need to update
			.append(obj.duc_serial_no)
			.append("00")
			.append(obj.file_type)
			.append(obj.file_identifier)
			.append("00000000") // chunk off set
			.append(StringUtils.leftPad("" + "5B", 4, "0"))// 850/2 byte
			.append(conf_buff)
			;
			String buffer= sb.toString();
			String CRC = Integer.toHexString(BaseUtil.calculateCRC(BaseUtil.convertHexToString(buffer.toString()).toCharArray(), BaseUtil.convertHexToString(buffer.toString()).length()));
			
			sb.append(StringUtils.leftPad("" + CRC, 2, "0"));
		} catch (Exception e) {
			LOG.error("Exception while making buffer : "+e.getMessage());
		}
		return sb.toString();
	}
	
	public static String getNodeConfZeroByteResponse(
			DownloadContentRequest obj) {

	
		StringBuffer sb = new StringBuffer();
		
		try {
			
			sb.append("55AA55AA")
			.append(obj.protocol_version)
			.append(obj.flag)
			.append(obj.dcu_identifier)
			.append(obj.dsn)
			.append("00")// command identifier
			.append("001C") // pay load len need to update
			.append(obj.duc_serial_no)
			.append("00")
			.append(obj.file_type)
			.append(obj.file_identifier)
			.append("0000005B") // chunk off set
			.append("0000")// 850/2 byte
						;
			String buffer= sb.toString();
			String CRC = Integer.toHexString(BaseUtil.calculateCRC(BaseUtil.convertHexToString(buffer.toString()).toCharArray(), BaseUtil.convertHexToString(buffer.toString()).length()));
			
			sb.append(StringUtils.leftPad("" + CRC, 2, "0"));
		} catch (Exception e) {
			LOG.error("Exception while making buffer : "+e.getMessage());
		}
		return sb.toString();
	
	}
	
	public static String getNODEConfigurationFileInitResponse(DeviceDownloadInitRequest obj) {
		
		StringBuffer sb = new StringBuffer();
	
		try {
			
			sb.append("55AA55AA")
			.append(obj.protocol_version)
			.append(obj.flag)
			.append(obj.dcu_identifier)
			.append(obj.dsn)
			.append("00")// command identifier
			.append("001C") // pay load len
			.append(obj.duc_serial_no)
			.append("00")
			.append(obj.file_type)
			.append(obj.file_identifier)
			//.append(obj.file_identifier)
			//.append(StringUtils.leftPad("" + Integer.toHexString(total_file_size), 8, "0"))
			.append(StringUtils.leftPad("" + "8F", 8, "0"))// 850/2 byte
			.append("0200")
			;
			String buffer= sb.toString();
			String CRC = Integer.toHexString(BaseUtil.calculateCRC(BaseUtil.convertHexToString(buffer.toString()).toCharArray(), BaseUtil.convertHexToString(buffer.toString()).length()));
			
			sb.append(StringUtils.leftPad("" + CRC, 2, "0"));
		} catch (Exception e) {
			LOG.error("Exception while making buffer : "+e.getMessage());
		}
		return sb.toString();
	}
	
	private static String getNodeConfData() {
		return "000000010001FFFFFFFF0000FF000000040006FFFFFFFF0005FF00000007000FFFFFFFFF0008FF000000080004FFFFFFFF0003FF00000009000AFFFFFFFF0004FF0000000A000BFFFFFFFF0009FF000000640116FFFFFFFF0101FF";
	}
	
//	return "000000010001FFFFFFFF0000FF000000020002FFFFFFFF0001FF000000030003FFFFFFFF0002FF000000040006FFFFFFFF0005FF000000050007FFFFFFFF0006FF000000060008FFFFFFFF0007FF00000007000FFFFFFFFF0008FF000000080004FFFFFFFF0003FF00000009000AFFFFFFFF0004FF0000000A000BFFFFFFFF0009FF00000064011CFFFFFFFFAB07FF";
	//return "0001C4C90001FFFFFFFF0000FF0001C4CA0002FFFFFFFF0001FF0001C4CB0003FFFFFFFF0002FF0001C4CC0006FFFFFFFF0005FF0001C4CD0007FFFFFFFF0006FF0001C4CE0008FFFFFFFF0007FF0001C4CF000FFFFFFFFF0008FF0001C4D00004FFFFFFFF0003FF0001C4D1000AFFFFFFFF0004FF0001C4D2000BFFFFFFFF0009FF0001C4D30112FFFFFFFFAA07FF";
	
}
