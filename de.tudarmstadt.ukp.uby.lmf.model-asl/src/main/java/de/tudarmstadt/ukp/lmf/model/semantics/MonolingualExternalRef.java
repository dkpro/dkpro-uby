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

/**
 * MonolingualExternalRef is a class representing the relationship between a UBY-LMF
 * class instance and an external system. 
 * 
 * @author Zijad Maksuti
 *
 */
public class MonolingualExternalRef implements Comparable<MonolingualExternalRef> {
	
	// this attribute uniquely determines the external system (for example WordNet 3.0 synset offset) 
	@VarType(type = EVarType.ATTRIBUTE)
	private String externalSystem;

	// the id of the referenced item in the External Resource
	@VarType(type = EVarType.ATTRIBUTE)
	private String externalReference;

	/**
	 * Sets a {@link String} representation of an external system that this
	 * {@link MonolingualExternalRef} instance is pointing to. Together with the
	 * external reference attribute, this value fully identifies a link of a LMF class instance
	 * to an external system.
	 * @param externalSystem string representation of the external system to which this
	 * monolingual external reference is pointing to
	 * 
	 * @see #setExternalSystem(String)
	 */
	public void setExternalSystem(String externalSystem) {
		this.externalSystem = externalSystem;
	}

	/**
	 * Returns a {@link String} representation of an external system that this
	 * {@link MonolingualExternalRef} instance is pointing to. Together with the
	 * external reference attribute, this value fully identifies a link of a LMF class instance
	 * to an external system.
	 * 
	 * @return externalSystem string representation of the external system to which this
	 * monolingual external reference is pointing to or null if this attribute is not set<p>
	 * <i>
	 * Note that according to UBY-LMF, all instances of MonolingualExternalRef class
	 * should have the value of this attribute set. Absence of external system information
	 * may indicate to incomplete conversion process of the original resource.
	 * </i>
	 * 
	 * @see #getExternalReference()
	 */
	public String getExternalSystem() {
		return externalSystem;
	}

	/**
	 * Sets the {@link String} representation of a particular node of an external system.
	 * Together with the external system attribute, this value fully identifies a link of a
	 * LMF class instance to an external system.
	 *  
	 * @param externalReference string representation a particular node of the external system to which this
	 * monolingual external reference is pointing to
	 * 
	 * @see #setExternalSystem(String)
	 */
	public void setExternalReference(String externalReference) {
		this.externalReference = externalReference;
	}

	/**
	 * Returns the {@link String} representation of a particular node of an external system.
	 * Together with the external system attribute, this value fully identifies a link of a
	 * LMF class instance to an external system. 
	 * 
	 * @return string representation a particular node of the external system to which this
	 * monolingual external reference is pointing to or null if this attribute is not set<p>
	 * <i>
	 * Note that according to UBY-LMF, all instances of MonolingualExternalRef class
	 * should have the value of this attribute set. Absence of external reference information
	 * may indicate to incomplete conversion process of the original resource.
	 * </i>
	 * 
	 * @see #getExternalSystem()
	 */
	public String getExternalReference() {
		return externalReference;
	}
	
	public String toString(){
		StringBuffer sb = new StringBuffer(64);
		sb.append("MonolingualExternalRef ");
		sb.append(" externalSystem: ").append(externalSystem);
		sb.append(" externalReference: ").append(externalReference);
		return sb.toString();
	}
	
	public int hashCode(){
		int hash = 1;
	    hash = hash * 31 + this.toString().hashCode();
	    return hash;
	}
	
	public boolean equals(Object o){
		if (this == o)
		      return true;
		    if (!(o instanceof MonolingualExternalRef))
		      return false;
		    MonolingualExternalRef otherMonolnugualExternalRef = (MonolingualExternalRef) o;
		return this.toString().equals(otherMonolnugualExternalRef.toString());
	}
	
	@Override
	public int compareTo(MonolingualExternalRef o) {
		return this.toString().compareTo(o.toString());
	}

}
