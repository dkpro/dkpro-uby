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

import java.util.Collections;
import java.util.List;

import de.tudarmstadt.ukp.lmf.model.core.LexicalEntry;
import de.tudarmstadt.ukp.lmf.model.core.Sense;
import de.tudarmstadt.ukp.lmf.model.enums.ERelTypeMorphology;
import de.tudarmstadt.ukp.lmf.model.miscellaneous.EVarType;
import de.tudarmstadt.ukp.lmf.model.miscellaneous.VarType;

/**
 * This class represents a RelatedForm
 * @author maksuti
 *
 */
public class RelatedForm implements Comparable<RelatedForm> {
	
	// targeted LexicalEntrie
	@VarType(type = EVarType.IDREF)
	private LexicalEntry targetLexicalEntry;
	
	// Relation type
	@VarType(type = EVarType.ATTRIBUTE)
	private ERelTypeMorphology relType;
	
	// FormRepresentations of this RelatedForm
	@VarType(type = EVarType.CHILDREN)
	private List<FormRepresentation> formRepresentations;
	
	// targetedSense
	@VarType(type = EVarType.IDREF)
	private Sense targetSense;

	/**
	 * @return the target
	 */
	public LexicalEntry getTargetLexicalEntry() {
		return targetLexicalEntry;
	}

	/**
	 * @param target the target to set
	 */
	public void setTargetLexicalEntry(LexicalEntry target) {
		this.targetLexicalEntry = target;
	}

	/**
	 * @return the relType
	 */
	public ERelTypeMorphology getRelType() {
		return relType;
	}

	/**
	 * @param relType the relType to set
	 */
	public void setRelType(ERelTypeMorphology relType) {
		this.relType = relType;
	}

	/**
	 * @param targetSense the targetSense to set
	 */
	public void setTargetSense(Sense targetSense) {
		this.targetSense = targetSense;
	}

	/**
	 * @return the targetSense
	 */
	public Sense getTargetSense() {
		return targetSense;
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
	
	public String toString(){
		StringBuffer sb = new StringBuffer(256);
		if(this.formRepresentations != null)
			Collections.sort(formRepresentations);
		sb.append("RelatedForm ").append("targetLexicalEntry: ");
		if(this.targetLexicalEntry != null)
			sb.append(targetLexicalEntry.toString());
		sb.append(" relType: ");
		if(this.relType != null)
			sb.append(this.relType);
		sb.append(" formRepresentations:").append(formRepresentations);
		return sb.toString();
	}

	public int compareTo(RelatedForm arg0) {
		return this.toString().compareTo(arg0.toString());
	}
	
	public boolean equals(Object other) {
	    if (this == other)
	      return true;
	    if (!(other instanceof RelatedForm))
	      return false;
	    RelatedForm otherRelatedForm = (RelatedForm) other;
	    return this.toString().equals(otherRelatedForm.toString());
	  }
	
	public int hashCode() {
		int hash = 1;
		hash = hash*31 + this.toString().hashCode();
	    return hash;
	  }
	
}
