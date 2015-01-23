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
package de.tudarmstadt.ukp.lmf.model.semantics;

import java.util.Collections;

import de.tudarmstadt.ukp.lmf.model.abstracts.SemanticRelation;
import de.tudarmstadt.ukp.lmf.model.core.LexicalEntry;
import de.tudarmstadt.ukp.lmf.model.core.Sense;
import de.tudarmstadt.ukp.lmf.model.enums.ERelTypeSemantics;
import de.tudarmstadt.ukp.lmf.model.miscellaneous.EVarType;
import de.tudarmstadt.ukp.lmf.model.miscellaneous.VarType;
import de.tudarmstadt.ukp.lmf.model.morphology.FormRepresentation;

/**
 * SenseRelation is a class represents an oriented relationship between {@link Sense} instances.
 * 
 * @author Zijad Maksuti
 *
 */
public class SenseRelation extends SemanticRelation<Sense> implements Comparable<SenseRelation>{
	
	@VarType(type = EVarType.CHILD)
	private FormRepresentation formRepresentation;
	
	/**
	 * Returns the targeted {@link Sense} of this {@link SenseRelation} instance.
	 * @return the sense targeted by this relation or null if the target is not set
	 */
	public Sense getTarget() {
		return target;
	}

	/**
	 * Sets the targeted {@link Sense} of this {@link SenseRelation} instance.
	 * @param target the targeted sense to set
	 */
	public void setTarget(Sense target) {
		this.target = target;
	}

	/**
	 * Returns a {@link FormRepresentation} instance which contains
	 * the written form of the {@link Sense} targeted by this
	 * {@link SenseRelation} instance.<p>
	 * Note that this reference is set only if the {@link LexicalEntry}
	 * containing the targeted Sense does not exist.
	 * 
	 * @return the form representation of the sense targeted by this
	 * sense relation or null if the form representation is not set
	 */
	public FormRepresentation getFormRepresentation() {
		return formRepresentation;
	}

	/**
	 * Sets a {@link FormRepresentation} instance which contains
	 * the written form of the {@link Sense} targeted by this
	 * {@link SenseRelation} instance.<p>
	 * Note that this reference should only be set if the {@link LexicalEntry}
	 * containing the targeted Sense does not exist.
	 * 
	 * @param formRepresentation the form representation containing the written form
	 * of the sense targeted by this sense relation or null if the form representation is not set
	 */
	public void setFormRepresentation(FormRepresentation formRepresentation) {
		this.formRepresentation = formRepresentation;
	}

	/**
	 * Returns the type of the relation represented by this {@link SenseRelation} instance.
	 * 
	 * @return the type of the sense relation
	 * 
	 * @see ERelTypeSemantics
	 */
	public ERelTypeSemantics getRelType() {
		return relType;
	}

	public void setRelType(ERelTypeSemantics relType) {
		this.relType = relType;
	}

	public String toString(){
		StringBuffer sb = new StringBuffer(512);
		sb.append("SenseRelation ").append("target:")
		.append(this.target).append(" targetLemma: ").append(this.formRepresentation)
		.append(" relType: ").append(this.relType).append("relName: ").append(this.relName);
		sb.append("source: ").append(source);
		sb.append("frequencies: ");
		Collections.sort(frequencies);
		sb.append(frequencies);
		
		return sb.toString();
	}

	public int compareTo(SenseRelation o) {
		return this.toString().compareTo(o.toString());
	}
	
	public boolean equals(Object other) {
	    if (this == other)
	      return true;
	    if (!(other instanceof SenseRelation))
	      return false;
	    SenseRelation otherSenseRelation = (SenseRelation) other;
	    
	    return this.toString().equals(otherSenseRelation.toString());
	  }

	public int hashCode() {
	    int hash = 1;
		hash = hash * 31 + this.toString().hashCode();
		return hash;
	  }
	

	public void setRelName(String relName) {
		this.relName = relName;
	}

	public String getRelName() {
		return relName;
	}

	/**
	 * Returns the {@link Sense} instance representing the source of this {@link SenseRelation}.<p>
	 * Note that this backlink is not a part of UBY-LMF and is added for convenience reasons.
	 * @return the source of the semantic relation or null if the source is not set
	 */
	public Sense getSource() {
		return super.getSource();
	}

	/**
	 * Sets the {@link Sense} instance representing the source of this {@link SenseRelation}.<p>
	 * Note that this backlink is not a part of UBY-LMF and is added for convenience reasons.
	 * @param source the source to set
	 */
	public void setSource(Sense source) {
		super.setSource(source);
	}
	
}
