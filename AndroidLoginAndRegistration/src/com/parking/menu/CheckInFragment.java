package com.parking.menu;

import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;

import com.parking.R;
import com.parking.data.Constants;
import com.parking.data.LoginData;
import com.parking.data.MessageVO;
import com.parking.utils.CipherUtil;
import com.parking.utils.HttpClientUtil;
import com.parking.utils.MessageUtils;
import com.parking.utils.RedirectUtils;
import com.parking.utils.SharedPreferencesUtils;

/**
 * User: special
 * Date: 13-12-22
 * Time: 下�?�3:28
 * Mail: specialcyci@gmail.com
 */
public class CheckInFragment extends Fragment {
	private static final String TAG = CheckInFragment.class.getSimpleName();
	private Context ctx;
	private CheckInAllowTask checkInAllowTask = null;
	private EditText bookingCode;
	private Button btnCheckIn;
	private ScrollView resultScrollView;
	private TextView bookingName;
	private TextView bookingPhone;
	private TextView bookingEmail;
	private TextView bookingMall;
	private TextView bookingId;
	private TextView bookingDate;
	private TextView bookingStatus;
	private String email;
	private String sessionkey;
	
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    	View rootView = inflater.inflate(R.layout.activity_check_in, container, false);
    	ctx = container.getContext();
    	btnCheckIn = (Button) rootView.findViewById(R.id.btnCheckIn);
    	bookingCode = (EditText) rootView.findViewById(R.id.bookingCode);
    	resultScrollView = (ScrollView) rootView.findViewById(R.id.resultScrollView);
    	bookingName = (TextView) rootView.findViewById(R.id.bookingName);
    	bookingPhone = (TextView) rootView.findViewById(R.id.bookingPhone);
    	bookingEmail = (TextView) rootView.findViewById(R.id.bookingEmail);
    	bookingMall = (TextView) rootView.findViewById(R.id.bookingMall);
    	bookingId = (TextView) rootView.findViewById(R.id.bookingId);
    	bookingDate = (TextView) rootView.findViewById(R.id.bookingDate);
    	bookingStatus = (TextView) rootView.findViewById(R.id.bookingStatus);
    	btnCheckIn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
           	 String bookingCodeInput = bookingCode.getText().toString();       
           	 LoginData loginData = SharedPreferencesUtils.getLoginData(ctx);            	 
           	// ambil dari session untuk email, session key
           	 email = loginData.getEmail();
           	 sessionkey = loginData.getSessionKey();		 
				if (!bookingCodeInput.isEmpty()) {	
					checkInAllowTask = new CheckInAllowTask();
					checkInAllowTask.execute("");       
				} else {
					MessageUtils messageUtils = new MessageUtils(ctx);
	             	messageUtils.messageLong(ctx.getResources().getString(R.string.message_booking_code_required));
				}


            }
        });
        return rootView;
    }
    
    public class CheckInAllowTask extends AsyncTask<String, Void, Boolean> {
       	private ProgressDialog dialog = new ProgressDialog(ctx);
       	private final HttpClient client = HttpClientUtil.getNewHttpClient();
       	String respString = null;
       	protected void onPreExecute() {
       		dialog = new ProgressDialog(ctx);
    			dialog.setIndeterminate(true);
    			dialog.setCancelable(true);
    			dialog.setMessage(ctx.getResources().getString(R.string.process_check_in));
    			dialog.show();
    		}
    		@Override
           protected Boolean doInBackground(String... params) {
           	boolean result = false;
           	try {
           		
//           		InqChangePasswordRequest inqChangePasswordRequest = new InqChangePasswordRequest();
//           		inqChangePasswordRequest.setEmail(email);
//           		inqChangePasswordRequest.setPassword(oldPassword.getText().toString());
//           		inqChangePasswordRequest.setNewPassword(newPassword.getText().toString());
//           		inqChangePasswordRequest.setSessionKey(sessionkey);
				String s = HttpClientUtil.getObjectMapper(ctx).writeValueAsString("");
				s = CipherUtil.encryptTripleDES(s, CipherUtil.PASSWORD);
           		Log.d(TAG,"Request: " + s);
                StringEntity entity = new StringEntity(s);    			
    			HttpPost post = new HttpPost(HttpClientUtil.URL_BASE+HttpClientUtil.URL_CHANGE_PASSWORD);
    			post.setHeader(HttpClientUtil.CONTENT_TYPE, HttpClientUtil.JSON);
    			post.setEntity(entity);
    			// Execute HTTP request
    			Log.d(TAG,"Executing request: " + post.getURI());
                HttpResponse response = client.execute(post);
                HttpEntity respEntity = response.getEntity();
                respString = EntityUtils.toString(respEntity);
    			result = true;
    			} catch (ClientProtocolException e) {
    				Log.e(TAG, "ClientProtocolException : "+e);
    				if (dialog.isShowing()) {
    					try
    	                {
    	            		dialog.dismiss();
    	                }catch(Exception e1) {
    	                	// nothing
    	                }
    	            }
    			} catch (IOException e) {
    				Log.e(TAG, "IOException : "+e);
    				if (dialog.isShowing()) {
    					try
    	                {
    	            		dialog.dismiss();
    	                }catch(Exception e1) {
    	                	// nothing
    	                }
    	            }		
    			} catch (Exception e) {
    				Log.e(TAG, "Exception : "+e);
    				if (dialog.isShowing()) {
    					try
    	                {
    	            		dialog.dismiss();
    	                }catch(Exception e1) {
    	                	// nothing
    	                }
    	            }				
    			}
           	return result;
           }

           @Override
           protected void onPostExecute(final Boolean success) {
        	   checkInAllowTask = null;          
               if (success) {
	               	if(!respString.isEmpty()){
	               		try {
	               			String respons = CipherUtil.decryptTripleDES(respString, CipherUtil.PASSWORD);
	               			MessageVO messageVO = HttpClientUtil.getObjectMapper(ctx).readValue(respons, MessageVO.class);		               	
		               		if(messageVO.getRc()==0){
		               			MessageUtils messageUtils = new MessageUtils(ctx);
				             	messageUtils.messageLong(messageVO.getOtherMessage());
		               		}else{
		               			MessageUtils messageUtils = new MessageUtils(ctx);
				             	messageUtils.messageLong(messageVO.getMessageRc());
				             	if(messageVO.getRc()==Constants.SESSION_EXPIRED){
				             		RedirectUtils redirectUtils = new RedirectUtils(ctx, getActivity());
				             		redirectUtils.redirectToLogin();
				             	}
		               		}
						} catch (Exception e) {
							MessageUtils messageUtils = new MessageUtils(ctx);
			             	messageUtils.messageLong(ctx.getResources().getString(R.string.message_unexpected_error_message_server));
						}	            
	               	}else{
	               	   MessageUtils messageUtils = new MessageUtils(ctx);
	             	   messageUtils.messageLong(ctx.getResources().getString(R.string.message_unexpected_error_server));
	               	}
               }else{
            	   MessageUtils messageUtils = new MessageUtils(ctx);
            	   messageUtils.messageLong(ctx.getResources().getString(R.string.message_unexpected_error_server));
               }
               if (dialog.isShowing()) {
               	try
                   {
               		dialog.dismiss();
                   }catch(Exception e1) {
                   	// nothing
                   }
               }
           }

       }

}
