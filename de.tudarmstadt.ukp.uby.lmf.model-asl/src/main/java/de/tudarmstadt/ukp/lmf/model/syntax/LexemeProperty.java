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
package de.tudarmstadt.ukp.lmf.model.syntax;

import de.tudarmstadt.ukp.lmf.model.enums.EAuxiliary;
import de.tudarmstadt.ukp.lmf.model.enums.ESyntacticProperty;
import de.tudarmstadt.ukp.lmf.model.miscellaneous.EVarType;
import de.tudarmstadt.ukp.lmf.model.miscellaneous.VarType;

public class LexemeProperty {
	
	// Auxiliary property of this LexemeProperty
	@VarType(type = EVarType.ATTRIBUTE)
	private EAuxiliary auxiliary;
	
	// Syntactic propertyof this LexemeProperty
	@VarType(type = EVarType.ATTRIBUTE)
	private ESyntacticProperty syntacticProperty;

	/**
	 * @return the auxiliary
	 */
	public EAuxiliary getAuxiliary() {
		return auxiliary;
	}

	/**
	 * @param auxiliary the auxiliary to set
	 */
	public void setAuxiliary(EAuxiliary auxiliary) {
		this.auxiliary = auxiliary;
	}

	/**
	 * @return the syntacticProperty
	 */
	public ESyntacticProperty getSyntacticProperty() {
		return syntacticProperty;
	}

	/**
	 * @param syntacticProperty the syntacticProperty to set
	 */
	public void setSyntacticProperty(ESyntacticProperty syntacticProperty) {
		this.syntacticProperty = syntacticProperty;
	}
	
	
	
}
