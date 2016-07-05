package com.amra.ServicesStatusTracker;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * 
 * @author pagaramol
 *
 */
public class PingService {
	
	public static String pingURL(String url, int timeout) {
	    url = url.replaceFirst("^https", "http"); // Otherwise an exception may be thrown on invalid SSL certificates.
	    String status = "red";
	    try {
	    	
	        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
	        connection.setConnectTimeout(timeout);
	        connection.setReadTimeout(timeout);
	        connection.setRequestMethod("HEAD");
	        int responseCode = connection.getResponseCode();
	        if(200 <= responseCode && responseCode <= 399){
	        	status = "green";
	        }
	        return status;
	       
	    } catch (IOException exception) {
	    	
	        return status;
	    }
	}

}
