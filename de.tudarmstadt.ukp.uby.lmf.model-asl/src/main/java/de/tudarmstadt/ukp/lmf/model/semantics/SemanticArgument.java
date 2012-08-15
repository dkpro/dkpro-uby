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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.tudarmstadt.ukp.lmf.model.core.Definition;
import de.tudarmstadt.ukp.lmf.model.enums.ECoreType;
import de.tudarmstadt.ukp.lmf.model.enums.EYesNo;
import de.tudarmstadt.ukp.lmf.model.interfaces.IHasDefinitions;
import de.tudarmstadt.ukp.lmf.model.interfaces.IHasID;
import de.tudarmstadt.ukp.lmf.model.interfaces.IHasSemanticLabels;
import de.tudarmstadt.ukp.lmf.model.meta.Frequency;
import de.tudarmstadt.ukp.lmf.model.meta.SemanticLabel;
import de.tudarmstadt.ukp.lmf.model.miscellaneous.AccessType;
import de.tudarmstadt.ukp.lmf.model.miscellaneous.EAccessType;
import de.tudarmstadt.ukp.lmf.model.miscellaneous.EVarType;
import de.tudarmstadt.ukp.lmf.model.miscellaneous.VarType;

/**
 * SemanticArgument is a class representing an argument of a given {@link SemanticPredicate}.
 * 
 * @author Zijad Maksuti
 *
 */
public class SemanticArgument implements IHasID, IHasDefinitions, IHasSemanticLabels, Comparable<SemanticArgument>{

	// ID of this SemanticArgument
	@VarType(type = EVarType.ATTRIBUTE)
	private String id;

	// Semantic role of this Argument
	@VarType(type = EVarType.ATTRIBUTE)
	private String semanticRole;

	// FN core type of this argument
	@VarType(type = EVarType.ATTRIBUTE)
	private ECoreType coreType;

	// Relations to other semantic arguments
	@VarType(type = EVarType.CHILDREN)
	private List<ArgumentRelation> argumentRelations = new ArrayList<ArgumentRelation>();

	// Frequency information for this argument
	@VarType(type = EVarType.CHILDREN)
	private List<Frequency> frequencies = new ArrayList<Frequency>();

	// Semantic class information for this argument
	@VarType(type = EVarType.CHILDREN)
	private List<SemanticLabel> semanticLabels = new ArrayList<SemanticLabel>();

	// Definitions of this semantic argument
	@VarType(type = EVarType.CHILDREN)
	private List<Definition> definitions = new ArrayList<Definition>();

	// incorporatedSemanticArgument yes/no?
	@VarType(type = EVarType.ATTRIBUTE)
	@AccessType(type = EAccessType.FIELD)
	private EYesNo isIncorporated;
	
	/**
	 * Constructs an empty {@link SemanticArgument} instance.
	 * 
	 * @since UBY 0.2.0
	 * 
	 * @see #SemanticArgument(String)
	 */
	public SemanticArgument(){
		// nothing to do
	}
	
	/**
	 * Constructs a {@link SemanticArgument} instance with the consumed
	 * identifier.
	 * 
	 * @param id the unique identifier of the constructed semantic argument
	 * 
	 * @since UBY 0.2.0
	 * 
	 * @see #SemanticArgument()
	 */
	public SemanticArgument(String id){
		this.id = id;
	}

	/**
	 * Returns a {@link List} of {@link Definition} instances representing narrative description
	 * of this {@link SemanticArgument} instance.
	 * @return the list of definitons describing this semantic argument or an empty list
	 * if the semantic argument does not have any definitions associated 
	 */
	public List<Definition> getDefinitions() {
		return definitions;
	}

