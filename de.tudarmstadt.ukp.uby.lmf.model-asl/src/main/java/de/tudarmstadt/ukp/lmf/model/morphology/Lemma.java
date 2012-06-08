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

import de.tudarmstadt.ukp.lmf.model.meta.Frequency;
import de.tudarmstadt.ukp.lmf.model.miscellaneous.EVarType;
import de.tudarmstadt.ukp.lmf.model.miscellaneous.VarType;

/**
 * Lemma is a class representing a form chosen by convention to designate the {@link LexicalEntry}.
 * The Lemma class is in a one to one aggregate association with the  lexical entry class and contains
 * at least one {@link FormRepresentation} instance.
 *  
 * @author Zijad Maksuti
 *
 */
public class Lemma {
	
	// FormRepresentations of this lemma
	@VarType(type = EVarType.CHILDREN)
	private List<FormRepresentation> formRepresentations = new ArrayList<FormRepresentation>();
	
	private String lexicalEntryId;
	
	// frequency information for this lemma
	@VarType(type = EVarType.CHILDREN)
	private List<Frequency> frequencies  = new ArrayList<Frequency>();
	
	

	/**
	 * Returns the unique identifier of the {@link LexicalEntry} instance associated with this
	 * {@link Lemma}.
	 * @return the unique identifier of the lexical entry associated with this lemma
	 */
	public String getLexicalEntryId() {
		return lexicalEntryId;
	}

	/**
	 * @param lexicalEntryId the lexicalEntryId to set
	 */
	public void setLexicalEntryId(String lexicalEntryId) {
		this.lexicalEntryId = lexicalEntryId;
	}

	/**
	 * Sets the {@link List} of all {@link FormRepresentation} instances of this {@link Lemma}.
	 * @param formRepresentations the list of all form representations to set
	 */
	public void setFormRepresentations(List<FormRepresentation> formRepresentations) {
		this.formRepresentations = formRepresentations;
	}

	/**
	 * Returns the {@link List} of all {@link FormRepresentation} instances of this {@link Lemma}
	 * instance.
	 * @return all form representation of this lemma or an empty list, if the lemma does not have any form
	 * representations set. <p>
	 * <i> Note that Uby-LMF requires that every lemma has at least one form representation. Absence
	 * of form representations may happen as a result of incomplete conversion of the original resource.</i>
	 */
	public List<FormRepresentation> getFormRepresentations() {
		return formRepresentations;
	}

	public void setFrequencies(List<Frequency> frequencies) {
		this.frequencies = frequencies;
	}

	public List<Frequency> getFrequencies() {
		return frequencies;
	}


}
