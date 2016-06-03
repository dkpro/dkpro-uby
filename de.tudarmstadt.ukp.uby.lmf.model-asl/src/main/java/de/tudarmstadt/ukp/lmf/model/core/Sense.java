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
package de.tudarmstadt.ukp.lmf.model.core;

import java.util.ArrayList;
import java.util.List;

import de.tudarmstadt.ukp.lmf.model.abstracts.HasMonolingualExternalRefs;
import de.tudarmstadt.ukp.lmf.model.enums.ELanguageIdentifier;
import de.tudarmstadt.ukp.lmf.model.interfaces.IHasDefinitions;
import de.tudarmstadt.ukp.lmf.model.interfaces.IHasFrequencies;
import de.tudarmstadt.ukp.lmf.model.interfaces.IHasID;
import de.tudarmstadt.ukp.lmf.model.interfaces.IHasSemanticLabels;
import de.tudarmstadt.ukp.lmf.model.meta.Frequency;
import de.tudarmstadt.ukp.lmf.model.meta.SemanticLabel;
import de.tudarmstadt.ukp.lmf.model.miscellaneous.AccessType;
import de.tudarmstadt.ukp.lmf.model.miscellaneous.EAccessType;
import de.tudarmstadt.ukp.lmf.model.miscellaneous.EVarType;
import de.tudarmstadt.ukp.lmf.model.miscellaneous.VarType;
import de.tudarmstadt.ukp.lmf.model.mrd.Context;
import de.tudarmstadt.ukp.lmf.model.mrd.Equivalent;
import de.tudarmstadt.ukp.lmf.model.semantics.MonolingualExternalRef;
import de.tudarmstadt.ukp.lmf.model.semantics.PredicativeRepresentation;
import de.tudarmstadt.ukp.lmf.model.semantics.SemanticArgument;
import de.tudarmstadt.ukp.lmf.model.semantics.SemanticPredicate;
import de.tudarmstadt.ukp.lmf.model.semantics.SenseExample;
import de.tudarmstadt.ukp.lmf.model.semantics.SenseRelation;
import de.tudarmstadt.ukp.lmf.model.semantics.Synset;

/**
 * Sense is a class representing one meaning of a lexical entry.<br>
 * The  Sense class allows for hierarchical senses in that a sense may be more specific
 * than another sense of the same lexical entry.
 * @author Zijad Maksuti
 *@see LexicalEntry
 */
public class Sense extends HasMonolingualExternalRefs implements IHasID, IHasDefinitions, IHasFrequencies, IHasSemanticLabels, Comparable<Sense>{
	// Id of this Sense
	@VarType(type = EVarType.ATTRIBUTE)
	private String id;

	// Index of this source
	@VarType(type = EVarType.ATTRIBUTE)
	private int index;

	// Synset of this Sense
	@VarType(type = EVarType.IDREF)
	private Synset synset;

	// Semantic Argument incorporated by this Sense
	@VarType(type = EVarType.IDREF)
	private SemanticArgument incorporatedSemArg;

	// Sense has transparent Meaning
	@VarType(type = EVarType.ATTRIBUTE)
	@AccessType(type = EAccessType.FIELD)
	private Boolean transparentMeaning;

	// List of more specific senses
	@VarType(type = EVarType.CHILDREN)
	private List<Sense> senses= new ArrayList<Sense>();

	// Contexts of this Sense
	@VarType(type = EVarType.CHILDREN)
	private List<Context> contexts = new ArrayList<Context>();

	// Predicative Representations of this Sense
	@VarType(type = EVarType.CHILDREN)
	private List<PredicativeRepresentation> predicativeRepresentations = new ArrayList<PredicativeRepresentation>();

	// Sense Examples of this Sense
	@VarType(type = EVarType.CHILDREN)
	private List<SenseExample> senseExamples = new ArrayList<SenseExample>();

	// Definitions of this Sense
	@VarType(type = EVarType.CHILDREN)
	private List<Definition> definitions = new ArrayList<Definition>();

	// Sense Relations of this Sense
	@VarType(type = EVarType.CHILDREN)
	private List<SenseRelation> senseRelations = new ArrayList<SenseRelation>();

	// Equivalents for this Sense
	@VarType(type = EVarType.CHILDREN)
	private List<Equivalent> equivalents = new ArrayList<Equivalent>();

