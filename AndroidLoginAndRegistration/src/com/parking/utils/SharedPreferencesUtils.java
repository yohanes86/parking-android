package com.parking.utils;

import java.io.IOException;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.DeserializationConfig.Feature;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.parking.data.LoginData;

public class SharedPreferencesUtils {
	private static final String TAG = "Share Preferences Utils";
	private static SharedPreferences prefs;
	
	public static void saveLoginData(String loginData,Context ctx){
    	ObjectMapper mapper = new ObjectMapper();
    	try {
    		String sb = mapper.writeValueAsString(loginData);
    		prefs = ctx.getSharedPreferences(com.parking.data.Constants.SHARED_PREF_LOGIN, 4); // 4 = multi process
    		
    		SharedPreferences.Editor editor = prefs.edit();
    		editor.putString(com.parking.data.Constants.SHARED_PREF_LOGIN, sb);
    		editor.commit();
    	} catch (JsonGenerationException e) {
			Log.e(TAG, "JsonGenerationException  saveLoginData: " + e);
			e.printStackTrace();			
		} catch (JsonMappingException e) {
			Log.e(TAG, "JsonMappingException saveLoginData: " + e);
			e.printStackTrace();
		} catch (IOException e) {
			Log.e(TAG, "IOException saveLoginData: " + e);
			e.printStackTrace();
		}
    	
    }
	
	public static LoginData getLoginData(Context ctx) {
		LoginData loginData = new LoginData();
		try {		
		prefs = ctx.getSharedPreferences(com.parking.data.Constants.SHARED_PREF_LOGIN, 4); // 4 = multi process
		String ld = prefs.getString(com.parking.data.Constants.SHARED_PREF_LOGIN, "");
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);    
		loginData = mapper.readValue(ld, new TypeReference<LoginData>(){});
		} catch (JsonGenerationException e) {
			Log.e(TAG, "JsonGenerationException  getLoginData: " + e);	
		} catch (JsonMappingException e) {
			Log.e(TAG, "JsonMappingException getLoginData: " + e);			
		} catch (IOException e) {
			Log.e(TAG, "IOException getLoginData: " + e);
		}
		return loginData;   
    }
}
