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

import de.tudarmstadt.ukp.lmf.model.abstracts.HasFrequencies;
import de.tudarmstadt.ukp.lmf.model.core.LexicalEntry;
import de.tudarmstadt.ukp.lmf.model.core.Sense;
import de.tudarmstadt.ukp.lmf.model.enums.ECase;
import de.tudarmstadt.ukp.lmf.model.enums.EComplementizer;
import de.tudarmstadt.ukp.lmf.model.enums.EDeterminer;
import de.tudarmstadt.ukp.lmf.model.enums.EGrammaticalFunction;
import de.tudarmstadt.ukp.lmf.model.enums.EGrammaticalNumber;
import de.tudarmstadt.ukp.lmf.model.enums.ESyntacticCategory;
import de.tudarmstadt.ukp.lmf.model.enums.ETense;
import de.tudarmstadt.ukp.lmf.model.enums.EVerbForm;
import de.tudarmstadt.ukp.lmf.model.enums.EYesNo;
import de.tudarmstadt.ukp.lmf.model.interfaces.IHasID;
import de.tudarmstadt.ukp.lmf.model.miscellaneous.AccessType;
import de.tudarmstadt.ukp.lmf.model.miscellaneous.EAccessType;
import de.tudarmstadt.ukp.lmf.model.miscellaneous.EVarType;
import de.tudarmstadt.ukp.lmf.model.miscellaneous.VarType;
import de.tudarmstadt.ukp.lmf.model.semantics.SynSemArgMap;

/**
 * SyntacticArgument is a class representing an argument of a given {@link SubcategorizationFrame} instance.
 * SyntacticArgument allows the connection with a semantic argument by means of a {@link SynSemArgMap} instance.
 *  
 * @author Zijad Maksuti
 *
 */
public class SyntacticArgument extends HasFrequencies implements IHasID, Comparable<SyntacticArgument>{

	// Id of this SyntacticArgument
	@VarType(type = EVarType.ATTRIBUTE)
	private String id;

	// Optional property of this SyntacticArgument
	@VarType(type = EVarType.ATTRIBUTE)
	@AccessType(type = EAccessType.FIELD)
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
	private EGrammaticalNumber number;

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

	/**
	 * Returns the determiner of this {@link SyntacticArgument} instance.
	 * 
	 * @return the determiner of this syntactic argument or null if the determiner is not set
	 * @see EDeterminer
	 */
	public EDeterminer getDeterminer() {
		return determiner;
	}

