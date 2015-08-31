package com.parking.menu;

import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.gc.materialdesign.views.ButtonRectangle;
import com.gc.materialdesign.widgets.ProgressDialog;
import com.parking.R;
import com.parking.activity.LoginActivity;
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
 * Time: ä¸‹å?ˆ3:28
 * Mail: specialcyci@gmail.com
 */
public class LogoutFragment extends Fragment {
	private static final String TAG = LogoutFragment.class.getSimpleName();
	private Context ctx;
	private ReqLogoutTask reqLogoutTask = null;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    	View rootView = inflater.inflate(R.layout.activity_logout, container, false);
    	ctx = container.getContext();
    	ButtonRectangle btnLogout = (ButtonRectangle) rootView.findViewById(R.id.btnLogout);
   	 	btnLogout.setOnClickListener(new OnClickListener() {
	         @Override
	         public void onClick(View arg0) {	        	 	        	         	 	   
	        	 reqLogoutTask = new ReqLogoutTask();
	        	 reqLogoutTask.execute("");       
	         }
	    });
        return rootView;
    }
    
    public class ReqLogoutTask extends AsyncTask<String, Void, Boolean> {
    	private ProgressDialog progressDialog = null;
       	private final HttpClient client = HttpClientUtil.getNewHttpClient();
       	String respString = null;
       	protected void onPreExecute() {       					
    			progressDialog = new ProgressDialog(ctx, ctx.getResources().getString(R.string.process_logout));
    			progressDialog.show();
    		}
    		@Override
           protected Boolean doInBackground(String... params) {
           	boolean result = false;
           	try {
           		
           		LoginData loginData = SharedPreferencesUtils.getLoginData(ctx);   
				String s = HttpClientUtil.getObjectMapper(ctx).writeValueAsString(loginData);
				s = CipherUtil.encryptTripleDES(s, CipherUtil.PASSWORD);
           		Log.d(TAG,"Request: " + s);
                StringEntity entity = new StringEntity(s);    			
    			HttpPost post = new HttpPost(HttpClientUtil.URL_BASE+HttpClientUtil.URL_LOGOUT);
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
    				if(progressDialog.isShowing()){
    					progressDialog.dismiss();
    				}
    			} catch (IOException e) {
    				Log.e(TAG, "IOException : "+e);  
    				if(progressDialog.isShowing()){
    					progressDialog.dismiss();
    				}
    			} catch (Exception e) {
    				Log.e(TAG, "Exception : "+e);  
    				if(progressDialog.isShowing()){
    					progressDialog.dismiss();
    				}
    			}
           	return result;
           }

           @Override
           protected void onPostExecute(final Boolean success) {
        	   reqLogoutTask = null;          
               if (success) {
	               	if(!respString.isEmpty()){
	               		try {
	               			String respons = CipherUtil.decryptTripleDES(respString, CipherUtil.PASSWORD);
	               			MessageVO messageVO = HttpClientUtil.getObjectMapper(ctx).readValue(respons, MessageVO.class);		               	
		               		if(messageVO.getRc()==0){
		               			MessageUtils messageUtils = new MessageUtils(ctx);
				             	messageUtils.snackBarMessage(getActivity(),messageVO.getOtherMessage());	
				             	goToLoginActivity(ctx);
		               		}else{
		               			MessageUtils messageUtils = new MessageUtils(ctx);
				             	messageUtils.snackBarMessage(getActivity(),messageVO.getMessageRc());
				             	if(messageVO.getRc()==Constants.SESSION_EXPIRED){
				             		RedirectUtils redirectUtils = new RedirectUtils(ctx, getActivity());
				             		redirectUtils.redirectToLogin();
				             	}
		               		}
						} catch (Exception e) {
							MessageUtils messageUtils = new MessageUtils(ctx);
			             	messageUtils.snackBarMessage(getActivity(),ctx.getResources().getString(R.string.message_unexpected_error_message_server));
						}	            
	               	}else{
	               	   MessageUtils messageUtils = new MessageUtils(ctx);
	             	   messageUtils.snackBarMessage(getActivity(),ctx.getResources().getString(R.string.message_unexpected_error_server));
	               	}
               }else{
            	   MessageUtils messageUtils = new MessageUtils(ctx);
            	   messageUtils.snackBarMessage(getActivity(),ctx.getResources().getString(R.string.message_unexpected_error_server));
               }    
               if(progressDialog.isShowing()){
					progressDialog.dismiss();
				}
           }

       }
    
    private void goToLoginActivity(Context ctx)
    {

    	getActivity().getSupportFragmentManager().beginTransaction().remove(this).commit();
    	getActivity().finish();
        Intent intent = new Intent(ctx, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

}
