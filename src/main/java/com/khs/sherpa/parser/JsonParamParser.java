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

import java.util.Calendar;
import java.util.Date;

import com.khs.sherpa.annotation.Param;
import com.khs.sherpa.context.ApplicationContext;
import com.khs.sherpa.context.ApplicationContextAware;
import com.khs.sherpa.exception.NoSuchManagedBeanExcpetion;
import com.khs.sherpa.json.service.JsonProvider;

public class JsonParamParser implements ApplicationContextAware, ParamParser<Object> {

	private JsonProvider jsonProvider;
	
	public boolean isValid(Class<?> clazz) {
		return !clazz.isAssignableFrom(Boolean.class) && 
				!clazz.isAssignableFrom(boolean.class) &&
				!clazz.isAssignableFrom(Calendar.class) &&
				!clazz.isAssignableFrom(Date.class) &&
				!clazz.isAssignableFrom(Double.class) &&
				!clazz.isAssignableFrom(double.class) &&
				!clazz.isAssignableFrom(float.class) &&
				!clazz.isAssignableFrom(Float.class) &&
				!clazz.isAssignableFrom(Integer.class) &&
				!clazz.isAssignableFrom(int.class) &&
				!clazz.isAssignableFrom(Integer.class) && 
				!clazz.isAssignableFrom(int.class);
	}

	public Object parse(String value, Param annotation, Class<?> clazz) {
		return jsonProvider.toObject(value, clazz);
	}

	public void setApplicationContext(ApplicationContext applicationContext) {
		try {
			this.jsonProvider = applicationContext.getManagedBean(JsonProvider.class);
		} catch (NoSuchManagedBeanExcpetion e) {
			e.printStackTrace();
		}
	}
	
}
