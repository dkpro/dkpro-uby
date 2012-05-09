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

import de.tudarmstadt.ukp.lmf.model.miscellaneous.EVarType;
import de.tudarmstadt.ukp.lmf.model.miscellaneous.VarType;

/**
 * This class represents the oriented relationship between instances of SemanticPredicate
 * @author maksuti
 *
 */
public class PredicateRelation {
	
	// SemanticPredicate targeted by this relation
	@VarType(type = EVarType.IDREF)
	private SemanticPredicate target;

	// Type of this relation
	@VarType(type = EVarType.ATTRIBUTE)
	private String relType;
	
	// Name of this relation
	@VarType(type = EVarType.ATTRIBUTE)
	private String relName;
	
	/*
	 * Example:
	 * reference to the SemanticPredicate that is relevant
	 * for FrameNet's "preceedes" relation
	 */
	@VarType(type = EVarType.IDREF)
	private SemanticPredicate relevantSemanticPredicate;

	/**
	 * @param targets the targets to set
	 */
	public void setTarget(SemanticPredicate target) {
		this.target = target;
	}

	/**
	 * @return the targets
	 */
	public SemanticPredicate getTarget() {
		return target;
	}
	
	/**
	 * 
	 * @param relType the relType to set
	 */
	public void setRelType(String relType) {
		this.relType = relType;
	}

	/**
	 * 
	 * @return the relType
	 */
	public String getRelType() {
		return relType;
	}

	/**
	 * 
	 * @param relName the relName to set
	 */
	public void setRelName(String relName) {
		this.relName = relName;
	}

	/**
	 * 
	 * @return the relName
	 */
	public String getRelName() {
		return relName;
	}

	/**
	 * @return the relevantSemanticPredicate
	 */
	public SemanticPredicate getRelevantSemanticPredicate() {
		return relevantSemanticPredicate;
	}

	/**
	 * @param relevantSemanticPredicate the relevantSemanticPredicate to set
	 */
	public void setRelevantSemanticPredicate(SemanticPredicate relevantSemanticPredicate) {
		this.relevantSemanticPredicate = relevantSemanticPredicate;
	}
}
