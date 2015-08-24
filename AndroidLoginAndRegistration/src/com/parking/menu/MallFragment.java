package com.parking.menu;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.type.TypeReference;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;

import com.fortysevendeg.swipelistview.BaseSwipeListViewListener;
import com.fortysevendeg.swipelistview.SwipeListView;
import com.parking.R;
import com.parking.data.Constants;
import com.parking.data.InqGetMallRequest;
import com.parking.data.LoginData;
import com.parking.data.MessageVO;
import com.parking.data.SettingData;
import com.parking.entity.Mall;
import com.parking.swipelistview.sample.adapters.MallAdapter;
import com.parking.swipelistview.sample.adapters.MallItem;
import com.parking.swipelistview.sample.dialogs.UsageTipsDialog;
import com.parking.swipelistview.sample.utils.PreferencesManager;
import com.parking.swipelistview.sample.utils.SettingsManager;
import com.parking.utils.CipherUtil;
import com.parking.utils.HttpClientUtil;
import com.parking.utils.ImageUtil;
import com.parking.utils.MessageUtils;
import com.parking.utils.SharedPreferencesUtils;

/**
 * User: special
 * Date: 13-12-22
 * Time: ä¸‹å?ˆ3:26
 * Mail: specialcyci@gmail.com
 */
public class MallFragment extends Fragment {
	private static final String TAG = MallFragment.class.getSimpleName();
	private static final int REQUEST_CODE_SETTINGS = 0;
    private MallAdapter adapter;
    private List<MallItem> data;
    private Context ctx;
    private SwipeListView swipeListView;
    private ReqGetMallTask reqGetMallTask = null;
    private ProgressDialog progressDialog;
    private String email;
	private String sessionkey;
    private View parentView;
    private ListView listView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        parentView = inflater.inflate(R.layout.swipe_list_view_activity, container, false);
//        listView   = (ListView) parentView.findViewById(R.id.listView);
//        initView();
        ctx = container.getContext();
        LoginData loginData = SharedPreferencesUtils.getLoginData(ctx);            	 
    	// ambil dari session untuk email, session key
        email = loginData.getEmail();
        sessionkey = loginData.getSessionKey();
        data = new ArrayList<MallItem>();

        adapter = new MallAdapter(ctx, data);

        swipeListView = (SwipeListView) parentView.findViewById(R.id.example_lv_list);

        swipeListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            swipeListView.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {

                @Override
                public void onItemCheckedStateChanged(ActionMode mode, int position,
                                                      long id, boolean checked) {
                    mode.setTitle("Selected (" + swipeListView.getCountSelected() + ")");
                }

                @Override
                public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.menu_delete:
                            swipeListView.dismissSelected();
                            mode.finish();
                            return true;
                        default:
                            return false;
                    }
                }

                @Override
                public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                    MenuInflater inflater = mode.getMenuInflater();
                    inflater.inflate(R.menu.menu_choice_items, menu);
                    return true;
                }

                @Override
                public void onDestroyActionMode(ActionMode mode) {
                    swipeListView.unselectedChoiceStates();
                }

                @Override
                public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                    return false;
                }
            });
        }

        swipeListView.setSwipeListViewListener(new BaseSwipeListViewListener() {
            @Override
            public void onOpened(int position, boolean toRight) {
            }

            @Override
            public void onClosed(int position, boolean fromRight) {
            }

            @Override
            public void onListChanged() {
            }

            @Override
            public void onMove(int position, float x) {
            }

            @Override
            public void onStartOpen(int position, int action, boolean right) {
                Log.d("swipe", String.format("onStartOpen %d - action %d", position, action));
            }

            @Override
            public void onStartClose(int position, boolean right) {
                Log.d("swipe", String.format("onStartClose %d", position));
            }

            @Override
            public void onClickFrontView(int position) {
                Log.d("swipe", String.format("onClickFrontView %d", position));
            }

            @Override
            public void onClickBackView(int position) {
                Log.d("swipe", String.format("onClickBackView %d", position));
            }

            @Override
            public void onDismiss(int[] reverseSortedPositions) {
                for (int position : reverseSortedPositions) {
                    data.remove(position);
                }
                adapter.notifyDataSetChanged();
            }

        });

        swipeListView.setAdapter(adapter);

        reload();
        
