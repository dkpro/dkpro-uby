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
package de.tudarmstadt.ukp.lmf.model.morphology;

import de.tudarmstadt.ukp.lmf.model.enums.ELanguageIdentifier;
import de.tudarmstadt.ukp.lmf.model.interfaces.IHasLanguageIdentifier;
import de.tudarmstadt.ukp.lmf.model.miscellaneous.EVarType;
import de.tudarmstadt.ukp.lmf.model.miscellaneous.VarType;

/**
 * This class represent a LMF-FormRepresentation
 * @author maksuti
 *
 */
public class FormRepresentation implements IHasLanguageIdentifier, Comparable<FormRepresentation>{
	
	// language identifier of this FormRepresentation
	@VarType(type = EVarType.ATTRIBUTE)
	private ELanguageIdentifier languageIdentifier;
	
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
	 * @return the writtenForm
	 */
	public String getWrittenForm() {
		return writtenForm;
	}

	/**
	 * @param writtenForm the writtenForm to set
	 */
	public void setWrittenForm(String writtenForm) {
		this.writtenForm = writtenForm;
	}

	/**
	 * @return the phoneticForm
	 */
	public String getPhoneticForm() {
		return phoneticForm;
	}

	/**
	 * @param phoneticForm the phoneticForm to set
	 */
	public void setPhoneticForm(String phoneticForm) {
		this.phoneticForm = phoneticForm;
	}

	/**
	 * @return the sound
	 */
	public String getSound() {
		return sound;
	}

	/**
	 * @param sound the sound to set
	 */
	public void setSound(String sound) {
		this.sound = sound;
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
	 * @return the hyphenation
	 */
	public String getHyphenation() {
		return hyphenation;
	}

	/**
	 * @param hyphenation the hyphenation to set
	 */
	public void setHyphenation(String hyphenation) {
		this.hyphenation = hyphenation;
	}

	/**
	 * @return the ortographyName
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
	    
	    
	    boolean result=
	    	((this.languageIdentifier == null && otherFormRepresentation.languageIdentifier == null) || (
	    	this.languageIdentifier != null && 	otherFormRepresentation.languageIdentifier != null &&	
	    	this.languageIdentifier.toString().equals(otherFormRepresentation.languageIdentifier.toString())))
	    && this.areEqual(this.writtenForm, otherFormRepresentation.writtenForm)
	    && this.areEqual(this.phoneticForm, otherFormRepresentation.phoneticForm)
	    && this.areEqual(this.sound, otherFormRepresentation.sound)
	    && this.areEqual(this.geographicalVariant, otherFormRepresentation.geographicalVariant)
	    && this.areEqual(this.orthographyName, otherFormRepresentation.orthographyName);
	    return result;
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
	
	/**
	 * Checks if two consumed String are equal
	 * this method is null-proof
	 * @param s1
	 * @param s2
	 * @return true only if s1 and s2 are equal
	 */
	private boolean areEqual(String s1, String s2){
		if(s1 == null)
			if(s2 == null)
				return true;
			else return false;
		else
			if(s2 == null)
				return false;
			else return s1.equals(s2);	
	}
	
	
}
