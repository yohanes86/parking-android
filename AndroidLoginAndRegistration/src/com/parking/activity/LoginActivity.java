package com.parking.activity;

import com.parking.R;


public class LoginActivity extends Activity {
	// LogCat tag
	private static final String TAG = RegisterActivity.class.getSimpleName();
	private Button btnLogin;
	private Button btnLinkToRegister;
	private Button btnLinkToForgetPassword;
	private EditText inputEmail;
	private EditText inputPassword;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

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
					// login user
//					SendHttpRequestLoginTask.execute("hh");
					
				} else {
					// Prompt user to enter credentials
					Toast.makeText(getApplicationContext(),
							"Please enter the credentials!", Toast.LENGTH_LONG).show();
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
	
	
	private class SendHttpRequestLoginTask  extends AsyncTask<String, Void, Void> {

		@Override
		protected Void doInBackground(String... arg0) {
			// TODO Auto-generated method stub
			return null;
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