//        new ListAppTask().execute();
        reqGetMallTask = new ReqGetMallTask();
        reqGetMallTask.execute("");    

//        progressDialog = new ProgressDialog(ctx);
//        progressDialog.setMessage(getString(R.string.loading));
//        progressDialog.setCancelable(false);
//        progressDialog.show();
        
        return parentView;
    }
    
    private void reload() {
//        SettingsManager settings = SettingsManager.getInstance();
//        swipeListView.setSwipeMode(settings.getSwipeMode());
//        swipeListView.setSwipeActionLeft(settings.getSwipeActionLeft());
//        swipeListView.setSwipeActionRight(settings.getSwipeActionRight());
//        swipeListView.setOffsetLeft(convertDpToPixel(settings.getSwipeOffsetLeft()));
//        swipeListView.setOffsetRight(convertDpToPixel(settings.getSwipeOffsetRight()));
//        swipeListView.setAnimationTime(settings.getSwipeAnimationTime());
//        swipeListView.setSwipeOpenOnLongPress(settings.isSwipeOpenOnLongPress());
    	SettingsManager settings = SettingsManager.getInstance();
        swipeListView.setSwipeMode(SwipeListView.SWIPE_MODE_RIGHT);
        swipeListView.setSwipeActionLeft(settings.getSwipeActionLeft());
        swipeListView.setSwipeActionRight(settings.getSwipeActionRight());
        swipeListView.setOffsetLeft(convertDpToPixel(settings.getSwipeOffsetLeft()));
        swipeListView.setOffsetRight(convertDpToPixel(settings.getSwipeOffsetRight()));
        swipeListView.setAnimationTime(750);
        swipeListView.setSwipeOpenOnLongPress(settings.isSwipeOpenOnLongPress());
    }

    public int convertDpToPixel(float dp) {
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        float px = dp * (metrics.densityDpi / 160f);
        return (int) px;
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        MenuInflater inflater = getMenuInflater();
//        inflater.inflate(R.menu.menu_app, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onMenuItemSelected(int featureId, MenuItem item) {
//        boolean handled = false;
//        switch (item.getItemId()) {
//            case android.R.id.home: //Actionbar home/up icon
//                finish();
//                break;
//            case R.id.menu_settings:
//                Intent intent = new Intent(this, SettingsActivity.class);
//                startActivityForResult(intent, REQUEST_CODE_SETTINGS);
//                break;
//        }
//        return handled;
//    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CODE_SETTINGS:
                reload();
        }
    }
    
    public class ReqGetMallTask extends AsyncTask<String, Void, Boolean> {
       	private ProgressDialog dialog = new ProgressDialog(ctx);
       	private final HttpClient client = HttpClientUtil.getNewHttpClient();
       	String respString = null;
       	protected void onPreExecute() {
       		dialog = new ProgressDialog(ctx);
    			dialog.setIndeterminate(true);
    			dialog.setCancelable(true);
    			dialog.setMessage(ctx.getResources().getString(R.string.process_get_list_mall));
    			dialog.show();
    		}
    		@Override
           protected Boolean doInBackground(String... params) {
           	boolean result = false;
           	try {
           		
           		InqGetMallRequest inqGetMallRequest = new InqGetMallRequest();
           		inqGetMallRequest.setEmail(email);           		
           		inqGetMallRequest.setSessionKey(sessionkey);
				String s = HttpClientUtil.getObjectMapper(ctx).writeValueAsString(inqGetMallRequest);
				s = CipherUtil.encryptTripleDES(s, CipherUtil.PASSWORD);
           		Log.d(TAG,"Request: " + s);
                StringEntity entity = new StringEntity(s);    			
    			HttpPost post = new HttpPost(HttpClientUtil.URL_BASE+HttpClientUtil.URL_GET_ALL_MALL);
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
        	   reqGetMallTask = null;          
               if (success) {
	               	if(!respString.isEmpty()){
	               		try {
	               			String respons = CipherUtil.decryptTripleDES(respString, CipherUtil.PASSWORD);
	               			MessageVO messageVO = HttpClientUtil.getObjectMapper(ctx).readValue(respons, MessageVO.class);		               	
		               		if(messageVO.getRc()==0){		               					               				
		               			List<Mall> listMall = HttpClientUtil.getObjectMapper(ctx).readValue(messageVO.getOtherMessage(), new TypeReference<List<Mall>>(){});
		               			List<MallItem> result = new ArrayList<MallItem>();
		               			for (Mall mall : listMall) {
		               				MallItem item = new MallItem();
		                            item.setName(mall.getMallName());
		                            item.setInformation(mall.getMallAddress()+" - " + mall.getMallPhone());                            
		                            item.setIcon(ctx.getResources().getDrawable(R.drawable.default_image)); // default image
//		                            item.setIcon(ctx.getPackageManager().getDrawable(content.packageName, content.icon, content));
		                            try {
		                            	if(!mall.getMallImage().isEmpty()){
			                            	item.setIcon(ImageUtil.getImage(ctx, mall.getMallImage()));
			                            }
									} catch (Exception e) {
										// TODO: handle exception
									}		                            		                            
		                            result.add(item);
								}
		               			data.clear();
		                        data.addAll(result);
		                        adapter.notifyDataSetChanged();
		                        if (progressDialog != null) {
		                            progressDialog.dismiss();
		                            progressDialog = null;
		                        }
		                        setupTipsDialog();
		               		}else{
		               			MessageUtils messageUtils = new MessageUtils(ctx);
				             	messageUtils.messageLong(messageVO.getMessageRc());
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
    
    private void setupTipsDialog(){
    	SettingData settingData = SharedPreferencesUtils.getSettingData(ctx, email);
        if(settingData!=null){
        	if(Constants.SHOW_TIPS > settingData.getShowTips()){
        		if (PreferencesManager.getInstance(ctx).getShowAbout()) {
                    UsageTipsDialog usageTipsDialog = new UsageTipsDialog();
                    usageTipsDialog.show(getActivity().getSupportFragmentManager(), "dialog");
                    settingData.setShowTips(settingData.getShowTips()+1);
                    try {
                    	SharedPreferencesUtils.saveSettingData(HttpClientUtil.getObjectMapper(ctx).writeValueAsString(settingData), ctx, email);
                    } catch (JsonGenerationException e) {
            			Log.e(TAG, "JsonGenerationException  saveSettingData: " + e);	
            		} catch (JsonMappingException e) {
            			Log.e(TAG, "JsonMappingException saveSettingData: " + e);			
            		} catch (IOException e) {
            			Log.e(TAG, "IOException saveSettingData: " + e);
            		}                        
                }
        	}
        }else{
        	settingData = new SettingData();
        	if (PreferencesManager.getInstance(ctx).getShowAbout()) {
                UsageTipsDialog usageTipsDialog = new UsageTipsDialog();
                usageTipsDialog.show(getActivity().getSupportFragmentManager(), "dialog");
                settingData.setShowTips(settingData.getShowTips()+1);
                try {
                	SharedPreferencesUtils.saveSettingData(HttpClientUtil.getObjectMapper(ctx).writeValueAsString(settingData), ctx, email);
                } catch (JsonGenerationException e) {
        			Log.e(TAG, "JsonGenerationException  saveSettingData: " + e);	
        		} catch (JsonMappingException e) {
        			Log.e(TAG, "JsonMappingException saveSettingData: " + e);			
        		} catch (IOException e) {
        			Log.e(TAG, "IOException saveSettingData: " + e);
        		}                        
            }
        }
    }

}
