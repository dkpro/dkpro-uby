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
package de.tudarmstadt.ukp.lmf.model.semantics;

import de.tudarmstadt.ukp.lmf.model.miscellaneous.EVarType;
import de.tudarmstadt.ukp.lmf.model.miscellaneous.VarType;
import de.tudarmstadt.ukp.lmf.model.syntax.SyntacticArgument;

/**
 * This class represents a mapping between a 
 * {@link SemanticArgument} instance and a {@link SyntacticArgument} instance.
 * 
 * @author Zijad Maksuti
 *
 */
public class SynSemArgMap {
	
	// SyntacticArgument of the Mapping
	@VarType(type = EVarType.IDREF)
	private SyntacticArgument syntacticArgument;
	
	// SemanticArgument of the Mapping
	@VarType(type = EVarType.IDREF)
	private SemanticArgument semanticArgument;

	/**
	 * Returns the {@link SyntacticArgument} contained in this {@link SynSemArgMap} instance.
	 * 
	 * @return the syntactic argument contained in this mapping or null if the syntactic argument is not set.<p>
	 * 
	 * Note that UBY-LMF requires the presence of a SyntacticArgument in every SynSemArgMap instance.
	 * Its absence may indicate to incomplete conversion of the original resource. 
	 */
	public SyntacticArgument getSyntacticArgument() {
		return syntacticArgument;
	}

	/**
	 * Sets the {@link SyntacticArgument} contained in this {@link SynSemArgMap} instance.
	 * 
	 * @param syntacticArgument the syntactic argument to set
	 */
	public void setSyntacticArgument(SyntacticArgument syntacticArgument) {
		this.syntacticArgument = syntacticArgument;
	}

	/**
	 * Returns the {@link SemanticArgument} contained in this {@link SynSemArgMap} instance.
	 * 
	 * @return the semantic argument contained in this mapping or null if the semantic argument is not set.<p>
	 * 
	 * Note that UBY-LMF requires the presence of a SemanticArgument in every SynSemArgMap instance.
	 * Its absence may indicate to incomplete conversion of the original resource. 
	 */
	public SemanticArgument getSemanticArgument() {
		return semanticArgument;
	}

	/**
	 * Sets the {@link SemanticArgument} contained in this {@link SynSemArgMap} instance.
	 * 
	 * @param semanticArgument the syntactic argument to set
	 */
	public void setSemanticArgument(SemanticArgument semanticArgument) {
		this.semanticArgument = semanticArgument;
	}
	
	
	
}
