package com.khs.sherpa.json.service;

public interface JsonProvider {

	public String toJson(Object object);
	
	public <T> T toObject(String json, Class<T> type);
}
