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
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parking.data.InqLoginRequest;
import com.parking.data.LoginData;
import com.parking.data.MessageVO;
import com.parking.menu.MenuActivity;
import com.parking.utils.CipherUtil;
import com.parking.utils.HttpClientUtil;
import com.parking.utils.MessageUtils;
import com.parking.utils.SharedPreferencesUtils;


public class LoginActivity extends Activity {
	// LogCat tag
	private static final String TAG = RegisterActivity.class.getSimpleName();
	private Button btnLogin;
	private Button btnLinkToRegister;
	private Button btnLinkToForgetPassword;
	private EditText inputEmail;
	private EditText inputPassword;
	private Context ctx;
	private ReqLoginTask reqLoginTask = null;
	SharedPreferences sharedpreferences;
	public static final String MyPREFERENCES = "MyPrefs" ;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		ctx = this.getApplicationContext();
		
		inputEmail = (EditText) findViewById(R.id.email);
		inputPassword = (EditText) findViewById(R.id.password);
		btnLogin = (Button) findViewById(R.id.btnLogin);
		btnLinkToRegister = (Button) findViewById(R.id.btnLinkToRegisterScreen);
		btnLinkToForgetPassword = (Button) findViewById(R.id.btnLinkToForgetPasswordScreen);

		// Login button Click Event
		btnLogin.setOnClickListener(new View.OnClickListener() {

			public void onClick(View view) {
				String email = inputEmail.getText().toString();
				String password = inputPassword.getText().toString();
				
				// Check for empty data in the form
				if (email.trim().length() > 0 && password.trim().length() > 0) {
					// validation email
					if (!isValidEmail(email)) {
						Toast.makeText(getApplicationContext(),
								"Email tidak valid!", Toast.LENGTH_LONG).show();
					}
					else{
						// login user
						reqLoginTask = new ReqLoginTask();
						reqLoginTask.execute("");
					}
				} else {
					// Prompt user to enter credentials
					MessageUtils messageUtils = new MessageUtils(ctx);
	             	messageUtils.messageLong(LoginActivity.this.getResources().getString(R.string.email_and_pass_required));
				}
			}

		});

		// Link to Register Screen
		btnLinkToRegister.setOnClickListener(new View.OnClickListener() {

			public void onClick(View view) {
				Intent i = new Intent(getApplicationContext(),
						RegisterActivity.class);
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
		private ProgressDialog dialog = new ProgressDialog(LoginActivity.this);
       	private final HttpClient client = HttpClientUtil.getNewHttpClient();
       	String respString = null;
       	protected void onPreExecute() {
       		dialog = new ProgressDialog(LoginActivity.this);
    			dialog.setIndeterminate(true);
    			dialog.setCancelable(true);
    			dialog.setMessage(LoginActivity.this.getResources().getString(R.string.process_login));
    			dialog.show();
    		}
		@Override
		protected Boolean doInBackground(String... arg0) {
			boolean result = false;
           	try {
           		InqLoginRequest inqLoginRequest = new InqLoginRequest();
           		inqLoginRequest.setEmail(inputEmail.getText().toString());
           		inqLoginRequest.setPassword(inputPassword.getText().toString());
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
				             	messageUtils.messageLong(messageVO.getMessageRc());
		               		}

						} catch (Exception e) {
							MessageUtils messageUtils = new MessageUtils(ctx);
			             	messageUtils.messageLong(LoginActivity.this.getResources().getString(R.string.message_unexpected_error_message_server));
						}	            
	               	}else{
	               	   MessageUtils messageUtils = new MessageUtils(ctx);
	             	   messageUtils.messageLong(LoginActivity.this.getResources().getString(R.string.message_unexpected_error_server));
	               	}
             }else{
          	   MessageUtils messageUtils = new MessageUtils(ctx);
          	   messageUtils.messageLong(LoginActivity.this.getResources().getString(R.string.message_unexpected_error_server));
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
	
	// validating email id
	private boolean isValidEmail(String email) {
		if (email == null) {
	        return false;
	    } else {
	    	return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
	    }
	}

}
