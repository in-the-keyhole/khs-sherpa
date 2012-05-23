package com.khs.sherpa.parser;

import com.khs.sherpa.annotation.Param;

public class FloatParamParser implements ParamParser<Float> {

	public boolean isValid(Class<?> clazz) {
		return (clazz.isAssignableFrom(float.class) || clazz.isAssignableFrom(Float.class));
	}

	public Float parse(String value, Param annotation, Class<?> clazz) {
		try {
			return Float.parseFloat(value);
		} catch (NumberFormatException e) {
			throw new RuntimeException(value+" must be float ");
		}
	}

}