	/**
	 * Sets the {@link List} of {@link Definition} instances representing narrative description
	 * of this {@link SemanticArgument} instance.
	 * @param definitions the list of definitions to set
	 */
	public void setDefinitions(List<Definition> definitions) {
		this.definitions = definitions;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	/**
	 * Returns the {@link String} describing the semantic role of this {@link SemanticArgument} instance.<br>
	 * Abstract semantic roles include thematic roles, such as agent, theme, and instrument, and secondary
	 * roles such as location, time, and manner.
	 * @return the semantic role of this semantic argument or null if the role is not set
	 */
	public String getSemanticRole() {
		return semanticRole;
	}

	/**
	 * Sets the {@link String} describing the semantic role of this {@link SemanticArgument} instance.<br>
	 * @param semanticRole the semantic role to set
	 */
	public void setSemanticRole(String semanticRole) {
		this.semanticRole = semanticRole;
	}

	/**
	 * Returns the {@link List} of all relations, represented by {@link ArgumentRelation} instances,
	 * in which this {@link SemanticArgument} participates.
	 * @return the list of all relations in which this semantic argument participates or an empty list
	 * if the semantic argument does not have any relations set
	 */
	public List<ArgumentRelation> getArgumentRelations() {
		return argumentRelations;
	}

	/**
	 * Sets the core type of this {@link SemanticArgument} instance.
	 * @param coreType the type to set
	 * @see ECoreType
	 */
	public void setCoreType(ECoreType coreType) {
		this.coreType = coreType;
	}

	/**
	 * Returns the core type of this {@link SemanticArgument} instance.
	 * @return the core type of this semantic argument or null if the type is not set
	 * @see ECoreType
	 */
	public ECoreType getCoreType() {
		return coreType;
	}

	/**
	 * Sets the {@link List} of all relations, represented by {@link ArgumentRelation} instances,
	 * in which this {@link SemanticArgument} participates.
	 * @param argumentRelations the list of all relations in which this semantic argument participates to set
	 */
	public void setArgumentRelations(List<ArgumentRelation> argumentRelations) {
		this.argumentRelations = argumentRelations;
	}

	/**
	 * Sets the {@link List} of all {@link Frequency} instances representing the corpus
	 * frequency of this {@link SemanticArgument}.
	 * @param frequencies the list of frequencies to set
	 */
	public void setFrequencies(List<Frequency> frequencies) {
		this.frequencies = frequencies;
	}

	/**
	 * Returns the {@link List} of all {@link Frequency} instances representing the corpus
	 * frequency of this {@link SemanticArgument}.
	 * @return the list of all corpus frequencies of this semantic argument
	 */
	public List<Frequency> getFrequencies() {
		return frequencies;
	}

	/**
	 * Associates a {@link List} of {@link SemanticLabel} instances to this
	 * {@link SemanticArgument}.
	 * @param semanticLabels the list of semantic labels to associate with this semantic argument
	 */
	public void setSemanticLabels(List<SemanticLabel> semanticLabels) {
		this.semanticLabels = semanticLabels;
	}

	/**
	 * Returns the {@link List} of {@link SemanticLabel} instances associated with this
	 * {@link SemanticArgument}.
	 * @return the list of semantic labels associated with this semantic argument
	 */
	public List<SemanticLabel> getSemanticLabels() {
		return semanticLabels;
	}

	/**
	 * Returns true if and only if this {@link SemanticArgument} instance is incorporated
	 * by a verb.<p>
	 * <i>
	 * Some verbs incorporate information about a particular semantic argument
	 * (a semantic argument corresponds to a Frame Element in FrameNet terminology) in their
	 * definition: such a semantic argument is called incorporated in FrameNet.<br>
	 * In the case of smile, grimace, frown, pout, and scowl, the affected body part
	 * is not separately expressed; it is said to be incorporated.
	 * Likewise, in the FrameNet Placing frame, many verbs incorporate the Goal (i.e. the place where the Theme ends up)
	 * such as bag.v, bin.v, bottle.v, box.v, cage.v, crate.v, file.v, garage.v.
	 * </i>
	 * @return true if this semantic argument is incorporated by a verb, false otherwise
	 */
	public boolean isIncorporated() {
		if(isIncorporated != null)
			return (isIncorporated.equals(EYesNo.yes)? true : false);
		else
			return false;
	}

	/**
	 * @param isIncorporated the isIncorporated to set
	 * @deprecated use {@link #setIncorporated(boolean)} instead
	 */
	@Deprecated
	public void setIsIncorporated(EYesNo isIncorporated) {
		this.isIncorporated = isIncorporated;
	}
	
	/**
	 * Sets the incorporated attribute of this {@link SemanticArgument} instance.
	 * @param incorporated set to true if this semantic argument instance is incorporated
	 * by a verb, otherwise set to false
	 */
	public void setIncorporated(boolean incorporated) {
		if(incorporated)
			this.isIncorporated = EYesNo.yes;
		else
			this.isIncorporated = EYesNo.no;
	}

	@Override
	public int compareTo(SemanticArgument o) {
		return this.toString().compareTo(o.toString());
	}

	@Override
	public String toString(){
		StringBuffer sb = new StringBuffer(512);
		sb.append("SemanticArgument ").append("id").append(id);
		sb.append(" semanticRole: ").append(semanticRole);
		sb.append(" coreType: ").append(coreType);
		Collections.sort(argumentRelations);
		sb.append( "argumentRelations: ").append(argumentRelations);
		Collections.sort(frequencies);
		sb.append(" frequencies: ").append(frequencies);
		Collections.sort(semanticLabels);
		sb.append(" semanticLabels: ").append(semanticLabels);
		Collections.sort(definitions);
		sb.append(" definitions: ").append(definitions);
		sb.append(" isIncorporated: ").append(isIncorporated);

		return sb.toString();
	}

	@Override
	public boolean equals(Object other) {
	    if (this == other) {
			return true;
		}
	    if (!(other instanceof SemanticArgument)) {
			return false;
		}
	    SemanticArgument otherSemanticArgument = (SemanticArgument) other;
	    return this.toString().equals(otherSemanticArgument.toString());
	    }

	@Override
	public int hashCode() {
	    int hash = 1;
	    hash = hash * 31 + this.toString().hashCode();
	    return hash;
	  }

}
