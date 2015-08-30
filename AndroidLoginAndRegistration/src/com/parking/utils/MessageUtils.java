package com.parking.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

import com.gc.materialdesign.widgets.SnackBar;
import com.parking.R;

public class MessageUtils {
	private static final String TAG = "MessageUtils";
	private Context context;
	private static SimpleDateFormat sdfDateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	/* STANDART MESSAGE */

	public MessageUtils(Context context) {
		super();
		this.context = context;
	}
	
	public static String displayDateTime(Date dateTime) {
		return sdfDateTime.format(dateTime);
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
	
	public void snackBarMessage(Activity act,String msg){
		SnackBar snackBar = new SnackBar(act, msg);
		snackBar.setBackgroundSnackBar(act.getResources().getColor(R.color.blue));
		snackBar.show();
	}
	
}
