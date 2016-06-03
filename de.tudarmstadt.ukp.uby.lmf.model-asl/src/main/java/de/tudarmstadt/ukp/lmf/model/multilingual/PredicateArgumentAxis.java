/*******************************************************************************
 * Copyright 2016
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

import de.tudarmstadt.ukp.lmf.model.core.Lexicon;
import de.tudarmstadt.ukp.lmf.model.interfaces.IHasID;
import de.tudarmstadt.ukp.lmf.model.meta.MetaData;
import de.tudarmstadt.ukp.lmf.model.miscellaneous.EVarType;
import de.tudarmstadt.ukp.lmf.model.miscellaneous.VarType;
import de.tudarmstadt.ukp.lmf.model.semantics.SemanticArgument;
import de.tudarmstadt.ukp.lmf.model.semantics.SemanticPredicate;

/**
 * PredicateArgumentAxis is a class representing the relationship between different closely related
 * {@link SemanticPredicate} and {@link SemanticArgument} instances.
 * @author Yevgen Chebotar
 *
 */
public class PredicateArgumentAxis implements IHasID {

	@VarType(type = EVarType.ATTRIBUTE)
	private String id;
	
	@VarType(type = EVarType.IDREF)
	private SemanticPredicate semanticPredicateOne;
	
	@VarType(type = EVarType.IDREF)
	private SemanticPredicate semanticPredicateTwo;

	@VarType(type = EVarType.IDREF)
	private SemanticArgument semanticArgumentOne;
	
	@VarType(type = EVarType.IDREF)
	private SemanticArgument semanticArgumentTwo;
	
	@VarType(type = EVarType.IDREF)
	private Lexicon lexiconOne;

	@VarType(type = EVarType.IDREF)
	private Lexicon lexiconTwo;
	
	@VarType(type = EVarType.ATTRIBUTE)
	private String axisType;
	
	@VarType(type = EVarType.ATTRIBUTE)	
	private double confidence;
	
	@VarType(type = EVarType.IDREF)
	private MetaData metaData;


	/**
	 * @return the semanticPredicateOne
	 */
	public SemanticPredicate getSemanticPredicateOne() {
		return semanticPredicateOne;
	}

	/**
	 * @param semanticPredicateOne the semanticPredicateOne to set
	 */
	public void setSemanticPredicateOne(SemanticPredicate semanticPredicateOne) {
		this.semanticPredicateOne = semanticPredicateOne;
	}

	/**
	 * @return the semanticPredicateTwo
	 */
	public SemanticPredicate getSemanticPredicateTwo() {
		return semanticPredicateTwo;
	}

	/**
	 * @param semanticPredicateTwo the semanticPredicateTwo to set
	 */
	public void setSemanticPredicateTwo(SemanticPredicate semanticPredicateTwo) {
		this.semanticPredicateTwo = semanticPredicateTwo;
	}

	/**
	 * @return the semanticArgumentOne
	 */
	public SemanticArgument getSemanticArgumentOne() {
		return semanticArgumentOne;
	}

	/**
	 * @param semanticArgumentOne the semanticArgumentOne to set
	 */
	public void setSemanticArgumentOne(SemanticArgument semanticArgumentOne) {
		this.semanticArgumentOne = semanticArgumentOne;
	}

	/**
	 * @return the semanticArgumentTwo
	 */
	public SemanticArgument getSemanticArgumentTwo() {
		return semanticArgumentTwo;
	}

	/**
	 * @param semanticArgumentTwo the semanticArgumentTwo to set
	 */
	public void setSemanticArgumentTwo(SemanticArgument semanticArgumentTwo) {
		this.semanticArgumentTwo = semanticArgumentTwo;
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
	 * @return the axisType
	 */
	public String getAxisType() {
		return axisType;
	}

	/**
	 * @param axisType the axisType to set
	 */
	public void setAxisType(String axisType) {
		this.axisType = axisType;
	}

	/**
	 * @return the confidence
	 */
	public double getConfidence() {
		return confidence;
	}

	/**
	 * @param confidence the confidence to set
	 */
	public void setConfidence(double confidence) {
		this.confidence = confidence;
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

	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id=id;
	}
}
