/*******************************************************************************
 * Copyright 2013
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

/**
 * Lexicon is a class containing all the lexical entries of a given language within the entire 
 * resource. A lexicon should contain at least one lexical entry.
 * @see LexicalResource
 * @see LexicalEntry
 * @author Zijad Maksuti
 *
 */
public class Lexicon implements IHasID, IHasLanguageIdentifier {
	
	// LanguageIdentifier of the Lexicon
	@VarType(type = EVarType.ATTRIBUTE)
	private String languageIdentifier;
	
	// Id of this Lexicon
	@VarType(type = EVarType.ATTRIBUTE)
	private String id;

	// name of this Lexicon
	@VarType(type = EVarType.ATTRIBUTE)
	private String name;
	
	// List of all LexicalEntries of this Lexicon
	@VarType(type = EVarType.CHILDREN)
	private List<LexicalEntry> lexicalEntries = new ArrayList<LexicalEntry>();
	
	// List of all SubcategorizationFrames of this Lexicon
	@VarType(type = EVarType.CHILDREN)
	private List<SubcategorizationFrame> subcategorizationFrames = new ArrayList<SubcategorizationFrame>();

	// List of all  SubcategorizationFrameSets of this Lexicon
	@VarType(type = EVarType.CHILDREN)
	private List<SubcategorizationFrameSet> subcategorizationFrameSets = new ArrayList<SubcategorizationFrameSet>();
	
	// List of all SemanticPredicates of this Lexicon
	@VarType(type = EVarType.CHILDREN)
	private List<SemanticPredicate> semanticPredicates = new ArrayList<SemanticPredicate>();
	
	// List of all Synsets of this Lexicon
	@VarType(type = EVarType.CHILDREN)
	private List<Synset> synsets = new ArrayList<Synset>();
	
	// List of all SynSemCorrespondences of this Lexicon
	@VarType(type = EVarType.CHILDREN)
	private List<SynSemCorrespondence> synSemCorrespondences = new ArrayList<SynSemCorrespondence>();
	
	// List of all ConstraintSets
	@VarType(type = EVarType.CHILDREN)
	private List<ConstraintSet> constraintSets = new ArrayList<ConstraintSet>();
	
	// Backlink to LexicalResource added for convenience
	@VarType(type = EVarType.NONE)
	private LexicalResource lexicalResource;
	

	/**
	 * Returns a {@link List} of all {@link LexicalEntry} instances contained in this {@link Lexicon}.
	 * @return a list of all lexical entries in the lexicon or an empty list, if this lexicon does not contain any
	 * lexical entries.
	 */
	public List<LexicalEntry> getLexicalEntries() {
		return lexicalEntries;
	}

	/**
	 * Sets a {@link List} of all {@link LexicalEntry} instances contained in this {@link Lexicon}.
	 * @param lexicalEntries the lexical entries to set
	 */
	public void setLexicalEntries(List<LexicalEntry> lexicalEntries) {
		this.lexicalEntries = lexicalEntries;
	}

	/**
	 * Returns the {@link List} of all {@link SubcategorizationFrame} instances contained in this {@link Lexicon}
	 * instance. 
	 * @return a list of all subcategorization frames or an empty list, if the
	 * lexicon does not contain subcategorization frames.
	 */
	public List<SubcategorizationFrame> getSubcategorizationFrames() {
		return subcategorizationFrames;
	}

	/**
	 * Sets the {@link List} of all {@link SubcategorizationFrame} instances contained in this {@link Lexicon}.
	 * @param subcategorizationFrames the subcategorization frames to set
	 */
	public void setSubcategorizationFrames(List<SubcategorizationFrame> subcategorizationFrames) {
		this.subcategorizationFrames = subcategorizationFrames;
	}

	/**
	 * Returns the {@link List} of all {@link SemanticPredicate} contained in this {@link Lexicon} instance.
	 * @return a list of all semantic predicates of this lexicon or an empty list, if the lexicon
	 * does not contain any semantic predicates
	 */
	public List<SemanticPredicate> getSemanticPredicates() {
		return semanticPredicates;
	}

	/**
	 * Sets the {@link List} of all {@link SemanticPredicate} instances contained in this {@link Lexicon} instance.
	 * @param semanticPredicates the semantic predicates to set
	 */
	public void setSemanticPredicates(List<SemanticPredicate> semanticPredicates) {
		this.semanticPredicates = semanticPredicates;
	}

	/**
	 * Returns the {@link List} of all {@link Synset} instances contained in this {@link Lexicon}.
	 * @return a list of all synsets contained in this lexicon or an empty list, if the lexicon does not contain
	 * any synsets
	 */
	public List<Synset> getSynsets() {
		return synsets;
	}

