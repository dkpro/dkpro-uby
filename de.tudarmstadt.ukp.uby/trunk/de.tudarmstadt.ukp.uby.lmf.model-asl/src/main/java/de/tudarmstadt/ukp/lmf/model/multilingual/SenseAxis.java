/*******************************************************************************
 * Copyright 2015
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
package de.tudarmstadt.ukp.lmf.model.multilingual;

import java.util.ArrayList;
import java.util.List;

import de.tudarmstadt.ukp.lmf.model.core.Lexicon;
import de.tudarmstadt.ukp.lmf.model.core.Sense;
import de.tudarmstadt.ukp.lmf.model.enums.ESenseAxisType;
import de.tudarmstadt.ukp.lmf.model.interfaces.IHasID;
import de.tudarmstadt.ukp.lmf.model.meta.MetaData;
import de.tudarmstadt.ukp.lmf.model.miscellaneous.EVarType;
import de.tudarmstadt.ukp.lmf.model.miscellaneous.VarType;
import de.tudarmstadt.ukp.lmf.model.semantics.Synset;

/**
 * SenseAxis is a class representing the relationship between different closely related
 * {@link Sense} instances in different languages and implements approach based on the
 * interlingual pivot.
 * The purpose is to describe the translation of lexemes, represented through
 * {@link LexicalEntry} instances, from one language to another.
 * 
 * @author Zijad Maksuti
 *
 */
public class SenseAxis implements IHasID {
		 
	// Id of this SenseAxis
	@VarType(type = EVarType.ATTRIBUTE)
	private String id;
	
	// SenseOne  of this SenseAxis
	@VarType(type = EVarType.IDREF)
	private Sense senseOne;

	// SenseTwo of this SenseAxis
	@VarType(type = EVarType.IDREF)
	private Sense senseTwo;

	// SynsetOne of this SenseAxis
	@VarType(type = EVarType.IDREF)
	private Synset synsetOne;

	// SynsetTwo of this SenseAxis
	@VarType(type = EVarType.IDREF)
	private Synset synsetTwo;

	// SenseAxis Type of this SenseAxis
	@VarType(type = EVarType.ATTRIBUTE)
	private ESenseAxisType senseAxisType;
	
	// Relations of this SenseAxis
	@VarType(type = EVarType.CHILDREN)
	private List<SenseAxisRelation> senseAxisRelations = new ArrayList<SenseAxisRelation>(); 

	@VarType(type = EVarType.IDREF)
	private MetaData metaData;
	
	@VarType(type = EVarType.IDREF)
	private Lexicon lexiconOne;
	
	@VarType(type = EVarType.IDREF)
	private Lexicon lexiconTwo;
	
	@VarType(type = EVarType.ATTRIBUTE)
	private Double confidence;
	
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	/**
	 * Returns the first of two {@link Sense} instances aligned by this
	 * {@link SenseAxis} instance.
	 * @return the first of the two senses aligned by this sense axis or null
	 * if the first sense is not set
	 * @see #getSenseTwo()
	 */
	public Sense getSenseOne() {
		return senseOne;
	}

	/**
	 * Sets the first of two {@link Sense} instances aligned by this
	 * {@link SenseAxis} instance.
	 * @param senseOne the first of the two senses aligned by this sense axis
	 * @see #setSenseOne(Sense)
	 */
	public void setSenseOne(Sense senseOne) {
		this.senseOne = senseOne;
	}

	/**
	 * Returns the second of two {@link Sense} instances aligned by this
	 * {@link SenseAxis} instance.
	 * @return the second of the two senses aligned by this sense axis or null
	 * if the second sense is not set
	 * @see #getSenseOne()
	 */
	public Sense getSenseTwo() {
		return senseTwo;
	}

	/**
	 * Sets the second of two {@link Sense} instances aligned by this
	 * {@link SenseAxis} instance.
	 * @param senseTwo the second of the two senses aligned by this sense axis
	 * @see #setSenseOne(Sense)
	 */
	public void setSenseTwo(Sense senseTwo) {
		this.senseTwo = senseTwo;
	}

	
	/**
	 * Returns the {@link Synset} containing the first of two {@link Sense} instances
	 * aligned by this {@link SenseAxis}.
	 * @return the synset containing the first of the two sense instances aligened
	 * by this sense axis or null if this attribute is not set
	 * @see #getSynsetTwo()
	 * @see #getSenseOne()
	 */
	public Synset getSynsetOne() {
		return synsetOne;
	}

