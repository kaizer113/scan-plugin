package com.coverity.scan.hudson;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ScanPluginConfiguration {
    public static final String DISPLAY_NAME = "Coverity Scan Analysis Report";
    public static final String GRAPH_NAME = "Coverity Scan Analysis";
    public static final String URL = "scan-plugin";
    public static final String ICON_FILE_NAME = "graph.gif";
    public static final String NO_DATA_YET = "Sorry Coverity scan results are not available yet, check back later.";
    public static final String YES_DATA = "Your results have been received from Coverity.";
    public static final String SUBMIT_URL = "http://scan6.coverity.com/cgi-bin/eclipse_submit.v2.py";
    public static final String REPORT_URL = "http://scan6.coverity.com/cgi-bin/get_snapshot_detail.py";
    
    public static String encodeUTF8(String str){
    	try {
    		return URLEncoder.encode(str, "UTF-8");
    	} catch (UnsupportedEncodingException ex){
    		Logger.getLogger(ScanPluginConfiguration.class.getName()).log(Level.SEVERE, null, ex);
    		return "Failed to encode";
    	} 
    	catch (Exception ex){
    		Logger.getLogger(ScanPluginConfiguration.class.getName()).log(Level.SEVERE, null, ex);
    		return "Failed to encode - General Exception";
    	} 
    }
    public static String badFunction(){
    	String result;
    	int i=4;
    	if (1==1) return "4";
    	if (1==2) return "4";
    	if (i==1) {
    		return "1";
    	} else return "2";
    	//if (i==3) return "3";
    	
    	
   // return result;
    }
}
