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

import de.tudarmstadt.ukp.lmf.model.meta.Frequency;
import de.tudarmstadt.ukp.lmf.model.miscellaneous.EVarType;
import de.tudarmstadt.ukp.lmf.model.miscellaneous.VarType;

/**
 * This class represents a Lemma
 * @author maksuti
 *
 */
public class Lemma {
	
	// FormRepresentations of this lemma
	@VarType(type = EVarType.CHILDREN)
	private List<FormRepresentation> formRepresentations;
	
	private String lexicalEntryId;
	
	// frequency information for this lemma
	@VarType(type = EVarType.CHILDREN)
	private List<Frequency> frequencies;
	
	

	/**
	 * @return the lexicalEntryId
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
	 * @param formRepresentations the formRepresentations to set
	 */
	public void setFormRepresentations(List<FormRepresentation> formRepresentations) {
		this.formRepresentations = formRepresentations;
	}

	/**
	 * @return the formRepresentations
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
