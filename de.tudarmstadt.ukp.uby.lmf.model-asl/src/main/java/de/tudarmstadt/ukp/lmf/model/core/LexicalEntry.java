/*******************************************************************************
 * Copyright 2017
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

import de.tudarmstadt.ukp.lmf.model.abstracts.HasFrequencies;
import de.tudarmstadt.ukp.lmf.model.enums.EPartOfSpeech;
import de.tudarmstadt.ukp.lmf.model.interfaces.IHasID;
import de.tudarmstadt.ukp.lmf.model.meta.Frequency;
import de.tudarmstadt.ukp.lmf.model.miscellaneous.EVarType;
import de.tudarmstadt.ukp.lmf.model.miscellaneous.VarType;
import de.tudarmstadt.ukp.lmf.model.morphology.FormRepresentation;
import de.tudarmstadt.ukp.lmf.model.morphology.Lemma;
import de.tudarmstadt.ukp.lmf.model.morphology.ListOfComponents;
import de.tudarmstadt.ukp.lmf.model.morphology.RelatedForm;
import de.tudarmstadt.ukp.lmf.model.morphology.WordForm;
import de.tudarmstadt.ukp.lmf.model.semantics.Synset;
import de.tudarmstadt.ukp.lmf.model.syntax.SyntacticBehaviour;


/**
 * LexicalEntry is a class representing a lexeme in a given language. The LexicalEntry is a
 * container for managing the {@link RelatedForm}, {@link WordForm} and {@link Sense} classes.
 * Therefore, the LexicalEntry manages the relationship between the forms and their related senses.
 * <p> A  LexicalEntry instance can have from zero to many different senses.
 *
 * @see RelatedForm
 * @see WordForm
 *
 * @author Zijad Maksuti
 *
 */
public class LexicalEntry extends HasFrequencies implements IHasID, Comparable<LexicalEntry>{
	// Id of this LexicalEntry
	@VarType(type = EVarType.ATTRIBUTE)
	private String id;

	// Part-of-Speech of this LexicalEntry
	@VarType(type = EVarType.ATTRIBUTE)
	private EPartOfSpeech partOfSpeech;

	// Separable Particle of this LexicalEntry
	@VarType(type = EVarType.ATTRIBUTE)
	private String separableParticle;

	// Lemma of this LexicalEntry
	@VarType(type = EVarType.CHILD)
	private Lemma lemma;

	// Word Forms of this LexicalEntry
	@VarType(type = EVarType.CHILDREN)
	private List<WordForm> wordForms = new ArrayList<WordForm>();

	// Related Forms of this LexicalEntry
	@VarType(type = EVarType.CHILDREN)
	private List<RelatedForm> relatedForms = new ArrayList<RelatedForm>();

	// Senses of this LexicalEntry
	@VarType(type = EVarType.CHILDREN)
	private List<Sense> senses = new ArrayList<Sense>();

	// Syntactic Behaviours of this LexicalEntry
	@VarType(type = EVarType.CHILDREN)
	private List<SyntacticBehaviour> syntacticBehaviours = new ArrayList<SyntacticBehaviour>();

	// List of components of this LexicalEntry
	@VarType(type = EVarType.CHILD)
	private ListOfComponents listOfComponents;

	// Backlink to Lexicon added for convenience
	@VarType(type = EVarType.NONE)
	private Lexicon lexicon;

	/**
	 * Constructs a {@link LexicalEntry} instance with the specified
	 * identifier.
	 *
	 * @param identifier the unique identifier of the lexical entry
	 *
	 * @since 0.2.0
	 */
	public LexicalEntry(String identifier){
		this.id = identifier;
	}

	/**
	 * Constructs an empty {@link LexicalEntry} instance.
	 *
	 * @see #LexicalEntry(String)
	 */
	public LexicalEntry(){
		// nothing to do here
	}


