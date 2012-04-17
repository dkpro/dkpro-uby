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

import de.tudarmstadt.ukp.lmf.model.enums.EYesNo;
import de.tudarmstadt.ukp.lmf.model.interfaces.IHasID;
import de.tudarmstadt.ukp.lmf.model.meta.Frequency;
import de.tudarmstadt.ukp.lmf.model.meta.SemanticLabel;
import de.tudarmstadt.ukp.lmf.model.miscellaneous.EVarType;
import de.tudarmstadt.ukp.lmf.model.miscellaneous.VarType;
import de.tudarmstadt.ukp.lmf.model.mrd.Context;
import de.tudarmstadt.ukp.lmf.model.semantics.MonolingualExternalRef;
import de.tudarmstadt.ukp.lmf.model.semantics.PredicativeRepresentation;
import de.tudarmstadt.ukp.lmf.model.semantics.SemanticArgument;
import de.tudarmstadt.ukp.lmf.model.semantics.SenseExample;
import de.tudarmstadt.ukp.lmf.model.semantics.SenseRelation;
import de.tudarmstadt.ukp.lmf.model.semantics.Synset;

public class Sense implements IHasID, Comparable<Sense>{
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
	private EYesNo transparentMeaning;

	// Sense is a bound Lexeme
	@VarType(type = EVarType.ATTRIBUTE)
	private EYesNo boundLexeme;

// Replace by SemanticLabel Class
//	// Semantic Class Label of this Sense
//	@VarType(type = EVarType.ATTRIBUTE)
//	private String semanticClassLabel;

	// ?
	@VarType(type = EVarType.CHILDREN)
	private List<Sense> senses;

	// Contexts of this Sense
	@VarType(type = EVarType.CHILDREN)
	private List<Context> contexts;

// Replace by SemanticLabel class
//	// Subject Fields fo this Sense
//	@VarType(type = EVarType.CHILDREN)
//	private List<SubjectField> subjectFields;

	// Predicative Representations of this Sense
	@VarType(type = EVarType.CHILDREN)
	private List<PredicativeRepresentation> predicativeRepresentations;

	// Sense Examples of this Sense
	@VarType(type = EVarType.CHILDREN)
	private List<SenseExample> senseExamples;

	// Definitions of this Sense
	@VarType(type = EVarType.CHILDREN)
	private List<Definition> definitions;

	// Sense Relations of this Sense
	@VarType(type = EVarType.CHILDREN)
	private List<SenseRelation> senseRelations;

	@VarType(type = EVarType.CHILDREN)
	private List<MonolingualExternalRef> monolingualExternalRefs;

	// Frequency information for this Sense
	@VarType(type = EVarType.CHILDREN)
	private List<Frequency> frequencies;

	// Semantic class informatin for this Sense
	@VarType(type = EVarType.CHILDREN)
	private List<SemanticLabel> semanticLabels;

	// Backlink to LexicalEntry added for convenience
	@VarType(type = EVarType.NONE)
	private LexicalEntry lexicalEntry;


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
	 * @return the index
	 */
	public int getIndex() {
		return index;
	}

	/**
	 * @param index the index to set
	 */
	public void setIndex(int index) {
		this.index = index;
	}

	/**
	 * @return the synset
	 */
	public Synset getSynset() {
		return synset;
	}

	/**
	 * @param synset the synset to set
	 */
	public void setSynset(Synset synset) {
		this.synset = synset;
	}

//	/**
//	 * @return the semanticClassLabel
//	 */
//	public String getSemanticClassLabel() {
//		return semanticClassLabel;
//	}

//	/**
//	 * @param semanticClassLabel the semanticClassLabel to set
//	 */
//	public void setSemanticClassLabel(String semanticClassLabel) {
//		this.semanticClassLabel = semanticClassLabel;
//	}

	/**
	 *
	 * @param incorporatedSemArg the incorporatedSemArg to set
	 */
	public void setIncorporatedSemArg(SemanticArgument incorporatedSemArg) {
		this.incorporatedSemArg = incorporatedSemArg;
	}

	/**
	 *
	 * @return the incorporatedSemArg
	 */
	public SemanticArgument getIncorporatedSemArg() {
		return incorporatedSemArg;
	}

	/**
	 *
	 * @param transparentMeaning the transparentMeaning to set
	 */
	public void setTransparentMeaning(EYesNo transparentMeaning) {
		this.transparentMeaning = transparentMeaning;
	}

	/**
	 *
	 * @return the transparentMeaning
	 */
	public EYesNo getTransparentMeaning() {
		return transparentMeaning;
	}

	/**
	 *
	 * @param boundLexeme the boundLexeme to set
	 */
	public void setBoundLexeme(EYesNo boundLexeme) {
		this.boundLexeme = boundLexeme;
	}

	/**
	 *
	 * @return the boundLexeme
	 */
	public EYesNo getBoundLexeme() {
		return boundLexeme;
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
	 * @return the contexts
	 */
	public List<Context> getContexts() {
		return contexts;
	}

	/**
	 * @param contexts the contexts to set
	 */
	public void setContexts(List<Context> contexts) {
		this.contexts = contexts;
	}

//	/**
//	 * @return the subjectFields
//	 */
//	public List<SubjectField> getSubjectFields() {
//		return subjectFields;
//	}
//
//	/**
//	 * @param subjectFields the subjectFields to set
//	 */
//	public void setSubjectFields(List<SubjectField> subjectFields) {
//		this.subjectFields = subjectFields;
//	}

	/**
	 * @return the predicativeRepresentations
	 */
	public List<PredicativeRepresentation> getPredicativeRepresentations() {
		return predicativeRepresentations;
	}

	/**
	 * @param predicativeRepresentations the predicativeRepresentations to set
	 */
	public void setPredicativeRepresentations(
			List<PredicativeRepresentation> predicativeRepresentations) {
		this.predicativeRepresentations = predicativeRepresentations;
	}

	/**
	 * @return the senseExamples
	 */
	public List<SenseExample> getSenseExamples() {
		return senseExamples;
	}

	/**
	 * @param senseExamples the senseExamples to set
	 */
	public void setSenseExamples(List<SenseExample> senseExamples) {
		this.senseExamples = senseExamples;
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
	 * Returns all relations in which this Sense is the source.
	 * Thus, it is guaranteed that s.equals(r.getSource()) is true for every SenseRelation r of a Sense s
	 *
	 * @return the senseRelations
	 */
	public List<SenseRelation> getSenseRelations() {
		return senseRelations;
	}

	/**
	 * @param senseRelations the senseRelations to set
	 */
	public void setSenseRelations(List<SenseRelation> senseRelations) {
		this.senseRelations = senseRelations;
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
	 * @return the lexicalEntry
	 */
	public LexicalEntry getLexicalEntry() {
		return lexicalEntry;
	}

	/**
	 * @param lexicalEntry the lexicalEntry to set
	 */
	public void setLexicalEntry(LexicalEntry lexicalEntry) {
		this.lexicalEntry = lexicalEntry;
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

	/**
	 *
	 * @param semanticLabels the semanticLabels to set
	 */
	public void setSemanticLabels(List<SemanticLabel> semanticLabels) {
		this.semanticLabels = semanticLabels;
	}

	/**
	 *
	 * @return the semanticLabels
	 */
	public List<SemanticLabel> getSemanticLabels() {
		return semanticLabels;
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
	    int hash = 1;
	    hash = hash * 31 + this.id==null?0:this.id.hashCode();
	    return hash;
	}
}
