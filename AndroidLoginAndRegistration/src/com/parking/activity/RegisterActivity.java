package com.parking.activity;


import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.iangclifton.android.floatlabel.FloatLabel;
import com.parking.R;
import com.parking.data.InqRegistrationRequest;
import com.parking.data.MessageVO;
import com.parking.utils.CipherUtil;
import com.parking.utils.CustomLabelAnimator;
import com.parking.utils.HttpClientUtil;
import com.parking.utils.MessageUtils;

public class RegisterActivity extends Activity {
	private static final String TAG = RegisterActivity.class.getSimpleName();
	private Button btnRegister;
	private Button btnLinkToLogin;
	private FloatLabel inputFullName;
	private FloatLabel inputEmail;
	private FloatLabel inputPassword;
	private FloatLabel inputLicenseNo;
	private FloatLabel inputPhoneNo;
	private ProgressDialog pDialog;
	private Context ctx;
	private ReqRegistrationTask reqRegistrationTask = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);

		inputFullName = (FloatLabel) findViewById(R.id.name);
		inputEmail = (FloatLabel) findViewById(R.id.email);
		inputLicenseNo = (FloatLabel) findViewById(R.id.licenseNo);
		inputPhoneNo = (FloatLabel) findViewById(R.id.phoneNo);
		inputPassword = (FloatLabel) findViewById(R.id.password);
		btnRegister = (Button) findViewById(R.id.btnRegister);
		btnLinkToLogin = (Button) findViewById(R.id.btnLinkToLoginScreen);
		
		// This is how you add a custom animator
		inputFullName.setLabelAnimator(new CustomLabelAnimator());
        inputEmail.setLabelAnimator(new CustomLabelAnimator());
        inputLicenseNo.setLabelAnimator(new CustomLabelAnimator());
        inputPhoneNo.setLabelAnimator(new CustomLabelAnimator());
        inputPassword.setLabelAnimator(new CustomLabelAnimator());
        
		
		ctx = this.getApplicationContext();

		// Progress dialog
		pDialog = new ProgressDialog(this);
		pDialog.setCancelable(false);

		

		// Register Button Click event
		btnRegister.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				String name = inputFullName.getEditText().getText().toString();
				String email = inputEmail.getEditText().getText().toString();
				String password = inputPassword.getEditText().getText().toString();
				String phoneNo = inputPhoneNo.getEditText().getText().toString();
				String licenseNo = inputLicenseNo.getEditText().getText().toString();
				

				if (!name.isEmpty() && !email.isEmpty() && !password.isEmpty()&& !phoneNo.isEmpty()&& !licenseNo.isEmpty()) {
					
					// register user
					reqRegistrationTask = new ReqRegistrationTask();
					reqRegistrationTask.execute("");
					
					
				} else {
					MessageUtils messageUtils = new MessageUtils(ctx);
	             	messageUtils.messageLong(RegisterActivity.this.getResources().getString(R.string.message_detail_required));
				}
			}
		});

		// Link to Login Screen
		btnLinkToLogin.setOnClickListener(new View.OnClickListener() {

			public void onClick(View view) {
				Intent i = new Intent(getApplicationContext(), LoginActivity.class);
				startActivity(i);
				finish();
			}
		});

	}
	
	public class ReqRegistrationTask  extends AsyncTask<String, Void, Boolean> {
		private ProgressDialog dialog = new ProgressDialog(RegisterActivity.this);
       	private final HttpClient client = HttpClientUtil.getNewHttpClient();
       	String respString = null;
       	protected void onPreExecute() {
       		dialog = new ProgressDialog(RegisterActivity.this);
    			dialog.setIndeterminate(true);
    			dialog.setCancelable(true);
    			dialog.setMessage(RegisterActivity.this.getResources().getString(R.string.process_register));
    			dialog.show();
    		}
		@Override
		protected Boolean doInBackground(String... arg0) {
			boolean result = false;
           	try {
           		InqRegistrationRequest inqRegRequest = new InqRegistrationRequest();           		
				inqRegRequest.setName(inputFullName.getEditText().getText().toString());
				inqRegRequest.setLicenseNo(inputLicenseNo.getEditText().getText().toString());
				inqRegRequest.setEmail(inputEmail.getEditText().getText().toString());
				inqRegRequest.setPassword(inputPassword.getEditText().getText().toString());
				inqRegRequest.setPhoneNo(inputPhoneNo.getEditText().getText().toString());
           		String s = HttpClientUtil.getObjectMapper(ctx).writeValueAsString(inqRegRequest);
				s = CipherUtil.encryptTripleDES(s, CipherUtil.PASSWORD);
           		Log.d(TAG,"Request: " + s);
                StringEntity entity = new StringEntity(s);    			
    			HttpPost post = new HttpPost(HttpClientUtil.URL_BASE+HttpClientUtil.URL_USER_REG);
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
			 reqRegistrationTask = null;          
             if (success) {
	               	if(!respString.isEmpty()){
	               		try {
	               			String respons = CipherUtil.decryptTripleDES(respString, CipherUtil.PASSWORD);
	               			MessageVO messageVO = HttpClientUtil.getObjectMapper(ctx).readValue(respons, MessageVO.class);
		               		if(messageVO.getRc()==0){
		               			MessageUtils messageUtils = new MessageUtils(ctx);
				             	messageUtils.messageLong(messageVO.getOtherMessage());
				             	Intent i = new Intent(ctx, LoginActivity.class);
								startActivity(i);
								finish();
		               		}
		               		else{
		               			MessageUtils messageUtils = new MessageUtils(ctx);
				             	messageUtils.messageLong(messageVO.getMessageRc());
		               		}

						} catch (Exception e) {
							MessageUtils messageUtils = new MessageUtils(ctx);
			             	messageUtils.messageLong(RegisterActivity.this.getResources().getString(R.string.message_unexpected_error_message_server));
						}	            
	               	}else{
	               	   MessageUtils messageUtils = new MessageUtils(ctx);
	             	   messageUtils.messageLong(RegisterActivity.this.getResources().getString(R.string.message_unexpected_error_server));
	               	}
             }else{
          	   MessageUtils messageUtils = new MessageUtils(ctx);
          	   messageUtils.messageLong(RegisterActivity.this.getResources().getString(R.string.message_unexpected_error_server));
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
