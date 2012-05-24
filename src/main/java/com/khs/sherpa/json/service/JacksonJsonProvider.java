package com.khs.sherpa.json.service;

import java.io.IOException;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

public class JacksonJsonProvider implements JsonProvider {

	public String toJson(Object object) {
		ObjectMapper mapper = new ObjectMapper();
		try {
			return mapper.writeValueAsString(object);
		} catch (JsonGenerationException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public <T> T toObject(String json, Class<T> type) {
		// map to json object
		ObjectMapper mapper = new ObjectMapper();
		try {
			return mapper.readValue(json, type);
		}  catch (JsonMappingException e) {
			throw new RuntimeException("ERROR mapping JSON parameter - Exception:"+e);
		} catch (IOException e) {
			throw new RuntimeException("endpoint JSON parameter error - cannot deserialize JSON string "+e);
		}
	}

}
