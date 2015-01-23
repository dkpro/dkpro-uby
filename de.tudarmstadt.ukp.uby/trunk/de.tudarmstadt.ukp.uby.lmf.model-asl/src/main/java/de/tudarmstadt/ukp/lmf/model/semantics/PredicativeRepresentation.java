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

import de.tudarmstadt.ukp.lmf.model.core.Sense;
import de.tudarmstadt.ukp.lmf.model.miscellaneous.EVarType;
import de.tudarmstadt.ukp.lmf.model.miscellaneous.VarType;

/**
 * PredicativeRepresentation class is a class representing the link between the {@link Sense} instance
 *  and the {@link SemanticPredicate} instances.
 *   
 * @author Zijad Maksuti
 *
 */
public class PredicativeRepresentation {
	// Semantic Predicate of this PredicativeRepresentation
	@VarType(type = EVarType.IDREF)
	private SemanticPredicate predicate;

	/**
	 * Returns the {@link SemanticPredicate} pointed by this {@link PredicativeRepresentation} instance.
	 * 
	 * @return the semantic predicate pointed by this predicative representation or null if the
	 * predicate is not set.<p>
	 * <i> Note that UBY-LMF requres that all instances of PredicativeRepresentation class should
	 * have the semantic predicate attribute set. Absence of this attribute may indicate to
	 * incomplete conversion process of the original resource.
	 * </i> 
	 */
	public SemanticPredicate getPredicate() {
		return predicate;
	}

	/**
	 * Sets the {@link SemanticPredicate} pointed by this {@link PredicativeRepresentation} instance.
	 * 
	 * @param predicate the semantic predicate pointed by this predicative representation to set<p>
	 * <i> Note that UBY-LMF requres that all instances of PredicativeRepresentation class should
	 * have the semantic predicate attribute set.
	 * </i>
	 */
	public void setPredicate(SemanticPredicate predicate) {
		this.predicate = predicate;
	}
}
