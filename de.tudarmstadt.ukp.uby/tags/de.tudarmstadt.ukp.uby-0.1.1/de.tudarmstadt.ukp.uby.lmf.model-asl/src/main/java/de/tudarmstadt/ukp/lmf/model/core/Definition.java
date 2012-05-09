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
package de.tudarmstadt.ukp.lmf.model.core;

import java.util.Collections;
import java.util.List;

import de.tudarmstadt.ukp.lmf.model.enums.EDefinitionType;
import de.tudarmstadt.ukp.lmf.model.miscellaneous.EVarType;
import de.tudarmstadt.ukp.lmf.model.miscellaneous.VarType;

public class Definition implements Comparable<Definition>{

	// Definition Type of this Definition
	@VarType(type = EVarType.ATTRIBUTE)
	private EDefinitionType definitionType;
	
	// Statements of this Definition
	@VarType(type = EVarType.CHILDREN)
	private List<Statement> statements;
	
	// Text Representations of this Definition
	@VarType(type = EVarType.CHILDREN)
	private List<TextRepresentation> textRepresentations;

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return this.toString().hashCode();
	}

	@Override
	public boolean equals(Object other) {
		 if (this == other)
		      return true;
		    if (!(other instanceof Definition))
		      return false;
		    Definition otherDefinition = (Definition) other;
		    return this.toString().equals(otherDefinition.toString());	
	}

	/**
	 * @return the definitionType
	 */
	public EDefinitionType getDefinitionType() {
		return definitionType;
	}

	/**
	 * @param definitionType the definitionType to set
	 */
	public void setDefinitionType(EDefinitionType definitionType) {
		this.definitionType = definitionType;
	}

	/**
	 * @return the statements
	 */
	public List<Statement> getStatements() {
		return statements;
	}

	/**
	 * @param statements the statements to set
	 */
	public void setStatements(List<Statement> statements) {
		this.statements = statements;
	}

	/**
	 * @return the textRepresentations
	 */
	public List<TextRepresentation> getTextRepresentations() {
		return textRepresentations;
	}

	/**
	 * @param textRepresentations the textRepresentations to set
	 */
	public void setTextRepresentations(List<TextRepresentation> textRepresentations) {
		this.textRepresentations = textRepresentations;
	}

	@Override
	public int compareTo(Definition o) {
		return this.toString().compareTo(o.toString());
	}
	
	public String toString(){
		StringBuffer sb = new StringBuffer();
		sb.append("Definition ").append("definitionType: ").append(definitionType);
		sb.append(" statements:");
		if(statements != null)
			Collections.sort(statements);
		sb.append(statements);
		sb.append(" textRepresentations:");
		if(textRepresentations != null)
			Collections.sort(textRepresentations);
		sb.append(textRepresentations);
		return sb.toString();
	}
	
	
}
