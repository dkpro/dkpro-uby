/*******************************************************************************
 * Copyright 2015
 * Ubiquitous Knowledge Processing (UKP) Lab
 * Technische Universität Darmstadt
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package de.tudarmstadt.ukp.lmf.transform;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class GenericUtils {

	/**
	 * Return boolean value of a string
	 * @param value
	 * @return
	 */
	public static boolean getBoolean(String value){
		if(value != null && (value.equals("yes") || value.equals("true")))
			return true;
		else return false;
	}

	/**
	 * Return int value of a string
	 * @param value
	 * @return
	 */
	public static int getInteger(String value){
		if(value != null)
			return Integer.parseInt(value);
		else return 0;
	}
	
	/**
	 * Return double value of a string
	 * @param value
	 * @return
	 */
	public static double getDouble(String value){
		if(value != null)
			return Double.parseDouble(value);
		else return 0;
	}
	
	/**
	 * Return Enum-value of a string
	 * @param fieldClass
	 * @param value
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static Object getEnum(Class fieldClass, String value) {
		if(value != null)
			return Enum.valueOf(fieldClass, value);
		else return null;
	}

	/**
	 * Return Date value of a string
	 * @param value
	 * @return
	 */
	public static Date getDate(String value){
		SimpleDateFormat df = new SimpleDateFormat("EEE MMM dd kk:mm:ss zzz yyyy", Locale.ENGLISH);
		Date date = null;
		try {
			date = df.parse(value);
		} catch (ParseException e) {}
		
		return date;
	}
}
