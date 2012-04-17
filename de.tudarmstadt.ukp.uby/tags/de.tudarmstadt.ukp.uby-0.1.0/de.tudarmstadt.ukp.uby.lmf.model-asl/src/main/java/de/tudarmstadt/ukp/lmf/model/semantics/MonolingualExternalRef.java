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

import de.tudarmstadt.ukp.lmf.model.miscellaneous.EVarType;
import de.tudarmstadt.ukp.lmf.model.miscellaneous.VarType;

/**
 * This class represent a reference of a LMF-Class to
 * an external Resource
 * @author maksuti
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
	 * @param externalSystem the externalSystem to set
	 */
	public void setExternalSystem(String externalSystem) {
		this.externalSystem = externalSystem;
	}

	/**
	 * 
	 * @return the externalSystem
	 */
	public String getExternalSystem() {
		return externalSystem;
	}

	/**
	 * 
	 * @param externalReference the externalReference to set
	 */
	public void setExternalReference(String externalReference) {
		this.externalReference = externalReference;
	}

	/**
	 * 
	 * @return the externalReference
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
