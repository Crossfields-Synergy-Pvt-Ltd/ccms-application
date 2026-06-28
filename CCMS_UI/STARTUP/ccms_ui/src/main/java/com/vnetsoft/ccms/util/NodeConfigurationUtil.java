package com.vnetsoft.ccms.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

public class NodeConfigurationUtil {

	public static void main(String[] args) {
		
		String inputFile = "C:/conf/nodeconfig.bin";
	       
		 
        try (
            InputStream inputStream = new FileInputStream(inputFile);
       
        ) {
 
            long fileSize = new File(inputFile).length();
 
            byte[] allBytes = new byte[(int) fileSize];
            
            inputStream.read(allBytes);
            String sTextAsHex = BaseUtil.getHexString(new String(allBytes));
          System.out.println("FILE DATA : " + sTextAsHex);
         //System.out.println(BaseUtil.convertHexToString(sTextAsHex));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
	}
	
	

}
