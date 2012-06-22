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

import de.tudarmstadt.ukp.lmf.model.abstracts.HasFrequencies;
import de.tudarmstadt.ukp.lmf.model.core.Definition;
import de.tudarmstadt.ukp.lmf.model.enums.EYesNo;
import de.tudarmstadt.ukp.lmf.model.interfaces.IHasDefinitions;
import de.tudarmstadt.ukp.lmf.model.interfaces.IHasID;
import de.tudarmstadt.ukp.lmf.model.interfaces.IHasSemanticLabels;
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
public class SemanticPredicate extends HasFrequencies implements IHasID, IHasDefinitions, IHasSemanticLabels, Comparable<SemanticPredicate>{

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

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	/**
	 * Returns a {@link String} representing the textual label of this {@link SemanticPredicate} instance.<br>
	 * Example: In case of FrameNet, every frame represents a semantic predicate and frames name (i.e. <i>Make_agreement_on_action</i> ) is
	 * the label of the semantic predicate.
	 * @return the label of this semantic predicate or null if the label is not set
	 */
	public String getLabel() {
		return label;
	}

	/**
	 * Sets a {@link String} representing the textual label of this {@link SemanticPredicate} instance.<br>
	 * Example: In case of FrameNet, every frame represents a semantic predicate and frames name (i.e. <i>Make_agreement_on_action</i> ) is
	 * the label of the semantic predicate.
	 * @param label the label to set
	 */
	public void setLabel(String label) {
		this.label = label;
	}

	/**
	 * Returns the 
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
	 * @deprecated use {@link #setLexicalized(boolean)} isntead
	 */
	@Deprecated
	public void setLexicalized(EYesNo lexicalized) {
		this.lexicalized = lexicalized;
	}
	
	public void setLexicalized(boolean lexicalized){
		if(lexicalized)
			this.lexicalized = EYesNo.yes;
		else
			this.lexicalized = EYesNo.no;	
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
	 * @depreca use {@link #setPerspectivalized(boolean)} instead
	 */
	@Deprecated
	public void setPerspectivalized(EYesNo perspectivalized) {
		this.perspectivalized = perspectivalized;
	}
	
	/**
	 * Sets the perspectivalized attribute of this {@link SemanticPredicate} instance.
	 * @param perspectivalized the boolean value of the attribute to set
	 */
	public void setPerspectivalized(boolean perspectivalized){
		if(perspectivalized)
			this.perspectivalized = EYesNo.yes;
		else
			this.perspectivalized = EYesNo.no;
	}

	/**
	 * Returns true if the perspectivalized attribute of this {@link SemanticPredicate}
	 * instance is set to true, false otherwise.<p>
	 * 
	 * This attribute is used when converting FrameNet to Uby-LMF. FrameNet frames which
	 * have a great diversity of lexical units, all of which share a kind of scene as a
	 * background are called non-perspectivalized frames. Such frames do not have a consistent
	 * set of semantic arguments (FEs in FrameNet terminology) for the target predicates,
	 * a consistent time assigned to the events or participants,
	 * or (most especially) a consistent point-of-view between target predicates.<p>
	 * 
	 * An example of a non-perspectivalized frame is the <i>Performers</i> and <i>roles</i> frame,
	 * which contains such diverse verbs as co-star, feature.
	 * 
	 * @return true if the perspectivalized attribute of the semantic predicate is set, false otherwise
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
