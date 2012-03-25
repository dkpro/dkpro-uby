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
package de.tudarmstadt.ukp.lmf.model.meta;

import de.tudarmstadt.ukp.lmf.model.miscellaneous.EVarType;
import de.tudarmstadt.ukp.lmf.model.miscellaneous.VarType;

/**
 * This class represents a LMF Meta class for corpus frequencies
 * @author sh
 *
 */
public class Frequency implements Comparable<Frequency> {
/*	<!ELEMENT Frequency EMPTY>
	<!--TODO: Needs ID attribute?-->
	<!ATTLIST Frequency
	    corpus CDATA #IMPLIED
	    frequency CDATA #IMPLIED
	    generator CDATA #IMPLIED>
*/
	// corpus frequencies stem from
	@VarType(type = EVarType.ATTRIBUTE)
	private String corpus;
	
	// corpus frequency count
	@VarType(type = EVarType.ATTRIBUTE)
	private Integer frequency; //Integer or String?

	// generator-conditions for frequency counts
	@VarType(type = EVarType.ATTRIBUTE)
	private String generator;

	private String parentId;
	
	/**
	 * 
	 * @param corpus the corpus to set
	 */
	public void setCorpus(String corpus) {
		this.corpus = corpus;
	}

	/**
	 * 
	 * @return the corpus 
	 */
	public String getCorpus() {
		return corpus;
	}

	/**
	 * 
	 * @param frequency the frequency to set
	 */
	public void setFrequency(Integer frequency) {
		this.frequency = frequency;
	}

	/**
	 * 
	 * @return the frequency
	 */
	public Integer getFrequency() {
		return frequency;
	}

	/**
	 * 
	 * @param generator the generator to set
	 */
	public void setGenerator(String generator) {
		this.generator = generator;
	}

	/**
	 * 
	 * @return the generator
	 */
	public String getGenerator() {
		return generator;
	}

	/**
	 * 
	 * @param parentId the parentId to set
	 */
	public void setParentId(String parentId) {
		this.parentId = parentId;
	}

	/**
	 * 
	 * @return the parentId
	 */
	public String getParentId() {
		return parentId;
	}
	
	public String toString(){
		StringBuffer sb = new StringBuffer(256);
		sb.append("*Frequency* ");
		sb.append("Corpus: ").append(corpus);
		sb.append("frequency: ").append(frequency);
		sb.append("generator: ").append(generator);
		
		return sb.toString();
	}

	@Override
	public int compareTo(Frequency o) {
		return this.toString().compareTo(o.toString());
	}
	
	public int hashCode() { 
	    int hash = 1;
	    hash = hash * 31 + this.toString().hashCode();
	    return hash;
	  }
	
	public boolean equals(Object o){
		if (this == o)
		      return true;
		    if (!(o instanceof Frequency))
		    	return false;
		    Frequency oteherFrequency = (Frequency) o;
		    return this.toString().equals(oteherFrequency.toString());
	}
}
