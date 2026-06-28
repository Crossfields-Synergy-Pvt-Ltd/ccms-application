package com.vetsoft.ccms.netty.parser.events;


import com.vetsoft.ccms.netty.parser.BaseUtil;
import com.vetsoft.ccms.netty.pojo.SinglePhaseMeterData;

public class SinglePhaseMeterDataParser extends BaseUtil {

	public static  SinglePhaseMeterData parseSinglePhaseMeterData(String buff) {
	int index = 10;
		
	SinglePhaseMeterData obj = new SinglePhaseMeterData();
	try {
		
		String tmp = buff.substring(index, (index + 16));
		obj.setMeter_serial_number(convertHexToString(tmp));
		System.out.println(convertHexToString(tmp));
		index += 16;
		
		tmp = buff.substring(index, (index + 12));
		obj.setRtc_date(convertHexToString(tmp));
		index += 12;
		
		tmp = buff.substring(index, (index + 12));
		obj.setRtc_time(convertHexToString(tmp));
		index += 12;
		
		
		tmp = buff.substring(index, (index + 6));
		obj.setInstantaneous_voltage(getDoubleValue(convertHexToString(tmp)));
		index += 6;
		
		tmp = buff.substring(index, (index + 10));
		obj.setInstantaneous_current(getDoubleValue(convertHexToString(tmp)));
		index += 10;
		
		tmp = buff.substring(index, (index + 14));
		obj.setInstantaneous_power(getDoubleValue(convertHexToString(tmp)));
		index += 14;
		
		tmp = buff.substring(index, (index + 12));
		obj.setInstantaneous_power_factor(getPowerFacrtorValue(convertHexToString(tmp)));
		index += 12;
		
		
		tmp = buff.substring(index, (index + 14));
		obj.setCumulative_active_energy(getDoubleValue(convertHexToString(tmp)));
		index += 14;
		
	/*	
		tmp = buff.substring(index, (index + 14));
		obj.setLbp1_cumulative_active_energy(convertHexToString(tmp));*/
		index += 14;
		
	/*	tmp = buff.substring(index, (index + 14));
		obj.setLbp2_cumulative_active_energy(convertHexToString(tmp));*/
		index += 14;
		
		
		/*tmp = buff.substring(index, (index + 14));
		obj.setLbp3_cumulative_active_energy(convertHexToString(tmp));*/
		index += 14;
		
		/*tmp = buff.substring(index, (index + 14));
		obj.setLbp4_cumulative_active_energy(convertHexToString(tmp));*/
		index += 14;
		
		/*tmp = buff.substring(index, (index + 14));
		obj.setLbp5_cumulative_active_energy(convertHexToString(tmp));*/
		index += 14;
		
		/*tmp = buff.substring(index, (index + 14));
		obj.setLbp6_cumulative_active_energy(convertHexToString(tmp));*/
		index += 14;
		
		tmp = buff.substring(index, (index + 2));
		obj.setCt_reverse_tamper_status(getvalue(convertHexToString(tmp)));
		index += 2;
		
		tmp = buff.substring(index, (index + 2));
		obj.setEarth_load_tamper_status(getvalue(convertHexToString(tmp)));
		index += 2;
		
		tmp = buff.substring(index, (index + 2));
		obj.setCover_open_tamper_status(getvalue(convertHexToString(tmp)));
		index += 2;
		
		tmp = buff.substring(index, (index + 2));
		obj.setMagnetic_influence_tamper_status(getvalue(convertHexToString(tmp)));
		index += 2;
		
		tmp = buff.substring(index, (index + 2));
		obj.setSingle_wire_tamper_status(getvalue(convertHexToString(tmp)));
		index += 2;
		
		return obj;
		//System.out.println(obj);
	} catch (Exception e) {
		System.out.println("Exceptions : "+ e.getMessage());
	}
		return null;
	}


	private static String getPowerFacrtorValue(String buff) {
		
		String tmp;

		try {
		//	302E38356C64 PF (0.85ld)-- If lg at end then value is Negative, If ld value is possitive
			if(buff.contains("lg")){ // is 
				int len = buff.length();
				tmp = buff.substring(0, (len -2));
				return "-" +String.valueOf(Double.valueOf(tmp));
			} else {
				return String.valueOf(Double.valueOf(buff));
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		return buff;
	}

	private static String getDoubleValue(String buff) {
		
		try {
			return String.valueOf(Double.valueOf(buff));
			
		} catch (Exception e) {
			// TODO: handle exception
		}
		return buff;
	}

	private static String getvalue(String data) {
		if(data.equalsIgnoreCase("P")){
			return "P";
		}else {
			return "R";
		}
	}

}
