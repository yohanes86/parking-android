package com.parking.utils;


public class MessageUtils {
	private static final String TAG = "MessageUtils";
	private Context context;
	
	/* STANDART MESSAGE */

	public MessageUtils(Context context) {
		super();
		this.context = context;
	}
	
	public void messageShort(String msg,int rc) {
		Toast toast = Toast.makeText(context, msg, Toast.LENGTH_SHORT);
		toast.setGravity(Gravity.CENTER | Gravity.CENTER, 0, 0);
		toast.show();
		Log.i(TAG, "messageShort " + msg + " rc : " + rc);
	}
	
	public void messageShort(String msg) {
		Toast toast = Toast.makeText(context, msg, Toast.LENGTH_SHORT);
		toast.setGravity(Gravity.CENTER | Gravity.CENTER, 0, 0);
		toast.show();
		Log.i(TAG, "messageShort " + msg);
	}
	
	public void messageLong(String msg) {
		Toast toast = Toast.makeText(context, msg, Toast.LENGTH_LONG);
		toast.setGravity(Gravity.CENTER | Gravity.CENTER, 0, 0);
		toast.show();
		Log.i(TAG, "messageLong " + msg);
	}
	
	public void messageLong(String msg,int rc) {
		Toast toast = Toast.makeText(context, msg, Toast.LENGTH_LONG);
		toast.setGravity(Gravity.CENTER | Gravity.CENTER, 0, 0);
		toast.show();
		Log.i(TAG, "messageLong " + msg + " rc: " + rc);
	}
	
}
