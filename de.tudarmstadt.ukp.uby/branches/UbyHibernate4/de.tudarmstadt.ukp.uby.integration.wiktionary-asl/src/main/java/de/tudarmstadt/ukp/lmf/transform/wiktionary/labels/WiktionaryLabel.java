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
package de.tudarmstadt.ukp.lmf.transform.wiktionary.labels;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Holds a Wiktionary Context label with all its attributes
 * @author Yevgen Chebotar
 * @author Christian M. Meyer
 */
public class WiktionaryLabel {
	
	private String unparsedText;	// Unparsed text of this label	
	private String name;			// Name of the label
	private String alias;			// Alias of this label as can be seen in the Wiktionary mark-up
	private HashMap<String, String> parametersByName; // Parameters of this label stored by name, if any name is present
	private List<String> parametersByNumber;		  // Parameters of this label stored by their order
	// Target word of this label, e.g. topic name of TOPIC labels, target form of FORM_OF labels
	private String targetWord;						  
	
	private WiktionaryLabelType labelType;	// Type of this label
	
	/**
	 * @param name Name of this label
	 * @param alias Alias of this label as can be seen in the Wiktionary mark-up
	 * @param labelType Type of this label
	 */
	public WiktionaryLabel(String name, String alias, WiktionaryLabelType labelType){
		this.labelType = labelType;
		this.name = name;
		this.alias = alias;
		parametersByName = new HashMap<String, String>();
		parametersByNumber = new ArrayList<String>();		
	}
	
	/**
	 * Add parameter to this label.
	 * @param name
	 * @param value
	 */
	public void addParameter(String name, String value){
		name = name.replace("]", "").replace("[", "");
		value = value.replace("]", "").replace("[", "");
		parametersByName.put(name, value);
		parametersByNumber.add(value);
	}
	
	/**
	 * @return parametersByName
	 */
	public HashMap<String, String> getParametersByName(){
		return parametersByName;
	}
	
	/**
	 * @return parametersByNumber
	 */
	public List<String> getParametersByNumber(){
		return parametersByNumber;
	}
	
	/**
	 * @param name
	 */
	public String getParameterByName(String name){
		return parametersByName.get(name);
	}
	
	/**
	 * @param number
	 */
	public String getParameterByNumber(int number){
		return parametersByNumber.get(number);
	}

	/**
	 * @return labelType
	 */
	public WiktionaryLabelType getLabelType(){
		return this.labelType;
	}
	
	/**
	 * @return name
	 */
	public String getName(){
		return this.name;
	}
	
	/**
	 * @return alias
	 */
	public String getAlias(){
		return this.alias;
	}
	
	/**
	 * @param targetWord
	 */
	public void setTargetWord(String targetWord){
		this.targetWord = targetWord;
	}
	
	/**
	 * @return targetWord
	 */
	public String getTargetWord(){
		return this.targetWord;
	}

	/**
	 * @return the unparsedText
	 */
	public String getUnparsedText() {
		return unparsedText;
	}

	/**
	 * @param unparsedText the unparsedText to set
	 */
	public void setUnparsedText(String unparsedText) {
		this.unparsedText = unparsedText;
	}
	
}
