package com.khs.sherpa.json.service;

import com.google.gson.Gson;

public class GsonJsonProvider implements JsonProvider {

	public String toJson(Object object) {
		Gson gson = new Gson();
		return gson.toJson(object);
	}

	public <T> T toObject(String json, Class<T> type) {
		Gson gson = new Gson();
		return gson.fromJson(json, type);
	}
	
}
