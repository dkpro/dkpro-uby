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
import de.tudarmstadt.ukp.lmf.model.miscellaneous.EVarType;
import de.tudarmstadt.ukp.lmf.model.miscellaneous.VarType;


/**
 * This class represents a subcategorization frame set
 * @author maksuti
 *
 */

public class SubcategorizationFrameSet implements IHasID, Comparable<SubcategorizationFrameSet>{
	// Id of this SubcategorizationFrameSet
	@VarType(type = EVarType.ATTRIBUTE)
	private String id;

	@VarType(type = EVarType.ATTRIBUTE)
	private String name;

	@VarType(type = EVarType.CHILDREN)
	private List<SubcatFrameSetElement> subcatFrameSetElements;
	
	@VarType(type = EVarType.CHILDREN)
	private List<SynArgMap> synArgMaps;
		
	@VarType(type = EVarType.IDREF)
	private SubcategorizationFrameSet parentSubcatFrame;

	/**
	 * @return the subcatFrameSetElements
	 */
	public List<SubcatFrameSetElement> getSubcatFrameSetElements() {
		return subcatFrameSetElements;
	}

	/**
	 * @param subcatFrameSetElements the subcatFrameSetElements to set
	 */
	public void setSubcatFrameSetElements(
			List<SubcatFrameSetElement> subcatFrameSetElements) {
		this.subcatFrameSetElements = subcatFrameSetElements;
	}

	/**
	 * @return the synArgMaps
	 */
	public List<SynArgMap> getSynArgMaps() {
		return synArgMaps;
	}

	/**
	 * @param synArgMaps the synArgMaps to set
	 */
	public void setSynArgMaps(List<SynArgMap> synArgMaps) {
		this.synArgMaps = synArgMaps;
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
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param id the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the parentSubcatFrame
	 */
	public SubcategorizationFrameSet getParentSubcatFrame() {
		return parentSubcatFrame;
	}

	/**
	 * @param parentSubcatFrame the parentSubcatFrame to set
	 */
	public void setParentSubcatFrame(SubcategorizationFrameSet parentSubcatFrame) {
		this.parentSubcatFrame = parentSubcatFrame;
	}

	
	public String toString(){
		StringBuffer sb = new StringBuffer(512);
		sb.append("SubcategorizationFrame ").append("id:")
		.append(this.id).append(" name: ").append(this.name);
		if(this.subcatFrameSetElements != null)
			Collections.sort(subcatFrameSetElements);
		sb.append(" subcatFrameSetElements: ").append(this.subcatFrameSetElements);
		if(this.synArgMaps != null)
			Collections.sort(this.synArgMaps);
		sb.append("parentSubcatFrame: ").append(this.parentSubcatFrame);
		return sb.toString();
	}

	@Override
	public int compareTo(SubcategorizationFrameSet o) {
		return this.toString().compareTo(o.toString());
	}
	
	public boolean equals(Object other) {
	    if (this == other)
	      return true;
	    if (!(other instanceof SubcategorizationFrameSet))
	      return false;
	    SubcategorizationFrameSet otherSubcategorizationFrameSet = (SubcategorizationFrameSet) other;
	    
	    boolean result = this.toString().equals(otherSubcategorizationFrameSet.toString());
	    return result;
	  }

	public int hashCode() { 
	    int hash = 1;
	    hash = hash * 31 + this.toString().hashCode();
	    return hash;
	  }

	
}
