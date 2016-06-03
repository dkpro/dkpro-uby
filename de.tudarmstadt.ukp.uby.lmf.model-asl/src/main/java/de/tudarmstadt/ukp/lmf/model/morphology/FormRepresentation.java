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
package de.tudarmstadt.ukp.lmf.model.morphology;

import de.tudarmstadt.ukp.lmf.model.interfaces.IHasLanguageIdentifier;
import de.tudarmstadt.ukp.lmf.model.interfaces.IHasParentSpecificTable;
import de.tudarmstadt.ukp.lmf.model.miscellaneous.EVarType;
import de.tudarmstadt.ukp.lmf.model.miscellaneous.VarType;

/**
 * FormRepresentation class is a class representing a lexeme, a morphological variant of a lexeme or
 * a morph. The class manages one or more orthographical variants of the written form
 * as well as data categories that describe the attributes of the word form (e.g. lemma, pronunciation, syllabification).
 * 
 * @author Zijad Maksuti
 *
 */
public class FormRepresentation implements IHasLanguageIdentifier, 
		IHasParentSpecificTable, Comparable<FormRepresentation>{
	
	// language identifier of this FormRepresentation
	@VarType(type = EVarType.ATTRIBUTE)
	private String languageIdentifier;
	
	// written form of this FormRepresentation
	@VarType(type = EVarType.ATTRIBUTE)
	private String writtenForm;
	
	// phonetic form of this FormRepresentation
	@VarType(type = EVarType.ATTRIBUTE)
	private String phoneticForm;
	
	// sound of this FormRepresentation
	@VarType(type = EVarType.ATTRIBUTE)
	private String sound;
	
	// geographicalVariant of this FormRepresentation
	@VarType(type = EVarType.ATTRIBUTE)
	private String geographicalVariant;
	
	// hyphenation of this FormRepresentation
	@VarType(type = EVarType.ATTRIBUTE)
	private String hyphenation;
	
	// orthographyName of this FormRepresentation
	@VarType(type = EVarType.ATTRIBUTE)
	private String orthographyName;


	public String getLanguageIdentifier() {
		return languageIdentifier;
	}

	public void setLanguageIdentifier(final String languageIdentifier) {
		this.languageIdentifier = languageIdentifier;
	}

	/**
	 * Returns the written string that represents this {@link FormRepresentation} instance.
	 * @return the written form representing the form representation or null, if the written form
	 * is not set
	 */
	public String getWrittenForm() {
		return writtenForm;
	}

	/**
	 * Sets the written string that represents this {@link FormRepresentation} instance.
	 * @param writtenForm the written string to set
	 */
	public void setWrittenForm(String writtenForm) {
		this.writtenForm = writtenForm;
	}

	/**
	 * Returns the spoken string of this {@link FormRepresentation} instance.
	 * @return the spoken string of the form representation or null, if the spoken string
	 * is not set
	 */
	public String getPhoneticForm() {
		return phoneticForm;
	}

	/**
	 * Sets the spoken string of this {@link FormRepresentation} instance.
	 * @param phoneticForm the spoken string to set
	 */
	public void setPhoneticForm(String phoneticForm) {
		this.phoneticForm = phoneticForm;
	}

	/**
	 * Returns a link to an external system containing the pronunciation of the
	 * lexeme or morph represented by this {@link FormRepresentation} instance.
	 * 
	 * @return the link to an external system containing the pronunciation of this form representation
	 * or null if the link is not set
	 */
	public String getSound() {
		return sound;
	}

	/**
	 * Sets a link to an external system containing the pronunciation of the
	 * lexeme or morph represented by this {@link FormRepresentation} instance.
	 * 
	 * @return the link to an external system containing the pronunciation of this form representation
	 * to set
	 * 
	 */
	public void setSound(String sound) {
		this.sound = sound;
	}

	/**
	 * Returns another variant of the written form in this {@link FormRepresentation} instance
	 * that is specific in a certain geographical region. 
	 * @return another variant of written text of this form representation or null, if
	 * the form representation does not have another geographical variant set 
	 * @see FormRepresentation#getWrittenForm()
	 */
	public String getGeographicalVariant() {
		return geographicalVariant;
	}

	/**
	 * Sets another variant of the written form used in this {@link FormRepresentation} instance
	 * that is specific in a certain geographical region.
	 * @param geographicalVariant the geographical variant to set
	 * @see FormRepresentation#setWrittenForm(String)
	 */
	public void setGeographicalVariant(String geographicalVariant) {
		this.geographicalVariant = geographicalVariant;
	}

	/**
	 * Returns the division of the written form of this {@link FormRepresentation} instance,
	 * such as at the end of a line, according to a given set of rules.<p>
	 * <i>Example (english): pho-ne-ti-cian <br>
	 * Words are hyphenated in order to block text efficiently and attractively for printing.
	 * Rules for syllabification and hyphenation can differ in some languages and in some situations.
	 * </i>
	 * @return the division of the written form of this form representation or null, if the
	 * form representation does not have the division of its written form set
	 * @see FormRepresentation#getWrittenForm()
	 */
	public String getHyphenation() {
		return hyphenation;
	}

	/**
	 * Sets the division of the written form of this {@link FormRepresentation} instance,
	 * such as at the end of a line, according to a given set of rules.<p>
	 * <i>Example (english): pho-ne-ti-cian <br>
	 * Words are hyphenated in order to block text efficiently and attractively for printing.
	 * Rules for syllabification and hyphenation can differ in some languages and in some situations.
	 * </i>
	 * @param hyphenation the division of the written form of this form representation to set
	 * @see FormRepresentation#setWrittenForm(String)
	 */
	public void setHyphenation(String hyphenation) {
		this.hyphenation = hyphenation;
	}

	/**
	 * Returns the name of the orthography used in this {@link FormRepresentation}.<p>
	 * For instance, an orthographe name can be <i>"arabic"</i> or <i>"arabic unpointed"</i>.
	 * @return the name of the orthography used in the written form of this form representation
	 * or null, if the name of orthography is not set
	 * @see FormRepresentation#getWrittenForm()
	 */
	public String getOrthographyName() {
		return orthographyName;
	}

	/**
	 * Sets the name of the orthography used in this {@link FormRepresentation} instance.<p>
	 * For instance, an orthographe name can be <i>"arabic"</i> or <i>"arabic unpointed"</i>.
	 * @param orthographyName the name to set
	 * @see FormRepresentation#setWrittenForm(String)
	 */
	public void setOrthographyName(String orthographyName) {
		this.orthographyName = orthographyName;
	}
	
	public String toString(){
		StringBuffer sb = new StringBuffer(128);
		sb.append("FormRepresentation ").append("languageIdentifier:")
		.append(this.languageIdentifier).append(" writtenForm: ").append(this.writtenForm)
		.append(" phoneticForm: ").append(this.phoneticForm).append(" ").append(" sound: ")
		.append(this.sound).append(" geographicalVariant: ").append(this.geographicalVariant)
		.append(" hyphenation: ").append(this.hyphenation).append(" orthographyName: ")
		.append(this.orthographyName);
		return sb.toString();
	}

	@Override
	public int compareTo(FormRepresentation o) {
		return this.toString().compareTo(o.toString());
	}
	
	public boolean equals(Object other) {
	    if (this == other)
	      return true;
	    if (!(other instanceof FormRepresentation))
	      return false;
	    FormRepresentation otherFormRepresentation = (FormRepresentation) other;
	    
	    
	   return this.toString().equals(otherFormRepresentation.toString());
	  }
	
	public int hashCode() { 
	    int hash = 1;
	    hash = hash * 31 + (this.languageIdentifier == null ? 0 : this.languageIdentifier.toString().hashCode());
	    hash = hash * 31 + (this.writtenForm == null ? 0 : this.writtenForm.hashCode());
	    hash = hash * 31 + (this.phoneticForm == null ? 0 : this.phoneticForm.hashCode());
	    hash = hash * 31 + (this.sound == null ? 0 : this.sound.hashCode());
	    hash = hash * 31 + (this.geographicalVariant == null ? 0 : this.geographicalVariant.hashCode());
	    hash = hash * 31 + (this.hyphenation == null ? 0 : this.hyphenation.hashCode());
	    hash = hash * 31 + (this.orthographyName == null ? 0 : this.orthographyName.hashCode());
	    return hash;
	  }
	
}