	// Frequency information for this Sense
	@VarType(type = EVarType.CHILDREN)
	private List<Frequency> frequencies = new ArrayList<Frequency>();

	// Semantic class informatin for this Sense
	@VarType(type = EVarType.CHILDREN)
	private List<SemanticLabel> semanticLabels = new ArrayList<SemanticLabel>();

	// Backlink to LexicalEntry added for convenience
	@VarType(type = EVarType.NONE)
	private LexicalEntry lexicalEntry;

	/**
	 * Constructs an empty {@link Sense} instance.
	 *
	 * @since UBY 0.2.0
	 */
	public Sense(){
		// nothing to do
	}

	/**
	 * Constructs a {@link Sense} instance with the specified
	 * unique identifier.
	 *
	 * @param id the unique identifier of the sense to be created
	 *
	 * @since UBY 0.2.0
	 */
	public Sense(String id){
		this.id = id;
	}


	/**
	 * Returns the unique identifier of this {@link Sense} instance.
	 * @return the unique identifier ot this sense or null, if the identifier is not set
	 */
	public String getId() {
		return id;
	}

	/**
	 * Sets the unique identifier to this {@link Sense} instance.
	 * @param id the identifier to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * Returns an integer representing the commonness of this {@link Sense} instance,
	 * with respect to the {@link LexicalEntry} containing it.
	 * @return the commonness of this sense or null, if the index is not set
	 */
	public int getIndex() {
		return index;
	}

	/**
	 * Sets the index to this {@link Sense} instance.<p>
	 * Index is an integer representing the commonness of a sense with respect
	 * to the lexical entry containing it.
	 * @param index the index to set
	 * @see LexicalEntry
	 */
	public void setIndex(int index) {
		this.index = index;
	}

	/**
	 * Returns the {@link Synset} instance associated to this {@link Sense}.
	 * @return the synset associated to this sense or null, if the sense does not have an associated synset
	 */
	public Synset getSynset() {
		return synset;
	}

	/**
	 * Associates a {@link Synset} instance to this {@link Sense} instance.
	 * @param synset the synset to associate
	 */
	public void setSynset(Synset synset) {
		this.synset = synset;
	}

	/**
	 * Sets a {@link SemanticArgument} instance incorporated in this {@link Sense} instance.
	 * @param incorporatedSemArg the incorporated semantic argument to set
	 */
	public void setIncorporatedSemArg(SemanticArgument incorporatedSemArg) {
		this.incorporatedSemArg = incorporatedSemArg;
	}

	/**
	 * Returns the {@link SemanticArgument} instance incorporated in this {@link Sense} instance.
	 * @return the semantic argument incorporated in this sense or null, if this sense does not
	 * incorporate a semantic argument
	 */
	public SemanticArgument getIncorporatedSemArg() {
		return incorporatedSemArg;
	}


	/**
	 * Sets the transparent meaning flag to this {@link Sense} instance.<p>
	 * Explanation:<br>
	 * If the syntactic head of a phrase is not its semantic center, it has a transparent meaning,
	 * i.e. it merely adds information to the semantic center of the phrase.
	 * @param flag set it to true to indicate that the sense has a transparent meaning, or to false otherwise
	 */
	public void setTransparentMeaning(Boolean transparentMeaning){
		this.transparentMeaning = transparentMeaning;
	}

	/**
	 * Returns true if this {@link Sense} instance has a transparent meaning, false otherwise.
	 * @return true if the sense has a transparent meaning, false otherwise
	 *
	 */
	public Boolean isTransparentMeaning() {
		return transparentMeaning;
	}

	/**
	 * Returns the {@link List} of more specific {@link Sense} instances.<p>
	 * <i>
	 * This method is reserved for future use.
	 * Currently, there are no senses which have attached (sub-)senses.
	 * </i>
	 * @return the list of more specific (sub-)senses of this sense or an empty list,
	 * if this sense does not have any (sub-)senses
	 */
	public List<Sense> getSenses() {
		return senses;
	}

	/**
	 * Sets the {@link List} of more specific {@link Sense} instances to
	 * this sense.
	 * @param senses the list of senses to set
	 */
	public void setSenses(List<Sense> senses) {
		this.senses = senses;
	}

	/**
	 * Returns the {@link List} of all {@link Context} instances associated to this {@link Sense}.
	 * @return the list of all contexts associated to this sense or an empty list, if the sense
	 * does not have any contexts associated
	 */
	public List<Context> getContexts() {
		return contexts;
	}

