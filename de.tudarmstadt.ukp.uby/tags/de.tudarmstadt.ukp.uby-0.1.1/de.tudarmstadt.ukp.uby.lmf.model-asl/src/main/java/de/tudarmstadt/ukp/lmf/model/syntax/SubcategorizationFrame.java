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

import java.util.Collections;
import java.util.List;

import de.tudarmstadt.ukp.lmf.model.interfaces.IHasID;
import de.tudarmstadt.ukp.lmf.model.meta.Frequency;
import de.tudarmstadt.ukp.lmf.model.miscellaneous.EVarType;
import de.tudarmstadt.ukp.lmf.model.miscellaneous.VarType;

public class SubcategorizationFrame implements IHasID, Comparable<SubcategorizationFrame>{
	// Id of this SubcategorizationFrame
	@VarType(type = EVarType.ATTRIBUTE)
	private String id;
	
	// links to superordinated (more generic) subcat frame
	@VarType(type = EVarType.IDREF)
	private SubcategorizationFrame parentSubcatFrame;
	
	// Subcategorization label of this SubcategorizationFrame
	@VarType(type = EVarType.ATTRIBUTE)
	private String subcatLabel;
	
	// LexemeProperty of this SubcategorizationFrame
	@VarType(type = EVarType.CHILD)
	private LexemeProperty lexemeProperty;
	
	// Syntactic Arguments of this SubcategorizationFrame
	@VarType(type = EVarType.CHILDREN)
	private List<SyntacticArgument> syntacticArguments;
	
	// Frequency information for this SubcategorizationFrame
	@VarType(type = EVarType.CHILDREN)
	private List<Frequency> frequencies;
	
	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @return the targets
	 */
	public SubcategorizationFrame getParentSubcatFrame() {
		return parentSubcatFrame;
	}

	/**
	 * @param parentSubcatFrame the targets to set
	 */
	public void setParentSubcatFrame(SubcategorizationFrame parentSubcatFrame) {
		this.parentSubcatFrame = parentSubcatFrame;
	}

	/**
	 * @return the subcatLabel
	 */
	public String getSubcatLabel() {
		return subcatLabel;
	}

	/**
	 * @param subcatLabel the subcatLabel to set
	 */
	public void setSubcatLabel(String subcatLabel) {
		this.subcatLabel = subcatLabel;
	}

	/**
	 * @return the lexemeProperty
	 */
	public LexemeProperty getLexemeProperty() {
		return lexemeProperty;
	}

	/**
	 * @param lexemeProperty the lexemeProperty to set
	 */
	public void setLexemeProperty(LexemeProperty lexemeProperty) {
		this.lexemeProperty = lexemeProperty;
	}

	/**
	 * @return the syntacticArguments
	 */
	public List<SyntacticArgument> getSyntacticArguments() {
		return syntacticArguments;
	}

	/**
	 * @param syntacticArguments the syntacticArguments to set
	 */
	public void setSyntacticArguments(List<SyntacticArgument> syntacticArguments) {
		this.syntacticArguments = syntacticArguments;
	}
	
	/**
	 * 
	 * @param frequencies the frequencies to set
	 */
	public void setFrequencies(List<Frequency> frequencies) {
		this.frequencies = frequencies;
	}

	/**
	 * 
	 * @return the frequencies
	 */
	public List<Frequency> getFrequencies() {
		return frequencies;
	}	
	
	public String toString(){
		StringBuffer sb = new StringBuffer();
		sb.append("SubcategorizationFrame ");
		sb.append("id:").append(id);
		sb.append(" parentSubcatFrame:").append(parentSubcatFrame);
		sb.append(" subcatLabel:").append(subcatLabel);
		sb.append(" lexemeProperty:").append(lexemeProperty);
		if(syntacticArguments != null)
			Collections.sort(syntacticArguments);
		sb.append(" syntacticArguments:").append(syntacticArguments);
		if(frequencies != null)
			Collections.sort(frequencies);
		sb.append(" frequencies").append(frequencies);
		
		return sb.toString();
	}

	@Override
	public int compareTo(SubcategorizationFrame o) {
		return this.toString().compareTo(o.toString());
	}
	
	public boolean equals(Object other) {
	    if (this == other)
	      return true;
	    if (!(other instanceof SubcategorizationFrame))
	      return false;
	    SubcategorizationFrame otherSubcategorizationFrame = (SubcategorizationFrame) other;
	    
	    boolean result = this.toString().equals(otherSubcategorizationFrame.toString());
	    return result;
	  }

	public int hashCode() { 
	    int hash = 1;
	    hash = hash * 31 + this.toString().hashCode();
	    return hash;
	  }
}
