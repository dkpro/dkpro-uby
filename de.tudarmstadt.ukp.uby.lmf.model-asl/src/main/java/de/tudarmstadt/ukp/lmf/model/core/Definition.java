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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.tudarmstadt.ukp.lmf.model.enums.EDefinitionType;
import de.tudarmstadt.ukp.lmf.model.interfaces.IHasTextRepresentations;
import de.tudarmstadt.ukp.lmf.model.miscellaneous.EVarType;
import de.tudarmstadt.ukp.lmf.model.miscellaneous.VarType;
import de.tudarmstadt.ukp.lmf.model.semantics.SemanticArgument;

/**
 * Definition is a class representing a narrative description of a {@link Sense}.
 * It is displayed for human users to facilitate their understanding of the meaning of a
 * {@link LexicalEntry}.<br>
 * Instances of Definition class are also used for narrative description of a {@link SemanticArgument} instance.
 * 
 * @author Zijad Maksuti
 *
 */
public class Definition implements IHasTextRepresentations, Comparable<Definition>{

	// Definition Type of this Definition
	@VarType(type = EVarType.ATTRIBUTE)
	private EDefinitionType definitionType;
	
	// Statements of this Definition
	@VarType(type = EVarType.CHILDREN)
	private List<Statement> statements =  new ArrayList<Statement>();
	
	// Text Representations of this Definition
	@VarType(type = EVarType.CHILDREN)
	private List<TextRepresentation> textRepresentations = new ArrayList<TextRepresentation>();

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
	 * Returns definition type of this {@link Definition} instance
	 * @return this definitions type, or null if the type is not set
	 * @see EDefinitionType
	 */
	public EDefinitionType getDefinitionType() {
		return definitionType;
	}

	/**
	 * Sets definition type of this {@link Definition} instance
	 * @param definitionType the definition type to set
	 * @see EDefinitionType
	 */
	public void setDefinitionType(EDefinitionType definitionType) {
		this.definitionType = definitionType;
	}

	/**
	 * Returns the list of this definitions statements
	 * @return the statements of this {@link Definition} instance. <br>
	 * The method returns an empty list if the definition has no statements.
	 * @see Statement
	 */
	public List<Statement> getStatements() {
		return statements;
	}

	/**
	 * Sets a list of statements to this {@link Definition} instance
	 * @param statements the statements to set
	 * @see Statement
	 */
	public void setStatements(List<Statement> statements) {
		this.statements = statements;
	}

	/**
	 * Returns all text representations of this {@link Definition} instance
	 * @return {@link List} of this definitions text representations. <br>
	 * If the definition has no text representation, this method returns an empty list
	 * @see TextRepresentation
	 */
	public List<TextRepresentation> getTextRepresentations() {
		return textRepresentations;
	}

	/**
	 * Sets a {@link List} of text representations to this {@link Definition} instance.
	 * @param textRepresentations the text representations to set
	 * @see TextRepresentation
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
