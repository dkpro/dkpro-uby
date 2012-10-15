/*******************************************************************************
 * Copyright 2012
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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Different useful functions
 * @author chebotar
 *
 */
public class StringUtils {
	
	private static final Pattern HTML_ENTITIES = Pattern.compile("^(.*?)&#(\\d+);"); 
	
	/** Replaces HTML entities such as &#803; with the corresponding 
	 *  characters. */
	public static String replaceHtmlEntities(final String text) {
		String t = text;
		StringBuilder result = new StringBuilder();
		do {
			Matcher matcher = HTML_ENTITIES.matcher(t);
			if (matcher.find()) {
				result.append(matcher.group(1));
				result.append((char) Integer.parseInt(matcher.group(2)));
				t = matcher.replaceFirst("");				
			} else
				break;
		} while (true);
		result.append(t);
//		if (!text.equals(result.toString()))
//			System.out.println(">" + text + "< to >" + result.toString() + "<");
		return result.toString();
	}
	
	/**
	 * Removes all UTF8 characters that cause errors in MySQL database
	 * @param text
	 * @return
	 */
	public static String replaceNonUtf8(String text){
		text = text.replaceAll("[^\\u0000-\\uFFFF]", "?");
		return text;
	}
	
	/**
	 * Removes all UTF8 characters that cause errors in MySQL database
	 * and trims the text to maxLenth
	 * @param text
	 * @param maxLength
	 * @return
	 */
	public static String replaceNonUtf8(String text, int maxLength){
		if(text.length() > maxLength)
			text = text.substring(0, maxLength-1);
		return replaceNonUtf8(text);
		
	}
}
