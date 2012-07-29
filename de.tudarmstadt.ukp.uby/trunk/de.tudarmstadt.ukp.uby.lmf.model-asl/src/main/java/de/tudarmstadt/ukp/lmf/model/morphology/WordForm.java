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

import java.util.ArrayList;
import java.util.List;

import de.tudarmstadt.ukp.lmf.model.abstracts.HasFrequencies;
import de.tudarmstadt.ukp.lmf.model.core.LexicalEntry;
import de.tudarmstadt.ukp.lmf.model.enums.ECase;
import de.tudarmstadt.ukp.lmf.model.enums.EDegree;
import de.tudarmstadt.ukp.lmf.model.enums.EGrammaticalGender;
import de.tudarmstadt.ukp.lmf.model.enums.EGrammaticalNumber;
import de.tudarmstadt.ukp.lmf.model.enums.EPerson;
import de.tudarmstadt.ukp.lmf.model.enums.ETense;
import de.tudarmstadt.ukp.lmf.model.enums.EVerbFormMood;
import de.tudarmstadt.ukp.lmf.model.meta.Frequency;
import de.tudarmstadt.ukp.lmf.model.miscellaneous.EVarType;
import de.tudarmstadt.ukp.lmf.model.miscellaneous.VarType;

/**
 * WordForm is a class representing a form that a lexeme, represented by a {@link LexicalEntry}
 * instance, can take when used in a sentence or a phrase. WordForm class contains
 * at least one {@link FormRepresentation} instance.
 * 
 * @author Zijad Maksuti
 *
 */
public class WordForm extends HasFrequencies{
	
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
	
	// degre of the word form
	@VarType(type = EVarType.ATTRIBUTE)
	private EDegree degree;
	
	// FormRepresentation of this WordForm
	@VarType(type = EVarType.CHILDREN)
	private List<FormRepresentation> formRepresentations = new ArrayList<FormRepresentation>();
	
	// Frequency information for this WordForm
	@VarType(type = EVarType.CHILDREN)
	private List<Frequency> frequencies = new ArrayList<Frequency>();
	

	/**
	 * Returns the grammatical number of this {@link WordForm} instance. 
	 * @return the grammatical number of this word form or null, if the
	 * word form does not have this attribute set
	 * @see EGrammaticalNumber
	 * @see #getFormRepresentations()
	 */
	public EGrammaticalNumber getGrammaticalNumber() {
		return grammaticalNumber;
	}

	/**
	 * Sets the grammatical number of this {@link WordForm} instance.
	 * @param grammaticalNumber the grammatical number to set
	 * @see EGrammaticalNumber
	 */
	public void setGrammaticalNumber(EGrammaticalNumber grammaticalNumber) {
		this.grammaticalNumber = grammaticalNumber;
	}

	/**
	 * Returns the grammatical gender of this {@link WordForm} instance. 
	 * @return the grammatical gender of this word form or null, if the
	 * word form does not have this attribute set
	 * @see EGrammaticalGender
	 * @see #getFormRepresentations()
	 */
	public EGrammaticalGender getGrammaticalGender() {
		return grammaticalGender;
	}

	/**
	 * Sets the grammatical gender of this {@link WordForm} instance.
	 * @param grammaticalGender the grammatical gender to set
	 * @see EGrammaticalGender
	 */
	public void setGrammaticalGender(EGrammaticalGender grammaticalGender) {
		this.grammaticalGender = grammaticalGender;
	}

	/**
	 * Returns the case attribute of this {@link WordForm} instance.
	 * @return the case of this word form or null, if the
	 * word form does not have this attribute set
	 * @see ECase
	 */
	public ECase getCase() {
		return _case;
	}

	/**
	 * Sets the case attribute of this {@link WordForm} instance.
	 * @param _case the case to set
	 * @see ECase
	 */
	public void setCase(ECase _case) {
		this._case = _case;
	}

