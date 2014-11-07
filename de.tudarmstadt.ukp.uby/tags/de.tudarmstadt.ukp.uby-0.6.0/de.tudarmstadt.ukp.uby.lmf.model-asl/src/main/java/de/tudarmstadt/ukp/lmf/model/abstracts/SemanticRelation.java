/*******************************************************************************
 * Copyright 2013
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
package de.tudarmstadt.ukp.lmf.model.abstracts;

import de.tudarmstadt.ukp.lmf.model.enums.ERelTypeSemantics;
import de.tudarmstadt.ukp.lmf.model.interfaces.ISemanticRelation;
import de.tudarmstadt.ukp.lmf.model.miscellaneous.EVarType;
import de.tudarmstadt.ukp.lmf.model.miscellaneous.VarType;

/**
 * A parent class for UBY-LMF classes that represent a semantic relation.
 * 
 * @author Zijad Maksuti
 * 
 * @param <Target> The class targeted by the relation
 *
 */
public class SemanticRelation<Target> extends HasFrequencies implements ISemanticRelation<Target> {
	
	// Backlink to source added for convenience
	@VarType(type = EVarType.NONE)
	protected Target source;
	
	// Targets of this Relation
	@VarType(type = EVarType.IDREF)
	protected Target target;
	
	// Type of this relation
	@VarType(type = EVarType.ATTRIBUTE)
	protected ERelTypeSemantics relType;
	
	// Rough classification of this relation
	@VarType(type = EVarType.ATTRIBUTE)
	protected String relName;

	@Override
	public ERelTypeSemantics getRelType() {
		return this.relType;
	}

	@Override
	public void setRelType(ERelTypeSemantics relType) {
		this.relType = relType;
		
	}

	@Override
	public Target getTarget() {
		return this.target;
	}

	@Override
	public void setTarget(Target target) {
		this.target = target;
		
	}

	@Override
	public String getRelName() {
		return this.relName;
	}

	@Override
	public void setRelName(String relName) {
		this.relName = relName;
	}
	
	/**
	 * Returns the UBY-LMF class instance representing the source of this {@link SemanticRelation}.<p>
	 * Note that this backlink is not a part of UBY-LMF and is added for convenience reasons.
	 * @return the source of the semantic relation or null if the source is not set
	 */
	public Target getSource() {
		return source;
	}

	/**
	 * Sets the UBY-LMF class instance representing the source of this {@link SemanticRelation}.<p>
	 * Note that this backlink is not a part of UBY-LMF and is added for convenience reasons.
	 * @param source the source to set
	 */
	public void setSource(Target source) {
		this.source = source;
	}

}