	/**
	 * Sets the {@link List} of all {@link Synset} instances to this {@link Lexicon} instance. 
	 * @param synsets the synsets to set
	 */
	public void setSynsets(List<Synset> synsets) {
		this.synsets = synsets;
	}

	/**
	 * Returns the {@link List} of all {@link SynSemCorrespondence} instances contained in this {@link Lexicon}. 
	 * @return a list of all syntactic-semantic correspondences contained in this lexicon or an empty list, if
	 * the lexicon does not contain any syntactic-semantic correspondences
 	 */
	public List<SynSemCorrespondence> getSynSemCorrespondences() {
		return synSemCorrespondences;
	}

	/**
	 * Sets the {@link List} of all {@link SynSemCorrespondence} instances to this {@link Lexicon} instance.
	 * @param synSemCorrespondences the list of syntactic-semantic correspondences to set
	 */
	public void setSynSemCorrespondences(
			List<SynSemCorrespondence> synSemCorrespondences) {
		this.synSemCorrespondences = synSemCorrespondences;
	}

	/**
	 * Returns a {@link List} of all {@link ConstraintSet} instances associated to this {@link Lexicon}.
	 * @return a list of all constraint sets associated to this lexicon or an empty list, if this lexicon does not have
	 * any contraint sets associated  
	 */
	public List<ConstraintSet> getConstraintSets() {
		return constraintSets;
	}

	/**
	 * Associates a {@link List} of all {@link ConstraintSet} instances to this {@link Lexicon}.
	 * @param constraintSets the list of constraint sets to associate
	 */
	public void setConstraintSets(List<ConstraintSet> constraintSets) {
		this.constraintSets = constraintSets;
	}

	public String getLanguageIdentifier() {
		return languageIdentifier;
	}

	public void setLanguageIdentifier(final String languageIdentifier) {
		this.languageIdentifier = languageIdentifier;
	}

	/**
	 * Returns the unique identifier of this {@link Lexicon} instance.
	 * @return the unique identifier of this lexicon or null, if the identifier is not set
	 */
	public String getId() {
		return id;
	}

	/**
	 * Sets the unique identifier to this {@link Lexicon} instance.
	 * @param id the unique identifier to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * Sets the human readable name to this {@link Lexicon} instance.
	 * @param name the name of the lexicon to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Returns the human readable name of this {@link Lexicon} instance.
	 * @return the name of this lexicon or null, if the name is not set
	 */
	public String getName() {
		return name;
	}

	/**
	 * Returns a {@link List} of all {@link SubcategorizationFrameSet} instances contained in this {@link Lexicon}.
	 * @return the list of all subcategorization frame sets contained in this lexicon or an empty list,
	 * if the lexicon does not contain any subcategorization frame sets
	 */
	public List<SubcategorizationFrameSet> getSubcategorizationFrameSets() {
		return subcategorizationFrameSets;
	}

	/**
	 * Sets a {@link List} of all {@link SubcategorizationFrameSet} instances to this {@link Lexicon}.
	 * @param subcategorizationFrameSets the list of subcategorization frame sets to set
	 */
	public void setSubcategorizationFrameSets(List<SubcategorizationFrameSet> subcategorizationFrameSets) {
		this.subcategorizationFrameSets = subcategorizationFrameSets;
	}

	/**
	 * Returns the {@link LexicalResource} containing this {@link Lexicon} instance. <p>
	 * <i>This backlink is not a part of Uby-LMF model and exists for convenience.</i>
	 * @return the lexical resource containing this lexicon or null if the backlink is not set
	 */
	public LexicalResource getLexicalResource() {
		return lexicalResource;
	}

	/**
	 * Sets the {@link LexicalResource} containing this {@link Lexicon} instance.<p>
	 * <i> This backlink is not a part of Uby-LMF model and exists for convenience.</i>
	 * @param lexicon the lexicon to set
	 */
	public void setLexicalResource(LexicalResource lexicalResource) {
		this.lexicalResource = lexicalResource;
	}
	
	/**
	 * Adds a {@link LexicalEntry} instance to this {@link Lexicon} instance.
	 * 
	 * @param lexicalEntry the lexical entry to add
	 * 
	 * @return <code>true</code> if this lexicon did not already contain
	 * an equal lexical entry, <code>false</code> otherwise
	 * 
	 * @since 0.2.0
	 */
	public boolean addLexicalEntry(LexicalEntry lexicalEntry){
		if(!this.lexicalEntries.contains(lexicalEntry)){
			this.lexicalEntries.add(lexicalEntry);
			return true;
		}
		return false;
	}
	
	
	
}
