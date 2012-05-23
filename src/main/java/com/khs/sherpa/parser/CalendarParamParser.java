package com.khs.sherpa.parser;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import com.khs.sherpa.annotation.Param;

public class CalendarParamParser implements ParamParser<Calendar> {

	public boolean isValid(Class<?> clazz) {
		return clazz.isAssignableFrom(Calendar.class);
	}

	public Calendar parse(String value, Param annotation, Class<?> clazz) {
		String format = annotation.format();
		try {
			DateFormat fmt = new SimpleDateFormat(format);
			Calendar cal = Calendar.getInstance();
			cal.setTime(fmt.parse(value));
			return cal;
		} catch (ParseException e) {
			throw new RuntimeException(value+" must be calender ");
		}
	}

}
