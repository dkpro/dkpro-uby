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
import de.tudarmstadt.ukp.lmf.model.interfaces.IHasID;
import de.tudarmstadt.ukp.lmf.model.meta.Frequency;
import de.tudarmstadt.ukp.lmf.model.meta.SemanticLabel;
import de.tudarmstadt.ukp.lmf.model.miscellaneous.EVarType;
import de.tudarmstadt.ukp.lmf.model.miscellaneous.VarType;

/**
 * SemanticArgument is a class representing an argument of a given {@link SemanticPredicate}.
 * 
 * @author Zijad Maksuti
 *
 */
public class SemanticArgument implements IHasID, Comparable<SemanticArgument>{

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

	// links to one or more synset instances
	//@VarType(type = EVarType.IDREFS)
	//private List<Synset> semanticTypes;

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
	private EYesNo isIncorporated;

	/**
	 * @return the definitions
	 */
	public List<Definition> getDefinitions() {
		return definitions;
	}

	/**
	 * @param definitions the definitions to set
	 */
	public void setDefinitions(List<Definition> definitions) {
		this.definitions = definitions;
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
	 * @return the semanticRole
	 */
	public String getSemanticRole() {
		return semanticRole;
	}

	/**
	 * @param semanticRole the semanticRole to set
	 */
	public void setSemanticRole(String semanticRole) {
		this.semanticRole = semanticRole;
	}

	/**
	 * @return the argumentRelations
	 */
	public List<ArgumentRelation> getArgumentRelations() {
		return argumentRelations;
	}

	/**
	 *
	 * @param coreType the coreType to set
	 */
	public void setCoreType(ECoreType coreType) {
		this.coreType = coreType;
	}

	/**
	 *
	 * @return the coreType
	 */
	public ECoreType getCoreType() {
		return coreType;
	}

	/**
	 * @param argumentRelations the argumentRelations to set
	 */
	public void setArgumentRelations(List<ArgumentRelation> argumentRelations) {
		this.argumentRelations = argumentRelations;
	}

	/**
	 *
	 * @param frequencies the frequencies to set
	 */
	public void setFrequencies(List<Frequency> frequencies) {
		this.frequencies = frequencies;
	}

	/**
	 *
	 * @return the frequencies
	 */
	public List<Frequency> getFrequencies() {
		return frequencies;
	}

	/**
	 *
	 * @param semanticLabels the semanticLabels to set
	 */
	public void setSemanticLabels(List<SemanticLabel> semanticLabels) {
		this.semanticLabels = semanticLabels;
	}

	/**
	 *
	 * @return the semanticLabels
	 */
	public List<SemanticLabel> getSemanticLabels() {
		return semanticLabels;
	}

	/**
	 * @return the isIncorporated
	 */
	@Deprecated
	public EYesNo getIsIncorporated() {
		return isIncorporated;
	}

	/**
	 *
	 * @return true if the isIncorporated attribute is yes, false otherwise
	 */
	public boolean isIncorporated() {
		return (isIncorporated.equals(EYesNo.yes)? true : false);
	}

	/**
	 * @param isIncorporated the isIncorporated to set
	 */
	public void setIsIncorporated(EYesNo isIncorporated) {
		this.isIncorporated = isIncorporated;
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
		if(argumentRelations != null) {
			Collections.sort(argumentRelations);
		}
		sb.append( "argumentRelations: ").append(argumentRelations);
		if(frequencies != null) {
			Collections.sort(frequencies);
		}
		sb.append(" frequencies: ").append(frequencies);
		if(semanticLabels != null) {
			Collections.sort(semanticLabels);
		}
		sb.append(" semanticLabels: ").append(semanticLabels);
		if(definitions != null) {
			Collections.sort(definitions);
		}
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
