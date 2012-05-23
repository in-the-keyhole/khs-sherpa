package com.khs.sherpa.parser;

import com.khs.sherpa.annotation.Param;

public interface ParamParser<T> {

	public boolean isValid(Class<?> clazz);
	
	public T parse(String value,  Param annotation, Class<?> clazz);
	
}