	/**
	 * Returns the unique identifier of this {@link LexicalEntry} instance.
	 * @return the id of this LexicalEntry instance, or null if the id is not set
	 */
	public String getId() {
		return id;
	}

	/**
	 * Sets a unique identifier to this {@link LexicalEntry} instance.
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * Returns the part of speech of this {@linki LexicalEntry} instance.
	 * @return the LexicalEntrys part of speech, or null of the part of speech is not set
	 * @see EPartOfSpeech
	 */
	public EPartOfSpeech getPartOfSpeech() {
		return partOfSpeech;
	}

	/**
	 * Sets the part of speech information to this {@link LexicalEntry} instance.
	 * @param partOfSpeech the part of speech information to set
	 * @see EPartOfSpeech
	 */
	public void setPartOfSpeech(EPartOfSpeech partOfSpeech) {
		this.partOfSpeech = partOfSpeech;
	}



	/**
	 * Returns the separable particle of this {@link LexicalEntry} instance.
	 * @return the LexicalEntrys separable particle or null,
	 * if the LexicalEntry does'n have a separable particle
	 */
	public String getSeparableParticle() {
		return separableParticle;
	}

	/**
	 * Sets a separable particle to this {@link LexicalEntry} instance.
	 * @param separableParticle the separable particle to set
	 */
	public void setSeparableParticle(String separableParticle) {
		this.separableParticle = separableParticle;
	}

	/**
	 * Returns this LexicalEntrys {@link Lemma}.
	 * @return the lemma of this LexicalEntry or null, if the lemma is not set
	 * @see LexicalEntry
	 */
	public Lemma getLemma() {
		return lemma;
	}

	/**
	 * Sets a {@link Lemma} to this {@link LexicalEntry} instance.
	 * @param lemma the lemma to set
	 */
	public void setLemma(Lemma lemma) {
		this.lemma = lemma;
	}

	/**
	 * Returns a {@link List} of all {@link WordForm} instances of this {@link LexicalEntry}
	 * @return all word forms of this lexical entry, or an empty list if the LexicalEntry has no word forms
	 */
	public List<WordForm> getWordForms() {
		return wordForms;
	}

	/**
	 * Sets word forms to this {@link LexicalEntry} instance
	 * @param wordForms the word forms to set
	 * @see WordForm
	 */
	public void setWordForms(List<WordForm> wordForms) {
		this.wordForms = wordForms;
	}

	/**
	 * Returns a {@link List} of all {@link RelatedForm} instances of this {@link LexicalEntry}
	 * @return all related forms of this lexical entry, or an empty list if the LexicalEntry has no related forms
	 */
	public List<RelatedForm> getRelatedForms() {
		return relatedForms;
	}

	/**
	 * Sets related forms to this {@link LexicalEntry} instance
	 * @param relatedForms the related forms to set
	 * @see RelatedForm
	 */
	public void setRelatedForms(List<RelatedForm> relatedForms) {
		this.relatedForms = relatedForms;
	}

	/**
	 * Returns a {@link List} of all {@link Sense} instances of this {@link LexicalEntry}
	 * @return all senses of this LexicalEntry or an empty list, if this LexicalEntry has no senses set
	 */
	public List<Sense> getSenses() {
		return senses;
	}

	/**
	 * Sets senses of this {@link LexicalEntry} instance
	 * @param senses the senses to set
	 * @see Sense
	 */
	public void setSenses(List<Sense> senses) {
		this.senses = senses;
	}

	/**
	 * Returns a {@link List}  of all {@link SyntacticBehaviour} instances of this {@link LexicalEntry}
	 * @return a list of syntactic behaviors of this lexical entry or an empty list, if this lexical entry has no
	 * syntactic behaviors set
	 */
	public List<SyntacticBehaviour> getSyntacticBehaviours() {
		return syntacticBehaviours;
	}

