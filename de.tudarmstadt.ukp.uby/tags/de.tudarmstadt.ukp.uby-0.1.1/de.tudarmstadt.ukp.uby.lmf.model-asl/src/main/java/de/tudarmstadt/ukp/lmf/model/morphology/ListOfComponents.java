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

import de.tudarmstadt.ukp.lmf.model.miscellaneous.EVarType;
import de.tudarmstadt.ukp.lmf.model.miscellaneous.VarType;

/**
 * This class represents a list of multiword components
 * @author sh
 *
 */
public class ListOfComponents implements Comparable<ListOfComponents> {
	
	// Components of this ListOfComponenets
	@VarType(type = EVarType.CHILDREN)
	private List<Component> components;
	
	/**
	 * 
	 * @param components the components to set
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
	 * 
	 * @return the Components
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
	
