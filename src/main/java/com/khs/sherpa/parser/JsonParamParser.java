package com.khs.sherpa.parser;

import com.khs.sherpa.annotation.Param;
import com.khs.sherpa.json.service.JsonProvider;

public class JsonParamParser implements ParamParser<Object> {

	private JsonProvider jsonProvider;
	
	public boolean isValid(Class<?> clazz) {
		return clazz.isAssignableFrom(Object.class);
	}

	public Object parse(String value, Param annotation, Class<?> clazz) {
		return jsonProvider.toObject(value, clazz);
	}

	public void setJsonProvider(JsonProvider jsonProvider) {
		this.jsonProvider = jsonProvider;
	}
	
}
