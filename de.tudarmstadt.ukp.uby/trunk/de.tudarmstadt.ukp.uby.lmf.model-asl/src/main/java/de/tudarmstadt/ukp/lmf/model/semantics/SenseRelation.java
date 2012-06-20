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
package de.tudarmstadt.ukp.lmf.model.semantics;

import java.util.Collections;
import java.util.List;

import de.tudarmstadt.ukp.lmf.model.core.Sense;
import de.tudarmstadt.ukp.lmf.model.enums.ERelTypeSemantics;
import de.tudarmstadt.ukp.lmf.model.interfaces.IHasFrequencies;
import de.tudarmstadt.ukp.lmf.model.meta.Frequency;
import de.tudarmstadt.ukp.lmf.model.miscellaneous.EVarType;
import de.tudarmstadt.ukp.lmf.model.miscellaneous.VarType;
import de.tudarmstadt.ukp.lmf.model.morphology.FormRepresentation;

/**
 * This class represents an oriented relationship between Senses
 * @author maksuti
 *
 */
public class SenseRelation implements IHasFrequencies, Comparable<SenseRelation>{
	
	
	// Frequency information for this SenseRelation
	@VarType(type = EVarType.CHILDREN)
	private List<Frequency> frequencies;
	
	// Sense targeted by this relation
	@VarType(type = EVarType.IDREF)
	private Sense target;
	
	@VarType(type = EVarType.CHILD)
	private FormRepresentation targetFormRepresentation;
	
	// rough classification of this relation
	@VarType(type = EVarType.ATTRIBUTE)
	private ERelTypeSemantics relType;
	
	@VarType(type = EVarType.ATTRIBUTE)
	private String relName;
	
	// Backlink to source Sense added for convenience
	@VarType(type = EVarType.NONE)
	private Sense source;
	
	/**
	 * @return the target
	 */
	public Sense getTarget() {
		return target;
	}

	/**
	 * @param target the target to set
	 */
	public void setTarget(Sense target) {
		this.target = target;
	}

	/**
	 * @return the targetFormRepresentation
	 */
	public FormRepresentation getTargetFormRepresentation() {
		return targetFormRepresentation;
	}

	/**
	 * @param targetFormRepresentation the targetFormRepresentation to set
	 */
	public void setTargetFormRepresentation(FormRepresentation targetFormRepresentation) {
		this.targetFormRepresentation = targetFormRepresentation;
	}

	/**
	 * @return the relType
	 */
	public ERelTypeSemantics getRelType() {
		return relType;
	}

	/**
	 * @param relType the relType to set
	 */
	public void setRelType(ERelTypeSemantics relType) {
		this.relType = relType;
	}

	public String toString(){
		StringBuffer sb = new StringBuffer(512);
		sb.append("SenseRelation ").append("target:")
		.append(this.target).append(" targetLemma: ").append(this.targetFormRepresentation)
		.append(" relType: ").append(this.relType).append("relName: ").append(this.relName);
		sb.append("source: ").append(source);
		sb.append("frequencies: ");
		if(this.frequencies != null)
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
	


	/**
	 * @param relName the relName to set
	 */
	public void setRelName(String relName) {
		this.relName = relName;
	}

	/**
	 * @return the relName
	 */
	public String getRelName() {
		return relName;
	}

	/**
	 * @return the source
	 */
	public Sense getSource() {
		return source;
	}

	/**
	 * @param source the source to set
	 */
	public void setSource(Sense source) {
		this.source = source;
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
