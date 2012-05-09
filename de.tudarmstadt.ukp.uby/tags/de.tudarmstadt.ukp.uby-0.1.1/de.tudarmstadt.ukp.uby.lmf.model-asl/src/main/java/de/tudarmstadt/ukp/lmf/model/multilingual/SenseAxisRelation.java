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
	 * @return the targets
	 */
	public SenseAxis getTarget() {
		return target;
	}

	/**
	 * @param targets the targets to set
	 */
	public void setTarget(SenseAxis target) {
		this.target = target;
	}

	/**
	 * @return the relType
	 */
	public String getRelType() {
		return relType;
	}

	/**
	 * @param relType the relType to set
	 */
	public void setRelType(String relType) {
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
}
