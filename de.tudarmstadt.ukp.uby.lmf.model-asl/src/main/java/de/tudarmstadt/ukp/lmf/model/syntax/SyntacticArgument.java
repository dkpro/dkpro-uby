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
package de.tudarmstadt.ukp.lmf.model.syntax;

import java.util.List;

import de.tudarmstadt.ukp.lmf.model.enums.ECase;
import de.tudarmstadt.ukp.lmf.model.enums.EComplementizer;
import de.tudarmstadt.ukp.lmf.model.enums.EDeterminer;
import de.tudarmstadt.ukp.lmf.model.enums.EGrammaticalFunction;
import de.tudarmstadt.ukp.lmf.model.enums.ENumber;
import de.tudarmstadt.ukp.lmf.model.enums.ESyntacticCategory;
import de.tudarmstadt.ukp.lmf.model.enums.ETense;
import de.tudarmstadt.ukp.lmf.model.enums.EVerbForm;
import de.tudarmstadt.ukp.lmf.model.enums.EYesNo;
import de.tudarmstadt.ukp.lmf.model.interfaces.IHasID;
import de.tudarmstadt.ukp.lmf.model.meta.Frequency;
import de.tudarmstadt.ukp.lmf.model.miscellaneous.EVarType;
import de.tudarmstadt.ukp.lmf.model.miscellaneous.VarType;

public class SyntacticArgument implements IHasID, Comparable<SyntacticArgument>{
	
	// Id of this SyntacticArgument
	@VarType(type = EVarType.ATTRIBUTE)
	private String id;
	
	// Optional property of this SyntacticArgument
	@VarType(type = EVarType.ATTRIBUTE)
	private EYesNo optional;
	
	// Grammatical Function property of this SyntacticArgument
	@VarType(type = EVarType.ATTRIBUTE)
	private EGrammaticalFunction grammaticalFunction;
	
	// Syntactic Category property of this SyntacticArgument
	@VarType(type = EVarType.ATTRIBUTE)
	private ESyntacticCategory syntacticCategory;
	
	// Case Property of this SyntacticArgument
	@VarType(type = EVarType.ATTRIBUTE)
	private ECase _case;
	
	// Preposition property of this SyntacticArgument
	@VarType(type = EVarType.ATTRIBUTE)
	private String preposition;
	
	// Preposition type of this SyntacticArgument
	@VarType(type = EVarType.ATTRIBUTE)
	private String prepositionType;
	
	// Number property of this SyntacticArgument
	@VarType(type = EVarType.ATTRIBUTE)
	private ENumber number;
	
	// Lexeme of this SyntacticArgument: it, there or specific complementizer, e.g. dass, that, how, wie ...
	@VarType(type = EVarType.ATTRIBUTE)
	private String lexeme;
	
	// Verb Form property of this SyntacticArgument
	@VarType(type = EVarType.ATTRIBUTE)
	private EVerbForm verbForm;
	
	// Tense property of this SyntacticArgument
	@VarType(type = EVarType.ATTRIBUTE)
	private ETense tense;
	
	// Complimentizer property of this SyntacticArgument
	@VarType(type = EVarType.ATTRIBUTE)
	private EComplementizer complementizer;
	
	
	// Determiner of this SyntacticArgument
	@VarType(type = EVarType.ATTRIBUTE)
	private EDeterminer determiner;
	
	// Frequency information for this SubcategorizationFrame
	@VarType(type = EVarType.CHILDREN)
	private List<Frequency> frequencies;
	
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
	 * @return the determiner
	 */
	public EDeterminer getDeterminer() {
		return determiner;
	}

	/**
	 * @param determiner the determiner to set
	 */
	public void setDeterminer(EDeterminer determiner) {
		this.determiner = determiner;
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
	 * @return the optional
	 */
	public EYesNo getOptional() {
		return optional;
	}

	/**
	 * @param optional the optional to set
	 */
	public void setOptional(EYesNo optional) {
		this.optional = optional;
	}

	/**
	 * @return the grammaticalFunction
	 */
	public EGrammaticalFunction getGrammaticalFunction() {
		return grammaticalFunction;
	}

	/**
	 * @param grammaticalFunction the grammaticalFunction to set
	 */
	public void setGrammaticalFunction(EGrammaticalFunction grammaticalFunction) {
		this.grammaticalFunction = grammaticalFunction;
	}

	/**
	 * @return the syntacticCategory
	 */
	public ESyntacticCategory getSyntacticCategory() {
		return syntacticCategory;
	}

	/**
	 * @param syntacticCategory the syntacticCategory to set
	 */
	public void setSyntacticCategory(ESyntacticCategory syntacticCategory) {
		this.syntacticCategory = syntacticCategory;
	}

	/**
	 * @return the _case
	 */
	public ECase getCase() {
		return _case;
	}

	/**
	 * @param case1 the _case to set
	 */
	public void setCase(ECase case1) {
		_case = case1;
	}

	/**
	 * @return the preposition
	 */
	public String getPreposition() {
		return preposition;
	}

	/**
	 * @param preposition the preposition to set
	 */
	public void setPreposition(String preposition) {
		this.preposition = preposition;
	}

	/**
	 * @param prepositionType the prepositionType to set
	 */
	public void setPrepositionType(String prepositionType) {
		this.prepositionType = prepositionType;
	}

	/**
	 * @return the prepositionType
	 */
	public String getPrepositionType() {
		return prepositionType;
	}

	/**
	 * @return the number
	 */
	public ENumber getNumber() {
		return number;
	}

	/**
	 * @param number the number to set
	 */
	public void setNumber(ENumber number) {
		this.number = number;
	}

	/**
	 * @return the lexeme
	 */
	public String getLexeme() {
		return lexeme;
	}

	/**
	 * @param lexeme the lexeme to set
	 */
	public void setLexeme(String lexeme) {
		this.lexeme = lexeme;
	}

	/**
	 * @return the verbForm
	 */
	public EVerbForm getVerbForm() {
		return verbForm;
	}

	/**
	 * @param verbForm the verbForm to set
	 */
	public void setVerbForm(EVerbForm verbForm) {
		this.verbForm = verbForm;
	}

	/**
	 * @return the tense
	 */
	public ETense getTense() {
		return tense;
	}

	/**
	 * @param tense the tense to set
	 */
	public void setTense(ETense tense) {
		this.tense = tense;
	}

	/**
	 * @return the complementizer
	 */
	public EComplementizer getComplementizer() {
		return complementizer;
	}

	/**
	 * @param complementizer the complementizer to set
	 */
	public void setComplementizer(EComplementizer complementizer) {
		this.complementizer = complementizer;
	}


	
	public String toString(){
		return this.id;
	}

	@Override
	public int compareTo(SyntacticArgument o) {
		return this.id.toString().compareTo(o.id.toString());
	}
	
	public boolean equals(Object other) {
	    if (this == other)
	      return true;
	    if (!(other instanceof SyntacticArgument))
	      return false;
	    SyntacticArgument otherSyntacticArgument = (SyntacticArgument) other;
	    
	    boolean result=this.id.equals(otherSyntacticArgument.id);
	    return result;
	  }
	
	public int hashCode() { 
	    int hash = 1;
	    hash = hash * 31 + this.id.hashCode();
	    return hash;
	  }	
	
}
