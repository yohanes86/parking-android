/*
 * Copyright (C) 2013 47 Degrees, LLC
 *  http://47deg.com
 *  hello@47deg.com
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.parking.swipelistview.sample.adapters;

import java.io.IOException;
import java.util.List;

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
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.MaterialDialog.Builder;
import com.fortysevendeg.swipelistview.SwipeListView;
import com.gc.materialdesign.views.ButtonRectangle;
import com.parking.R;
import com.parking.activity.InputCreditCardActivity;
import com.parking.data.Constants;
import com.parking.data.LoginData;
import com.parking.data.MessageVO;
import com.parking.data.SlotsParkingVO;
import com.parking.utils.CipherUtil;
import com.parking.utils.HttpClientUtil;
import com.parking.utils.MessageUtils;
import com.parking.utils.RedirectUtils;
import com.parking.utils.SharedPreferencesUtils;

public class MallAdapter extends BaseAdapter {
	private static final String TAG = MallAdapter.class.getSimpleName();
    private List<MallItem> data;
    private Context ctx;
    private Activity act;
    private LoginData login;
    private ReqSlotByMallTask reqSlotByMallTask = null;
    private ReqReleaseSlotByMallTask reqReleaseSlotByMallTask = null;

    public MallAdapter(Context context,Activity activity, List<MallItem> data) {
        this.ctx = context;
        this.data = data;
        this.act = activity;
    }
    
    public MallAdapter(Context context,Activity activity, List<MallItem> data,LoginData loginData) {
        this.ctx = context;
        this.data = data;
        this.act = activity;
        this.login = loginData;
    }
    
    public MallAdapter(Context context,List<MallItem> data) {
        this.ctx = context;
        this.data = data;        
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public MallItem getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

//    @Override
//    public boolean isEnabled(int position) {
//        if (position == 2) {
//            return false;
//        } else {
//            return true;
//        }
//    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final MallItem item = getItem(position);
        ViewHolder holder;
        if (convertView == null) {
            LayoutInflater li = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = li.inflate(R.layout.mall_row, parent, false);
            holder = new ViewHolder();
            holder.ivImage = (ImageView) convertView.findViewById(R.id.example_row_iv_image);
            holder.tvMallName = (TextView) convertView.findViewById(R.id.example_row_tv_title);
            holder.tvInformation = (TextView) convertView.findViewById(R.id.example_row_tv_description);
            holder.bAction1 = (ButtonRectangle) convertView.findViewById(R.id.example_row_b_action_1);
            holder.bAction2 = (ButtonRectangle) convertView.findViewById(R.id.example_row_b_action_2);
            
            if(login.getGroupUser().equalsIgnoreCase(Constants.USER)){
            	holder.bAction2.setVisibility(View.GONE);
            }else if(login.getGroupUser().equalsIgnoreCase(Constants.STAFF)){
            	holder.bAction2.setVisibility(View.VISIBLE);
            	holder.bAction1.setVisibility(View.GONE);
            }else if(login.getGroupUser().equalsIgnoreCase(Constants.ADMIN)){
            	holder.bAction2.setVisibility(View.VISIBLE);
            	holder.bAction1.setVisibility(View.VISIBLE);
            }
            
//            holder.bAction3 = (ButtonRectangle) convertView.findViewById(R.id.example_row_b_action_3);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        ((SwipeListView)parent).recycle(convertView, position);

        holder.ivImage.setImageDrawable(item.getIcon());
        holder.tvMallName.setText(item.getName());
        holder.tvInformation.setText(item.getInformation());


        holder.bAction1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = context.getPackageManager().getLaunchIntentForPackage(item.getInformation());
//                if (intent != null) {
//                    context.startActivity(intent);
//                } else {
//                    Toast.makeText(context, R.string.cantOpen, Toast.LENGTH_SHORT).show();
//                }
//            	Intent i = new Intent(ctx, InputCreditCardActivity.class);            	
//            	i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);  
//            	i.putExtra("mallName", item.getName());
//            	ctx.startActivity(i);      
            	
            	reqSlotByMallTask = new ReqSlotByMallTask();
            	reqSlotByMallTask.execute(item.getName());
            }
        });

        holder.bAction2.setOnClickListener(new View.OnClickListener() {
        	 @Override
             public void onClick(View v) {

        		 reqReleaseSlotByMallTask = new ReqReleaseSlotByMallTask();
        		 reqReleaseSlotByMallTask.execute(item.getName());
             }
        });

//        holder.bAction3.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Uri packageUri = Uri.parse("package:" + item.getPackageName());
//                Intent uninstallIntent;
//                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
//                    uninstallIntent = new Intent(Intent.ACTION_DELETE, packageUri);
//                } else {
//                    uninstallIntent = new Intent(Intent.ACTION_UNINSTALL_PACKAGE, packageUri);
//                }
//                context.startActivity(uninstallIntent);
//            }
//        });


        return convertView;
    }

    static class ViewHolder {
        ImageView ivImage;
        TextView tvMallName;
        TextView tvInformation;
        ButtonRectangle bAction1;
        ButtonRectangle bAction2;
//        ButtonRectangle bAction3;
    }

    private boolean isPlayStoreInstalled() {
        Intent market = new Intent(Intent.ACTION_VIEW, Uri.parse("market://search?q=dummy"));
        PackageManager manager = ctx.getPackageManager();
        List<ResolveInfo> list = manager.queryIntentActivities(market, 0);

        return list.size() > 0;
    }
    
    private void goToPayScreen(String mallName, long hargaParkir,String slotName,String bookingId) {
    	Intent i = new Intent(ctx, InputCreditCardActivity.class);            	
    	i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);  
    	i.putExtra("mallName", mallName);
    	i.putExtra("hargaParkir", hargaParkir);
    	i.putExtra("slotName", slotName);
    	i.putExtra("bookingId", bookingId);
    	ctx.startActivity(i);
    }
    
    public class ReqSlotByMallTask extends AsyncTask<String, Void, Boolean> {
       	private Builder materialDialog = null;
       	private final HttpClient client = HttpClientUtil.getNewHttpClient();
       	String respString = null;
       	protected void onPreExecute() {       		
    			materialDialog = new MaterialDialog.Builder(ctx).title(ctx.getResources().getString(R.string.progress_dialog))
                        .content(R.string.process_find_slots)
                        .progress(true, 0)
                        .progressIndeterminateStyle(false);
        			materialDialog.show();
    		}
    		@Override
           protected Boolean doInBackground(String... params) {
           	boolean result = false;
           	try {
           		LoginData loginData = SharedPreferencesUtils.getLoginData(ctx); 
           		SlotsParkingVO slotsParkingVO = new SlotsParkingVO();        
           		slotsParkingVO.setEmail(loginData.getEmail());  
           		slotsParkingVO.setMallName(params[0]);
           		slotsParkingVO.setSessionKey(loginData.getSessionKey());
				String s = HttpClientUtil.getObjectMapper(ctx).writeValueAsString(slotsParkingVO);
				s = CipherUtil.encryptTripleDES(s, CipherUtil.PASSWORD);
           		Log.d(TAG,"Request: " + s);
                StringEntity entity = new StringEntity(s);    			
    			HttpPost post = new HttpPost(HttpClientUtil.URL_BASE+HttpClientUtil.URL_FIND_SLOT_BY_MALL);
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
    			} catch (IOException e) {
    				Log.e(TAG, "IOException : "+e);    					
    			} catch (Exception e) {
    				Log.e(TAG, "Exception : "+e);    							
    			}
           	return result;
           }

           @Override
           protected void onPostExecute(final Boolean success) {
        	   reqSlotByMallTask = null;          
               if (success) {
	               	if(!respString.isEmpty()){
	               		try {
	               			String respons = CipherUtil.decryptTripleDES(respString, CipherUtil.PASSWORD);
	               			MessageVO messageVO = HttpClientUtil.getObjectMapper(ctx).readValue(respons, MessageVO.class);		               	
		               		if(messageVO.getRc()==0){
				             	SlotsParkingVO slotsParkingVO = HttpClientUtil.getObjectMapper(ctx).readValue(messageVO.getOtherMessage(), SlotsParkingVO.class);
				             	goToPayScreen(slotsParkingVO.getMallName(), slotsParkingVO.getSlotsPrice(),slotsParkingVO.getSlotsName(),slotsParkingVO.getBookingId());
		               		}else{
		               			MessageUtils messageUtils = new MessageUtils(ctx);
				             	messageUtils.snackBarMessage(act,messageVO.getMessageRc());		
				             	if(messageVO.getRc()==Constants.SESSION_EXPIRED){
				             		RedirectUtils redirectUtils = new RedirectUtils(ctx, act);
				             		redirectUtils.redirectToLogin();
				             	}
		               		}
						} catch (Exception e) {
							MessageUtils messageUtils = new MessageUtils(ctx);
			             	messageUtils.snackBarMessage(act,ctx.getResources().getString(R.string.message_unexpected_error_message_server));
						}	            
	               	}else{
	               	   MessageUtils messageUtils = new MessageUtils(ctx);
	             	   messageUtils.snackBarMessage(act,ctx.getResources().getString(R.string.message_unexpected_error_server));
	               	}
               }else{
            	   MessageUtils messageUtils = new MessageUtils(ctx);
            	   messageUtils.snackBarMessage(act,ctx.getResources().getString(R.string.message_unexpected_error_server));
               }               
           }

       }
    
    
    public class ReqReleaseSlotByMallTask extends AsyncTask<String, Void, Boolean> {
       	private Builder materialDialog = null;
       	private final HttpClient client = HttpClientUtil.getNewHttpClient();
       	String respString = null;
       	protected void onPreExecute() {       		
    			materialDialog = new MaterialDialog.Builder(ctx).title(ctx.getResources().getString(R.string.progress_dialog))
                        .content(R.string.process_find_slots)
                        .progress(true, 0)
                        .progressIndeterminateStyle(false);
        			materialDialog.show();
    		}
    		@Override
           protected Boolean doInBackground(String... params) {
           	boolean result = false;
           	try {
           		LoginData loginData = SharedPreferencesUtils.getLoginData(ctx); 
           		SlotsParkingVO slotsParkingVO = new SlotsParkingVO();        
           		slotsParkingVO.setEmail(loginData.getEmail());  
           		slotsParkingVO.setMallName(params[0]);
           		slotsParkingVO.setSessionKey(loginData.getSessionKey());
				String s = HttpClientUtil.getObjectMapper(ctx).writeValueAsString(slotsParkingVO);
				s = CipherUtil.encryptTripleDES(s, CipherUtil.PASSWORD);
           		Log.d(TAG,"Request: " + s);
                StringEntity entity = new StringEntity(s);    			
    			HttpPost post = new HttpPost(HttpClientUtil.URL_BASE+HttpClientUtil.URL_RELEASE_SLOT_PARKING);
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
    			} catch (IOException e) {
    				Log.e(TAG, "IOException : "+e);    					
    			} catch (Exception e) {
    				Log.e(TAG, "Exception : "+e);    								
    			}
           	return result;
           }

           @Override
           protected void onPostExecute(final Boolean success) {
        	   reqSlotByMallTask = null;          
               if (success) {
	               	if(!respString.isEmpty()){
	               		try {
	               			String respons = CipherUtil.decryptTripleDES(respString, CipherUtil.PASSWORD);
	               			MessageVO messageVO = HttpClientUtil.getObjectMapper(ctx).readValue(respons, MessageVO.class);		               	
		               		if(messageVO.getRc()==0){
		               			MessageUtils messageUtils = new MessageUtils(ctx);
				             	messageUtils.snackBarMessage(act,messageVO.getOtherMessage());
		               		}else{
		               			MessageUtils messageUtils = new MessageUtils(ctx);
				             	messageUtils.snackBarMessage(act,messageVO.getMessageRc());		
				             	if(messageVO.getRc()==Constants.SESSION_EXPIRED){
				             		RedirectUtils redirectUtils = new RedirectUtils(ctx, act);
				             		redirectUtils.redirectToLogin();
				             	}
		               		}
						} catch (Exception e) {
							MessageUtils messageUtils = new MessageUtils(ctx);
			             	messageUtils.snackBarMessage(act,ctx.getResources().getString(R.string.message_unexpected_error_message_server));
						}	            
	               	}else{
	               	   MessageUtils messageUtils = new MessageUtils(ctx);
	             	   messageUtils.snackBarMessage(act,ctx.getResources().getString(R.string.message_unexpected_error_server));
	               	}
               }else{
            	   MessageUtils messageUtils = new MessageUtils(ctx);
            	   messageUtils.snackBarMessage(act,ctx.getResources().getString(R.string.message_unexpected_error_server));
               }               
           }

       }

}
