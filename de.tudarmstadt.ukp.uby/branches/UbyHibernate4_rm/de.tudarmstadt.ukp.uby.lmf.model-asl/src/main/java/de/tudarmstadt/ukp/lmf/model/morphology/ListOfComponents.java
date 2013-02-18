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
package de.tudarmstadt.ukp.lmf.model.morphology;

import java.util.Collections;
import java.util.List;

import de.tudarmstadt.ukp.lmf.model.core.LexicalEntry;
import de.tudarmstadt.ukp.lmf.model.miscellaneous.EVarType;
import de.tudarmstadt.ukp.lmf.model.miscellaneous.VarType;

/**
 * ListOfComponents is a class representing the aggregative aspect of a multiword expression.<br>
 * The List Of Components class is in a zero or one aggregate relationship with the {@link LexicalEntry}class.
 * Each ListOfComponents instance should have at least two {@link Component} instances.
 * 
 * @author Silvana Hartmann
 * 
 */
public class ListOfComponents implements Comparable<ListOfComponents> {
	
	// Components of this ListOfComponenets
	@VarType(type = EVarType.CHILDREN)
	private List<Component> components;
	
	/**
	 * Sets the {@link List} of all {@link Component} instances contained in this {@link ListOfComponents} instance.
	 * @param components list of all components to set
	 */
	public void setComponents(List<Component> components) {
		this.components = components;
	}
	
	public String toString(){
		StringBuffer sb = new StringBuffer(256);
		if(this.components != null)
			Collections.sort(components);
		sb.append("ListOfComponents: ").append(components);
		return sb.toString();
	}

	/**
	 * Returns the {@link List} of all {@link Component} instances contained in this
	 * {@link ListOfComponents}.
	 * @return all components contained in this list of components or an empty list,
	 * if the components are not set. <p>
	 * <i> Note that Uby-LMF requires that all list of components should contain at least two
	 * components. Absence of the components may happen as a result of an incomplete conversion
	 * of the original resource.
	 * </i>
	 */
	public List<Component> getComponents() {
		return components;
	}


	public int compareTo(ListOfComponents o) {
		return this.toString().compareTo(o.toString());
	}
	
	public int hashCode(){
		return this.toString().hashCode();
	}
	
	public boolean equals(Object o){
		if (this == o)
		      return true;
		    if (!(o instanceof ListOfComponents))
		      return false;
		ListOfComponents loc = (ListOfComponents) o;
		return this.toString().equals(loc.toString());
	}
	
}
	
