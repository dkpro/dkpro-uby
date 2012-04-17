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

import java.util.List;

import de.tudarmstadt.ukp.lmf.model.enums.ELanguageIdentifier;
import de.tudarmstadt.ukp.lmf.model.interfaces.IHasID;
import de.tudarmstadt.ukp.lmf.model.interfaces.IHasLanguageIdentifier;
import de.tudarmstadt.ukp.lmf.model.miscellaneous.ConstraintSet;
import de.tudarmstadt.ukp.lmf.model.miscellaneous.EVarType;
import de.tudarmstadt.ukp.lmf.model.miscellaneous.VarType;
import de.tudarmstadt.ukp.lmf.model.semantics.SemanticPredicate;
import de.tudarmstadt.ukp.lmf.model.semantics.SynSemCorrespondence;
import de.tudarmstadt.ukp.lmf.model.semantics.Synset;
import de.tudarmstadt.ukp.lmf.model.syntax.SubcategorizationFrame;
import de.tudarmstadt.ukp.lmf.model.syntax.SubcategorizationFrameSet;

public class Lexicon implements IHasID, IHasLanguageIdentifier {
	
	// LanguageIdentifier of the Lexicon
	@VarType(type = EVarType.ATTRIBUTE)
	private ELanguageIdentifier languageIdentifier;
	
	// Id of this Lexicon
	@VarType(type = EVarType.ATTRIBUTE)
	private String id;

	// name of this Lexicon
	@VarType(type = EVarType.ATTRIBUTE)
	private String name;
	
	// List of all LexicalEntries of this Lexicon
	@VarType(type = EVarType.CHILDREN)
	private List<LexicalEntry> lexicalEntries;
	
	// List of all SubcategorizationFrames of this Lexicon
	@VarType(type = EVarType.CHILDREN)
	private List<SubcategorizationFrame> subcategorizationFrames;

	// List of all  SubcategorizationFrameSets of this Lexicon
	@VarType(type = EVarType.CHILDREN)
	private List<SubcategorizationFrameSet> subcategorizationFrameSets;
	
	// List of all SemanticPredicates of this Lexicon
	@VarType(type = EVarType.CHILDREN)
	private List<SemanticPredicate> semanticPredicates;
	
	// List of all Synsets of this Lexicon
	@VarType(type = EVarType.CHILDREN)
	private List<Synset> synsets;
	
	// List of all SynSemCorrespondences of this Lexicon
	@VarType(type = EVarType.CHILDREN)
	private List<SynSemCorrespondence> synSemCorrespondences;
	
	// List of all ConstraintSets
	@VarType(type = EVarType.CHILDREN)
	private List<ConstraintSet> constraintSets;
	
	// Backlink to LexicalResource added for convenience
	@VarType(type = EVarType.NONE)
	private LexicalResource lexicalResource;
	

	/**
	 * @return the lexicalEntries
	 */
	public List<LexicalEntry> getLexicalEntries() {
		return lexicalEntries;
	}

	/**
	 * @param lexicalEntries the lexicalEntries to set
	 */
	public void setLexicalEntries(List<LexicalEntry> lexicalEntries) {
		this.lexicalEntries = lexicalEntries;
	}

	/**
	 * @return the subcategorizationFrames
	 */
	public List<SubcategorizationFrame> getSubcategorizationFrames() {
		return subcategorizationFrames;
	}

	/**
	 * @param subcategorizationFrames the subcategorizationFrames to set
	 */
	public void setSubcategorizationFrames(
			List<SubcategorizationFrame> subcategorizationFrames) {
		this.subcategorizationFrames = subcategorizationFrames;
	}

	/**
	 * @return the semanticPredicates
	 */
	public List<SemanticPredicate> getSemanticPredicates() {
		return semanticPredicates;
	}

	/**
	 * @param semanticPredicates the semanticPredicates to set
	 */
	public void setSemanticPredicates(List<SemanticPredicate> semanticPredicates) {
		this.semanticPredicates = semanticPredicates;
	}

	/**
	 * @return the synsets
	 */
	public List<Synset> getSynsets() {
		return synsets;
	}

	/**
	 * @param synsets the synsets to set
	 */
	public void setSynsets(List<Synset> synsets) {
		this.synsets = synsets;
	}

	/**
	 * @return the synSemCorrespondences
	 */
	public List<SynSemCorrespondence> getSynSemCorrespondences() {
		return synSemCorrespondences;
	}

	/**
	 * @param synSemCorrespondences the synSemCorrespondences to set
	 */
	public void setSynSemCorrespondences(
			List<SynSemCorrespondence> synSemCorrespondences) {
		this.synSemCorrespondences = synSemCorrespondences;
	}

	/**
	 * @return the constraintSets
	 */
	public List<ConstraintSet> getConstraintSets() {
		return constraintSets;
	}

	/**
	 * @param constraintSets the constraintSets to set
	 */
	public void setConstraintSets(List<ConstraintSet> constraintSets) {
		this.constraintSets = constraintSets;
	}

	/**
	 * @return the languageIdentifier
	 */
	public ELanguageIdentifier getLanguageIdentifier() {
		return languageIdentifier;
	}

	/**
	 * @param languageIdentifier the languageIdentifier to set
	 */
	public void setLanguageIdentifier(ELanguageIdentifier languageIdentifier) {
		this.languageIdentifier = languageIdentifier;
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
	 * 
	 * @param name the name of the lexicon
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * 
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the subcategorizationFrameSets
	 */
	public List<SubcategorizationFrameSet> getSubcategorizationFrameSets() {
		return subcategorizationFrameSets;
	}

	/**
	 * @param subcategorizationFrameSets the subcategorizationFrameSets to set
	 */
	public void setSubcategorizationFrameSets(
			List<SubcategorizationFrameSet> subcategorizationFrameSets) {
		this.subcategorizationFrameSets = subcategorizationFrameSets;
	}

	/**
	 * @return the lexicalResource
	 */
	public LexicalResource getLexicalResource() {
		return lexicalResource;
	}

	/**
	 * @param lexicalResource the lexicalResource to set
	 */
	public void setLexicalResource(LexicalResource lexicalResource) {
		this.lexicalResource = lexicalResource;
	}
	
	
	
}
