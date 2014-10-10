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
package de.tudarmstadt.ukp.lmf.model.semantics;

import java.util.ArrayList;
import java.util.List;

import de.tudarmstadt.ukp.lmf.model.abstracts.HasMonolingualExternalRefs;
import de.tudarmstadt.ukp.lmf.model.core.Definition;
import de.tudarmstadt.ukp.lmf.model.core.LexicalEntry;
import de.tudarmstadt.ukp.lmf.model.core.Lexicon;
import de.tudarmstadt.ukp.lmf.model.core.Sense;
import de.tudarmstadt.ukp.lmf.model.core.TextRepresentation;
import de.tudarmstadt.ukp.lmf.model.enums.ELanguageIdentifier;
import de.tudarmstadt.ukp.lmf.model.interfaces.IHasDefinitions;
import de.tudarmstadt.ukp.lmf.model.interfaces.IHasID;
import de.tudarmstadt.ukp.lmf.model.miscellaneous.EVarType;
import de.tudarmstadt.ukp.lmf.model.miscellaneous.VarType;
import de.tudarmstadt.ukp.lmf.model.mrd.Equivalent;

/**
 * Synset is a class representing the set of shared meanings within the same language. Synset
 * links synonyms forming a synonym set. A  Synset can link {@link Sense} instances of different
 * {@link LexicalEntry} instances with the same part of speech.
 *
 * @author Zijad Maksuti
 * @author Silvana Hartmann
 *
 */
public class Synset extends HasMonolingualExternalRefs implements IHasID, IHasDefinitions, Comparable<Synset> {

	// Id of this Synset
	@VarType(type = EVarType.ATTRIBUTE)
	private String id;

	// Definitions of the Synset
	@VarType(type = EVarType.CHILDREN)
	private List<Definition> definitions = new ArrayList<Definition>();

	// Relations to other Synsets
	@VarType(type = EVarType.CHILDREN)
	private List<SynsetRelation> synsetRelations = new ArrayList<SynsetRelation>();


	// Senses of this synset - not in the model, added for convenience
	@VarType(type = EVarType.NONE)
	private List<Sense> senses = new ArrayList<Sense>();

	// Backlink to Lexicon added for convenience
	@VarType(type = EVarType.NONE)
	private Lexicon lexicon;
	
	
	/**
	 * Constructs an empty {@link Synset} instance.
	 *
	 * @since UBY 0.2.0
	 *
	 * @see #Synset(String)
	 *
	 */
	public Synset(){
		// nothing to do
	}

