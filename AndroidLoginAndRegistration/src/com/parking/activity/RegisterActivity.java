package com.parking.activity;


import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.util.EntityUtils;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parking.data.InqRegistrationRequest;
import com.parking.data.InqRegistrationResponse;
import com.parking.data.LoginData;
import com.parking.data.MessageVO;
import com.parking.menu.MenuActivity;
import com.parking.service.ConnectionService;
import com.parking.service.SessionService;
import com.parking.set.SetValue;
import com.parking.utils.CipherUtil;
import com.parking.utils.HttpClientUtil;
import com.parking.utils.MessageUtils;
import com.parking.utils.SharedPreferencesUtils;
import com.parking.utils.StringUtils;

public class RegisterActivity extends Activity {
	private static final String TAG = RegisterActivity.class.getSimpleName();
	private Button btnRegister;
	private Button btnLinkToLogin;
	private EditText inputFullName;
	private EditText inputEmail;
	private EditText inputPassword;
	private EditText inputLicenseNo;
	private EditText inputPhoneNo;
	private ProgressDialog pDialog;
	private Context ctx;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);

		inputFullName = (EditText) findViewById(R.id.name);
		inputEmail = (EditText) findViewById(R.id.email);
		inputLicenseNo = (EditText) findViewById(R.id.licenseNo);
		inputPhoneNo = (EditText) findViewById(R.id.phoneNo);
		inputPassword = (EditText) findViewById(R.id.password);
		btnRegister = (Button) findViewById(R.id.btnRegister);
		btnLinkToLogin = (Button) findViewById(R.id.btnLinkToLoginScreen);
		
		ctx = this.getApplicationContext();

		// Progress dialog
		pDialog = new ProgressDialog(this);
		pDialog.setCancelable(false);

		

		// Register Button Click event
		btnRegister.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				String name = inputFullName.getText().toString();
				String email = inputEmail.getText().toString();
				String password = inputPassword.getText().toString();
				String phoneNo = inputPhoneNo.getText().toString();
				String licenseNo = inputLicenseNo.getText().toString();
				

				if (!name.isEmpty() && !email.isEmpty() && !password.isEmpty()&& !phoneNo.isEmpty()&& !licenseNo.isEmpty()) {
					InqRegistrationRequest inqRegRequest = new InqRegistrationRequest();
					inqRegRequest.setName(name);
					inqRegRequest.setLicenseNo(licenseNo);
					inqRegRequest.setEmail(email);
					inqRegRequest.setPassword(password);
					inqRegRequest.setPhoneNo(phoneNo);
					
					String s = HttpClientUtil.getObjectMapper(ctx).writeValueAsString(inqRegRequest);
					s = CipherUtil.encryptTripleDES(s, CipherUtil.PASSWORD);
					
					String paramRegistration = composeHttpPostParamReg(inqRegRequest);
					
					grabURL(SetValue.USER_REG, paramRegistration);
					
					
				} else {
					Toast.makeText(getApplicationContext(),
							"Please fill your details!", Toast.LENGTH_LONG).show();
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
	
	private void grabURL (String addedUrl,String param){
		String urlServer = HttpClientUtil.URL_BASE;
//		toastMsg(urlServer);
		
		Log.v(TAG, urlServer+ addedUrl);
		
		new grabURL().execute(urlServer + addedUrl,param);
		
	}
	
	public class grabURL extends AsyncTask<String, Void, Boolean> {

		private ProgressDialog dialog = new ProgressDialog(RegisterActivity.this);
//		private final HttpClient client = new DefaultHttpClient();
		private final HttpClient client = HttpClientUtil.getNewHttpClient();
		private String content;
		private String error = null;
		private String speedtestResult = null;
		String respString = null;

		protected void onPreExecute() {
			dialog.setMessage(getString(R.string.label_retrieveData));
			dialog.show();
		}
		
		@Override
		protected Boolean doInBackground(String... params) {
			// TODO Auto-generated method stub
			boolean result = false;
			try{
		
				HttpPost httppost = new HttpPost(params[0]);
				StringEntity entity = new StringEntity(params[1]);
				httppost.setHeader(HttpClientUtil.CONTENT_TYPE, HttpClientUtil.JSON);
				httppost.setEntity(entity);
				
				
				/*ResponseHandler<String> responseHandler = new BasicResponseHandler();
				content = client.execute(httppost, responseHandler);
				result = true;*/
				HttpResponse response = client.execute(httppost);
                HttpEntity respEntity = response.getEntity();
                respString = EntityUtils.toString(respEntity);
    			result = true;

			}catch (ClientProtocolException e){
				/*dialog.dismiss();*/
				if  (dialog.isShowing()) {
					try
	                {
						dialog.dismiss();
	                }catch(Exception e1) {
	                // nothing
	                }
	            }
				speedtestResult = ConnectionService.errorConnectionMessage();
				Log.v(TAG, "speedtestResult: " + speedtestResult);
			
				error = speedtestResult + " - " + SetValue.ENGINE_MSG_DOWN;
				
				Log.v(TAG, "CLIENT ERROR: " + error);
				cancel(true);
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			/*dialog.dismiss();*/
			if  (dialog.isShowing()) {
				try
                {
					dialog.dismiss();
                }catch(Exception e1) {
                // nothing
                }
            }
			speedtestResult = ConnectionService.errorConnectionMessage();
			Log.v(TAG, "speedtestResult: " + speedtestResult);

			error = speedtestResult + " - " + SetValue.ENGINE_MSG_DOWN;

			Log.v(TAG, "IO ERROR: " + error);
			cancel(true);
		} catch (Exception e) {
			/*dialog.dismiss();*/
			if  (dialog.isShowing()) {
				try
                {
					dialog.dismiss();
                }catch(Exception e1) {
                // nothing
                }
            }
			speedtestResult = ConnectionService.errorConnectionMessage();
			Log.v(TAG, "speedtestResult: " + speedtestResult);

			error = speedtestResult + e.getMessage();

			Log.v(TAG, "EX ERROR: " + error);
			cancel(true);
		}

		return result;
			
		}protected void onPostExecute(Boolean success) {
			/*dialog.dismiss();*/
			if  (dialog.isShowing()) {
				try
                {
					dialog.dismiss();
                }catch(Exception e1) {
                // nothing
                }
            }
			if(success){
			if (!StringUtils.isEmpty(error)) {
				Log.v(TAG, "ERROR: " + error);
			} else {
					try{
						String respons = CipherUtil.decryptTripleDES(respString, CipherUtil.PASSWORD);
               			MessageVO messageVO = HttpClientUtil.getObjectMapper(ctx).readValue(respons, MessageVO.class);
	               		if(messageVO.getRc()==SetValue.RC_SUCCESS){
	               			registrationSuccess();
	               		}else{
	               			MessageUtils messageUtils = new MessageUtils(ctx);
			             	messageUtils.messageLong(messageVO.getMessageRc());
	               		}
						
					
					} catch (JsonGenerationException e) {	
		        		Log.e(TAG, "JsonGenerationException : "+e);
		    			e.printStackTrace();			    		
		    		} catch (JsonMappingException e) {			
		    			Log.e(TAG, "JsonMappingException : "+e);
		    			e.printStackTrace();		    			
		    		} catch (IOException e) {			
		    			Log.e(TAG, "IOException : "+e);
		    			e.printStackTrace();		    			
		    		} 
				}
			}
		}
		
		@Override
		protected void onCancelled() {

			toastMsg(error);
		}
	}
	
	//Compose Param
	
	private String composeHttpPostParamReg(InqRegistrationRequest inqReg) {
		ObjectMapper mapper = new ObjectMapper();
		String result = "";
		try {
			result = mapper.writeValueAsString(inqReg);
		} catch (JsonGenerationException e) {
			Log.e(TAG, "composeHttpPostParamLogin JsonGenerationException : " +e.toString());
			e.printStackTrace();
		} catch (JsonMappingException e) {
			Log.e(TAG, "composeHttpPostParamLogin JsonMappingException : " +e.toString());
			e.printStackTrace();
		} catch (IOException e) {
			Log.e(TAG, "composeHttpPostParamLogin IOException : " +e.toString());
			e.printStackTrace();
		}
		return result;
	}
	
	private void registrationSuccess() {
		startActivity(new Intent(this, LoginActivity.class));
		this.finish();
	}
	
	//TOAST MESSAGE
	private void toastMsg(String msg){
		Toast toast = Toast.makeText(this, msg, Toast.LENGTH_LONG);
		toast.setGravity(Gravity.TOP | Gravity.CENTER, 0, 0);
		toast.show();
	}
	

}