	/**
	 * Sets the determiner of this {@link SyntacticArgument} instance.
	 * 
	 * @param determiner the determiner to set
	 * @see EDeterminer
	 */
	public void setDeterminer(EDeterminer determiner) {
		this.determiner = determiner;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	/**
	 * Returns true if this {@link SyntacticArgument} has the optional attribute set.<br>
	 * An optional syntactic argument is a complement of a verb (or a noun or an adjective) that can be omitted.<p>
	 * Example: In the sentence "She paid the bill to her mother",
	 * the argument "the bill" is not an optional syntactic argument, because it cannot be omitted: "*She paid to her mother".
	 * @return true if the optional attribute is yes, false otherwise
	 */
	public boolean isOptional() {
		if(optional != null)
			return (optional.equals(EYesNo.yes)? true : false);
		else
			return false;
	}


	/**
	 * @param optional the optional to set
	 * @deprecated use {@link #setOptional(boolean)} instead
	 */
	@Deprecated
	public void setOptional(EYesNo optional) {
		this.optional = optional;
	}
	
	/**
	 * Sets the optional attribute of this {@link SyntacticArgument} instance.
	 * An optional syntactic argument is a complement of a verb (or a noun or an adjective) that can be omitted.<p>
	 * Example: In the sentence "She paid the bill to her mother",
	 * the argument "the bill" is not an optional syntactic argument, because it cannot be omitted: "*She paid to her mother".
	 * @param optional boolean value of the attribute to set
	 */
	public void setOptional(boolean optional) {
		if(optional)
			this.optional = EYesNo.yes;
		else
			this.optional = EYesNo.no;
	}

	/**
	 * Returns the grammatical function of this {@link SyntacticArgument} instance.
	 * 
	 * @return the grammatical function of this syntactic argument or null if the
	 * grammatical function is not set
	 * @see EGrammaticalFunction
	 */
	public EGrammaticalFunction getGrammaticalFunction() {
		return grammaticalFunction;
	}

	/**
	 * Sets the grammatical function of this {@link SyntacticArgument} instance.
	 * 
	 * @param grammaticalFunction the grammatical function to set
	 * @see EGrammaticalFunction
	 */
	public void setGrammaticalFunction(EGrammaticalFunction grammaticalFunction) {
		this.grammaticalFunction = grammaticalFunction;
	}

	/**
	 * Returns the syntactic category of this {@link SyntacticArgument} instance.
	 * 
	 * @return the syntactic category of the syntactic argument
	 * @see ESyntacticCategory
	 */
	public ESyntacticCategory getSyntacticCategory() {
		return syntacticCategory;
	}

	/**
	 * Sets the syntactic category of this {@link SyntacticArgument} instance.
	 * 
	 * @param syntacticCategory the syntactic category to set
	 * @see ESyntacticCategory
	 */
	public void setSyntacticCategory(ESyntacticCategory syntacticCategory) {
		this.syntacticCategory = syntacticCategory;
	}

	/**
	 * Returns the case of this {@link SyntacticArgument} instance.
	 * 
	 * @return the case of this syntactic argument or null if the case is not set
	 * @see ECase
	 */
	public ECase getCase() {
		return _case;
	}

	/**
	 * Sets the case of this {@link SyntacticArgument} instance.
	 * 
	 * @param _case the case of this syntactic argument to set
	 * @see ECase
	 */
	public void setCase(ECase _case) {
		this._case = _case;
	}

	/**
	 * Returns the {@link String} representing the preposition of this {@link SyntacticArgument} instance.
	 * Usually, a preposition indicates position, direction, time or an abstract relation. Example: "into the woods"
	 * 
	 * @return the preposition of this syntactic argument instance or null if the preposition is not set
	 */
	public String getPreposition() {
		return preposition;
	}

	/**
	 * Sets the {@link String} representing the preposition of this {@link SyntacticArgument} instance.
	 * Usually, a preposition indicates position, direction, time or an abstract relation. Example: "into the woods"
	 * 
	 * @param preposition the preposition to set
	 */
	public void setPreposition(String preposition) {
		this.preposition = preposition;
	}

	/**
	 * Sets the {@link String} representing the type of this {@link SyntacticArgument} instances preposition.<p>
	 * A preposition type is a node in a hierarchy of prepositions. For example in VerbNet, the preposition type "dir"
	 * is subordinated to the preposition type "path"; the preposition type "dir" comprises prepositions such as "across",
	 * "along" and "around".
	 * 
	 * @param prepositionType the preposition type to set
	 * @see #setPreposition(String)
	 */
	public void setPrepositionType(String prepositionType) {
		this.prepositionType = prepositionType;
	}

	/**
	 * Returns the {@link String} representing the type of this {@link SyntacticArgument} instances preposition.<p>
	 * A preposition type is a node in a hierarchy of prepositions. For example in VerbNet, the preposition type "dir"
	 * is subordinated to the preposition type "path"; the preposition type "dir" comprises prepositions such as "across",
	 * "along" and "around".
	 * 
	 * @return the type of this syntactic arguments preposition or null if the type is not sett
	 * @see #getPreposition()
	 */
	public String getPrepositionType() {
		return prepositionType;
	}

	/**
	 * Returns the grammatical number of this {@link SyntacticArgument} instance.
	 * 
	 * @return the grammatical number of this syntactic argument or null if the attribute is not set
	 * @see EGrammaticalNumber
	 */
	public EGrammaticalNumber getNumber() {
		return number;
	}

	/**
	 * Sets the grammatical number of this {@link SyntacticArgument} instance.
	 * 
	 * @param number the grammatical number to set
	 * @see EGrammaticalNumber
	 */
	public void setNumber(EGrammaticalNumber number) {
		this.number = number;
	}

	/**
	 * Returns a {@link String} representing a lexeme of this {@link SyntacticArgument} instance.<p>
	 * 
	 * A lexeme is a minimal unit of language which has a semantic interpretation and embodies a distinct cultural concept.
	 * In UBY-LMF, a lexeme is not a lexical entry. A lexeme is a pair {@link LexicalEntry} / {@link Sense}.
	 * 
	 * @return the string representing the lexeme of this syntactic argument or null if the lexeme is not set
	 */
	public String getLexeme() {
		return lexeme;
	}

	/**
	 * Sets the {@link String} representing the lexeme of this {@link SyntacticArgument} instance.<p>
	 * 
	 * A lexeme is a minimal unit of language which has a semantic interpretation and embodies a distinct cultural concept.
	 * In UBY-LMF, a lexeme is not a lexical entry. A lexeme is a pair {@link LexicalEntry} / {@link Sense}.
	 * 
	 * @param lexeme the string representing the lexeme of this syntactic argument to set
	 */
	public void setLexeme(String lexeme) {
		this.lexeme = lexeme;
	}

	/**
	 * Returns a form of the verb described by this {@link SyntacticArgument} instance.
	 * 
	 * @return the form of the verb described by this syntactic argument or null
	 * if the verb form is not set
	 * @see EVerbForm
	 */
	public EVerbForm getVerbForm() {
		return verbForm;
	}

	/**
	 * Sets the form of the verb described by this {@link SyntacticArgument} instance.
	 * 
	 * @param verbForm the form to set
	 * @see EVerbForm
	 */
	public void setVerbForm(EVerbForm verbForm) {
		this.verbForm = verbForm;
	}

	/**
	 * Returns the tense property of this {@link SyntacticArgument} instance.
	 * 
	 * @return the tense property of this syntactic argument or null if the property is not set
	 * @see ETense
	 */
	public ETense getTense() {
		return tense;
	}

	/**
	 * Sets the tense property of this {@link SyntacticArgument} instance.
	 * 
	 * @param tense the tense property of this syntactic argument to set
	 * @see ETense
	 */
	public void setTense(ETense tense) {
		this.tense = tense;
	}

	/**
	 * Returns the complementizer of this {@link SyntacticArgument} instance.
	 * @return the complementizer of this syntactic argument instance or null
	 * if the complementizer of this syntactic argument is not set
	 * @see EComplementizer
	 */
	public EComplementizer getComplementizer() {
		return complementizer;
	}

	/**
	 * Sets the complementizer of this {@link SyntacticArgument} instance.
	 * @param complementizer the complementizer to set
	 * @see EComplementizer
	 */
	public void setComplementizer(EComplementizer complementizer) {
		this.complementizer = complementizer;
	}



	@Override
	public String toString(){
		return this.id;
	}

	@Override
	public int compareTo(SyntacticArgument o) {
		return this.id.toString().compareTo(o.id.toString());
	}

	@Override
	public boolean equals(Object other) {
	    if (this == other) {
			return true;
		}
	    if (!(other instanceof SyntacticArgument)) {
			return false;
		}
	    SyntacticArgument otherSyntacticArgument = (SyntacticArgument) other;

	    boolean result=this.id.equals(otherSyntacticArgument.id);
	    return result;
	  }

	@Override
	public int hashCode() {
	    int hash = 1;
	    hash = hash * 31 + this.id.hashCode();
	    return hash;
	  }

}
