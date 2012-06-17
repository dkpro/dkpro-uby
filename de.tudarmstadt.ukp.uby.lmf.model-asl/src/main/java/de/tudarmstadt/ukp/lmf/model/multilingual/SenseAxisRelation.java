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
package de.tudarmstadt.ukp.lmf.model.multilingual;

import de.tudarmstadt.ukp.lmf.model.miscellaneous.EVarType;
import de.tudarmstadt.ukp.lmf.model.miscellaneous.VarType;

/**
 * SenseAxisRelation class represents the relationship between two different
 * {@link SenseAxis} instances.
 * 
 * @author Zijad Maksuti
 *
 */
public class SenseAxisRelation {
	// Targets of this SenseAxisRelation
	@VarType(type = EVarType.IDREF)
	private SenseAxis target;
	
	// Relation type of this SenseAxisRelation
	@VarType(type = EVarType.ATTRIBUTE)
	private String relType;
	
	// Relation name of this SenseAxisRelation
	@VarType(type = EVarType.ATTRIBUTE)
	private String relName;

	/**
	 * Returns the {@link SenseAxis} instance targeted by this {@ink SenseAxisRelation}.
	 * @return the sense axis targeted by this relation or null if the relation does not
	 * have the target set
	 */
	public SenseAxis getTarget() {
		return target;
	}

	/**
	 * Sets the {@link SenseAxis} instance targeted by this {@link SenseAxisRelation}.
	 * @param target the sense axis to set
	 */
	public void setTarget(SenseAxis target) {
		this.target = target;
	}

	/**
	 * Returns the type of this {@link SenseAxisRelation} instance.
	 * @return the type of this relation or null if the type is not set
	 */
	public String getRelType() {
		return relType;
	}

	/**
	 * Sets the type of this {@link SenseAxisRelation} instance.
	 * @param relType the type of the relation to set
	 */
	public void setRelType(String relType) {
		this.relType = relType;
	}
	
	/**
	 * Sets the name of the relation represented by this {@link SenseAxisRelation} instance.
	 * @param relName the name to set
	 */
	public void setRelName(String relName) {
		this.relName = relName;
	}

	/**
	 * Returns the name of the relation represented by this {@link SenseAxisRelation} instance.
	 * @return the name of the relation represented by this sense axis relation or null
	 * if the name is not set
	 */
	public String getRelName() {
		return relName;
	}
}
