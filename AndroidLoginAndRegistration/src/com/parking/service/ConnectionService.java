package com.parking.service;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.util.Log;

import com.parking.set.SetValue;

public class ConnectionService {

	private static final String TAG = "CSF Connection Service";
	
//	private SetValue SetValue = new SetValue();

	public static boolean checkSpeedTest() {
		HttpURLConnection urlc = null;
		try {

			URL url = new URL(SetValue.URL_CHECK_CONNECTION);

			urlc = (HttpURLConnection) url.openConnection();
			// urlc.setRequestProperty("User-Agent", "Android Application:"
			// + Z.APP_VERSION);
			urlc.setRequestProperty("Connection", "close");
			urlc.setConnectTimeout(1000 * 300); // mTimeout is in seconds			
			urlc.connect();
			
			return true;

		} catch (MalformedURLException mue) {
			// TODO Auto-generated catch block
			Log.v(TAG, "ERROR : " + mue);
			

		} catch (IOException ioe) {
			// TODO Auto-generated catch block
			Log.v(TAG, "ERROR : " + ioe);
			
		}finally{
			if(urlc!=null){
				urlc.disconnect();
			}
		}
		return false;
	}
	
	public static String errorConnectionMessage(){
		String speedtestResult = "";

		if (ConnectionService.checkSpeedTest() == true) {			
			speedtestResult = SetValue.SPEEDTEST_MSG_AVAILABLE;
			Log.v(TAG, SetValue.SPEEDTEST_MSG_AVAILABLE);					
		}else{
			speedtestResult = SetValue.SPEEDTEST_MSG_DOWN;
			Log.v(TAG, SetValue.SPEEDTEST_MSG_DOWN);	
		}
		
		return speedtestResult;
	}
}
