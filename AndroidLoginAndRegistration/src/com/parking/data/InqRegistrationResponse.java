package com.parking.data;

import com.parking.entity.UserData;


public class InqRegistrationResponse extends BaseResponse {

	private static final long serialVersionUID = 1L;
	
	private UserData userData;
	
	public UserData getUserData() {
		return userData;
	}
	public void setUserData(UserData userData) {
		this.userData = userData;
	}
	
}
