package com.khs.sherpa.util;

import com.khs.sherpa.json.service.GsonJsonProvider;
import com.khs.sherpa.json.service.JsonProvider;
import com.khs.sherpa.parser.BooleanParamParser;
import com.khs.sherpa.parser.CalendarParamParser;
import com.khs.sherpa.parser.DateParamParser;
import com.khs.sherpa.parser.DoubleParamPaser;
import com.khs.sherpa.parser.FloatParamParser;
import com.khs.sherpa.parser.IntegerParamParser;
import com.khs.sherpa.parser.JsonParamParser;
import com.khs.sherpa.parser.StringParamParser;

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



public class Defaults {
	
	public final static String DATE_FORMAT = "MM/dd/yyyy";
	public final static String DATE_TIME_FORMAT = "MM/dd/yyyy hh:mm:ss a";
	public final static long SESSION_TIMEOUT = 0;
	public final static boolean ENDPOINT_AUTHENTICATION = false;
	public final static String BOOLEAN_FORMAT = "0";
	public final static boolean ACTIVITY_LOG = true;
	public final static String ENCODE = null;
	public final static String SHERPA_ADMIN = "ROLE_ADMIN";
	public final static boolean JSONP_SUPPORT = false;
	public final static Class<? extends JsonProvider> JSON_PROVIDER = GsonJsonProvider.class;

	public final static Class<?> BOOLEAN_PARAM_PARSER = BooleanParamParser.class;
	public final static Class<?> CALENDAR_PARAM_PARSER = CalendarParamParser.class;
	public final static Class<?> DATE_PARAM_PARSER = DateParamParser.class;
	public final static Class<?> DOULBE_PARAM_PARSER = DoubleParamPaser.class;
	public final static Class<?> FLOAT_PARAM_PARSER = FloatParamParser.class;
	public final static Class<?> INTEGER_PARAM_PARSER = IntegerParamParser.class;
	public final static Class<?> JSON_PARAM_PARSER = JsonParamParser.class;
	public final static Class<?> STRING_PARAM_PARSER = StringParamParser.class;
	
}
