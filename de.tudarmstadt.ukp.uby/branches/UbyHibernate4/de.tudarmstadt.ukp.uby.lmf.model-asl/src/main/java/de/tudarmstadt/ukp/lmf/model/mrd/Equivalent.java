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
import de.tudarmstadt.ukp.lmf.model.interfaces.IHasLanguageIdentifier;
import de.tudarmstadt.ukp.lmf.model.miscellaneous.EVarType;
import de.tudarmstadt.ukp.lmf.model.miscellaneous.VarType;

/**
 * The Equivalent class represents translation equivalents of a {@link Sense}.
 * <br>
 * The class is in a zero to many aggregation of the {@link Sense} class.
 * @author Christian M. Meyer
 */
public class Equivalent implements IHasLanguageIdentifier {
	
	// http://www.isocat.org/rest/dc/279
	@VarType(type = EVarType.ATTRIBUTE)
	private String languageIdentifier;

	// http://www.isocat.org/rest/dc/1836
	@VarType(type = EVarType.ATTRIBUTE)
	private String writtenForm;

	// http://www.isocat.org/rest/dc/1848
//	transliteration 1848, e.g., rasténije
	@VarType(type = EVarType.ATTRIBUTE_OPTIONAL)
	private String transliteration;
	
	// http://www.isocat.org/rest/dc/3764
	@VarType(type = EVarType.ATTRIBUTE_OPTIONAL)
	private String usage;
	
	// http://www.isocat.org/rest/dc/1851
//	geographicalVariant 1851, e.g., Moscow
//	@VarType(type = EVarType.ATTRIBUTE_OPTIONAL)
//	private String geographicalVariant;

	// http://www.isocat.org/rest/dc/2176
//	orthographyName 2176, e.g., since 1956
//	@VarType(type = EVarType.ATTRIBUTE_OPTIONAL)
//	private String orthographyName;

	
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
	 * @param languageIdentifier A string representation of the written form;
	 *   never to be null or empty.
	 * @throws NullPointerException if the writtenForm is null or empty.
	 */
	public void setWrittenForm(final String writtenForm) {
		if (writtenForm == null || writtenForm.isEmpty())
			throw new NullPointerException("writtenForm is not to be null or empty!");
		this.writtenForm = writtenForm;
	}
	
	public String getLanguageIdentifier() {
		return languageIdentifier;
	}
	
	public void setLanguageIdentifier(final String languageIdentifier) {
		if (languageIdentifier == null)
			throw new NullPointerException("languageIdentifier is not to be null; use an empty string for unknown language.");
		this.languageIdentifier = languageIdentifier;
	}

	/** Returns a transliteration of the translated word. The Russian 
	 *  translation "растение" of "plant" can, for instance, be 
	 *  transliterated as "rasténije".*/
	public String getTransliteration() {
		return transliteration;
	}
	
	public void setTransliteration(final String transliteration) {
		this.transliteration = transliteration;
	}

	/** Returns additional usage information of this translation including
	 *  syntactic information (like grammatical gender, number, case, etc.),
	 *  frequency (like "rare") or comments explaining the situation(s) in 
	 *  which this translation is to be used. */
	public String getUsage() {
		return usage;
	}
	
	public void setUsage(final String usage) {
		this.usage = usage;
	}
	
//	public String getGeographicalVariant() {
//		return geographicalVariant;
//	}
//	
//	public void setGeographicalVariant(final String geographicalVariant) {
//		this.geographicalVariant = geographicalVariant;
//	}
	
	/* Returns the name of the orthography system that the 
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
	
}
