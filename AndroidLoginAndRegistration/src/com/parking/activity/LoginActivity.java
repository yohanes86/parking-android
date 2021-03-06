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
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.gc.materialdesign.views.ButtonFlat;
import com.gc.materialdesign.views.ButtonRectangle;
import com.gc.materialdesign.widgets.ProgressDialogParking;
import com.iangclifton.android.floatlabel.FloatLabel;
import com.parking.R;
import com.parking.data.InqLoginRequest;
import com.parking.data.LoginData;
import com.parking.data.MessageVO;
import com.parking.menu.MenuActivity;
import com.parking.utils.CipherUtil;
import com.parking.utils.CustomLabelAnimator;
import com.parking.utils.HttpClientUtil;
import com.parking.utils.MessageUtils;
import com.parking.utils.SharedPreferencesUtils;
import com.romainpiel.shimmer.Shimmer;
import com.romainpiel.shimmer.ShimmerTextView;


public class LoginActivity extends Activity {
	// LogCat tag
	private static final String TAG = RegisterActivity.class.getSimpleName();
	private ButtonRectangle btnLogin;
	private ButtonFlat btnLinkToRegister;
	private ButtonFlat btnLinkToForgetPassword;
	private FloatLabel inputEmail;
	private FloatLabel inputPassword;
	private Context ctx;
	private ReqLoginTask reqLoginTask = null;
	SharedPreferences sharedpreferences;
	public static final String MyPREFERENCES = "MyPrefs" ;
	Shimmer shimmer;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		ctx = LoginActivity.this;
		
		ShimmerTextView tvFancy = (ShimmerTextView) findViewById(R.id.tvFancy);
		inputEmail = (FloatLabel) findViewById(R.id.email);
		inputPassword = (FloatLabel) findViewById(R.id.password);
		btnLogin = (ButtonRectangle) findViewById(R.id.btnLogin);
		btnLinkToRegister = (ButtonFlat) findViewById(R.id.btnLinkToRegisterScreen);
		btnLinkToForgetPassword = (ButtonFlat) findViewById(R.id.btnLinkToForgetPasswordScreen);
		
		if (shimmer != null && shimmer.isAnimating()) {
			shimmer.cancel();
        } else {
        	shimmer = new Shimmer();
        	shimmer.start(tvFancy);
        }

		// This is how you add a custom animator
        inputPassword.setLabelAnimator(new CustomLabelAnimator());
        inputEmail.setLabelAnimator(new CustomLabelAnimator());
        
		
		// Login button Click Event
		btnLogin.setOnClickListener(new View.OnClickListener() {

			public void onClick(View view) {
				String email = inputEmail.getEditText().getText().toString();
				String password = inputPassword.getEditText().getText().toString();
				
				// Check for empty data in the form
				if (email.trim().length() > 0 && password.trim().length() > 0) {
					// validation email
					if (!isValidEmail(email)) {						
						MessageUtils messageUtils = new MessageUtils(ctx);
		             	messageUtils.snackBarMessage(LoginActivity.this, LoginActivity.this.getResources().getString(R.string.email_not_valid));		             	
					}
					else{
						// login user
						reqLoginTask = new ReqLoginTask();
						reqLoginTask.execute("");
					}
				} else {
					// Prompt user to enter credentials
					MessageUtils messageUtils = new MessageUtils(ctx);
	             	messageUtils.snackBarMessage(LoginActivity.this, LoginActivity.this.getResources().getString(R.string.email_and_pass_required));
				}
			}

		});

		// Link to Register Screen
		btnLinkToRegister.setOnClickListener(new View.OnClickListener() {

			public void onClick(View view) {
				Intent i = new Intent(getApplicationContext(), RegisterActivity.class);
				startActivity(i);
				finish();
			}
		});
		
