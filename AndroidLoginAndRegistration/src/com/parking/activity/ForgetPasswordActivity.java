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
import android.widget.EditText;

import com.parking.data.InqForgotPasswordRequest;
import com.parking.data.InqForgotPasswordResponse;
import com.parking.data.MessageVO;
import com.parking.menu.MenuActivity;
import com.parking.utils.CipherUtil;
import com.parking.utils.HttpClientUtil;
import com.parking.utils.MessageUtils;

public class ForgetPasswordActivity extends Activity {
	private static final String TAG = ForgetPasswordActivity.class.getSimpleName();
	private Button btnForgotPassword;
	private Button btnLinkToLogin;
	private EditText inputEmail;
	private Context ctx;
	private ReqForgotPasswordTask reqForgotPasswordTask = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_forget_password);
		ctx = this.getApplicationContext();
		
		inputEmail = (EditText) findViewById(R.id.email);
		btnForgotPassword = (Button) findViewById(R.id.btnForgotPassword);
		btnLinkToLogin = (Button) findViewById(R.id.btnLinkToLoginScreen);

		

		// Forgot Pass Button Click event
		btnForgotPassword.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				String email = inputEmail.getText().toString();
				if (!email.isEmpty()) {	
					reqForgotPasswordTask = new ReqForgotPasswordTask();
					reqForgotPasswordTask.execute("");       
				} else {
					MessageUtils messageUtils = new MessageUtils(ctx);
	             	messageUtils.messageLong(ForgetPasswordActivity.this.getResources().getString(R.string.message_email_required));
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
	
	public class ReqForgotPasswordTask extends AsyncTask<String, Void, Boolean> {
       	private ProgressDialog dialog = new ProgressDialog(ForgetPasswordActivity.this);
       	private final HttpClient client = HttpClientUtil.getNewHttpClient();
       	String respString = null;
       	protected void onPreExecute() {
       		dialog = new ProgressDialog(ForgetPasswordActivity.this);
    			dialog.setIndeterminate(true);
    			dialog.setCancelable(true);
    			dialog.setMessage(ForgetPasswordActivity.this.getResources().getString(R.string.process_forgot_password));
    			dialog.show();
    		}
    		@Override
           protected Boolean doInBackground(String... params) {
           	boolean result = false;
           	try {
           		InqForgotPasswordRequest inqForgotPasswordRequest = new InqForgotPasswordRequest();
				inqForgotPasswordRequest.setEmail(inputEmail.getText().toString());
				String s = HttpClientUtil.getObjectMapper(ctx).writeValueAsString(inqForgotPasswordRequest);
				s = CipherUtil.encryptTripleDES(s, CipherUtil.PASSWORD);
           		Log.d(TAG,"Request: " + s);
                StringEntity entity = new StringEntity(s);    			
    			HttpPost post = new HttpPost(HttpClientUtil.URL_BASE+HttpClientUtil.URL_FORGOT_PASSWORD);
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
        	   reqForgotPasswordTask = null;          
               if (success) {
	               	if(!respString.isEmpty()){
	               		try {
	               			String respons = CipherUtil.decryptTripleDES(respString, CipherUtil.PASSWORD);
	               			MessageVO messageVO = HttpClientUtil.getObjectMapper(ctx).readValue(respons, MessageVO.class);
		               		InqForgotPasswordResponse inqForgotPasswordResponse = new InqForgotPasswordResponse();
		               		inqForgotPasswordResponse.setMessageVO(messageVO);
		               		if(inqForgotPasswordResponse.getMessageVO().getRc()==0){
		               			MessageUtils messageUtils = new MessageUtils(ctx);
				             	messageUtils.messageLong(messageVO.getOtherMessage());
				             	Intent i = new Intent(ctx, MenuActivity.class);
								startActivity(i);
								finish();
		               		}else{
		               			MessageUtils messageUtils = new MessageUtils(ctx);
				             	messageUtils.messageLong(messageVO.getMessageRc());
		               		}
						} catch (Exception e) {
							MessageUtils messageUtils = new MessageUtils(ctx);
			             	messageUtils.messageLong(ForgetPasswordActivity.this.getResources().getString(R.string.message_unexpected_error_message_server));
						}	            
	               	}else{
	               	   MessageUtils messageUtils = new MessageUtils(ctx);
	             	   messageUtils.messageLong(ForgetPasswordActivity.this.getResources().getString(R.string.message_unexpected_error_server));
	               	}
               }else{
            	   MessageUtils messageUtils = new MessageUtils(ctx);
            	   messageUtils.messageLong(ForgetPasswordActivity.this.getResources().getString(R.string.message_unexpected_error_server));
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
