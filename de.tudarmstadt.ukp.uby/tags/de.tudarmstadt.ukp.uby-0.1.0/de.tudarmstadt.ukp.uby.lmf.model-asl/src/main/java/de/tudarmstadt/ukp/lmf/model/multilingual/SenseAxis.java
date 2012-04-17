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
package de.tudarmstadt.ukp.lmf.model.multilingual;

import java.util.List;

import de.tudarmstadt.ukp.lmf.model.core.Sense;
import de.tudarmstadt.ukp.lmf.model.enums.ESenseAxisType;
import de.tudarmstadt.ukp.lmf.model.miscellaneous.EVarType;
import de.tudarmstadt.ukp.lmf.model.miscellaneous.VarType;
import de.tudarmstadt.ukp.lmf.model.semantics.Synset;

public class SenseAxis {
		 
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
	private List<SenseAxisRelation> senseAxisRelations; 

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
	 * @return senseOne
	 */
	public Sense getSenseOne() {
		return senseOne;
	}

	/**
	 * @param set senseOne 
	 */
	public void setSenseOne(Sense senseOne) {
		this.senseOne = senseOne;
	}

	/**
	 * @return senseTwo
	 */
	public Sense getSenseTwo() {
		return senseTwo;
	}

	/**
	 * @param set senseTwo 
	 */
	public void setSenseTwo(Sense senseTwo) {
		this.senseTwo = senseTwo;
	}

	
	/**
	 * @return the synsetOne
	 */
	public Synset getSynsetOne() {
		return synsetOne;
	}

	/**
	 * @param synsetOne the synsetOne to set
	 */
	public void setSynsetOne(Synset synsetOne) {
		this.synsetOne = synsetOne;
	}

	
	/**
	 * @return the synsetTwo
	 */
	public Synset getSynsetTwo() {
		return synsetTwo;
	}

	/**
	 * @param synsetTwo the synsetTwo to set
	 */
	public void setSynsetTwo(Synset synsetTwo) {
		this.synsetTwo = synsetTwo;
	}

	/**
	 * @return the senseAxisType
	 */
	public ESenseAxisType getSenseAxisType() {
		return senseAxisType;
	}

	/**
	 * @param senseAxisType the senseAxisType to set
	 */
	public void setSenseAxisType(ESenseAxisType senseAxisType) {
		this.senseAxisType = senseAxisType;
	}

	/**
	 * @param senseAxisRelations the senseAxisRelations to set
	 */
	public void setSenseAxisRelations(List<SenseAxisRelation> senseAxisRelations) {
		this.senseAxisRelations = senseAxisRelations;
	}

	/**
	 * @return the senseAxisRelations
	 */
	public List<SenseAxisRelation> getSenseAxisRelations() {
		return senseAxisRelations;
	}
}