	/**
	 * Sets the syntactic behaviours to this {@link LexicalEntry}
	 * @param syntacticBehaviours the syntactic behaviours to set
	 * @see SyntacticBehaviour
	 */
	public void setSyntacticBehaviours(List<SyntacticBehaviour> syntacticBehaviours) {
		this.syntacticBehaviours = syntacticBehaviours;
	}


	/**
	 * Returns the {@link Lexicon} containing this {@link LexicalEntry} instance. <p>
	 * <i>This backlink is not a part of Uby-LMF model and exists for convenience.</i>
	 * @return the lexicon containing this lexical entry or null if the backlink is not set
	 */
	public Lexicon getLexicon() {
		return lexicon;
	}

	/**
	 * Sets the {@link Lexicon} containing this {@link LexicalEntry} instance.<p>
	 * <i> This backlink is not a part of Uby-LMF model and exists for convenience.</i>
	 * @param lexicon the lexicon to set
	 */
	public void setLexicon(Lexicon lexicon) {
		this.lexicon = lexicon;
	}


	/**
	 * Returns all {@link Synset} instances of this {@link LexicalEntry} by aggregating them from lexical entry's {@link Sense} instances
	 * @return a {@link List} of all synsets of this lexical entry or an empty list, if none of lexical entry's senses has a synset
	 */
	public List<Synset> getSynsets(){
		List<Synset> result = new ArrayList<Synset>();
		for(Sense sense : senses){
			if(sense.getSynset()!=null)
				result.add(sense.getSynset());
		}
		return result;
	}

	/**
	 * Returns the written form of this {@link LexicalEntry}'s {@link Lemma} first {@link FormRepresentation}.
	 * @return the written form of the first form representation of this lexical entry's lemma or null, if the lemma has no form representations
	 */
	public String getLemmaForm(){
		List<FormRepresentation> temp = lemma.getFormRepresentations();
		if(temp.isEmpty())
			return null;
		else
			return temp.get(0).getWrittenForm();
	}

	/**
	 * Sets the list of components of this {@link LexicalEntry} instance.
	 * @param listOfComponents the list of components to set
	 * @see ListOfComponents
	 */
	public void setListOfComponents(ListOfComponents listOfComponents) {
		this.listOfComponents = listOfComponents;
	}

	/**
	 * Returns the list of components of this {@link LexicalEntry} instance.
	 * @return list of components of this lexical entry or null, if this lexical entry is not a multiword expression
	 * or the list of components is not set
	 * @see ListOfComponents
	 */
	public ListOfComponents getListOfComponents() {
		return listOfComponents;
	}

	/**
	 * Sets the {@link List} of {@link Frequency} instances to this lexical entry.
	 * @param frequencies the frequencies to set
	 * @see LexicalEntry
	 */
	@Override
	public void setFrequencies(List<Frequency> frequencies) {
		super.setFrequencies(frequencies);
	}

	/**
	 * Returns a {@link List} of all {@link Frequency} instances of this lexical entry.
	 * @return a list of all frequencies of this lexical entry or an empty list, if this lexical entry has no frequencies set
	 * @see LexicalEntry
	 */
	@Override
	public List<Frequency> getFrequencies() {
		return super.getFrequencies();
	}

	@Override
	public String toString(){
		return this.id == null?"":this.id.toString();
	}

	@Override
	public int compareTo(LexicalEntry o) {
		return this.toString().compareTo(o.toString());
	}
	@Override
	public boolean equals(Object other) {
	    if (this == other)
	      return true;
	    if (!(other instanceof LexicalEntry))
	      return false;
	    LexicalEntry otherLexicalEntry = (LexicalEntry) other;
	    return this.id==null ? otherLexicalEntry.id==null : this.id.equals(otherLexicalEntry.id);
	 }
	@Override
	public int hashCode() {
		return 31 + (id == null ? 0 : id.hashCode());
//	    int hash = 1;
//	    hash = hash * 31 + this.id==null?0:this.id.hashCode(); <-- ChM: ???
//	    return hash;
	}


}