	/**
	 * Associates a {@link List} of {@link Context} instances to this {@link Sense} instance.
	 * @param contexts the list of contexts to set
	 */
	public void setContexts(List<Context> contexts) {
		this.contexts = contexts;
	}

	/**
	 * Returns the {@link List} of all {@link PredicativeRepresentation} instances which
	 * link this {@link Sense} to one or more {@link SemanticPredicate} instances.
	 * @return the list of all predicative representations of this sense or an empty list, if the
	 * sense does not have any predicative representations set
	 */
	public List<PredicativeRepresentation> getPredicativeRepresentations() {
		return predicativeRepresentations;
	}

	/**
	 * Sets the {@link List} of all {@link PredicativeRepresentation} instances to this {@link Sense}.
	 * @param predicativeRepresentations the list of predicative representations to set
	 */
	public void setPredicativeRepresentations(
			List<PredicativeRepresentation> predicativeRepresentations) {
		this.predicativeRepresentations = predicativeRepresentations;
	}

	/**
	 * Returns the {@link List} of all {@link SenseExample} instances illustrating
	 * the particular meaning of this {@link Sense} instance.
	 * @return the list of all examples of this sense or an empty list, if the sense does not have
	 * any examples set
	 */
	public List<SenseExample> getSenseExamples() {
		return senseExamples;
	}

	/**
	 * Sets the {@link List} of all {@link SenseExample} instances to this {@link Sense} instance.
	 * @param senseExamples the list of sense examples to set
	 */
	public void setSenseExamples(List<SenseExample> senseExamples) {
		this.senseExamples = senseExamples;
	}

	/**
	 * Returns the {@link List} of all {@link Definition} instances representing the
	 * narrative description of this {@link Sense} instance.
	 * @return the list of all definitions of this sense or an empty list, if the sense does not have
	 * any definitions set
	 */
	public List<Definition> getDefinitions() {
		return definitions;
	}

	/**
	 * Sets the {@link List} of all {@link Definition} instances to this {@link Sense}.
	 * @param definitions the definitions to set
	 */
	public void setDefinitions(List<Definition> definitions) {
		this.definitions = definitions;
	}

	/**
	 * Returns the {@link List} of all {@link SenseRelation} instances in which
	 * this {@link Sense} instance is the source.
	 * @return th elist of all sense relations in which this sense is the source or
	 * an empty list, if this sense does not have any sense relations set
	 */
	public List<SenseRelation> getSenseRelations() {
		return senseRelations;
	}

	/**
	 * Sets the {@link List} of all {@link SenseRelation} instances to this
	 * {@link Sense} in which the Sense is the source of the relation.
	 * @param senseRelations the list of sense relations to set
	 */
	public void setSenseRelations(List<SenseRelation> senseRelations) {
		this.senseRelations = senseRelations;
	}

	/**
	 * Returns the {@link List} of all {@link Equivalent} instances of this
	 * {@link Sense}. That is, the translation of the this sense.
	 * @return the list of all equivalents; might be empty, but never null.
	 */
	public List<Equivalent> getEquivalents() {
		return equivalents;
	}

	/**
	 * Returns the {@link List} of all {@link Equivalent} instances of this
	 * {@link Sense} in the specified language. That is, the translation of the this sense into a language
	 * specified by a language code, usually defined by a constant in {@link ELanguageIdentifier}.
	 * @return the list of all equivalents in this language; might be empty, but never null.
	 */
	public List<Equivalent> getEquivalentsByLanguage(String language) {
		List<Equivalent> language_equivalents = new ArrayList<Equivalent>();
		for(Equivalent eq : equivalents)
		{
			if(eq.getLanguageIdentifier().equals(language))
			{
				language_equivalents.add(eq);
			}
		}
		return language_equivalents;
	}
	/**
	 * Sets the {@link List} of all {@link Equivalent} instances to this
	 * {@link Sense}.
	 * @param equivalents the list of equivalents to set
	 */
	public void setEquivalents(List<Equivalent> equivalents) {
		this.equivalents = equivalents;
	}

	/**
	 * Returns the {@link List} of all {@link MonolingualExternalRef} instances which
	 * link this {@link Sense} to an external system.
	 * @return the list of all monolingual external references of this sense or an empty
	 * list if the sense does not have any monolingual external references set
	 */
	@Override
	public List<MonolingualExternalRef> getMonolingualExternalRefs() {
		return monolingualExternalRefs;
	}

