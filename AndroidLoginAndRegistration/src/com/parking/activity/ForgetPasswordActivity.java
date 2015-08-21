package com.parking.activity;

import com.parking.data.InqForgotPasswordRequest;
import com.parking.data.InqRegistrationRequest;

import info.androidhive.loginandregistration.R;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class ForgetPasswordActivity extends Activity {
	private static final String TAG = ForgetPasswordActivity.class.getSimpleName();
	private Button btnRegister;
	private Button btnLinkToLogin;
	private EditText inputEmail;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_forget_password);

		
		inputEmail = (EditText) findViewById(R.id.email);
		btnRegister = (Button) findViewById(R.id.btnRegister);
		btnLinkToLogin = (Button) findViewById(R.id.btnLinkToLoginScreen);

		

		// Register Button Click event
		btnRegister.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				String email = inputEmail.getText().toString();
				if (!email.isEmpty()) {
					InqForgotPasswordRequest inqForgotPasswordRequest = new InqForgotPasswordRequest();
					inqForgotPasswordRequest.setEmail(email);
				} else {
					Toast.makeText(getApplicationContext(),
							"Email Harus Diisi!", Toast.LENGTH_LONG).show();
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

}
