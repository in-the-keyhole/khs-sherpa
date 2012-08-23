package com.khs.sherpa.parser;

/*
* Copyright 2012 the original author or authors.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*      http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import com.khs.sherpa.annotation.Param;
import com.khs.sherpa.context.ApplicationContext;
import com.khs.sherpa.context.ApplicationContextAware;

public class CalendarParamParser implements ApplicationContextAware, ParamParser<Calendar> {

	public static final String DEFAULT = "com.khs.sherpa.DEFUALT_CALENDAR_FORMAT";
	
	private ApplicationContext applicationContext;
	
	public boolean isValid(Class<?> clazz) {
		return clazz.isAssignableFrom(Calendar.class);
	}

	public Calendar parse(String value, Param annotation, Class<?> clazz) {
		String format = annotation.format();
		if(format == null) {
			format = (String) applicationContext.getAttribute(DEFAULT);
		}
		
		try {
			DateFormat fmt = new SimpleDateFormat(format);
			Calendar cal = Calendar.getInstance();
			cal.setTime(fmt.parse(value));
			return cal;
		} catch (ParseException e) {
			throw new RuntimeException(value+" must be calender ");
		}
	}

	public void setApplicationContext(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}

}
