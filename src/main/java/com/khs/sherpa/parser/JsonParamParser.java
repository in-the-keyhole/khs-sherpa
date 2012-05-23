package com.khs.sherpa.parser;

import java.io.IOException;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import com.khs.sherpa.annotation.Param;

public class JsonParamParser implements ParamParser<Object> {

	public boolean isValid(Class<?> clazz) {
		return clazz.isAssignableFrom(Object.class);
	}

	public Object parse(String value, Param annotation, Class<?> clazz) {
		// map to json object
		ObjectMapper mapper = new ObjectMapper();
		try {
			return (Object) mapper.readValue(value, clazz);
		} catch (JsonParseException e) {
			throw new RuntimeException("ERROR parsing JSON parameter - Exception:"+e);
		} catch (JsonMappingException e) {
			throw new RuntimeException("ERROR mapping JSON parameter - Exception:"+e);
		} catch (IOException e) {
			throw new RuntimeException("endpoint JSON parameter error - cannot deserialize JSON string "+e);
		}
	}

	
}
