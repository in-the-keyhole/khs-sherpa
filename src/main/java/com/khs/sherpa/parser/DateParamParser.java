package com.khs.sherpa.parser;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.khs.sherpa.annotation.Param;
import com.khs.sherpa.util.SettingsContext;

public class DateParamParser implements ParamParser<Date> {

	public boolean isValid(Class<?> clazz) {
		return clazz.isAssignableFrom(Date.class);
	}

	public Date parse(String value, Param annotation, Class<?> clazz) {
		String format = annotation.format();
		if(format == null || format.equals("")) {
			format = SettingsContext.getSettings().dateFormat;
		}
		
		try {
			DateFormat fmt = new SimpleDateFormat(format);
			return fmt.parse(value);
		} catch (ParseException e) {
			throw new RuntimeException(value+" must be date ");
		}
	}

}
