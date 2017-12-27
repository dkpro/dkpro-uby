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
package de.tudarmstadt.ukp.lmf.model.meta;

import de.tudarmstadt.ukp.lmf.model.interfaces.IHasID;
import de.tudarmstadt.ukp.lmf.model.miscellaneous.EVarType;
import de.tudarmstadt.ukp.lmf.model.miscellaneous.VarType;

/**
 * This class represents a LMF meta class for storing of corpus frequencies of
 * particular classes.
 * 
 * @author Silvana Hartmann
 *
 */
public class Frequency implements Comparable<Frequency> {

	// corpus frequencies stem from
	@VarType(type = EVarType.ATTRIBUTE)
	private String corpus;
	
	// corpus frequency count
	@VarType(type = EVarType.ATTRIBUTE)
	private Integer frequency;

	// generator-conditions for frequency counts
	@VarType(type = EVarType.ATTRIBUTE)
	private String generator;

	private String parentId;

	@VarType(type = EVarType.NONE)
	private IHasID parent;
	
	/**
	 * Sets the name of the corpus which was used for extracting the values
	 * of this {@link Frequency} instance.
	 * 
	 * @param corpus the name of the corpus to set
	 */
	public void setCorpus(String corpus) {
		this.corpus = corpus;
	}

	/**
	 * Returns the name of the corpus which was used for extracting the values
	 * of this {@link Frequency} instance.
	 * 
	 * @return the name of the corpus or null if the name is not set
	 */
	public String getCorpus() {
		return corpus;
	}

	/**
	 * Sets the frequency count of this {@link Frequency} instance.
	 * 
	 * @param frequency the frequency count to set
	 */
	public void setFrequency(Integer frequency) {
		this.frequency = frequency;
	}

	/**
	 * Returns the frequency count of this {@link Frequency} instance.
	 * 
	 * @return the frequency count or null if the count is not set
	 */
	public Integer getFrequency() {
		return frequency;
	}

	/**
	 * Sets the name of the generator which is used for
	 * creating this {@link Frequency} instance.
	 * 
	 * @param generator the name of the generator to set
	 */
	public void setGenerator(String generator) {
		this.generator = generator;
	}

	/**
	 * Returns the name of the generator which is used for creating this
	 * {@link Frequency} instance.
	 * @return the name of the generator or null if the name is not set
	 */
	public String getGenerator() {
		return generator;
	}
	
	/**
	 * Sets the parent UBY-LMF class instance containing this
	 * {@link Frequency} instance.
	 * 
	 * @param parent the parent to set
	 * 
	 * @since UBY 0.2.0
	 */
	public void setParent(IHasID parent){
		this.parent = parent;
		this.parentId = parent.getId();
	}
	
	/**
	 * Returns the parent UBY-LMF class instance containing this
	 * {@link Frequency} instance.
	 * 
	 * @return the parent of this frequency or <code>null</code> if the parent
	 * is not set
	 * 
	 * @since UBY 0.2.0
	 */
	public IHasID getParent(){
		return this.parent;
	}

	/**
	 * @param parentId the parentId to set
	 * 
	 * @deprecated use {@link #setParent(IHasID)} instead
	 */
	public void setParentId(String parentId) {
		this.parentId = parentId;
	}

	/**
	 * @return the parentId
	 * 
	 * @deprecated use {@link #getParent()} instead
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
