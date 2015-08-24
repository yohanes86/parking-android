package com.parking.set;

import java.io.File;

import android.os.Environment;

public class SetValue
{
	// NAVIGATION
	
	public int navigationStatus = 0;
	public int scrollIconValue;
	public int scrollMenuValue;
	
	public String countNotificationsScoring;
//	public Integer countNotificationsScoring;
	public String countNotificationsNetRate;
	public String countNotificationsQuota;
	public String countNotificationsPokokHutang;
	
	
	// CONSTANTS
	public static final String URL_SERVER_REAL ="https://192.168.0.25:8080/csf-trx/trx";

	

	public static final int CONNECTION_TIMEOUT_SECONDS = 30;
	public static final int SOCKET_TIMEOUT_SECONDS = 30;
	public static final String TIMEOUT_SECONDS = "1";
	
	public static final String URL_CHECK_CONNECTION = "http://speedtest.net";
	public static final String SPEEDTEST_MSG_AVAILABLE = "koneksi jaringan sukses";
	public static final String SPEEDTEST_MSG_DOWN = "koneksi jaringan gagal";
	public static final String ENGINE_MSG_DOWN = "koneksi terputus ke engine";
	
	public static final String LOGIN_TRUE = "1";
	public static final String LOGIN_FALSE = "";
	
	public static final String APP_FOLDER_NAME = "/parking";
	public static final File DEFAULT_PATH = Environment.getExternalStorageDirectory();
	public static final String STORAGE_PATH = DEFAULT_PATH.toString();
	public static final String BASEPATH = STORAGE_PATH + APP_FOLDER_NAME;
	public static String DEFAULT_NOTIF_DELAY_TAB = "28800"; // 28800 detik = 8 jam
	
	//for http
//	public static final boolean IS_HTTP_MODE = true;
	
	//for https
	public static final boolean IS_HTTPS_MODE = false;
	
	//SYMBOLS
	
	public static final String EMPTY_STR = "";
	public static final String CHAR_NUMBER_SIGN = "#";
	public static final String EMPTY_DATA_SIGN = "-";
	public static final String CHAR_EQUALS = "=";
	public static final String CHAR_DOT = ".";
	public static final String CHAR_QUESTION_MARK = "?";
	public static final String CHAR_AND = "&";
	public static final String CHAR_SEMICOLON = ";";
	public static final String CHAR_COMMA = ",";
	public static final String CHAR_BRACKET_L = "{";
	public static final String CHAR_BRACKET_R = "}";
	
	
	// SHARED PREFERENCES
	
	public static String SHARED_PREF_ME = "SHARED_PREF_ME";
	public static String SHARED_PREF_ME_LOGIN_DATA = "LOGIN_DATA";
	
	
	// TIMEOUT FOR LOGIN
	public static final String SESSION_TIMEOUT = "TIMEOUT";
	public static final String SESSION_PAUSE_TIME = "PAUSE_TIME";
	public static final String SESSION_LOGIN_PAGE = "LOGIN_PAGE";
	public static final String SESSION_SERVICE_KEY = "SESSION_KEY";
	
	public static final String TRX_TYPE_INQ_LIST_SERVICE = "INQ_LIST_SERVICE"; 
	
	public static final int LENGTH_SESSION_GENERATE = 16;
	public static final String PARAM_SESSION_ID = "SESSION_ID";
	public static final String PARAM_PASS_SERVER = "PASSWORD";
	public static final String PARAM_USER_ID = "USER_ID";
	public static final String PARAM_TRX_CODE = "TRX_CODE";
	
	public static final String USER_LOGIN= "/processingLogin"; // LOGIN
	public static final String USER_REG= "/processingRegistration"; // Registration
	
	
	//RC
	
	public static final String RC_SUCCESS		= "00";
	public static final String RC_TIMEOUT 		= "99";
	
	
	
}