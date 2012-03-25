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

import de.tudarmstadt.ukp.lmf.model.enums.EStatementType;
import de.tudarmstadt.ukp.lmf.model.miscellaneous.EVarType;
import de.tudarmstadt.ukp.lmf.model.miscellaneous.VarType;

public class Statement implements Comparable<Statement> {
	
	// Statement Type of this Statement
	@VarType(type = EVarType.ATTRIBUTE)
	private EStatementType statementType;
	
	// Text Representations of this Statement
	@VarType(type = EVarType.CHILDREN)
	private List<TextRepresentation> textRepresentations;

	/**
	 * @return the statementType
	 */
	public EStatementType getStatementType() {
		return statementType;
	}

	/**
	 * @param statementType the statementType to set
	 */
	public void setStatementType(EStatementType statementType) {
		this.statementType = statementType;
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
	
	public String toString(){
		StringBuffer sb = new StringBuffer(512);
		sb.append("Statement ");
		sb.append("statementType:").append(statementType);
		sb.append(" textRepresentations:");
		if(textRepresentations != null)
			Collections.sort(textRepresentations);
		sb.append(textRepresentations);
		return sb.toString();	
	}

	@Override
	public int compareTo(Statement o) {
		return this.toString().compareTo(o.toString());
	}
	
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
