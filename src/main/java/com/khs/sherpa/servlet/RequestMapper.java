package com.khs.sherpa.servlet;

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

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringEscapeUtils;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import com.khs.sherpa.annotation.Encode;
import com.khs.sherpa.annotation.Param;

public class RequestMapper {
			
	public RequestMapper(Settings settings) {
		this.settings = settings;
	}
	
	private Settings settings = null;
	
	public Object map(String endpoint,String action,Class<?> type, HttpServletRequest request, Annotation annotation) {

		Param a = null;
		Object result = null;

		if (annotation.annotationType() == Param.class) {
			a = (Param) annotation;
		}

		if (a == null) {
			throw new RuntimeException("endpoint @Param annotation required");
		}

		if (a.name() == null) {
			throw new RuntimeException("parameters required");
		}

		String parameterName = a.name();
		String parameterValue = request.getParameter(parameterName);
		
		if (parameterValue == null) {
				throw new RuntimeException("Endpoint = "+endpoint+" Action = "+action+" - Parameter name ("+parameterName+") not found in request");			
		}
		
		if (type == String.class) {
			String s = new String(parameterValue);
			if (a.format().length() > 0 ) {
				s = applyEncoding(s,a.format());
			} else if (this.settings.encode != null) {
				s = applyEncoding(s,this.settings.encode);
			}
			result = s;
		} else if (type == Integer.class || type == int.class) {
			String s = parameterValue;
			try {
				result = Integer.parseInt(s);
			} catch (NumberFormatException e) {
				throw new RuntimeException("endpoint parameter " + a.name() + "= "+s+" must be integer ");
			}
		} else if (type == Float.class || type == float.class) {

			String s = parameterValue;
			try {
				result = Float.parseFloat(s);
			} catch (NumberFormatException e) {
				throw new RuntimeException("endpoint parameter " + a.name() + "= "+s+" must be float ");
			}

		} else if (type == Double.class || type == double.class) {

			String s = parameterValue;
			try {
				result = Double.parseDouble(s);
			} catch (NumberFormatException e) {
				throw new RuntimeException("endpoint parameter " + a.name() + "= "+s+"  must be double ");
			}
		} else if (type == Date.class) {

			String s = parameterValue;
			if (s == null) {
				return null;
			} else {
			String fmt = this.settings.dateFormat;
			// annotation format value overrides property format
			if (!a.format().isEmpty()) {
				fmt = a.format();
			}
			try {
			   if (!a.format().isEmpty()) {
					fmt = a.format();
				}
				DateFormat format = new SimpleDateFormat(fmt);
				result = format.parseObject(s);
			  } catch (ParseException e) {
				throw new RuntimeException("endpoint parameter " + a.name() + "= "+s+" invalid date format must be " + fmt);
			  }
			}

		} else if (type == Calendar.class) { 
			
			String s = parameterValue;
			if (s == null) {
				return null;
			} else {
			String fmt = this.settings.dateTimeFormat;
	
			try {
				// annotation format value overrides property format
			   if (!a.format().isEmpty()) {
					fmt = a.format();
				}
				DateFormat format = new SimpleDateFormat(fmt);
				Date date = format.parse(s);
				Calendar cal = Calendar.getInstance();
				cal.setTime(date);
				result = cal;
			  } catch (ParseException e) {
				throw new RuntimeException("endpoint parameter " + a.name() + "= "+s+" invalid date/time format must be " + fmt);
			  }
			}
			
		} else if (type == Boolean.class || type == boolean.class) {

			String s = parameterValue;
			if (!(s.equalsIgnoreCase("1") || s.equalsIgnoreCase("Y") || s.equalsIgnoreCase("0") || s.equalsIgnoreCase("N"))) {
				throw new RuntimeException("endpoint parameter " + a.name() + "= "+s+" invalid boolean format must be Y/N or 0/1");
			}
			if (s.equalsIgnoreCase("Y") || s.equalsIgnoreCase("1")) {
				result = new Boolean(true);
			} else {
				result = new Boolean(false);
			}

		} else if (type == Calendar.class) {

			String s = parameterValue;
			String fmt = this.settings.dateFormat;
			try {
				// annotation format value overrides property format
				if (!a.format().isEmpty()) {
					fmt = a.format();
				}
				DateFormat format = new SimpleDateFormat(fmt);
				Calendar cal = Calendar.getInstance();
				cal.setTime((Date) format.parseObject(s));
				result = format.parseObject(s);
			} catch (ParseException e) {
				throw new RuntimeException("endpoint parameter " + a.name() + " invalid date format must be " + fmt);
			}

		} else {
			Param p = (Param) annotation;
			String jsonString = new String(request.getParameter(p.name()));
			// map to json object
			ObjectMapper mapper = new ObjectMapper();
			try {
			 result	= mapper.readValue(jsonString,type);
			} catch (JsonParseException e) {
				throw new RuntimeException("ERROR parsing JSON parameter "+p.name()+" Exception:"+e);
			} catch (JsonMappingException e) {
				throw new RuntimeException("ERROR mapping JSON parameter "+p.name()+" Exception:"+e);
			} catch (IOException e) {
				throw new RuntimeException("endpoint JSON parameter error " + p.name() + "cannot deserialize JSON string "+e);
			}
			
		}

		return result;

	}
	
	private String applyEncoding(String value,String format) {
		String result = value;
		if (format.equals(Encode.XML)) {
			result = StringEscapeUtils.escapeXml(value);		
		} else if (format.equals(Encode.HTML)) {
			result = StringEscapeUtils.escapeHtml4(value);		
		} else if (format.equals(Encode.CSV)) {
			result = StringEscapeUtils.escapeCsv(value);
		}
		  
		return result;
	}
	
	

}