	/**
	 * Sets the {@link List} of all {@link MonolingualExternalRef} instances to this
	 * {@link Sense}.
	 * @param monolingualExternalRefs the monolingual external references to set
	 */
	@Override
	public void setMonolingualExternalRefs(List<MonolingualExternalRef> monolingualExternalRefs) {
		this.monolingualExternalRefs = monolingualExternalRefs;
	}

	/**
	 * Returns the {@link LexicalEntry} containing this {@link Sense} instance. <p>
	 * <i>This backlink is not a part of Uby-LMF model and exists for convenience.</i>
	 * @return the lexical entry containing this lexicon or null if the backlink is not set
	 */
	public LexicalEntry getLexicalEntry() {
		return lexicalEntry;
	}

	/**
	 * Sets the {@link LexicalEntry} containing this {@link Sense} instance.<p>
	 * <i> This backlink is not a part of Uby-LMF model and exists for convenience.</i>
	 * @param lexicalEntry the lexical entry
	 */
	public void setLexicalEntry(LexicalEntry lexicalEntry) {
		this.lexicalEntry = lexicalEntry;
	}

	/**
	 * Returns the definition text of this {@link Sense} instance.<br>
	 * Definition text is extracted from the first {@link Definition} instance
	 * of this Sense.
	 *
	 * @return definition text of this sense or null
	 * if the sense does not have any definitions attached or the first
	 * attached definition does not contain any text representations
	 *
	 * @see TextRepresentation
	 *
	 */
	public String getDefinitionText(){

		if(!definitions.isEmpty()){
			List<TextRepresentation> textRepresentations = definitions.get(0).getTextRepresentations();
			if(!textRepresentations.isEmpty()) {
				return textRepresentations.get(0).getWrittenText();
			}
		}

		return null;
	}

	/**
	 * Set the {@link List} of all {@link Frequency} instances to this {@link Sense}
	 * instance.
	 * @param frequencies the list of frequencies to set
	 */
	public void setFrequencies(List<Frequency> frequencies) {
		this.frequencies = frequencies;
	}

	/**
	 * Returns the {@link List} of all {@link Frequency} instances of this {@link Sense}
	 * instance.
	 * @return the list of all frequencies of the sense or an empty list, if the sense does
	 * not have any frequencies set
	 */
	public List<Frequency> getFrequencies() {
		return frequencies;
	}

	/**
	 * Sets the {@link List} of all {@link SemanticLabel} instances attached to
	 * this {@link Sense} instance.
	 * @param semanticLabels the list of all semantic labels to set
	 */
	public void setSemanticLabels(List<SemanticLabel> semanticLabels) {
		this.semanticLabels = semanticLabels;
	}

	/**
	 * Returns the list of all {@link SemanticLabel} instances attached to this
	 * {@link Sense} instance.
	 * @return the list of all semantic labels attached to this sense
	 * or an empty list, if the sense does not have any semantic labels attached
	 */
	public List<SemanticLabel> getSemanticLabels() {
		return semanticLabels;
	}

	/**
	 * Attaches a {@link SemanticLabel} instance to this {@link Sense} instance.
	 *
	 * @param semanticLabel the semantic label to attach to this sense
	 *
	 * @return true if semanticLabel is not already attached to this sense, false if not or semanticLabel
	 * is null
	 */
	public boolean addSemanticLabel(SemanticLabel semanticLabel){
		if(semanticLabel == null || this.semanticLabels.contains(semanticLabel)) {
			return false;
		}
		else{
			this.semanticLabels.add(semanticLabel);
			return true;
		}
	}

	@Override
	public String toString(){
		return this.id == null?"":this.id.toString();
	}

	@Override
	public int compareTo(Sense o) {
		return this.toString().compareTo(o.toString());
	}

	@Override
	public boolean equals(Object other) {
	    if (this == other) {
			return true;
		}
	    if (!(other instanceof Sense)) {
			return false;
		}
	    Sense otherSense = (Sense) other;
	    return this.id==null ? otherSense.id==null : this.id.equals(otherSense.id);
	 }

	@Override
	public int hashCode() {
		return 31 + (id == null ? 0 : id.hashCode());
//	    int hash = 1;
//	    hash = hash * 31 + this.id==null?0:this.id.hashCode(); <-- ChM: ???
//	    return hash;
	}

}
