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
	
	public static void saveLoginData(String loginData,Context ctx){    	
    		prefs = ctx.getSharedPreferences(com.parking.data.Constants.SHARED_PREF_LOGIN, 4); // 4 = multi process    		
    		SharedPreferences.Editor editor = prefs.edit();
    		editor.putString(com.parking.data.Constants.SHARED_PREF_LOGIN, loginData);
    		editor.commit();
    }
	
	public static LoginData getLoginData(Context ctx) {
		LoginData loginData = null;
		try {		
		prefs = ctx.getSharedPreferences(com.parking.data.Constants.SHARED_PREF_LOGIN, 4); // 4 = multi process
		String ld = prefs.getString(com.parking.data.Constants.SHARED_PREF_LOGIN, "");
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
