package com.parking.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.content.Context;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.MaterialDialog.ButtonCallback;
import com.gc.materialdesign.widgets.SnackBar;
import com.parking.R;

public class MessageUtils {
	private static final String TAG = "MessageUtils";
	private Context ctx;
	private static SimpleDateFormat sdfDateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	/* STANDART MESSAGE */

	public MessageUtils(Context context) {
		super();
		this.ctx = context;
	}
	
	public static String displayDateTime(Date dateTime) {
		return sdfDateTime.format(dateTime);
	}
	
//	public void messageShort(String msg,int rc) {
//		Toast toast = Toast.makeText(context, msg, Toast.LENGTH_SHORT);
//		toast.setGravity(Gravity.CENTER | Gravity.CENTER, 0, 0);
//		toast.show();
//		Log.i(TAG, "messageShort " + msg + " rc : " + rc);
//	}
//	
//	public void messageShort(String msg) {
//		Toast toast = Toast.makeText(context, msg, Toast.LENGTH_SHORT);
//		toast.setGravity(Gravity.CENTER | Gravity.CENTER, 0, 0);
//		toast.show();
//		Log.i(TAG, "messageShort " + msg);
//	}
//	
//	public void messageLong(String msg) {
//		Toast toast = Toast.makeText(context, msg, Toast.LENGTH_LONG);
//		toast.setGravity(Gravity.CENTER | Gravity.CENTER, 0, 0);
//		toast.show();
//		Log.i(TAG, "messageLong " + msg);
//	}
//	
//	public void messageLong(String msg,int rc) {
//		Toast toast = Toast.makeText(context, msg, Toast.LENGTH_LONG);
//		toast.setGravity(Gravity.CENTER | Gravity.CENTER, 0, 0);
//		toast.show();
//		Log.i(TAG, "messageLong " + msg + " rc: " + rc);
//	}
	
	public void snackBarMessage(Activity act,String msg){
		SnackBar snackBar = new SnackBar(act, msg);
		snackBar.setBackgroundSnackBar(act.getResources().getColor(R.color.blue));
		snackBar.show();
	}
	
	public void showDialogInfoCallback(String title,String content,ButtonCallback callback) {
        new MaterialDialog.Builder(ctx)
                .title(title)
                .content(content)
                .positiveText(R.string.ok)
                .callback(callback)
                .show();
    }
	
	public void showDialogInfo(String title,String content) {
        new MaterialDialog.Builder(ctx)
                .title(title)
                .content(content)
                .positiveText(R.string.ok)                
                .show();
    }
		
	
}
