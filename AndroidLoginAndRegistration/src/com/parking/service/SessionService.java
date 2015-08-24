package com.parking.service;

import java.util.HashMap;
import java.util.Map;

public class SessionService {
	
	private static SessionService _session;
	
	private Map<String, String> data;
	
	
	private SessionService() {
		data = new HashMap<String, String>();
	}
	
	public static SessionService getInstance() {
		if (_session == null)
			_session = new SessionService();
		return _session;
	}
	
	public void setData(String key, String value) {
		data.put(key, value);
	}
	
	public String getData(String key) {
		return data.get(key);
	}
	
	public void clear() {
		data.clear();
	}

	
	
}
