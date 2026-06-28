package com.vnetsoft.ccms.scheduler;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.apache.log4j.Logger;

import com.vnetsoft.ccms.pojo.IOUIObject;

public class IODataGenerator {

	static final Logger logger = Logger.getLogger(IODataGenerator.class);

	/*
	 * public static void main(String[] args) { List<IOUIObject> ui_io_obj_list
	 * = getIOUIObjects(); System.out.println(ui_io_obj_list); }
	 */
	public static List<IOUIObject> getIOUIObjects(String file_name) {
		List<IOUIObject> data_list = new ArrayList<IOUIObject>();
		try {
			Map<Integer, IOStatusTmpPojo> obj_list = getMeterData(file_name);
			//System.out.println(obj_list);

			int cum_on_hour = 0, cum_on_min = 0, cum_off_hour = 0, cum_off_min = 0;

			Set<Integer> keys = obj_list.keySet();

			TreeSet<Integer> sorted_keys = new TreeSet<Integer>();

			sorted_keys.addAll(keys);

			int count = 0;
			IOStatusTmpPojo prv = null;
			for (Integer key : sorted_keys) {
				count++;
				IOStatusTmpPojo obj = obj_list.get(key);
				if (prv == null) {
					prv = obj;
					continue;
				}

				if (count == sorted_keys.size()) {
					//System.out.println("LAST DATA");
					IOUIObject ui_obj = new IOUIObject();
					obj.setHour(24);
					obj.setMin(00);
					if (prv.getStatus().equals("ON")) {

						ui_obj.setOff_hour_min(obj.getHour() + ":"
								+ obj.getMin());
						ui_obj.setOn_hour_min(prv.getHour() + ":"
								+ prv.getMin());

						ui_obj.setOn_hours(getOnHour(prv, obj));
						ui_obj.setOff_hours("00:00");
						cum_on_hour = updateCumHour(prv, obj, cum_on_hour);
						cum_on_min = updateCumMin(prv, obj, cum_on_min);
					} else if (prv.getStatus().equals("OFF")) {

						ui_obj.setOn_hour_min(prv.getHour() + ":"
								+ prv.getMin());
						ui_obj.setOff_hour_min(obj.getHour() + ":"
								+ obj.getMin());

						cum_off_hour = updateCumHour(prv, obj, cum_off_hour);
						cum_off_min = updateCumMin(prv, obj, cum_off_min);
						ui_obj.setOff_hours(getOnHour(prv, obj));
						ui_obj.setOn_hours("00:00");
					}
					ui_obj.setDate(obj.getDate());
					ui_obj.setDcu_id(obj.getDcu_id());

					if (cum_on_min > 60) {
						cum_on_hour += 1;
						cum_on_min -= 60;
					}

					if (cum_off_min > 60) {
						cum_off_hour += 1;
						cum_off_min -= 60;
					}

					ui_obj.setCumulative_on_hour(cum_on_hour + ":" + cum_on_min);
					ui_obj.setCumulative_off_hour(cum_off_hour + ":"
							+ cum_off_min);
					data_list.add(ui_obj);
					continue;
				}
				if (obj.getStatus().equals(prv.getStatus())) {
					continue;
				}

				IOUIObject ui_obj = new IOUIObject();

				if (prv.getStatus().equals("ON")) {

					ui_obj.setOff_hour_min(obj.getHour() + ":" + obj.getMin());
					ui_obj.setOn_hour_min(prv.getHour() + ":" + prv.getMin());

					ui_obj.setOn_hours(getOnHour(prv, obj));
					ui_obj.setOff_hours("00:00");
					cum_on_hour = updateCumHour(prv, obj, cum_on_hour);
					cum_on_min = updateCumMin(prv, obj, cum_on_min);
				} else if (prv.getStatus().equals("OFF")) {

					ui_obj.setOn_hour_min(prv.getHour() + ":" + prv.getMin());
					ui_obj.setOff_hour_min(obj.getHour() + ":" + obj.getMin());

					ui_obj.setOff_hours(getOnHour(prv, obj));
					ui_obj.setOn_hours("00:00");
					cum_off_hour = updateCumHour(prv, obj, cum_off_hour);
					cum_off_min = updateCumMin(prv, obj, cum_off_min);
				}
				ui_obj.setDate(obj.getDate());
				ui_obj.setDcu_id(obj.getDcu_id());

				if (cum_on_min > 60) {
					cum_on_hour += 1;
					cum_on_min -= 60;
				}

				if (cum_off_min > 60) {
					cum_off_hour += 1;
					cum_off_min -= 60;
				}

				ui_obj.setCumulative_on_hour(cum_on_hour + ":" + cum_on_min);
				ui_obj.setCumulative_off_hour(cum_off_hour + ":" + cum_off_min);
				data_list.add(ui_obj);

				prv = obj;
			}
		} catch (Exception e) {
			logger.error("Exception : " + e.getMessage());
			logger.error(e.getStackTrace());
		}

		// System.out.println(data_list);
		return data_list;
	}

	private static int updateCumMin(IOStatusTmpPojo prv, IOStatusTmpPojo obj,
			int cum_min) {
		int min = 0;
		if (prv.getMin() != 00) {
			min = (60 - prv.getMin()) + obj.getMin();
		} else {
			min = prv.getMin() + obj.getMin();
		}
		if (min > 60) {

			min = (min - 60);
		}
		return (cum_min + min);
	}

	private static int updateCumHour(IOStatusTmpPojo prv, IOStatusTmpPojo obj,
			int cum_hour) {
		int hour = obj.getHour() - prv.getHour();
		return (cum_hour + hour);
	}

	private static String getOnHour(IOStatusTmpPojo prv, IOStatusTmpPojo obj) {

		int hour = obj.getHour() - prv.getHour();
		int min = 0;
		if (prv.getMin() != 00) {
			min = (60 - prv.getMin()) + obj.getMin();
		} else {
			min = prv.getMin() + obj.getMin();
		}
		if (min > 60) {

			min = (min - 60);
		}
		return (hour + ":" + min);
	}

	public static Map<Integer, IOStatusTmpPojo> getMeterData(String file_name) {

		Map<Integer, IOStatusTmpPojo> meter_data = new TreeMap<Integer, IOStatusTmpPojo>();

		BufferedReader fileBufferReader = null;
		try {

			fileBufferReader = new BufferedReader(new FileReader(file_name));
			String buffer;
			while ((buffer = fileBufferReader.readLine()) != null) {
				IOStatusTmpPojo ui_obj = new IOStatusTmpPojo();

				// System.out.println(buffer);
				if (buffer.length() > 20) {
					String tmp[] = buffer.split(",");
					ui_obj.setDate(tmp[2].trim());
					ui_obj.setDcu_id(tmp[1].trim());
					ui_obj.setHour(Integer.parseInt(tmp[3].trim()));
					ui_obj.setMin(Integer.parseInt(tmp[4].trim()));
					ui_obj.setStatus(tmp[6]);
					ui_obj.setOpration_resone(Integer.parseInt(tmp[7].trim()));

					meter_data.put(Integer.parseInt(tmp[3].trim() + ""
							+ tmp[4].trim()), ui_obj);
				}
			}

		} catch (Exception e) {
			logger.error("Exception : " + e.getMessage());
			logger.error(e.getStackTrace());
		} finally {
			try {
				fileBufferReader.close();
			} catch (Exception e) {
				logger.error("Exception : " + e.getMessage());
				logger.error(e.getStackTrace());
			}
		}

		return meter_data;
		// System.out.println(meter_data);
	}
}
