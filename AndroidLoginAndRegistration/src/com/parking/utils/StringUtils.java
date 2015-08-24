package com.parking.utils;

import java.io.IOException;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.parking.activity.LoginActivity;
import com.parking.set.SetValue;

public class StringUtils {
	private static final String TAG = "MAS StringUtils";
	private static SharedPreferences prefs;
	private static String prefName = SetValue.SHARED_PREF_ME;
	

	private static Random r = new Random();

	public static String getFileExtension(String fullFileName) {
		String result = "";
		int dot = fullFileName.lastIndexOf(SetValue.CHAR_DOT);
		result = fullFileName.substring(dot);

		return result;
	}

	public static boolean isEmpty(String str) {
		if (str == null || str.trim().equals("")) {
			return true;
		} else {
			return false;
		}
	}

	public static String delimiterChanger(String oldDelimiter,
			String newDelimiter, String msgStr) {
		String result = msgStr.replace(oldDelimiter, newDelimiter);

		return result;
	}

	public static String urlEncode(String value) {
		String encodeResult = "";

		try {
			encodeResult = URLEncoder.encode(value, "utf-8");
		} catch (Exception e) {
			Log.v(TAG, "ERROR ENCODE URL UTILS: " + e.getMessage());
		}

		return encodeResult;
	}

	public static String adjustmentLine(String msg, int lengthAdjust,
			boolean isPadLeft, String delimiter) {
		String result = msg;

		int lengthWord = msg.length();
		int remainSpace = 0;

		if (lengthWord < lengthAdjust) {
			remainSpace = lengthAdjust - lengthWord;
			String space = "";

			for (int i = 0; i < remainSpace; i++) {
				space = space + delimiter;
			}

			if (isPadLeft == true) {
				result = space + result;
			} else {
				result = result + space;
			}
		}

		return result;

	}

	public static String justifyLine(String msg, int lengthAdjust,
			String delimiter) {
		String result = msg;

		int lengthWord = msg.length();
		int remainSpace = 0;

		if (lengthWord < lengthAdjust) {
			remainSpace = lengthAdjust - lengthWord;
			String space = "";

			int leftPad = remainSpace / 2;
			int rightPad = remainSpace - leftPad;

			for (int i = 0; i < leftPad; i++) {
				space = space + delimiter;
			}

			result = space + result;

			for (int i = 0; i < rightPad; i++) {
				space = space + delimiter;
			}

			result = result + space;

		}

		return result;

	}

	public static String rcNoContent(String content) {
		String rc = content.substring(
				content.indexOf(SetValue.CHAR_EQUALS) + 1,
				content.indexOf(SetValue.CHAR_NUMBER_SIGN));

		return rc;
	}

	public static String contentNoRc(String content) {
		String nonRc = content.substring(content
				.indexOf(SetValue.CHAR_NUMBER_SIGN) + 1);

		return nonRc;
	}

	public static boolean regexPasswordChecker(String regex, String password) {
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(password);
		return matcher.matches();
	}

	public static String formatCurrency(String value, String format) {
		double convert = Double.parseDouble(value);

		NumberFormat formatter = new DecimalFormat(format);
		String resultFormat = formatter.format(convert);

		return resultFormat;
	}

	public static String generateAlphaNumeric(int length) {
		String C = "QWERTYUIOPLKJHGFDAZXCVBNM0987654321";
		StringBuffer sb = new StringBuffer(length);
		for (int i = 0; i < length; i++) {
			int idx = r.nextInt(C.length());
			sb.append(C.substring(idx, idx + 1));
		}
		return sb.toString();
	}
	
	public static LoginActivity getSettingLoginData(Context ctx) {
		LoginActivity loginTabData = new LoginActivity();
		try {		
		prefs = ctx.getSharedPreferences(prefName, 4); // 4 = multi process
		String setting = prefs.getString(SetValue.SHARED_PREF_ME_LOGIN_DATA, "");
		ObjectMapper mapper = new ObjectMapper();
		loginTabData = mapper.readValue(setting, new TypeReference<LoginActivity>(){});
		} catch (JsonGenerationException e) {
			Log.e(TAG, "JsonGenerationException  getSettingLoginData: " + e);
			e.printStackTrace();			
		} catch (JsonMappingException e) {
			Log.e(TAG, "JsonMappingException getSettingLoginData: " + e);
			e.printStackTrace();
		} catch (IOException e) {
			Log.e(TAG, "IOException getSettingLoginData: " + e);
			e.printStackTrace();
		}
		return loginTabData;   
    }
	
	

	
	
}
