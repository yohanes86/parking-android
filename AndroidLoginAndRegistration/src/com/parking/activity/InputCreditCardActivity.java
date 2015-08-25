package com.parking.activity;


import id.co.veritrans.android.api.VTDirect;
import id.co.veritrans.android.api.VTInterface.ITokenCallback;
import id.co.veritrans.android.api.VTModel.VTCardDetails;
import id.co.veritrans.android.api.VTModel.VTToken;
import id.co.veritrans.android.api.VTUtil.VTConfig;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.parking.R;
import com.parking.data.Address;
import com.parking.data.CustomerDetail;
import com.parking.data.InqCreditCardRequest;
import com.parking.data.LoginData;
import com.parking.data.MessageVO;
import com.parking.data.Product;
import com.parking.data.TransactionDetails;
import com.parking.data.VeriTransVO;
import com.parking.utils.CipherUtil;
import com.parking.utils.HttpClientUtil;
import com.parking.utils.MessageUtils;
import com.parking.utils.SharedPreferencesUtils;
import com.parking.view.CustomWebView;


public class InputCreditCardActivity extends Activity {
	private static final String TAG = InputCreditCardActivity.class.getSimpleName();
	private Button btnPay;
	private EditText noCC;
	private EditText cardExpireMonth;
	private EditText cardExpireYear;
	private EditText cardCvv;
	private TextView paymentFor;
	private TextView total;
	private TextView txtSlotName;
	
