package com.vnetsoft.ccms.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.vnetsoft.ccms.pojo.HierarchyDetails;
import com.vnetsoft.ccms.pojo.Status;
import com.vnetsoft.ccms.services.DCUServices;

@Controller
@RequestMapping("/filter")
public class HierarchyController {

	@Autowired
	DCUServices userServices;

	static final Logger logger = Logger.getLogger(HierarchyController.class);
	
	
	public static void main(String[] args) {

		String file_name = "/home/ccms/dontdelete/FinalMasterDataFile.txt";
		 BufferedReader fileBufferReader = null;
			try {
				
				if (!new File(file_name).exists()) {
					System.out.println("file not exist : "+ file_name);
					//return "FILE NOT FOUND";
				}
				fileBufferReader = new BufferedReader(new FileReader(file_name));
				String buffer;
				while ((buffer = fileBufferReader.readLine()) != null) {
					HierarchyDetails ui_obj = new HierarchyDetails();
					
					 //System.out.println(buffer);
					 if(buffer.length() > 20){
						String tmp[] = buffer.split(",");
					 	ui_obj.setDistrict(tmp[0].trim());
					 	ui_obj.setDivision(tmp[1]);
						ui_obj.setMandal(tmp[2]);
						ui_obj.setGp(tmp[3]);
						ui_obj.setId(System.currentTimeMillis());
					
						System.out.println(ui_obj);
					 }
				}

			} catch (Exception e) {
				logger.error("Exception : " + e.getMessage());
				logger.error(e.getStackTrace());
			}finally{
				try {
					fileBufferReader.close();
				} catch (Exception e) {
					System.out.println("Eceception : "+ e.getMessage());
					e.printStackTrace();
				}
			}
		//return "DONE";
	
	}
	
	
	
	@RequestMapping(value = "/generate_db", method = RequestMethod.GET)
	public @ResponseBody String generateHierarchyDataFromFile() {
		//String file_name = "/home/ccms/dontdelete/FinalMasterDataFile.txt";
		String file_name = "/home/cspl19/FinalMasterDataFile.txt";
		 BufferedReader fileBufferReader = null;
			try {
				
				if (!new File(file_name).exists()) {
					System.out.println("file not exist : "+ file_name);
					return "FILE NOT FOUND";
				}
				fileBufferReader = new BufferedReader(new FileReader(file_name));
				String buffer;
				while ((buffer = fileBufferReader.readLine()) != null) {
					HierarchyDetails ui_obj = new HierarchyDetails();
					
					 //System.out.println(buffer);
					 if(buffer.length() > 20){
							String tmp[] = buffer.split(",");
						 	ui_obj.setDistrict(tmp[0].trim());
						 	ui_obj.setDivision(tmp[1].trim());
							ui_obj.setMandal(tmp[2].trim());
							ui_obj.setGp(tmp[3].trim());
							ui_obj.setId(System.currentTimeMillis());
						
					
						System.out.println(ui_obj);
						userServices.addHierarchy(ui_obj);
					 }
				}

			} catch (Exception e) {
				logger.error("Exception : " + e.getMessage());
				logger.error(e.getStackTrace());
			}finally{
				try {
					fileBufferReader.close();
				} catch (Exception e) {
					System.out.println("Eceception : "+ e.getMessage());
					e.printStackTrace();
				}
			}
		return "DONE";
	}
	@RequestMapping(value = "/create", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Status add(@RequestBody HierarchyDetails obj) {

		try {
			
			if(logger.isDebugEnabled()) {
				 logger.debug(obj);
			}
			
			userServices.addHierarchy(obj);
			return new Status(200, "Success");
		} catch (Exception e) {
			return new Status(0, e.toString());
		}
	}
	
	

	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public @ResponseBody List<HierarchyDetails> getAll() {


		if(logger.isDebugEnabled()) {
			 logger.debug("GET ALL REQUEST RECIVED");
		}
		
		List<HierarchyDetails> userList = null;
		try {
		
			userList = userServices.getHierarchyDetailsList();
			if(logger.isDebugEnabled()) {
				 logger.debug(userList);
			}
		
		} catch (Exception e) {
			System.out.println("Exception : " + e);
			e.printStackTrace();
		}

		return userList;
	}
	
	
	
	
	@RequestMapping(value = "/get_mandal", method = RequestMethod.GET)
	public @ResponseBody List<String> getMandalList(@RequestParam("district") String district) {
		List<String> obj = null;

		try {
			obj  = userServices.getMandalByDistrict(district);
			obj.add("ALL");
			obj.add("Other");
			
			Set<String> tmp = new HashSet<String>();
			tmp.addAll(obj);
			obj.clear();
			obj.addAll(tmp);
			return obj;
		} catch (Exception e) {
			e.getStackTrace();
		}
		return obj;
	}
	
	@RequestMapping(value = "/get_gp", method = RequestMethod.GET)
	public @ResponseBody List<String> getGPList(@RequestParam("mandal") String mandal) {
		List<String> obj = null;

		try {
			obj  = userServices.getGPByMandal(mandal);
			obj.add("ALL");
			obj.add("Other");
			Set<String> tmp = new HashSet<String>();
			tmp.addAll(obj);
			obj.clear();
			obj.addAll(tmp);
			return obj;
		} catch (Exception e) {
			e.getStackTrace();
		}
		return obj;
	}
	
	
	@RequestMapping(value = "/get_vilage", method = RequestMethod.GET)
	public @ResponseBody List<String> getVilageList(@RequestParam("gp") String gp) {
		List<String> obj = null;

		try {
			obj  = userServices.getVilageByGP(gp);
			obj.add("ALL");
			obj.add("Other");
			Set<String> tmp = new HashSet<String>();
			tmp.addAll(obj);
			obj.clear();
			obj.addAll(tmp);
			return obj;
		} catch (Exception e) {
			e.getStackTrace();
		}
		return obj;
	}
	
	
}
