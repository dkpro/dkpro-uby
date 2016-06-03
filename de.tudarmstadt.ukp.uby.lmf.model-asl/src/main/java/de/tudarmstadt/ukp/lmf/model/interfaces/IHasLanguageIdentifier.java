/*******************************************************************************
 * Copyright 2016
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
package de.tudarmstadt.ukp.lmf.model.interfaces;

import de.tudarmstadt.ukp.lmf.model.enums.ELanguageIdentifier;

/**
 * Interface for UBY-LMF classes that have a language identifier.
 *
 * @author Zijad Maksuti
 * @author Christian M. Meyer
 * @see ELanguageIdentifier
 *
 */
public interface IHasLanguageIdentifier {

	/**
	 * Returns the language identifier of this instance. The languages are,
	 * in general, represented as ISO 639-3 codes. The most frequently used
	 * language codes, as well as our additions for representing
	 * macrolanguages, dialects, retired languages, and not-yet-standardized
	 * languages are modeled as string constants in {@link ELanguageIdentifier}.
	 * Data category reference: http://www.isocat.org/rest/dc/279
	 * @return the language identifier of this instance or null, if no
	 *   certain language identifier has been set or the language is unknown.
	 * @see ELanguageIdentifier
	 */
	String getLanguageIdentifier();

	/**
	 * Set the given language identifier for this object. In most cases,
	 * choosing one of the string constants in {@link ELanguageIdentifier}
	 * should be sufficient. However, it is possible to use other language
	 * identifiers that are not yet modeled as constants. Use valid
	 * ISO 639-3 codes in these cases or extend the set of constant types.
	 * Data category reference: http://www.isocat.org/rest/dc/279
	 * @param languageIdentifier language identifier to be set.
	 * @see ELanguageIdentifier
	 */
	void setLanguageIdentifier(final String languageIdentifier);

}
