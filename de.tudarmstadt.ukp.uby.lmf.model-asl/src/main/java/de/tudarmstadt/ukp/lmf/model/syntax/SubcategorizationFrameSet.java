/*******************************************************************************
 * Copyright 2015
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.tudarmstadt.ukp.lmf.model.interfaces.IHasID;
import de.tudarmstadt.ukp.lmf.model.miscellaneous.EVarType;
import de.tudarmstadt.ukp.lmf.model.miscellaneous.VarType;


/**
 * SubcategorizationFrameSet  is a class representing a set of syntactic constructions,
 * represented by {@link SubcatFrameSetElement} instances. A SubcategorizationFrameSet can
 * inherit attributes from another more generic SubcategorizationFrameSet.
 * Therefore, it is possible to integrate a hierarchical structure of SubcategorizationFrameSet instances.
 * 
 * @author Zijad Maksuti
 * @author Judith Eckle-Kohler
 *
 */

public class SubcategorizationFrameSet implements IHasID, Comparable<SubcategorizationFrameSet>{
	// Id of this SubcategorizationFrameSet
	@VarType(type = EVarType.ATTRIBUTE)
	private String id;

	@VarType(type = EVarType.ATTRIBUTE)
	private String name;

	@VarType(type = EVarType.CHILDREN)
	private List<SubcatFrameSetElement> subcatFrameSetElements = new ArrayList<SubcatFrameSetElement>();
	
	@VarType(type = EVarType.CHILDREN)
	private List<SynArgMap> synArgMaps = new ArrayList<SynArgMap>();
		
	@VarType(type = EVarType.IDREF)
	private SubcategorizationFrameSet parentSubcatFrameSet;

	/**
	 * Returns a set of syntactic constructions, represented by a {@link List} of {@link SubcatFrameSetElement} instances,
	 * of this {@link SubcategorizationFrameSet}.
	 * 
	 * @return the list of elements of this subcategorization frame set or an empty list
	 * if this set does not contain any elements
	 */
	public List<SubcatFrameSetElement> getSubcatFrameSetElements() {
		return subcatFrameSetElements;
	}

	/**
	 * Sets a set of syntactic constructions, represented by a {@link List} of {@link SubcatFrameSetElement} instances,
	 * of this {@link SubcategorizationFrameSet}.
	 * 
	 * @param subcatFrameSetElements the list of elements of this subcategorization frame set to set
	 */
	public void setSubcatFrameSetElements(List<SubcatFrameSetElement> subcatFrameSetElements) {
		this.subcatFrameSetElements = subcatFrameSetElements;
	}

	/**
	 * Returns a {@link List} of {@link SynArgMap} instances which represent the relationship
	 * that maps various  {@link SyntacticArgument} instances of this {@link SubcategorizationFrameSet} instance.
	 *  
	 * @return the list of syntactic argument mappings of this subcategorization frame set or an empty list
	 * if the subcategorization frame set does not contain any mappings
	 */
	public List<SynArgMap> getSynArgMaps() {
		return synArgMaps;
	}

	/**
	 * Sets a {@link List} of {@link SynArgMap} instances which represent the relationship
	 * that maps various  {@link SyntacticArgument} instances of this {@link SubcategorizationFrameSet} instance.
	 *  
	 * @param synArgMaps the list of syntactic argument mappings of this subcategorization frame set to set
	 * 
	 */
	public void setSynArgMaps(List<SynArgMap> synArgMaps) {
		this.synArgMaps = synArgMaps;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	/**
	 * Returns a {@link String} which represents the name of this {@link SubcategorizationFrameSet} instance.
	 * 
	 * @return the name of this subcategorization frame set instance or null if the name is not set
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets a {@link String} which represents the name of this {@link SubcategorizationFrameSet} instance.
	 * 
	 * @param the name of this subcategorization frame set instance to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Returns a more generic "parent" {@link SubcategorizationFrameSet} instance of this
	 * SubcategorizationFrameSet instance.
	 * 
	 * @return the more generic "parent" subcategorization frame set or null if this
	 * subcategorization frame set does not have a parent
	 * 
	 */
	public SubcategorizationFrameSet getParentSubcatFrameSet() {
		return parentSubcatFrameSet;
	}

	/**
	 * Sets a more generic "parent" {@link SubcategorizationFrameSet} instance of this
	 * SubcategorizationFrameSet instance.
	 * 
	 * @param the more generic "parent" subcategorization frame set to set
	 * 
	 */
	public void setParentSubcatFrameSet(SubcategorizationFrameSet parentSubcatFrameSet) {
		this.parentSubcatFrameSet = parentSubcatFrameSet;
	}

	
	public String toString(){
		StringBuffer sb = new StringBuffer(512);
		sb.append("SubcategorizationFrame ").append("id:")
		.append(this.id).append(" name: ").append(this.name);
		//Collections.sort(subcatFrameSetElements);
		sb.append(" subcatFrameSetElements: ").append(this.subcatFrameSetElements);
		//Collections.sort(this.synArgMaps);
		sb.append("parentSubcatFrameSet: ").append(this.parentSubcatFrameSet);
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
