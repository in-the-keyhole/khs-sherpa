package com.khs.sherpa.parser;

import com.khs.sherpa.annotation.Param;

public class DoubleParamPaser implements ParamParser<Double> {

	public boolean isValid(Class<?> clazz) {
		return (clazz.isAssignableFrom(Double.class) || clazz.isAssignableFrom(double.class));
	}

	public Double parse(String value, Param annotation, Class<?> clazz) {
		try {
			return Double.parseDouble(value);
		} catch (NumberFormatException e) {
			throw new RuntimeException(value+" must be float ");
		}
	}

}
