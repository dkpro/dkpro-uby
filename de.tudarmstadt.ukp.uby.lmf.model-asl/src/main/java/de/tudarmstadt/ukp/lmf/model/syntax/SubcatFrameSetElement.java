/*******************************************************************************
 * Copyright 2016
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

import de.tudarmstadt.ukp.lmf.model.miscellaneous.EVarType;
import de.tudarmstadt.ukp.lmf.model.miscellaneous.VarType;

/**
 * SubcatFrameSetElement is a class representing a syntactic construction. It acts as a container
 * of a single {@link SubcategorizationFrame} instance.
 * 
 * @author Zijad Maksuti
 * @author Judith Eckle-Kohler
 */
public class SubcatFrameSetElement implements Comparable<SubcatFrameSetElement>{
	
	// Pointer to SubcategorizationFrame
	@VarType(type = EVarType.IDREF)
	private SubcategorizationFrame element;

	/**
	 * Returns the element, represented by a {@link SubcategorizationFrame} instance,
	 * contained in this {@link SubcatFrameSetElement} instance.
	 * 
	 * @return the subcategorization frame representing a syntactic construction or null if the
	 * frame is not set. <p>
	 * 
	 * Note that UBY-LMF requires that every SubcatFrameSetElement should contains
	 * a reference to an existing SubcategorizationFrame. Absence of this attribute may indicate to
	 * an incomplete conversion process of the original resource.
	 */
	public SubcategorizationFrame getElement() {
		return element;
	}

	/**
	 * Sets the element, represented by a {@link SubcategorizationFrame} instance,
	 * contained in this {@link SubcatFrameSetElement} instance.
	 * 
	 * @param element the subcategorization frame representing a syntactic construction.<p>
	 * 
	 * Note that UBY-LMF requires that every SubcatFrameSetElemnt should contain
	 * a reference to an existing SubcategorizationFrame.
	 * 
	 */
	public void setElement(SubcategorizationFrame element) {
		this.element = element;
	}
	
	public String toString(){
		StringBuffer sb = new StringBuffer(256);
		sb.append("SubcatFrameSetElement ").append("element: ").append(element);
		return sb.toString();
	}
	
	public boolean equals(Object other) {
	    if (this == other)
	      return true;
	    if (!(other instanceof SubcatFrameSetElement))
	      return false;
	    SubcatFrameSetElement otherSubcatFrameSetElement = (SubcatFrameSetElement) other;
	    
	    return this.toString().equals(otherSubcatFrameSetElement.toString());
	  }
	
	public int hashCode() {
	    int hash = 1;
		hash = hash * 31 + this.toString().hashCode();
		return hash;
	  }

	@Override
	public int compareTo(SubcatFrameSetElement o) {
		return this.toString().compareTo(o.toString());
	}
	
	
}
