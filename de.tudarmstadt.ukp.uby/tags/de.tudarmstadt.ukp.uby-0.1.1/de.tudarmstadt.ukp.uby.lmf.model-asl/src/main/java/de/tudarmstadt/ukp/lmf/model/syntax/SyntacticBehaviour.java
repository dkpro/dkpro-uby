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

import de.tudarmstadt.ukp.lmf.model.core.Sense;
import de.tudarmstadt.ukp.lmf.model.interfaces.IHasID;
import de.tudarmstadt.ukp.lmf.model.miscellaneous.EVarType;
import de.tudarmstadt.ukp.lmf.model.miscellaneous.VarType;

public class SyntacticBehaviour implements IHasID, Comparable<SyntacticBehaviour>{
	// Id of this SyntacticBehaviour
	@VarType(type = EVarType.ATTRIBUTE)
	private String id;
	
	// Sense of this SyntacticBehaviour
	@VarType(type = EVarType.IDREF)
	private Sense sense;
	
	// SubcategorizationFrame of this SyntacticBehaviour
	@VarType(type = EVarType.IDREF)
	private SubcategorizationFrame subcategorizationFrame;

	// SubcategorizationFrame of this SyntacticBehaviour
	@VarType(type = EVarType.IDREF)
	private SubcategorizationFrameSet subcategorizationFrameSet;
	
	/**
	 * @return the subcategorizationFrameSet
	 */
	public SubcategorizationFrameSet getSubcategorizationFrameSet() {
		return subcategorizationFrameSet;
	}

	/**
	 * @param subcategorizationFrameSet the subcategorizationFrameSet to set
	 */
	public void setSubcategorizationFrameSet(
			SubcategorizationFrameSet subcategorizationFrameSet) {
		this.subcategorizationFrameSet = subcategorizationFrameSet;
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
	 * @return the sense
	 */
	public Sense getSense() {
		return sense;
	}

	/**
	 * @param sense the sense to set
	 */
	public void setSense(Sense sense) {
		this.sense = sense;
	}

	/**
	 * @return the subcategorizationFrame
	 */
	public SubcategorizationFrame getSubcategorizationFrame() {
		return subcategorizationFrame;
	}

	/**
	 * @param subcategorizationFrame the subcategorizationFrame to set
	 */
	public void setSubcategorizationFrame(
			SubcategorizationFrame subcategorizationFrame) {
		this.subcategorizationFrame = subcategorizationFrame;
	}
	
	public String toString(){
		StringBuffer sb = new StringBuffer(512);
		sb.append("SyntacticBehaviour ").append(" id: ").append(id);
		sb.append( "Sense: ").append(sense);
		sb.append(" subcategorizationFrame: ").append(subcategorizationFrame);
		sb.append(" subcategorizationFrameSet: ").append(subcategorizationFrameSet);
		return sb.toString();
	}
	
	public int hashCode(){
		int hashCode = 1;
		return hashCode*31 + this.toString().hashCode();
	}
	
	public boolean equals(Object other) {
	    if (this == other)
	      return true;
	    if (!(other instanceof SyntacticBehaviour))
	      return false;
	    SyntacticBehaviour otherSyntacticBehaviour = (SyntacticBehaviour) other;
	    
	    boolean result=this.toString().equals(otherSyntacticBehaviour.toString());
	    return result;
	  }
	
	

	@Override
	public int compareTo(SyntacticBehaviour o) {
		return this.toString().compareTo(o.toString());
	}
}
