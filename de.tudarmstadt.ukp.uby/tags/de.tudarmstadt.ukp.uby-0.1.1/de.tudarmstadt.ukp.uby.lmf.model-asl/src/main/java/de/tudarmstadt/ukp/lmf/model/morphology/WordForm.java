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

import java.util.List;

import de.tudarmstadt.ukp.lmf.model.enums.ECase;
import de.tudarmstadt.ukp.lmf.model.enums.EGrammaticalGender;
import de.tudarmstadt.ukp.lmf.model.enums.EGrammaticalNumber;
import de.tudarmstadt.ukp.lmf.model.enums.EPerson;
import de.tudarmstadt.ukp.lmf.model.enums.ETense;
import de.tudarmstadt.ukp.lmf.model.enums.EVerbFormMood;
import de.tudarmstadt.ukp.lmf.model.meta.Frequency;
import de.tudarmstadt.ukp.lmf.model.miscellaneous.EVarType;
import de.tudarmstadt.ukp.lmf.model.miscellaneous.VarType;

/**
 * This class represents a LMF-WordForm
 * @author maksuti
 *
 */
public class WordForm {
	
	// GrammaticalNumber of this WordForm
	@VarType(type = EVarType.ATTRIBUTE)
	private EGrammaticalNumber grammaticalNumber;
	
	// grammatical gender of this WordForm
	@VarType(type = EVarType.ATTRIBUTE)
	private EGrammaticalGender grammaticalGender;
	
	// case of this WordForm
	@VarType(type = EVarType.ATTRIBUTE)
	private ECase _case;
	
	// person of this WordForm
	@VarType(type = EVarType.ATTRIBUTE)
	private EPerson person;
	
	// tense of this WordForm
	@VarType(type = EVarType.ATTRIBUTE)
	private ETense tense;
	
	// mood of the verb
	@VarType(type = EVarType.ATTRIBUTE)
	private EVerbFormMood verbFormMood;
	
	// FormRepresentation of this WordForm
	@VarType(type = EVarType.CHILDREN)
	private List<FormRepresentation> formRepresentations;
	
	// Frequency information for this WordForm
	@VarType(type = EVarType.CHILDREN)
	private List<Frequency> frequencies;
	

	/**
	 * @return the grammaticalNumber
	 */
	public EGrammaticalNumber getGrammaticalNumber() {
		return grammaticalNumber;
	}

	/**
	 * @param grammaticalNumber the grammaticalNumber to set
	 */
	public void setGrammaticalNumber(EGrammaticalNumber grammaticalNumber) {
		this.grammaticalNumber = grammaticalNumber;
	}

	/**
	 * @return the grammaticalGender
	 */
	public EGrammaticalGender getGrammaticalGender() {
		return grammaticalGender;
	}

	/**
	 * @param grammaticalGender the grammaticalGender to set
	 */
	public void setGrammaticalGender(EGrammaticalGender grammaticalGender) {
		this.grammaticalGender = grammaticalGender;
	}

	/**
	 * @return the _case
	 */
	public ECase getCase() {
		return _case;
	}

	/**
	 * @param case1 the _case to set
	 */
	public void setCase(ECase case1) {
		_case = case1;
	}

	/**
	 * @return the person
	 */
	public EPerson getPerson() {
		return person;
	}

	/**
	 * @param person the person to set
	 */
	public void setPerson(EPerson person) {
		this.person = person;
	}

	/**
	 * @return the tense
	 */
	public ETense getTense() {
		return tense;
	}

	/**
	 * @param tense the tense to set
	 */
	public void setTense(ETense tense) {
		this.tense = tense;
	}

	/**
	 * @return the verbFormMood
	 */
	public EVerbFormMood getVerbFormMood() {
		return verbFormMood;
	}

	/**
	 * @param verbFormMood the verbFormMood to set
	 */
	public void setVerbFormMood(EVerbFormMood verbFormMood) {
		this.verbFormMood = verbFormMood;
	}

	/**
	 * @return the formRepresentations
	 */
	public List<FormRepresentation> getFormRepresentations() {
		return formRepresentations;
	}

	/**
	 * @param formRepresentations the formRepresentations to set
	 */
	public void setFormRepresentations(List<FormRepresentation> formRepresentations) {
		this.formRepresentations = formRepresentations;
	}
	
	/**
	 * 
	 * @param frequencies the frequencies to set
	 */
	public void setFrequencies(List<Frequency> frequencies) {
		this.frequencies = frequencies;
	}

	/**
	 * 
	 * @return the frequencies
	 */
	public List<Frequency> getFrequencies() {
		return frequencies;
	}
}
