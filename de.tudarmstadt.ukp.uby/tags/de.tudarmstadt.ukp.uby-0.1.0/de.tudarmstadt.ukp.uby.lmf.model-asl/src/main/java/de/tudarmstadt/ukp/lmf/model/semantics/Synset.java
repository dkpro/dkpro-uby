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
import de.tudarmstadt.ukp.lmf.model.core.Sense;
import de.tudarmstadt.ukp.lmf.model.interfaces.IHasID;
import de.tudarmstadt.ukp.lmf.model.miscellaneous.EVarType;
import de.tudarmstadt.ukp.lmf.model.miscellaneous.VarType;

/**
 * This class represents a LMF-Synset
 * @author maksuti
 *
 */
public class Synset implements IHasID, Comparable<Synset> {

	// Id of this Synset
	@VarType(type = EVarType.ATTRIBUTE)
	private String id;

	// Definitions of the Synset
	@VarType(type = EVarType.CHILDREN)
	private List<Definition> definitions;

	// Relations to other Synsets
	@VarType(type = EVarType.CHILDREN)
	private List<SynsetRelation> synsetRelations;

	// Reference to a external Resource
	@VarType(type = EVarType.CHILDREN)
	private List<MonolingualExternalRef> monolingualExternalRefs;


	// Senses of this synset - not in the model, added for convenience
	@VarType(type = EVarType.NONE)
	private List<Sense> senses;

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
	 * Returns all relations in which this Synset is the source.
	 * Thus, it is guaranteed that s.equals(r.getSource()) is true for every SenseRelation r of a Synset s
	 *
	 * @return the synsetRelations
	 */
	public List<SynsetRelation> getSynsetRelations() {
		return synsetRelations;
	}

	/**
	 * @param synsetRelations the synsetRelations to set
	 */
	public void setSynsetRelations(List<SynsetRelation> synsetRelations) {
		this.synsetRelations = synsetRelations;
	}

	/**
	 * @return the monolingualExternalRefs
	 */
	public List<MonolingualExternalRef> getMonolingualExternalRefs() {
		return monolingualExternalRefs;
	}

	/**
	 * @param monolingualExternalRefs the monolingualExternalRefs to set
	 */
	public void setMonolingualExternalRefs(
			List<MonolingualExternalRef> monolingualExternalRefs) {
		this.monolingualExternalRefs = monolingualExternalRefs;
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
	 * Creates gloss of this Synset by aggregating
	 * defintion texts of all Senses
	 * @return
	 */
	public String getGloss(){
		StringBuilder result = new StringBuilder();
		for(Sense sense : senses){
			result.append(sense.getDefinitionText()+" ");
		}
		return result.toString();
	}

	/**
	 * Returns writtenText of first TextRepresentation of first Definition
	 * @return
	 */
	public String getDefinitionText(){
		if(definitions.isEmpty()) {
			return "";
		}
		Definition firstDefinition = definitions.get(0);
		if(firstDefinition.getTextRepresentations().isEmpty()) {
			return "";
		}
		else {
			return firstDefinition.getTextRepresentations().get(0).getWrittenText();
		}
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