	/**
	 * Returns the person attribute of this {@link WordForm} instance.
	 * 
	 * @return the person of this word form or null, if the
	 * attribute is not set
	 * 
	 * @see EPerson
	 */
	public EPerson getPerson() {
		return person;
	}

	/**
	 * Sets the person attribute of this {@link WordForm} instance.
	 * 
	 * @param person the person to set or null, if the
	 * attribute is not set
	 * 
	 * @see EPerson
	 */
	public void setPerson(EPerson person) {
		this.person = person;
	}

	/**
	 * Returns the tense of this {@link WordForm} instance.
	 * 
	 * @return the tense attribute of this word  form or null,
	 * if the attribute is not set
	 * 
	 * @see ETense
	 */
	public ETense getTense() {
		return tense;
	}

	/**
	 * Sets the tense attribute of this {@link WordForm} instance.
	 * 
	 * @param tense the tense attribute to set
	 * 
	 * @See ETense
	 */
	public void setTense(ETense tense) {
		this.tense = tense;
	}

	/**
	 * Return the mood of the verb described by this {@link WordForm} instance.
	 *  
	 * @return the mood of the verb described by this word form or null
	 * if the mood is not set
	 * 
	 * @see EVerbFormMood
	 */
	public EVerbFormMood getVerbFormMood() {
		return verbFormMood;
	}

	/**
	 * Sets the mood of the verb described by this {@link WordForm} instance.
	 * 
	 * @param verbFormMood the mood to set
	 * 
	 * @see EVerbFormMood
	 */
	public void setVerbFormMood(EVerbFormMood verbFormMood) {
		this.verbFormMood = verbFormMood;
	}

	/**
	 * Returns the {@link List} of all {@link FormRepresentation} instances of this
	 * {@link WordForm}.
	 * 
	 * @return the list of all form representations of this word form or an empty list,
	 * if the word form does not have any form representations set. <p>
	 * 
	 * <i>Note that, according to Uby-LMF, every word form contains
	 * at least one form representation. Absence of form representations in a word
	 * form may indicate an error in the conversion of the original resource.</i>
	 */
	public List<FormRepresentation> getFormRepresentations() {
		return formRepresentations;
	}

	/**
	 * Sets the {@link List} of all {@link FormRepresentation} instances of this
	 * {@link WordForm}.
	 * 
	 * @param formRepresentations the list of form representations to set. <p>
	 * 
	 * <i>Note that, according to Uby-LMF, every word form contains
	 * at least one form representation.
	 * </i>
	 * 
	 */
	public void setFormRepresentations(List<FormRepresentation> formRepresentations) {
		this.formRepresentations = formRepresentations;
	}
	
	/**
	 * Sets the {@link List} of all {@link Frequency} instances which
	 * represent the commonness of this {@link WordForm}. 
	 * @param frequencies the list of frequencies to set
	 */
	public void setFrequencies(List<Frequency> frequencies) {
		this.frequencies = frequencies;
	}

	/**
	 * Returns the {@link List} of all {@link Frequency} instances which
	 * represent the commonness of this {@link WordForm}.
	 * 
	 * @return the list of frequencies which represent the commonness of this
	 * word form or an empty list, if the word form does not have any
	 * frequencies set
	 */
	public List<Frequency> getFrequencies() {
		return frequencies;
	}

	/**
	 * Returns a degree of this {@link WordForm} instance.<br>
	 * This attribute only applies for adjectives and adverbs.
	 * 
	 * @return the degree of this word form or <code>null</code> if the degree is not set
	 * 
	 * @see EDegree
	 * 
	 * @since 0.2.0
	 */
	public EDegree getDegree() {
		return degree;
	}

	/**
	 * Sets the degree of this {@link WordForm} instance.<br>
	 * Note that only adjectives and adverbs can have a degree.
	 * 
	 * @param degree the degree to set
	 * 
	 * @since 0.2.0
	 */
	public void setDegree(EDegree degree) {
		this.degree = degree;
	}
}
