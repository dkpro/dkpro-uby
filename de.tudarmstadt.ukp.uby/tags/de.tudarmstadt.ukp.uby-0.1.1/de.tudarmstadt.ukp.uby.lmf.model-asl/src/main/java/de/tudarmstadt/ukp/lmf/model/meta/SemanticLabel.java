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

import java.util.Collections;
import java.util.List;

import de.tudarmstadt.ukp.lmf.model.miscellaneous.EVarType;
import de.tudarmstadt.ukp.lmf.model.miscellaneous.VarType;
import de.tudarmstadt.ukp.lmf.model.semantics.MonolingualExternalRef;

/**
 * This class represents the semanticLabel class for diverse information types
 * @author sh
 *
 */
public class SemanticLabel implements Comparable<SemanticLabel>{
/* From DTD:
 * <!Element SemanticLabel (MonolingualExternalRef*)>
<!ATTLIST SemanticLabel 
    label CDATA #IMPLIED
    type CDATA #IMPLIED
    quantification CDATA #IMPLIED>
 */
	// the label of the semantic class
	@VarType(type = EVarType.ATTRIBUTE)
	private String label;
	
	// the type of semantic class
	@VarType(type = EVarType.ATTRIBUTE)
	private String type;
	
	// the quantification of the label class
	@VarType(type = EVarType.ATTRIBUTE)
	private String quantification; //TODO: String or Float?
	
	// Reference to a external Resource
	@VarType(type = EVarType.CHILDREN)
	private List<MonolingualExternalRef> monolingualExternalRefs;
	
	
	private String parentId;
	
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

	/**
	 * 
	 * @param label the label to set
	 */
	public void setLabel(String label) {
		this.label = label;
	}

	/**
	 * 
	 * @return the label
	 */
	public String getLabel() {
		return label;
	}

	/**
	 * 
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * 
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * 
	 * @param quantification the quantification to set
	 */
	public void setQuantification(String quantification) {
		this.quantification = quantification;
	}

	/**
	 * 
	 * @return the quantification
	 */
	public String getQuantification() {
		return quantification;
	}
	
	/**
	 * @return the monolingualExternalRefs
	 */
	public List<MonolingualExternalRef> getMonolingualExternalRefs() {
		return monolingualExternalRefs;
	}

	/**
	 * @param monolingualExternalRefs the monolingualExternalRefs to set
	 */
	public void setMonolingualExternalRefs(
			List<MonolingualExternalRef> monolingualExternalRefs) {
		this.monolingualExternalRefs = monolingualExternalRefs;
	}
	
	public String toString(){
		StringBuffer sb = new StringBuffer(512);
		sb.append("SemanticLabel ").append("label: ").append(label);
		sb.append(" type: ").append(type);
		sb.append(" quantification: ").append(quantification);
		if(monolingualExternalRefs != null)
			Collections.sort(monolingualExternalRefs);
		sb.append(" monolingualExternalRefs: ").append(monolingualExternalRefs);
		return sb.toString();
	}
	
	@Override
	public int compareTo(SemanticLabel o) {
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
		    if (!(o instanceof SemanticLabel))
		    	return false;
		    SemanticLabel other = (SemanticLabel) o;
		    return this.toString().equals(other.toString());
	}
	
}
