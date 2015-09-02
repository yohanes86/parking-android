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
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.gc.materialdesign.views.ButtonRectangle;
import com.gc.materialdesign.widgets.ProgressDialogParking;
import com.parking.R;
import com.parking.data.Constants;
import com.parking.data.InqChangePasswordRequest;
import com.parking.data.MessageVO;
import com.parking.utils.CipherUtil;
import com.parking.utils.HttpClientUtil;
import com.parking.utils.MessageUtils;
import com.parking.utils.RedirectUtils;

public class RefreshingMallFragment extends Fragment{
	private static final String TAG = RefreshingMallFragment.class.getSimpleName();
	private Context ctx;
	private ReqRefreshListMallTask reqRefreshListMallTask = null;
	
	 @Override
	    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
	    	 View rootView = inflater.inflate(R.layout.activity_refreshing_list_mall, container, false);
	    	 ctx = container.getContext();
	    	 ButtonRectangle btnChangePassword = (ButtonRectangle) rootView.findViewById(R.id.btnRefreshMall);
	    	
	    	 btnChangePassword.setOnClickListener(new OnClickListener() {
	             @Override
	             public void onClick(View arg0) {	        	
	            	 reqRefreshListMallTask = new ReqRefreshListMallTask();
	            	 reqRefreshListMallTask.execute("");       
	             }
	         });
	        return rootView;
	    }
	 
	 public class ReqRefreshListMallTask extends AsyncTask<String, Void, Boolean> {	     
		 	private ProgressDialogParking progressDialog = null;
	       	private final HttpClient client = HttpClientUtil.getNewHttpClient();
	       	String respString = null;
	       	protected void onPreExecute() {	       			    			
	        			progressDialog = new ProgressDialogParking(ctx, ctx.getResources().getString(R.string.process_refresh_mall),ctx.getResources().getString(R.string.progress_dialog));
	        			progressDialog.show();
	    		}
	    		@Override
	           protected Boolean doInBackground(String... params) {
	           	boolean result = false;
	           	try {
	           		
	           		InqChangePasswordRequest inqChangePasswordRequest = new InqChangePasswordRequest();
					String s = HttpClientUtil.getObjectMapper(ctx).writeValueAsString(inqChangePasswordRequest);
					s = CipherUtil.encryptTripleDES(s, CipherUtil.PASSWORD);
	           		Log.d(TAG,"Request: " + s);
	                StringEntity entity = new StringEntity(s);    			
	    			HttpPost post = new HttpPost(HttpClientUtil.URL_BASE+HttpClientUtil.URL_REFRESHING_LIST_MALL);
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
	        	   reqRefreshListMallTask = null;          
	               if (success) {
		               	if(!respString.isEmpty()){
		               		try {
		               			String respons = CipherUtil.decryptTripleDES(respString, CipherUtil.PASSWORD);
		               			MessageVO messageVO = HttpClientUtil.getObjectMapper(ctx).readValue(respons, MessageVO.class);		               	
			               		if(messageVO.getRc()==0){
			               			MessageUtils messageUtils = new MessageUtils(ctx);
					             	messageUtils.snackBarMessage(getActivity(),messageVO.getOtherMessage());					             	
			               		}else{
			               			MessageUtils messageUtils = new MessageUtils(ctx);
					             	messageUtils.snackBarMessage(getActivity(),messageVO.getMessageRc());
					             	if(messageVO.getRc()==Constants.SESSION_EXPIRED||messageVO.getRc()==Constants.SESSION_DIFFERENT||messageVO.getRc()==Constants.USER_NOT_LOGIN){
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
}
