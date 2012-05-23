package com.khs.sherpa.parser;

import org.apache.commons.lang3.StringEscapeUtils;

import com.khs.sherpa.annotation.Encode;
import com.khs.sherpa.annotation.Param;
import com.khs.sherpa.util.SettingsContext;

public class StringParamParser implements ParamParser<String> {
	
	public String parse(String value, Param annotation, Class<?> clazz) {
		String format = annotation.format();
		
		if(format == null || format.equals("")) {
			format = SettingsContext.getSettings().encode;
		}
		
		return this.applyEncoding(value, format);
	}

	public boolean isValid(Class<?> clazz) {
		return clazz.isAssignableFrom(String.class);
	}

	private String applyEncoding(String value,String format) {
		String result = value;
		if (format != null) {
			if (format.equals(Encode.XML)) {
				result = StringEscapeUtils.escapeXml(value);		
			} else if (format.equals(Encode.HTML)) {
				result = StringEscapeUtils.escapeHtml4(value);		
			} else if (format.equals(Encode.CSV)) {
				result = StringEscapeUtils.escapeCsv(value);
			}
		}
		  
		return result;
	}
}
