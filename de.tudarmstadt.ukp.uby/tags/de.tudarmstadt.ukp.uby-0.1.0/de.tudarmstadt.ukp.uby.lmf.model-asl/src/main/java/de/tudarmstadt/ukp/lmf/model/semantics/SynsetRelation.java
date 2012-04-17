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

import java.util.List;

import de.tudarmstadt.ukp.lmf.model.enums.ERelTypeSemantics;
import de.tudarmstadt.ukp.lmf.model.meta.Frequency;
import de.tudarmstadt.ukp.lmf.model.miscellaneous.EVarType;
import de.tudarmstadt.ukp.lmf.model.miscellaneous.VarType;

/**
 * This class represents a relation to other Synsets
 * @author maksuti
 *
 */
public class SynsetRelation {
	
	// Synsets targeted by this Relation
	@VarType(type = EVarType.IDREF)
	private Synset target;
	
	// Type of this relation
	@VarType(type = EVarType.ATTRIBUTE)
	private ERelTypeSemantics relType;
	
	// Rough classification of this relation
	@VarType(type = EVarType.ATTRIBUTE)
	private String relName;
	
	// Frequency information for this SynsetRelation
	@VarType(type = EVarType.CHILDREN)
	private List<Frequency> frequencies;
	
	// Backlink to source Synset added for convenience
	@VarType(type = EVarType.NONE)
	private Synset source;
	
	
	/**
	 * @return the target
	 */
	public Synset getTarget() {
		return target;
	}

	/**
	 * @param target the targets to set
	 */
	public void setTarget(Synset target) {
		this.target = target;
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
	public Synset getSource() {
		return source;
	}

	/**
	 * @param source the source to set
	 */
	public void setSource(Synset source) {
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
