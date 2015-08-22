package com.parking.utils;

import java.io.IOException;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.type.TypeReference;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.parking.data.LoginData;

public class SharedPreferencesUtils {
	private static final String TAG = "Share Preferences Utils";
	private static SharedPreferences prefs;
	
	private static SharedPreferences getPreferences(Context context) {
	    return context.getSharedPreferences(com.parking.data.Constants.PARKING_PREFERENCE, 
	    		Context.MODE_MULTI_PROCESS); //4
	}
	
	public static void saveLoginData(String loginData,Context ctx){    	
		SharedPreferences.Editor editor = getPreferences(ctx).edit();
		editor.putString(com.parking.data.Constants.LOGIN_DATA_PREF, loginData);
		editor.commit();
    }
	
	public static LoginData getLoginData(Context ctx) {
		LoginData loginData = null;
		try {		   	
		String ld = getPreferences(ctx).getString(com.parking.data.Constants.LOGIN_DATA_PREF, "");
		loginData = HttpClientUtil.getObjectMapper(ctx).readValue(ld, new TypeReference<LoginData>(){});
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
