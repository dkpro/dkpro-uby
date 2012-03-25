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

import de.tudarmstadt.ukp.lmf.model.miscellaneous.EVarType;
import de.tudarmstadt.ukp.lmf.model.miscellaneous.VarType;

public class CoreArgumentSet {
/*	<!ELEMENT CoreArgumentSet EMPTY>
	<!ATTLIST CoreArgumentSet
	    elements IDREFS # IMPLIED>
	    */
	
	// semantic types of this SemanticPredicate
	@VarType(type = EVarType.IDREFS)
	private List<SemanticArgument> semanticArguments;

	/**
	 * 
	 * @param semanticArguments the semanticArguments to set
	 */
	public void setSemanticArguments(List<SemanticArgument> semanticArguments) {
		this.semanticArguments = semanticArguments;
	}

	/**
	 * 
	 * @return the semanticArguments
	 */
	public List<SemanticArgument> getSemanticArguments() {
		return semanticArguments;
	}
}
