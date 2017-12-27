/*******************************************************************************
 * Copyright 2017
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

/**
 * This class represents an oriented relationship between {@link SemanticArgument} instances.
 * 
 * @author Zijad Maksuti
 * 
 */
public class ArgumentRelation implements Comparable<ArgumentRelation> {
	
	// Semantic argument targeted by this relation
	@VarType(type = EVarType.IDREF)
	private SemanticArgument target;

	// Type of this relation
	@VarType(type = EVarType.ATTRIBUTE)
	private String relType;
	
	// Name of this relation
	@VarType(type = EVarType.ATTRIBUTE)
	private String relName;
	
	/**
	 * Returns the {@link SemanticArgument} instance targeted by this {@link ArgumentRelation}.
	 * @return the semantic argument targeted by this argument relation or null if the
	 * target is not set
	 */
	public SemanticArgument getTarget() {
		return target;
	}

	/**
	 * Sets the {@link SemanticArgument} instance targeted by this {@link ArgumentRelation}.
	 * @param target the targeted semantic argument to set 
	 */
	public void setTarget(SemanticArgument target) {
		this.target = target;
	}

	/**
	 * Sets the type of this {@link ArgumentRelation}.
	 * @param relType the type to set to this argument relation
	 */
	public void setRelType(String relType) {
		this.relType = relType;
	}

	/**
	 * Returns the type of this {@link ArgumentRelation} instance.
	 * 
	 * @return the type of this argument relation or null if the type is not set
	 */
	public String getRelType() {
		return relType;
	}

	/**
	 * Sets the name of the relation represented by this {@link ArgumentRelation} instance.
	 * @param relName the name to set to this argument relation
	 */
	public void setRelName(String relName) {
		this.relName = relName;
	}

	/**
	 * Returns the name of the relation represented by this {@link ArgumentRelation} instance.
	 * @return the name of this argument relation or null if the name is not set
	 */
	public String getRelName() {
		return relName;
	}
	
	/**
	 * 
	 */
	public String toString(){
		StringBuffer sb = new StringBuffer(512);
		sb.append("ArgumentRelation ").append("target: ").append(target.getId());
		sb.append("relTType: ").append(relType);
		sb.append("relName: ").append(relName);
		return sb.toString();
	}
	
	public boolean equals(Object other) {
	    if (this == other)
	      return true;
	    if (!(other instanceof ArgumentRelation))
	      return false;
	    ArgumentRelation otherArgumentRelation = (ArgumentRelation) other;
	    return this.toString().equals(otherArgumentRelation.toString());
	    }
	
	public int hashCode() { 
	    int hash = 1;
	    hash = hash * 31 + this.toString().hashCode();
	    return hash;
	  }
	
	public int compareTo(ArgumentRelation o) {
		return this.toString().compareTo(o.toString());
	}
}
