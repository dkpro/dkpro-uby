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
package de.tudarmstadt.ukp.lmf.model.syntax;

import de.tudarmstadt.ukp.lmf.model.core.LexicalEntry;
import de.tudarmstadt.ukp.lmf.model.enums.EAuxiliary;
import de.tudarmstadt.ukp.lmf.model.enums.ESyntacticProperty;
import de.tudarmstadt.ukp.lmf.model.miscellaneous.EVarType;
import de.tudarmstadt.ukp.lmf.model.miscellaneous.VarType;

/**
 * LexemeProperty is a class representing the central node of the {@link SubcategorizationFrame}.
 * A LexemeProperty instance connected to a SubcategorizationFrame instance is shared by all the {@link LexicalEntry}
 * instances that have the same syntactic behaviour.
 *  
 * @author Zijad Maksuti
 *
 */
public class LexemeProperty {
	
	// Auxiliary property of this LexemeProperty
	@VarType(type = EVarType.ATTRIBUTE)
	private EAuxiliary auxiliary;
	
	// Syntactic property of this LexemeProperty
	@VarType(type = EVarType.ATTRIBUTE)
	private ESyntacticProperty syntacticProperty;

	/**
	 * Returns the auxiliary property of the {@link LexicalEntry} connected to this {@link LexemeProperty} instance.
	 * 
	 * @return the auxiliary property of the connected lexical entries or null if the attribute is not set
	 * 
	 * @see EAuxiliary
	 */
	public EAuxiliary getAuxiliary() {
		return auxiliary;
	}

	/**
	 * Sets the auxiliary property of the {@link LexicalEntry} connected to this {@link LexemeProperty} instance .
	 * 
	 * @param auxiliary the property to set
	 * 
	 * @see EAuxiliary
	 */
	public void setAuxiliary(EAuxiliary auxiliary) {
		this.auxiliary = auxiliary;
	}

	/**
	 * Returns the syntactic property of a {@link LexicalEntry} connected to this {@link LexemeProperty} instance.
	 * 
	 * @return the syntactic property of the connected lexical entries or null if the attribute is not set
	 */
	public ESyntacticProperty getSyntacticProperty() {
		return syntacticProperty;
	}

	/**
	 * Sets the syntactic property of a {@link LexicalEntry} connected to this {@link LexemeProperty} instance.
	 * 
	 * @param syntacticProperty the syntactic property to set
	 */
	public void setSyntacticProperty(ESyntacticProperty syntacticProperty) {
		this.syntacticProperty = syntacticProperty;
	}
	
	
	
}
