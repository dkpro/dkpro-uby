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
package de.tudarmstadt.ukp.lmf.model.core;

import java.util.ArrayList;
import java.util.List;

import de.tudarmstadt.ukp.lmf.model.abstracts.HasTextRepresentations;
import de.tudarmstadt.ukp.lmf.model.enums.EStatementType;
import de.tudarmstadt.ukp.lmf.model.miscellaneous.EVarType;
import de.tudarmstadt.ukp.lmf.model.miscellaneous.VarType;

/**
 * Statement is a class representing a narrative description and refines
 * or complements {@link Definition}.
 */
public class Statement extends HasTextRepresentations
		implements Comparable<Statement> {

	// Statement Type of this Statement
	@VarType(type = EVarType.ATTRIBUTE)
	private EStatementType statementType;

	// Text Representations of this Statement
	@VarType(type = EVarType.CHILDREN)
	private List<TextRepresentation> textRepresentations = new ArrayList<TextRepresentation>();

	/**
	 * Returns the type of this {@link Statement} instance.
	 * @return the statements type or null, if the type is not set
	 * @see EStatementType
	 */
	public EStatementType getStatementType() {
		return statementType;
	}

	/**
	 * Sets the type of this {@link Statement} instance.
	 * @param statementType the statement type to set
	 * @see EStatementType
	 */
	public void setStatementType(EStatementType statementType) {
		this.statementType = statementType;
	}

	/**
	 * Returns the {@link List} of all {@link TextRepresentation} instances
	 * representing different textual contents of this {@link Statement}.
	 * @return all text representations of this statement or an empty list,
	 * if the statement does not have any text representations set
	 */
	public List<TextRepresentation> getTextRepresentations() {
		return textRepresentations;
	}

	/**
	 * Sets the {@link List} of all {@link TextRepresentation} instances to this
	 * {@link Statement} instance.
	 * @param textRepresentations the list of text representations to set
	 */
	public void setTextRepresentations(List<TextRepresentation> textRepresentations) {
		this.textRepresentations = textRepresentations;
	}

	@Override
	public String toString(){
		StringBuffer sb = new StringBuffer(512);
		sb.append("Statement ");
		sb.append("statementType:").append(statementType);
		sb.append(" textRepresentations:");
		sb.append(textRepresentations);
		return sb.toString();
	}

	@Override
	public int compareTo(Statement o) {
		return this.toString().compareTo(o.toString());
	}

	@Override
	public int hashCode(){
		return this.toString().hashCode();
	}

	@Override
	public boolean equals(Object other) {
	    if (this == other)
	      return true;
	    if (!(other instanceof Statement))
	      return false;
	    Statement otherStatement = (Statement) other;
	    return this.toString().equals(otherStatement.toString());
	 }

}