	private ProgressDialog pDialog;
	private Context ctx;
	private String mallName;
	private String slotName;
	private String bookingId;
	AlertDialog dialog3ds;
    ProgressDialog sendServerProgress;
    int totalPrice;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_credit_card);
		ctx = InputCreditCardActivity.this;
		paymentFor = (TextView) findViewById(R.id.paymentFor);
		txtSlotName = (TextView) findViewById(R.id.txtSlotName);
		total = (TextView) findViewById(R.id.total);
		noCC = (EditText) findViewById(R.id.noCC);
		cardExpireMonth = (EditText) findViewById(R.id.card_expire_month);
		cardCvv = (EditText) findViewById(R.id.card_cvv);
		cardExpireYear = (EditText) findViewById(R.id.card_expire_year);
		btnPay = (Button) findViewById(R.id.btnPay);
		Intent intent = getIntent();
		mallName = intent.getStringExtra("mallName");
		slotName = intent.getStringExtra("slotName");
		totalPrice = intent.getIntExtra("hargaParkir", 10000);
		// Progress dialog
		pDialog = new ProgressDialog(this);
		pDialog.setCancelable(false);
		
		paymentFor.setText("V-Mobile "+ mallName);
		total.setText("GRAND TOTAL: "+ totalPrice);
		txtSlotName.setText("Area name : " + slotName);
		bookingId = intent.getStringExtra("bookingId");
		
		btnPay.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				String noCCInput = noCC.getText().toString();
				String cardExpireMonthInput = cardExpireMonth.getText().toString();
				String cardExpireYearInput = cardExpireYear.getText().toString();				
				String cardCvvInput = cardCvv.getText().toString();
								

				if (!noCCInput.isEmpty() && !cardExpireMonthInput.isEmpty() && !cardExpireYearInput.isEmpty()&& !cardCvvInput.isEmpty()) {
					InqCreditCardRequest inqCreditCardRequest = new InqCreditCardRequest();
					inqCreditCardRequest.setNoCC(noCCInput);
					inqCreditCardRequest.setCardExpireMonth(Integer.parseInt(cardExpireMonthInput));
					inqCreditCardRequest.setCardExpireYear(Integer.parseInt(cardExpireYearInput));
					inqCreditCardRequest.setCardCvv(cardCvvInput);					
					inqCreditCardRequest.setGross_amount(Integer.toString(totalPrice));					
					//set environment
	                VTConfig.VT_IsProduction = false;
	                //set client key
	                VTConfig.CLIENT_KEY = HttpClientUtil.CLIENT_KEY; //agus	                

	                VTDirect vtDirect = new VTDirect();
	                //set using 3dsecure or not	             
	                VTCardDetails cardDetails = null;
	                
	                cardDetails = CardFactory(true,inqCreditCardRequest);
	               
	                vtDirect.setCard_details(cardDetails);

	                //set loading dialog
	                final ProgressDialog loadingDialog = ProgressDialog.show(ctx,"","Authenticating credit card...",true);
	                vtDirect.getToken(new ITokenCallback() {
	                    @Override
	                    public void onSuccess(VTToken token) {
	                        loadingDialog.cancel();
	                        if(token.getRedirect_url() != null){
	                            //using 3d secure
	                            //show it to user using webview
	                            Log.d("VtLog",token.getToken_id());

	                            CustomWebView webView = new CustomWebView(ctx);
	                            webView.getSettings().setJavaScriptEnabled(true);
	                            webView.setOnTouchListener(new View.OnTouchListener() {
	                                @Override
	                                public boolean onTouch(View v, MotionEvent event) {
	                                    switch (event.getAction()) {
	                                        case MotionEvent.ACTION_DOWN:
	                                        case MotionEvent.ACTION_UP:
	                                            if (!v.hasFocus()) {
	                                                v.requestFocus();
	                                            }
	                                            break;
	                                    }
	                                    return false;
	                                }
	                            });
	                            webView.setWebChromeClient(new WebChromeClient());
	                            webView.setWebViewClient(new VtWebViewClient(token.getToken_id(),totalPrice+""));
	                            webView.loadUrl(token.getRedirect_url());

	                            AlertDialog.Builder alertBuilder = new AlertDialog.Builder(ctx);
	                            dialog3ds = alertBuilder.create();


	                            dialog3ds.setTitle("3D Secure");
	                            dialog3ds.setView(webView);
	                            webView.requestFocus(View.FOCUS_DOWN);
	                            alertBuilder.setNegativeButton("Close", new DialogInterface.OnClickListener() {
	                                @Override
	                                public void onClick(DialogInterface dialog, int id) {
	                                    dialog.dismiss();
	                                }
	                            });

	                            dialog3ds.show();

	                        }
//	                        //print token
//	                        TextView tokenText = (TextView) getView().findViewById(R.id.txt_token);
//	                        tokenText.setText(token.getToken_id());

	                    }

	                    @Override
	                    public void onError(Exception e) {
	                        loadingDialog.cancel();
	                        Toast.makeText(ctx,e.getMessage(),Toast.LENGTH_SHORT);
	                    }
	                });
					
				} else {
					MessageUtils messageUtils = new MessageUtils(ctx);
	             	messageUtils.messageLong(ctx.getResources().getString(R.string.message_detail_required));
				}
			}
		});

	}
	
	private class SendTokenAsync extends AsyncTask<String, Void, Boolean>{		
		private final HttpClient client = HttpClientUtil.getNewHttpClient();
       	String respString = null;       	   		
		
		@Override
		protected Boolean doInBackground(String... params) {
           	boolean result = false;
           	try {
           		LoginData loginData = SharedPreferencesUtils.getLoginData(ctx);            	 
           		// ambil dari session untuk email, session key	         	
           		VeriTransVO veriTransVO = new VeriTransVO();
           		veriTransVO.setBookingId(bookingId);
    			veriTransVO.setEmail(loginData.getEmail());
    			veriTransVO.setSessionKey(loginData.getSessionKey());
    			veriTransVO.setTotalPriceIdr(new Long(params[1]));
    			veriTransVO.setTokenId(URLEncoder.encode(params[0],"UTF-8"));
    			veriTransVO.setPaymentMethod("Credit Card");
    			CustomerDetail customerDetail = new CustomerDetail();
    			customerDetail.setFirstName(loginData.getName());
    			customerDetail.setLastName(loginData.getName());
    			customerDetail.setEmail(loginData.getEmail());
    			customerDetail.setPhone(loginData.getPhoneNo());
    			Address billAddress = new Address();
    			billAddress.setFirstName(loginData.getName());
    			billAddress.setLastName(loginData.getName());
    			billAddress.setAddress("");
    			billAddress.setCity("");
    			billAddress.setPhone(loginData.getPhoneNo());
    			billAddress.setPostalCode("15210");
    			
    			Address shipAddress = new Address();
    			shipAddress.setFirstName(loginData.getName());
    			shipAddress.setLastName(loginData.getName());
    			shipAddress.setAddress("");
    			shipAddress.setCity("");
    			shipAddress.setPhone(loginData.getPhoneNo());
    			shipAddress.setPostalCode("15210");
    			customerDetail.setBillingAddress(billAddress);
    			customerDetail.setShippingAddress(shipAddress);
    			
    			TransactionDetails transactionDetails = new  TransactionDetails();
    			transactionDetails.setOrderId(UUID.randomUUID().toString());
    			transactionDetails.setGrossAmount(new Long(totalPrice));
    			
    			Product product = new Product();
    			product.setId(new Long(1));
    			product.setLongName("Parking Online "+ mallName);
    			product.setPriceIdr(new Long(totalPrice));
    			product.setShortName("V-Mobile "+ mallName);
    			product.setThumbnailFilePath("");
    			List<Product> listProducts = new ArrayList<Product>();
    			listProducts.add(product);
    			veriTransVO.setCustomerDetail(customerDetail);
    			veriTransVO.setTransactionDetails(transactionDetails);
    			veriTransVO.setListProducts(listProducts);
				String s = HttpClientUtil.getObjectMapper(ctx).writeValueAsString(veriTransVO);
				s = CipherUtil.encryptTripleDES(s, CipherUtil.PASSWORD);
//           		Log.d(TAG,"Request: " + s);
                StringEntity entity = new StringEntity(s);    			
    			HttpPost post = new HttpPost(HttpClientUtil.URL_BASE+HttpClientUtil.URL_RECEIVE_TRX_VERITRANS);
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
            if (success) {
	               	if(!respString.isEmpty()){
	               		try {
	               			String respons = CipherUtil.decryptTripleDES(respString, CipherUtil.PASSWORD);
	               			MessageVO messageVO = HttpClientUtil.getObjectMapper(ctx).readValue(respons, MessageVO.class);		               	
		               		if(messageVO.getRc()==0){
		               			MessageUtils messageUtils = new MessageUtils(ctx);
				             	messageUtils.messageLong(messageVO.getOtherMessage());	
				             	finish();
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
            if (sendServerProgress.isShowing()) {
            	try
                {
            		sendServerProgress.dismiss();
                }catch(Exception e1) {
                	// nothing
                }
            }
            
            
        }
    }
	
	private class VtWebViewClient extends WebViewClient {

        String token;
        String price;

        public VtWebViewClient(String token, String price){
            this.token = token;
            this.price = price;
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);

            Log.d("VtLog", url);

            if (url.startsWith(HttpClientUtil.getPaymentApiUrl() + "/callback/")) {
                //send token to server
                SendTokenAsync sendTokenAsync = new SendTokenAsync();
                sendTokenAsync.execute(token,price);
                //close web dialog
                dialog3ds.dismiss();
                //show loading dialog
                sendServerProgress = ProgressDialog.show(ctx,"",ctx.getResources().getString(R.string.process_verification_to_server),true);

            } else if (url.startsWith(HttpClientUtil.getPaymentApiUrl() + "/redirect/") || url.contains("3dsecure")) {
                /* Do nothing */
            } else {
                if(dialog3ds != null){
                    dialog3ds.dismiss();
                }
            }
        }

    }
	
	private VTCardDetails CardFactory(boolean secure,InqCreditCardRequest inqCreditCardRequest){
        VTCardDetails cardDetails = new VTCardDetails();
//        cardDetails.setCard_number("4811111111111114");
//        cardDetails.setCard_cvv("123");
//        cardDetails.setCard_exp_month(12);
//        cardDetails.setCard_exp_year(2020);
//        cardDetails.setSecure(secure);
//        cardDetails.setGross_amount(Integer.toString(totalPrice));
        cardDetails.setCard_number(inqCreditCardRequest.getNoCC());
        cardDetails.setCard_cvv(inqCreditCardRequest.getCardCvv());
        cardDetails.setCard_exp_month(inqCreditCardRequest.getCardExpireMonth());
        cardDetails.setCard_exp_year(inqCreditCardRequest.getCardExpireYear());
        cardDetails.setSecure(secure);
        cardDetails.setGross_amount(inqCreditCardRequest.getGross_amount());
        return cardDetails;
    }
	

}
