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
package de.tudarmstadt.ukp.lmf.model.meta;

import java.util.Collections;
import java.util.List;

import de.tudarmstadt.ukp.lmf.model.abstracts.HasMonolingualExternalRefs;
import de.tudarmstadt.ukp.lmf.model.core.Sense;
import de.tudarmstadt.ukp.lmf.model.enums.ELabelTypeSemantics;
import de.tudarmstadt.ukp.lmf.model.interfaces.IHasID;
import de.tudarmstadt.ukp.lmf.model.miscellaneous.EVarType;
import de.tudarmstadt.ukp.lmf.model.miscellaneous.VarType;
import de.tudarmstadt.ukp.lmf.model.semantics.MonolingualExternalRef;
import de.tudarmstadt.ukp.lmf.model.semantics.SemanticArgument;
import de.tudarmstadt.ukp.lmf.model.semantics.SemanticPredicate;

/**
 * This class models semantic labels for {@link Sense}, {@link SemanticArgument} and
 * {@link SemanticPredicate} instances.
 * 
 * @author Silvana Hartmann
 *
 */
public class SemanticLabel extends HasMonolingualExternalRefs implements Comparable<SemanticLabel>{

	// the label of the semantic class
	@VarType(type = EVarType.ATTRIBUTE)
	private String label;
	
	// the type of semantic class
	@VarType(type = EVarType.ATTRIBUTE)
	private ELabelTypeSemantics type;
	
	// the quantification of the label class
	@VarType(type = EVarType.ATTRIBUTE)
	private String quantification;
	
	// backlink to the parent, not part of UBY-LMF
	@VarType(type = EVarType.IDREF)
	private IHasID parent;
	
	/**
	 * Creates a {@link SemanticLabel} instance based on the consumed parameters.
	 * @param label the label of the semantic label
	 * @param type the type of the semantic label
	 * @param quantification the numerical representation of the semantic labels quantification
	 * @param parent the parent UBY-LMF class instance containing the semantic label
	 * @param monolingualExternalRefs {@link List} containing monolingual external references of the semantic label
	 */
	public SemanticLabel(String label, ELabelTypeSemantics type, String quantification,
			IHasID parent, List<MonolingualExternalRef> monolingualExternalRefs){
		
		this.setLabel(label);
		this.setParent(parent);
		this.setType(type);
		this.setQuantification(quantification);
		this.setMonolingualExternalRefs(monolingualExternalRefs);
	}
	
	/**
	 * Creates an empty {@link SemanticLabel} instance.<p>
	 * 
	 * Use {@link #SemanticLabel(String, String, String, IHasID, List)} to create an instance
	 * by specifying attribute values.
	 * 
	 */
	public SemanticLabel(){
		
	}
	
	/**
	 * Sets the parent class containing this {@link SemanticLabel} instance.
	 * The parent class has to impement {@link IHasID} interface.
	 * 
	 * @param parent the parent to set
	 */
	public void setParent(IHasID parent) {
		this.parent = parent;
	}

	/**
	 * Returns the parent containing this {@link SemanticLabel} instance.
	 * 
	 * @return the parent containing this semantic label or null if the parent is not set <p>
	 * <i>
	 * Note that this attribute is not a part of UBY-LMF. Therefore a semantic label may not have
	 * a parent set. In this case, this method returns null
	 * </i>
	 */
	public IHasID getParent() {
		return this.parent;
	}

	/**
	 * Sets the {@link String} instance representing the written label of this {@link SemanticLabel} instance.
	 * 
	 * @param label the label to set
	 */
	public void setLabel(String label) {
		this.label = label;
	}

	/**
	 * Returns the {@link String} instance representing the written label of this {@link SemanticLabel} instance.
	 * 
	 * @return the label of this semantic label or null if the label is not set
	 */
	public String getLabel() {
		return label;
	}

	/**
	 * Sets the {@link ELabelTypeSemantics} value representing the type of this {@link SemanticLabel} instance.
	 * 
	 * @param type the type of the semantic label to set
	 */
	public void setType(ELabelTypeSemantics type) {
		this.type = type;
	}

	/**
	 * Returns the {@link ELabelTypeSemantics} instance representing the type of this {@link SemanticLabel} instance.
	 * 
	 * @return the type of semantic label or null if the type is not set
	 */
	public ELabelTypeSemantics getType() {
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
	 * Returns a {@link String} representing the numerical quantification of this {@link SemanticLabel} instance.
	 * 
	 * @return the quantification of the semantic label or null if the quantification is not set
	 */
	public String getQuantification() {
		return quantification;
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
