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
package de.tudarmstadt.ukp.lmf.model.core;

import java.util.ArrayList;
import java.util.List;

import de.tudarmstadt.ukp.lmf.model.enums.EPartOfSpeech;
import de.tudarmstadt.ukp.lmf.model.interfaces.IHasID;
import de.tudarmstadt.ukp.lmf.model.meta.Frequency;
import de.tudarmstadt.ukp.lmf.model.miscellaneous.EVarType;
import de.tudarmstadt.ukp.lmf.model.miscellaneous.VarType;
import de.tudarmstadt.ukp.lmf.model.morphology.Lemma;
import de.tudarmstadt.ukp.lmf.model.morphology.ListOfComponents;
import de.tudarmstadt.ukp.lmf.model.morphology.RelatedForm;
import de.tudarmstadt.ukp.lmf.model.morphology.WordForm;
import de.tudarmstadt.ukp.lmf.model.semantics.Synset;
import de.tudarmstadt.ukp.lmf.model.syntax.SyntacticBehaviour;


/**
 * LexicalEntry is a class representing a lexeme in a given language. The LexicalEntry is a 
 * container for managing the Form and {@link Sense} classes.
 * Therefore, the LexicalEntry manages the relationship between the forms and their related senses.
 * <p> A  LexicalEntry instance can have from zero to many different senses.
 * 
 * @see RelatedForm
 * @see WordForm
 * 
 * @author Zijad Maksuti
 *
 */
public class LexicalEntry implements IHasID, Comparable<LexicalEntry>{
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

	// Frequency information for this LexicalEntry
	@VarType(type = EVarType.CHILDREN)
	private List<Frequency> frequencies = new ArrayList<Frequency>();
	
	// Backlink to Lexicon added for convenience
	@VarType(type = EVarType.NONE)
	private Lexicon lexicon;

	
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
	 * @return the wordForms
	 */
	public List<WordForm> getWordForms() {
		return wordForms;
	}

	/**
	 * @param wordForms the wordForms to set
	 */
	public void setWordForms(List<WordForm> wordForms) {
		this.wordForms = wordForms;
	}

	/**
	 * @return the relatedForms
	 */
	public List<RelatedForm> getRelatedForms() {
		return relatedForms;
	}

	/**
	 * @param relatedForms the relatedForms to set
	 */
	public void setRelatedForms(List<RelatedForm> relatedForms) {
		this.relatedForms = relatedForms;
	}

	/**
	 * @return the senses
	 */
	public List<Sense> getSenses() {
		return senses;
	}

	/**
	 * @param senses the senses to set
	 */
	public void setSenses(List<Sense> senses) {
		this.senses = senses;
	}

	/**
	 * @return the syntacticBehaviours
	 */
	public List<SyntacticBehaviour> getSyntacticBehaviours() {
		return syntacticBehaviours;
	}

	/**
	 * @param syntacticBehaviours the syntacticBehaviours to set
	 */
	public void setSyntacticBehaviours(List<SyntacticBehaviour> syntacticBehaviours) {
		this.syntacticBehaviours = syntacticBehaviours;
	}
	
	
	/**
	 * @return the lexicon
	 */
	public Lexicon getLexicon() {
		return lexicon;
	}

	/**
	 * @param lexicon the lexicon to set
	 */
	public void setLexicon(Lexicon lexicon) {
		this.lexicon = lexicon;
	}
	
	
	/**
	 * Returns all Synsets of this LexicalEntry by aggregating them from all Senses
	 * @return
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
	 * Returns writtenForm of first FormRepresentation of Lemma
	 * @return
	 */
	public String getLemmaForm(){
		if(lemma.getFormRepresentations().isEmpty())			
			return "";
		else return lemma.getFormRepresentations().get(0).getWrittenForm();
	}
		
	/**
	 * 
	 * @param listOfComponents the listOfComponents to set
	 */
	public void setListOfComponents(ListOfComponents listOfComponents) {
		this.listOfComponents = listOfComponents;
	}

	/**
	 * 
	 * @return the listOfComponents
	 */
	public ListOfComponents getListOfComponents() {
		return listOfComponents;
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
	    int hash = 1;
	    hash = hash * 31 + this.id==null?0:this.id.hashCode();
	    return hash;
	}


}
