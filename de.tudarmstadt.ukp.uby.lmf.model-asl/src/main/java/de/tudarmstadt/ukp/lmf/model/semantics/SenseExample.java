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

import de.tudarmstadt.ukp.lmf.model.core.TextRepresentation;
import de.tudarmstadt.ukp.lmf.model.enums.EExampleType;
import de.tudarmstadt.ukp.lmf.model.interfaces.IHasID;
import de.tudarmstadt.ukp.lmf.model.miscellaneous.EVarType;
import de.tudarmstadt.ukp.lmf.model.miscellaneous.VarType;

/**
 * This class represents the particular meaning of a Sense
 * @author maksuti
 *
 */
public class SenseExample implements IHasID, Comparable<SenseExample> {
	
	// Unique Id of this SenseExample
	@VarType(type = EVarType.ATTRIBUTE)
	private String id;
	
	// TextRepresentation of this SenseExample
	@VarType(type = EVarType.CHILDREN)
	private List<TextRepresentation> textRepresentations;
	
	// Example type of this Sense Example
	@VarType(type = EVarType.ATTRIBUTE)
	private EExampleType exampleType;
	
	/**
	 * @return the exampleType
	 */
	public EExampleType getExampleType() {
		return exampleType;
	}

	/**
	 * @param exampleType the exampleType to set
	 */
	public void setExampleType(EExampleType exampleType) {
		this.exampleType = exampleType;
	}

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
		return this.id.toString();
	}

	@Override
	public int compareTo(SenseExample o) {
		return this.toString().compareTo(o.toString());
	}
	
	public boolean equals(Object other) {
	    if (this == other)
	      return true;
	    if (!(other instanceof SenseExample))
	      return false;
	    SenseExample otherSenseExample = (SenseExample) other;
	    return this.id.equals(otherSenseExample.id);
	   
	  }
	
	public int hashCode() { 
	    int hash = 1;
	    hash = hash * 31 + this.id.hashCode();
	    return hash;
	  }	
}
