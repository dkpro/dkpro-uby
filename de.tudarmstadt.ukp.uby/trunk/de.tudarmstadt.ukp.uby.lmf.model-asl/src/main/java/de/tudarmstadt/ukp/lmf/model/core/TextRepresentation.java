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
package de.tudarmstadt.ukp.lmf.model.core;

import java.util.HashSet;
import java.util.Set;

import de.tudarmstadt.ukp.lmf.model.enums.ELanguageIdentifier;
import de.tudarmstadt.ukp.lmf.model.interfaces.IHasLanguageIdentifier;
import de.tudarmstadt.ukp.lmf.model.miscellaneous.EVarType;
import de.tudarmstadt.ukp.lmf.model.miscellaneous.VarType;

/**
 * This class represents a LMF-TextRepresentation
 * @author maksuti
 *
 */
public class TextRepresentation implements IHasLanguageIdentifier, Comparable<TextRepresentation>{

	// Language identifier of this TextRepresentation
	@VarType(type = EVarType.ATTRIBUTE)
	private ELanguageIdentifier languageIdentifier;

	// Orthography name of this TextRepresentation
	@VarType(type = EVarType.ATTRIBUTE)
	private String orthographyName;

	// geographical variant of this TextRepresentation
	@VarType(type = EVarType.ATTRIBUTE)
	private String geographicalVariant;

	// written Text of this TextRepresentation
	@VarType(type = EVarType.ATTRIBUTE)
	private String writtenText;

	/*
	 * FOR DEBUGGING PURPOSES ONLY
	 */
	private static Set<String> tags;
	static{
		tags = new HashSet<String>();
		tags.add("&lt;");
		tags.add("&gt;");
		tags.add("&#10;");
		tags.add("&quot;");
	}

	/**
	 * @return the languageIdentifier
	 */
	public ELanguageIdentifier getLanguageIdentifier() {
		return languageIdentifier;
	}

	/**
	 * @param languageIdentifier the languageIdentifier to set
	 */
	public void setLanguageIdentifier(ELanguageIdentifier languageIdentifier) {
		this.languageIdentifier = languageIdentifier;
	}

	/**
	 * @return the orthographyName
	 */
	public String getOrthographyName() {
		return orthographyName;
	}

	/**
	 * @param orthographyName the ortographyName to set
	 */
	public void setOrthographyName(String orthographyName) {
		this.orthographyName = orthographyName;
	}

	/**
	 * @return the geographicalVariant
	 */
	public String getGeographicalVariant() {
		return geographicalVariant;
	}

	/**
	 * @param geographicalVariant the geographicalVariant to set
	 */
	public void setGeographicalVariant(String geographicalVariant) {
		this.geographicalVariant = geographicalVariant;
	}

	/**
	 * @return the writtenText
	 */
	public String getWrittenText() {
		return writtenText;
	}

	/**
	 * @param writtenText the writtenText to set
	 */
	public void setWrittenText(String writtenText) {
		/*
		 * For DEBUGGING purposes only
		 */
	/*	for(String tag : tags)
			if(writtenText.contains(tag)){
				System.err.println("FNConveretr, Warning, TextRerpresentation.writtenText contains invalid characters: " + tag);
				System.err.println(writtenText);
			}*/
		this.writtenText = writtenText;
	}

	@Override
	public String toString(){
		StringBuffer sb = new StringBuffer(512);
		sb.append("TextRepresentation ");
		sb.append("languageIdentifier:").append(languageIdentifier);
		sb.append(" ortographyName:").append(orthographyName);
		sb.append(" geographicalVariant:").append(geographicalVariant);
		sb.append(" writtenText:").append(writtenText);
		return sb.toString();
	}

	@Override
	public int hashCode(){
		return this.toString().hashCode();
	}

	@Override
	public int compareTo(TextRepresentation o) {
		return this.toString().compareTo(o.toString());
	}

	@Override
	public boolean equals(Object other) {
	    if (this == other) {
			return true;
		}
	    if (!(other instanceof TextRepresentation)) {
			return false;
		}
	    TextRepresentation otherTextRepresentation = (TextRepresentation) other;
	    return this.toString().equals(otherTextRepresentation.toString());
	 }

}
