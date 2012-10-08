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
package de.tudarmstadt.ukp.lmf.model.mrd;

import de.tudarmstadt.ukp.lmf.model.core.Sense;
import de.tudarmstadt.ukp.lmf.model.miscellaneous.EVarType;
import de.tudarmstadt.ukp.lmf.model.miscellaneous.VarType;

/**
 * The Equivalent class represents translation equivalents of a {@link Sense}.
 * <br>
 * The class is in a zero to many aggregation of the {@link Sense} class.
 * @author Christian M. Meyer
 */
public class Equivalent {
	
	/*
	geographicalVariant 1851 External link mark

	    e.g., Moscow 

	orthographyName 2176 External link mark

	    e.g., since 1956 

	transliteration 1848 External link mark

	    e.g., rasténije 
*/

	// http://www.isocat.org/rest/dc/279
	@VarType(type = EVarType.ATTRIBUTE)
	private String languageIdentifer;

	// http://www.isocat.org/rest/dc/1836
	@VarType(type = EVarType.ATTRIBUTE)
	private String writtenForm;

	// http://www.isocat.org/rest/dc/1851
//	@VarType(type = EVarType.ATTRIBUTE_OPTIONAL)
//	private String geographicalVariant;
//
//	// http://www.isocat.org/rest/dc/2176
//	@VarType(type = EVarType.ATTRIBUTE_OPTIONAL)
//	private String orthographyName;

	// http://www.isocat.org/rest/dc/1848
	@VarType(type = EVarType.ATTRIBUTE_OPTIONAL)
	private String transliteration;
	
	// http://www.isocat.org/rest/dc/3764
	@VarType(type = EVarType.ATTRIBUTE_OPTIONAL)
	private String usage;
	
	/**
	 * Returns an identifier of the target language, a sense is translated to.
	 * That is, the language of the {@link #getWrittenForm()}. An example is
	 * "rus" indicating a translation equivalent in the Russian language.
	 * Data category reference: http://www.isocat.org/rest/dc/279
	 * @return A string representation of the language encoded as an ISO 
	 *   639-2 language code. The string might be empty for unknown languages,
	 *   but is never null.
	 */
	public String getLanguageIdentifer() {
		return languageIdentifer;
	}

	/**
	 * Assigns the given language identifier for this equivalent.
	 * @param languageIdentifer A string representation of the target language 
	 *   encoded as an ISO 639-2 language code. The string might be empty for 
	 *   unknown languages, but never null.
	 * @throws NullPointerException if the languageIdentifier is null.
	 */
	public void setLanguageIdentifer(final String languageIdentifer) {
		if (languageIdentifer == null)
			throw new NullPointerException("languageIdentifier is not to be null; use an empty string for unknown language.");
		this.languageIdentifer = languageIdentifer;
	}
	
	/**
	 * Returns the written word form of the translation. For example,
	 * "растение" (the Russian translation of "plant").
	 * Data category reference: http://www.isocat.org/rest/dc/1836
	 * @return A string representation of the translated word; 
	 *   never null or empty.
	 */
	public String getWrittenForm() {
		return writtenForm;
	}
	
	/**
	 * Assigns the given written form to this equivalent.
	 * @param languageIdentifer A string representation of the written form;
	 *   never to be null or empty.
	 * @throws NullPointerException if the writtenForm is null or empty.
	 */
	public void setWrittenForm(final String writtenForm) {
		if (writtenForm == null || writtenForm.isEmpty())
			throw new NullPointerException("writtenForm is not to be null or empty!");
		this.writtenForm = writtenForm;
	}
	
//	public String getGeographicalVariant() {
//		return geographicalVariant;
//	}
//	
//	public void setGeographicalVariant(final String geographicalVariant) {
//		this.geographicalVariant = geographicalVariant;
//	}
	
	/** Returns the name of the orthography system that the 
	 * {@link #getWrittenForm()} follows (e.g., German orthography 
	 * reform 1996). The orthographyName should be empty if no specific
	 * orthography is encoded. 
	 * Data category reference: http://www.isocat.org/rest/dc/2176 
	 * @return A String representation of the orthography system; might be
	 *   null or empty. 
	 */
//	public String getOrthographyName() {
//		return orthographyName;
//	}
//	
//	public void setOrthographyName(final String orthographyName) {
//		this.orthographyName = orthographyName;
//	}
	
	public String getTransliteration() {
		return transliteration;
	}
	
	public void setTransliteration(final String transliteration) {
		this.transliteration = transliteration;
	}

	public String getUsage() {
		return usage;
	}
	
	public void setUsage(final String usage) {
		this.usage = usage;
	}
	
}
