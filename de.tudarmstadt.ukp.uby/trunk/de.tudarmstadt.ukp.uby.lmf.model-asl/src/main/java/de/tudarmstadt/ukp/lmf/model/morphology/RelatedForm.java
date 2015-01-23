/*******************************************************************************
 * Copyright 2015
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
import java.util.Collections;
import java.util.List;

import de.tudarmstadt.ukp.lmf.model.core.LexicalEntry;
import de.tudarmstadt.ukp.lmf.model.core.Sense;
import de.tudarmstadt.ukp.lmf.model.enums.ERelTypeMorphology;
import de.tudarmstadt.ukp.lmf.model.miscellaneous.EVarType;
import de.tudarmstadt.ukp.lmf.model.miscellaneous.VarType;

/**
 * RelatedForm is a class representing a word form or a morph that can be related to the
 * {@link LexicalEntry} in one of a variety of ways (e.g. derivation, root).
 * 
 * @author Zijad Maksuti
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
	private List<FormRepresentation> formRepresentations = new ArrayList<FormRepresentation>();
	
	// targetedSense
	@VarType(type = EVarType.IDREF)
	private Sense targetSense;

	/**
	 * Returns the targeted {@link LexicalEntry} of the relation represented by
	 * this {@link RelatedForm} instance.
	 * @return the targeted lexical entry or null, if the related form does
	 * not have a targeted lexical entry set
	 */
	public LexicalEntry getTargetLexicalEntry() {
		return targetLexicalEntry;
	}

	/**
	 * Sets the targeted {@link LexicalEntry} of the relation represented by
	 * this {@link RelatedForm} instance.
	 * @param target the targeted lexical entry to set
	 */
	public void setTargetLexicalEntry(LexicalEntry target) {
		this.targetLexicalEntry = target;
	}

	/**
	 * Returns the type of the relation represented by this {@link RelatedForm}
	 * instance.
	 * @return the relation type of this related form or null, if the related form
	 * does not have the relation type set
	 * @see ERelTypeMorphology
	 */
	public ERelTypeMorphology getRelType() {
		return relType;
	}

	/**
	 * Sets the type of the relation represented by this {@link RelatedForm} instance.
	 * @param relType the relation type to set
	 * @see ERelTypeMorphology
	 */
	public void setRelType(ERelTypeMorphology relType) {
		this.relType = relType;
	}

	/**
	 * Sets the targeted {@link Sense} of the relation represented by
	 * this {@link RelatedForm} instance.
	 * @param targetSense the targeted sense to set
	 */
	public void setTargetSense(Sense targetSense) {
		this.targetSense = targetSense;
	}

	/**
	 * Returns the targeted {@link Sense} of the relation represented by
	 * this {@link RelatedForm} instance.
	 * @return the targeted sense or null, if the related form does
	 * not have a targeted sense set
	 */
	public Sense getTargetSense() {
		return targetSense;
	}

	/**
	 * Returns the {@link List} of all {@link FormRepresentation} instances of the
	 * {@link Lemma} associated to the {@link LexicalEntry} targeted by the
	 * relation represented by this {@link RelatedForm} instance.
	 *  
	 * @return the list of all form representations of the lemma associated
	 * to the lexical entry targeted by this related form.<br>
	 * If the targeted lexical entry's lemma does not have any form representations,
	 * this method should return an empty list.
	 * 
	 * @see RelatedForm#getTargetLexicalEntry()
	 * @see RelatedForm#setFormRepresentations(List)
	 */
	public List<FormRepresentation> getFormRepresentations() {
		return formRepresentations;
	}

	/**
	 * Sets the {@link List} of all {@link FormRepresentation} instances of the
	 * {@link Lemma} associated to the {@link LexicalEntry} targeted by the
	 * relation represented by this {@link RelatedForm} instance.
	 *  
	 * @param formRepresentations the list of all form representations of the lemma associated
	 * to the lexical entry targeted by this related form to set<br>
	 * 
	 * @see RelatedForm#getTargetLexicalEntry()
	 */
	public void setFormRepresentations(List<FormRepresentation> formRepresentations) {
		this.formRepresentations = formRepresentations;
	}
	
	public String toString(){
		StringBuffer sb = new StringBuffer(256);
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
