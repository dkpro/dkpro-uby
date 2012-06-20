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
import java.util.List;

import de.tudarmstadt.ukp.lmf.model.core.Definition;
import de.tudarmstadt.ukp.lmf.model.enums.EYesNo;
import de.tudarmstadt.ukp.lmf.model.interfaces.IHasDefinitions;
import de.tudarmstadt.ukp.lmf.model.interfaces.IHasFrequencies;
import de.tudarmstadt.ukp.lmf.model.interfaces.IHasID;
import de.tudarmstadt.ukp.lmf.model.interfaces.IHasSemanticLabels;
import de.tudarmstadt.ukp.lmf.model.meta.Frequency;
import de.tudarmstadt.ukp.lmf.model.meta.SemanticLabel;
import de.tudarmstadt.ukp.lmf.model.miscellaneous.EVarType;
import de.tudarmstadt.ukp.lmf.model.miscellaneous.VarType;

/**
 * SemanticPredicate is a class representing an abstract meaning together with its association
 * with the {@link SemanticArgument} class.
 * 
 * @author Zijad Maksuti
 *
 */
public class SemanticPredicate implements IHasID, IHasDefinitions, IHasFrequencies, IHasSemanticLabels, Comparable<SemanticPredicate>{

	// Id of the SemanticPredicate
	@VarType(type = EVarType.ATTRIBUTE)
	private String id;

	// Semantic predicate's label
	@VarType(type = EVarType.ATTRIBUTE)
	private String label;

	// semantic types of this SemanticPredicate
	@VarType(type = EVarType.IDREFS)
	private List<Synset> semanticTypes = new ArrayList<Synset>();

	// Semantic predicate is lexicalized
	@VarType(type = EVarType.ATTRIBUTE)
	private EYesNo lexicalized;

	// Semantic predicate is perspectivalized
	@VarType(type = EVarType.ATTRIBUTE)
	private EYesNo perspectivalized;

	// Definitions of this SemanticPredicate
	@VarType(type = EVarType.CHILDREN)
	private List<Definition> definitions = new ArrayList<Definition>();

	// Semantic Arguments of this SemanticPredicate
	@VarType(type = EVarType.CHILDREN)
	private List<SemanticArgument> semanticArguments = new ArrayList<SemanticArgument>();

	// Predicate Relations of this SemanticPredicate
	@VarType(type = EVarType.CHILDREN)
	private List<PredicateRelation> predicateRelations = new ArrayList<PredicateRelation>();

	// Frequency information for this SubcategorizationFrame
	@VarType(type = EVarType.CHILDREN)
	private List<Frequency> frequencies = new ArrayList<Frequency>();

	// Semantic class information for this Sense
	@VarType(type = EVarType.CHILDREN)
	private List<SemanticLabel> semanticLabels = new ArrayList<SemanticLabel>();

	// Semantic class information for this Sense
	@VarType(type = EVarType.CHILDREN)
	private List<CoreArgumentSet> coreArgumentSets = new ArrayList<CoreArgumentSet>();

	public List<Definition> getDefinitions() {
		return definitions;
	}

	public void setDefinitions(List<Definition> definitions) {
		this.definitions = definitions;
	}

	/**
	 * Returns the {@link List} of all {@link SemanticArgument} instances associated
	 * with this {@link SemanticPredicate}.
	 * @return the list of all semantic arguments associated with this semantic predicate class or an empty list
	 * if the semantic predicate does not have any associated arguments
	 */
	public List<SemanticArgument> getSemanticArguments() {
		return semanticArguments;
	}

	/**
	 * Sets the {@link List} of all {@link SemanticArgument} instances associated
	 * with this {@link SemanticPredicate}.
	 * @param the list of all associated semantic arguments to set
	 */
	public void setSemanticArguments(List<SemanticArgument> semanticArguments) {
		this.semanticArguments = semanticArguments;
	}

	/**
	 * Returns the {@link List} of all relations, represented by {@link PredicateRelation} instances,
	 * in which this {@link SemanticArgument} instance participates.
	 * @return the list of all predicate relations in which this semantic predicate participates or an empty
	 * list if the semantic predicate does not participate in any relation
	 */
	public List<PredicateRelation> getPredicateRelations() {
		return predicateRelations;
	}

	/**
	 * Sets the {@link List} of all relations, represented by {@link PredicateRelation} instances,
	 * in which this {@link SemanticArgument} instance participates.
	 * @param the list of all predicate relations in which this semantic predicate participates to set
	 */
	public void setPredicateRelations(List<PredicateRelation> predicateRelations) {
		this.predicateRelations = predicateRelations;
	}

	public void setFrequencies(List<Frequency> frequencies) {
		this.frequencies = frequencies;
	}

	public List<Frequency> getFrequencies() {
		return frequencies;
	}

	public void setSemanticLabels(List<SemanticLabel> semanticLabels) {
		this.semanticLabels = semanticLabels;
	}

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
	@Deprecated
	public EYesNo getLexicalized() {
		return lexicalized;
	}

	/**
	 *
	 * @return true if the lexicalized attribute is yes, false otherwise
	 */
	public boolean isLexicalized() {
		return (lexicalized.equals(EYesNo.yes)? true : false);
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
	@Deprecated
	public EYesNo getPerspectivalized() {
		return perspectivalized;
	}

	/**
	 *
	 * @return true if the perspectivalized attribute is yes, false otherwise
	 */
	public boolean isPerspectivalized() {
		return (perspectivalized.equals(EYesNo.yes)? true : false);
	}

	@Override
	public String toString(){
		return this.id;
	}

	@Override
	public int compareTo(SemanticPredicate o) {
		return this.toString().compareTo(o.toString());
	}

	@Override
	public boolean equals(Object other) {
	    if (this == other) {
			return true;
		}
	    if (!(other instanceof SemanticPredicate)) {
			return false;
		}
	    SemanticPredicate otherSemanticPredicate = (SemanticPredicate) other;
	    return this.id.equals(otherSemanticPredicate.id);
	    }

	@Override
	public int hashCode() {
	    int hash = 1;
	    hash = hash * 31 + this.id.hashCode();
	    return hash;
	  }
}
