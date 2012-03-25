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
 * This class represents a LexicalEntry
 * @author zijad
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
	private List<WordForm> wordForms;
	
	// Related Forms of this LexicalEntry
	@VarType(type = EVarType.CHILDREN)
	private List<RelatedForm> relatedForms;
	
	// Senses of this LexicalEntry
	@VarType(type = EVarType.CHILDREN)
	private List<Sense> senses;
	
	// Syntactic Behaviours of this LexicalEntry
	@VarType(type = EVarType.CHILDREN)
	private List<SyntacticBehaviour> syntacticBehaviours;
	
	// List of components of this LexicalEntry
	@VarType(type = EVarType.CHILD)
	private ListOfComponents listOfComponents;

	// Frequency information for this LexicalEntry
	@VarType(type = EVarType.CHILDREN)
	private List<Frequency> frequencies;
	
	// Backlink to Lexicon added for convenience
	@VarType(type = EVarType.NONE)
	private Lexicon lexicon;

	
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
	 * @return the partOfSpeech
	 */
	public EPartOfSpeech getPartOfSpeech() {
		return partOfSpeech;
	}

	/**
	 * @param partOfSpeech the partOfSpeech to set
	 */
	public void setPartOfSpeech(EPartOfSpeech partOfSpeech) {
		this.partOfSpeech = partOfSpeech;
	}



	/**
	 * @return the separableParticle
	 */
	public String getSeparableParticle() {
		return separableParticle;
	}

	/**
	 * @param separableParticle the separableParticle to set
	 */
	public void setSeparableParticle(String separableParticle) {
		this.separableParticle = separableParticle;
	}

	/**
	 * @return the lemma
	 */
	public Lemma getLemma() {
		return lemma;
	}

	/**
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