		// Link to ForgetPassword Screen
		btnLinkToForgetPassword.setOnClickListener(new View.OnClickListener() {

			public void onClick(View view) {
				Intent i = new Intent(getApplicationContext(),
						ForgetPasswordActivity.class);
				startActivity(i);
				finish();
			}
		});

	}
	
	
	public class ReqLoginTask  extends AsyncTask<String, Void, Boolean> {
		private ProgressDialogParking progressDialog = null;
       	private final HttpClient client = HttpClientUtil.getNewHttpClient();
       	String respString = null;
       	protected void onPreExecute() {
    			progressDialog = new ProgressDialogParking(ctx, ctx.getResources().getString(R.string.process_login),ctx.getResources().getString(R.string.progress_dialog));
    			progressDialog.show();
    		}
		@Override
		protected Boolean doInBackground(String... arg0) {
			boolean result = false;
           	try {
           		InqLoginRequest inqLoginRequest = new InqLoginRequest();
           		inqLoginRequest.setEmail(inputEmail.getEditText().getText().toString());
           		inqLoginRequest.setPassword(inputPassword.getEditText().getText().toString());
           		String s = HttpClientUtil.getObjectMapper(ctx).writeValueAsString(inqLoginRequest);
				s = CipherUtil.encryptTripleDES(s, CipherUtil.PASSWORD);
           		Log.d(TAG,"Request: " + s);
                StringEntity entity = new StringEntity(s);    			
    			HttpPost post = new HttpPost(HttpClientUtil.URL_BASE+HttpClientUtil.URL_LOGIN);
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
    				respString = ctx.getResources().getString(R.string.message_unexpected_error_message_server);
    				cancel(true);    				
    			} catch (IOException e) {
    				Log.e(TAG, "IOException : "+e); 
    				respString = ctx.getResources().getString(R.string.message_no_internet_connection);
    				cancel(true);    				
    			} catch (Exception e) {
    				Log.e(TAG, "Exception : "+e);  
    				respString = ctx.getResources().getString(R.string.message_unexpected_error_message_server);
    				cancel(true);    				
    			}
           	return result;
           }
		
		 @Override
	     protected void onCancelled() {
			 if(progressDialog.isShowing()){
				progressDialog.dismiss();
			 }
			 MessageUtils messageUtils = new MessageUtils(ctx);
          	 messageUtils.snackBarMessage(LoginActivity.this,respString);
	     }
		
		 @Override
         protected void onPostExecute(final Boolean success) {
			 reqLoginTask = null;          
             if (success) {
	               	if(!respString.isEmpty()){
	               		try {
	               			String respons = CipherUtil.decryptTripleDES(respString, CipherUtil.PASSWORD);
	               			MessageVO messageVO = HttpClientUtil.getObjectMapper(ctx).readValue(respons, MessageVO.class);
		               		if(messageVO.getRc()==0){
		               			SharedPreferencesUtils.saveLoginData(messageVO.getOtherMessage(), ctx);
		               			LoginData loginData = SharedPreferencesUtils.getLoginData(ctx);		               					           
		               			Intent i = new Intent(ctx, MenuActivity.class);
								startActivity(i);
								finish();
		               		}
		               		else{
		               			MessageUtils messageUtils = new MessageUtils(ctx);
				             	messageUtils.snackBarMessage(LoginActivity.this,messageVO.getMessageRc());
		               		}

						} catch (Exception e) {
							MessageUtils messageUtils = new MessageUtils(ctx);
			             	messageUtils.snackBarMessage(LoginActivity.this,LoginActivity.this.getResources().getString(R.string.message_unexpected_error_message_server));
						}	            
	               	}else{
	               	   MessageUtils messageUtils = new MessageUtils(ctx);
	             	   messageUtils.snackBarMessage(LoginActivity.this,LoginActivity.this.getResources().getString(R.string.message_unexpected_error_server));
	               	}
             }else{
          	   MessageUtils messageUtils = new MessageUtils(ctx);
          	   messageUtils.snackBarMessage(LoginActivity.this,LoginActivity.this.getResources().getString(R.string.message_unexpected_error_server));
             }        
             if(progressDialog.isShowing()){
					progressDialog.dismiss();
				}
         }
	}
	
	// validating email id
	private boolean isValidEmail(String email) {
		if (email == null) {
	        return false;
	    } else {
	    	return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
	    }
	}

}
