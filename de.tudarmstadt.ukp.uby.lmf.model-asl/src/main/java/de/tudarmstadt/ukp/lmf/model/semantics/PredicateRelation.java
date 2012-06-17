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
 * This class represents the oriented relationship between instances of
 * {@link SemanticPredicate} class.
 * 
 * @author Zijad Maksuti
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
	 * for FrameNets "preceedes" relation
	 */
	@VarType(type = EVarType.IDREF)
	private SemanticPredicate relevantSemanticPredicate;

	/**
	 * Sets the {@link SemanticPredicate} instance targeted by this {@link PredicateRelation}.
	 * @param target the semantic predicate targeted by this predicate relation
	 */
	public void setTarget(SemanticPredicate target) {
		this.target = target;
	}

	/**
	 * Returns the {@link SemanticPredicate} instance targeted by this {@link PredicateRelation}.
	 * @return the semantic predicate targeted by this predicate relation or null if the target
	 * is not set
	 */
	public SemanticPredicate getTarget() {
		return target;
	}
	
	/**
	 * Sets the type of the relation represented by this {@link PredicateRelation} instance.
	 * @param relType the type of the predicate relation to set
	 */
	public void setRelType(String relType) {
		this.relType = relType;
	}

	/**
	 * Returns the type of the relation represented by this {@link PredicateRelation} instance.
	 * @return the type of this predicate relation or null if the type is not set
	 */
	public String getRelType() {
		return relType;
	}

	/**
	 * Sets the name of the relation represented by this {@link PredicateRelation} instance.
	 * @param relName the name of the predicate relation to set
	 */
	public void setRelName(String relName) {
		this.relName = relName;
	}

	/**
	 * Returns the name of the relation represented by this {@link PredicateRelation} instance.
	 * @return the name of this predicate relation or null if the name is not set
	 */
	public String getRelName() {
		return relName;
	}

	/**
	 * Returns a {@link SemanticPredicate} instance which is the parent in
	 * a relation of two SemanticPredicate instances.<br>
	 * I.E. semantic predicate in the FrameNets "Precedes" relation.   
	 * @return the parent in a relation of two semantic predicates or null if the parent
	 * is not set or the type of the relation does not specify its existence    
	 */
	public SemanticPredicate getRelevantSemanticPredicate() {
		return relevantSemanticPredicate;
	}

	/**
	 * Sets a {@link SemanticPredicate} instance which is the parent in
	 * a relation of two SemanticPredicate instances.<br>
	 * I.E. semantic predicate in the FrameNets "Precedes" relation.   
	 * @param relevantSemanticPredicate the parent in a relation of two semantic predicates or null if the parent
	 * is not set or the type of the relation does not specify its existence    
	 */
	public void setRelevantSemanticPredicate(SemanticPredicate relevantSemanticPredicate) {
		this.relevantSemanticPredicate = relevantSemanticPredicate;
	}
}
