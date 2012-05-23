package com.khs.sherpa.parser;

import com.khs.sherpa.annotation.Param;

public class IntegerParamParser implements ParamParser<Integer> {

	public boolean isValid(Class<?> clazz) {
		return (clazz.isAssignableFrom(Integer.class) || clazz.isAssignableFrom(int.class));
	}

	public Integer parse(String value, Param annotation, Class<?> clazz) {
		try {
			return Integer.parseInt(value);
		} catch (NumberFormatException e) {
			throw new RuntimeException(value+" must be integer ");
		}
	}


}