	/**
	 * Constructs a {@link Synset} instance with the
	 * consumed identifier.
	 *
	 * @param id the identifier of the created synset
	 *
	 * @since UBY 0.2.0
	 *
	 * @see #Synset()
	 */
	public Synset(String id){
		this.id = id;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public List<Definition> getDefinitions() {
		return definitions;
	}

	public void setDefinitions(List<Definition> definitions) {
		this.definitions = definitions;
	}

	/**
	 * Returns all {@link SynsetRelation} instances in which this {@link Synset} is the
	 * source of the relation.
	 *
	 * @return the list of all relations in which this synset is the source of the relation or an empty list
	 * if the synset is not the source of any relation
	 */
	public List<SynsetRelation> getSynsetRelations() {
		return synsetRelations;
	}

	/**
	 * Sets all {@link SynsetRelation} instances in which this {@link Synset} is the
	 * source of the relation.
	 *
	 * @param synsetRelations the list of all relations in which this synset is the source of the relation
	 *
	 */
	public void setSynsetRelations(List<SynsetRelation> synsetRelations) {
		this.synsetRelations = synsetRelations;
	}

	/**
	 * Returns all {@link Sense} instances contained in this
	 * {@link Synset}.<p>
	 * Note that this reference is not a part of UBY-LMF model and is added for convenience reasons.
	 *
	 * @return the list of senses contained in this synset or an empty list if
	 * the senses are not set
	 */
	public List<Sense> getSenses() {
		return this.senses;
	}

	/**
	 * Sets all {@link Sense} instances contained in this
	 * {@link Synset}.<p>
	 * Note that this reference is not a part of UBY-LMF model and is added for convenience reasons.
	 *
	 * @param senses the list of senses contained in this synset
	 *
	 */
	public void setSenses(List<Sense> senses) {
		this.senses = senses;
	}

	/**
	 * Returns the {@link List} of all {@link Equivalent} instances of the
	 * {@link Sense} instances belonging to this {@link Synset}. That is, the translations of all senses of the synset.
	 * @return the list of all equivalents; might be empty, but never null.
	 */
	public List<Equivalent> getEquivalents() {
		List<Equivalent> equivalents = new ArrayList<Equivalent>();
		for (Sense s: this.getSenses())
		{
			equivalents.addAll(s.getEquivalents());
		}
		return equivalents;
	}

	/**
	 * Returns the {@link List} of all {@link Equivalent} instances of the
	 * {@link Sense} instances belonging to this {@link Synset} in a certain language. That is, the translations of all senses of the synset into a language
	 * specified by a language code, usually defined by a constant in {@link ELanguageIdentifier}.
	 * @return the list of all equivalents; might be empty, but never null.
	 */
	public List<Equivalent> getEquivalentsByLanguage(String language) {
		List<Equivalent> equivalents = new ArrayList<Equivalent>();
		for (Sense s: this.getSenses())
		{
			equivalents.addAll(s.getEquivalentsByLanguage(language));
		}
		return equivalents;
	}

	/**
	 * Returns the gloss instance by aggregating defintion texts of all {@link Sense} instances
	 * of this {@link Synset}.
	 *
	 * @return gloss of this synset instance
	 *
	 * @see Sense#getDefinitionText()
	 */
	//TODO: This conflicts with Synset.getDefinitions (Synset vs. Sense definition).
	public String getGloss(){
		StringBuilder result = new StringBuilder();
		for(Sense sense : senses){
			String definitionText = sense.getDefinitionText();
			if(definitionText != null) {
				result.append(sense.getDefinitionText()).append(" ");
			}
		}
		return result.toString();
	}

	/**
	 * Returns the definition text of this {@link Synset} instance.
	 *
	 * The returned text is extracted from first {@link TextRepresentation} instance
	 * of the first {@link Definition} of this synset.<br> If the first definition
	 * of this synset does not have a text representation, this method returns null.
	 *
	 * @return definition text of this synset or null if this synset does not have a definition
	 * text set
	 */
	public String getDefinitionText(){
		if(definitions.isEmpty()) {
			return "";
		}
		Definition firstDefinition = definitions.get(0);
		if(firstDefinition.getTextRepresentations().isEmpty()) {
			return null;
		}
		else {
			return firstDefinition.getTextRepresentations().get(0).getWrittenText();
		}
	}

	/**
	 * Returns the {@link Lexicon} containing this {@link Synset} instance. <p>
	 * <i>This backlink is not a part of Uby-LMF model and exists for convenience.</i>
	 * @return the lexicon containing this synset or null if the backlink is not set
	 */
	public Lexicon getLexicon() {
		return lexicon;
	}

	/**
	 * Sets the {@link Lexicon} containing this {@link Synset} instance.<p>
	 * <i> This backlink is not a part of Uby-LMF model and exists for convenience.</i>
	 * @param lexicon the synset to set
	 */
	public void setLexicon(Lexicon lexicon) {
		this.lexicon = lexicon;
	}
	
	@Override
	public String toString(){
		return this.id == null?"":this.id.toString();
	}

	@Override
	public int compareTo(Synset o) {
		return this.toString().compareTo(o.toString());
	}
	@Override
	public boolean equals(Object other) {
	    if (this == other) {
			return true;
		}
	    if (!(other instanceof Synset)) {
			return false;
		}
	    Synset otherSynset = (Synset) other;
	    return this.id==null ? otherSynset.id==null : this.id.equals(otherSynset.id);
	 }
	@Override
	public int hashCode() {
	    int hash = 1;
	    hash = hash * 31 + this.id==null?0:this.id.hashCode();
	    return hash;
	}
}
