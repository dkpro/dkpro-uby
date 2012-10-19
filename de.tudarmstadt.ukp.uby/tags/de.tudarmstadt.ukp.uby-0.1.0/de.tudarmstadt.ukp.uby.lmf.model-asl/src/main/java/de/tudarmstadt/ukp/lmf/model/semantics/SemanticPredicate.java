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

import java.util.List;

import de.tudarmstadt.ukp.lmf.model.core.Definition;
import de.tudarmstadt.ukp.lmf.model.enums.EYesNo;
import de.tudarmstadt.ukp.lmf.model.interfaces.IHasID;
import de.tudarmstadt.ukp.lmf.model.meta.Frequency;
import de.tudarmstadt.ukp.lmf.model.meta.SemanticLabel;
import de.tudarmstadt.ukp.lmf.model.miscellaneous.EVarType;
import de.tudarmstadt.ukp.lmf.model.miscellaneous.VarType;

/**
 * This class represents a SemanticPredicate
 * @author maksuti
 *
 */
/*
 * <!ELEMENT SemanticPredicate (Definition*, SemanticArgument*, PredicateRelation*, Frequency*, SemanticLabel*, CoreArgumentSet*)>
<!--	attribute name		comment
	semanticTypes		links to one or more synset instances -->
<!ATTLIST SemanticPredicate
    id                      ID #REQUIRED
    label CDATA #IMPLIED
    semanticTypes IDREFS #IMPLIED
    lexicalized (yes|no) #IMPLIED 
    perspectivalized (yes|no) #IMPLIED>
 */
public class SemanticPredicate implements IHasID, Comparable<SemanticPredicate>{
	
	// Id of the SemanticPredicate
	@VarType(type = EVarType.ATTRIBUTE)
	private String id;
	
	// Semantic predicate's label
	@VarType(type = EVarType.ATTRIBUTE)
	private String label;
	
	// semantic types of this SemanticPredicate
	@VarType(type = EVarType.IDREFS)
	private List<Synset> semanticTypes;

	// Semantic predicate is lexicalized
	@VarType(type = EVarType.ATTRIBUTE)
	private EYesNo lexicalized;
	
	// Semantic predicate is perspectivalized
	@VarType(type = EVarType.ATTRIBUTE)
	private EYesNo perspectivalized;
	
	// Definitions of this SemanticPredicate
	@VarType(type = EVarType.CHILDREN)
	private List<Definition> definitions;
	
	// Semantic Arguments of this SemanticPredicate
	@VarType(type = EVarType.CHILDREN)
	private List<SemanticArgument> semanticArguments;
	
	// Predicate Relations of this SemanticPredicate
	@VarType(type = EVarType.CHILDREN)
	private List<PredicateRelation> predicateRelations;	
	
	// Frequency information for this SubcategorizationFrame
	@VarType(type = EVarType.CHILDREN)
	private List<Frequency> frequencies;
	
	// Semantic class information for this Sense
	@VarType(type = EVarType.CHILDREN)
	private List<SemanticLabel> semanticLabels;
	
	// Semantic class information for this Sense
	@VarType(type = EVarType.CHILDREN)
	private List<CoreArgumentSet> coreArgumentSets;

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
	 * @return the semanticArguments
	 */
	public List<SemanticArgument> getSemanticArguments() {
		return semanticArguments;
	}

	/**
	 * @param semanticArguments the semanticArguments to set
	 */
	public void setSemanticArguments(List<SemanticArgument> semanticArguments) {
		this.semanticArguments = semanticArguments;
	}

	/**
	 * @return the predicateRelations
	 */
	public List<PredicateRelation> getPredicateRelations() {
		return predicateRelations;
	}

	/**
	 * @param predicateRelations the predicateRelations to set
	 */
	public void setPredicateRelations(List<PredicateRelation> predicateRelations) {
		this.predicateRelations = predicateRelations;
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
	 * 
	 * @param coreArgumentSets the coreArgumentsSets
	 */
	public void setCoreArgumentSet(List<CoreArgumentSet> coreArgumentSets) {
		this.coreArgumentSets = coreArgumentSets;
	}

	/**
	 * 
	 * @return the coreArgumentSets
	 */
	public List<CoreArgumentSet> getCoreArgumentSet() {
		return coreArgumentSets;
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
	 * @return the label
	 */
	public String getLabel() {
		return label;
	}

	/**
	 * @param label the label to set
	 */
	public void setLabel(String label) {
		this.label = label;
	}

	/**
	 * @return the semanticTypes
	 */
	public List<Synset> getSemanticTypes() {
		return semanticTypes;
	}

	/**
	 * @param semanticTypes the semanticTypes to set
	 */
	public void setSemanticTypes(List<Synset> semanticTypes) {
		this.semanticTypes = semanticTypes;
	}
	
	/**
	 * 
	 * @param lexicalized the lexicalized to set
	 */
	public void setLexicalized(EYesNo lexicalized) {
		this.lexicalized = lexicalized;
	}

	/**
	 * 
	 * @return the lexicalized
	 */
	public EYesNo getLexicalized() {
		return lexicalized;
	}

	/**
	 * 
	 * @param perspectivalized the perspectivalized to set
	 */
	public void setPerspectivalized(EYesNo perspectivalized) {
		this.perspectivalized = perspectivalized;
	}

	/**
	 * 
	 * @return the perspectivalized
	 */
	public EYesNo getPerspectivalized() {
		return perspectivalized;
	}

	public String toString(){
		return this.id;
	}

	@Override
	public int compareTo(SemanticPredicate o) {
		return this.toString().compareTo(o.toString());
	}
	
	public boolean equals(Object other) {
	    if (this == other)
	      return true;
	    if (!(other instanceof SemanticPredicate))
	      return false;
	    SemanticPredicate otherSemanticPredicate = (SemanticPredicate) other;
	    return this.id.equals(otherSemanticPredicate.id);
	    }
	
	public int hashCode() { 
	    int hash = 1;
	    hash = hash * 31 + this.id.hashCode();
	    return hash;
	  }
}