	/**
	 * Sets the {@link Synset} containing the first of two {@link Sense} instances
	 * aligned by this {@link SenseAxis}.
	 * @param synsetOne the synset containing the first of the two sense aligned by
	 * this sense axis
	 * @see #setSynsetTwo(Synset)
	 * @see #setSenseOne(Sense)
	 */
	public void setSynsetOne(Synset synsetOne) {
		this.synsetOne = synsetOne;
	}

	
	/**
	 * Returns the {@link Synset} containing the second of two {@link Sense} instances
	 * aligned by this {@link SenseAxis}.
	 * @return the synset containing the second of the two sense instances aligened
	 * by this sense axis or null if this attribute is not set
	 * @see #getSynsetTwo()
	 * @see #getSenseOne()
	 */
	public Synset getSynsetTwo() {
		return synsetTwo;
	}

	/**
	 * Sets the {@link Synset} containing the second of two {@link Sense} instances
	 * aligned by this {@link SenseAxis}.
	 * @param synsetOne the synset containing the second of the two sense aligned by
	 * this sense axis
	 * @see #setSynsetOne(Synset)
	 * @see #setSenseTwo(Sense)
	 */
	public void setSynsetTwo(Synset synsetTwo) {
		this.synsetTwo = synsetTwo;
	}

	/**
	 * Returns the type of this {@link SenseAxis} instance.
	 * @return the type of this sense axis or null if the type attribute is not set
	 * @see ESenseAxisType
	 */
	public ESenseAxisType getSenseAxisType() {
		return senseAxisType;
	}

	/**
	 * Sets the type of this {@link SenseAxis} instance.
	 * @param senseAxisType the type to set to this sense axis
	 */
	public void setSenseAxisType(ESenseAxisType senseAxisType) {
		this.senseAxisType = senseAxisType;
	}

	/**
	 * Sets the {@link List} of relations, represented by {@link SenseAxisRelation} instances,
	 * which include this {@link SenseAxis} instance.
	 * @param senseAxisRelations the list of sense axis relations which include this
	 * sense axis
	 */
	public void setSenseAxisRelations(List<SenseAxisRelation> senseAxisRelations) {
		this.senseAxisRelations = senseAxisRelations;
	}

	/**
	 * Returns the {@link List} of relations, represented by {@link SenseAxisRelation} instances,
	 * which include this {@link SenseAxis} instance.
	 * @return the the list of sense axis relations which include this sense axis or
	 * an empty list if the sense axis is not included by any relation 
	 */
	public List<SenseAxisRelation> getSenseAxisRelations() {
		return senseAxisRelations;
	}
	
	/**
	 * @return the metaData
	 */
	public MetaData getMetaData() {
		return metaData;
	}

	/**
	 * @param metaData the metaData to set
	 */
	public void setMetaData(MetaData metaData) {
		this.metaData = metaData;
	}
	
	/**
	 * @return the lexiconOne
	 */
	public Lexicon getLexiconOne() {
		return lexiconOne;
	}

	/**
	 * @param lexiconOne the lexiconOne to set
	 */
	public void setLexiconOne(Lexicon lexiconOne) {
		this.lexiconOne = lexiconOne;
	}

	/**
	 * @return the lexiconTwo
	 */
	public Lexicon getLexiconTwo() {
		return lexiconTwo;
	}

	/**
	 * @param lexiconTwo the lexiconTwo to set
	 */
	public void setLexiconTwo(Lexicon lexiconTwo) {
		this.lexiconTwo = lexiconTwo;
	}

	/**
	 * @return the confidence
	 */
	public Double getConfidence() {
		return confidence;
	}

	/**
	 * @param confidence the confidence to set
	 */
	public void setConfidence(Double confidence) {
		this.confidence = confidence;
	}
}
