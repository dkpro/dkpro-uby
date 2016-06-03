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
package de.tudarmstadt.ukp.lmf.model.syntax;

import de.tudarmstadt.ukp.lmf.model.core.LexicalEntry;
import de.tudarmstadt.ukp.lmf.model.core.Lexicon;
import de.tudarmstadt.ukp.lmf.model.core.Sense;
import de.tudarmstadt.ukp.lmf.model.interfaces.IHasID;
import de.tudarmstadt.ukp.lmf.model.miscellaneous.EVarType;
import de.tudarmstadt.ukp.lmf.model.miscellaneous.VarType;

/**
 * SyntacticBehaviour is a class representing one of the possible behaviours of a lexeme.
 * The SyntacticBehaviour instance is attached to the {@link LexicalEntry} instance and a {@link Sense} instance.
 * The presence in a given {@link Lexicon} instance of one SyntacticBehaviour instance for a lexical entry
 * means that this lexeme can have this behaviour in the language of the lexicon.
 *  
 * @author Zijad Maksuti
 *
 */
public class SyntacticBehaviour implements IHasID, Comparable<SyntacticBehaviour>{
	// Id of this SyntacticBehaviour
	@VarType(type = EVarType.ATTRIBUTE)
	private String id;
	
	// Sense of this SyntacticBehaviour
	@VarType(type = EVarType.IDREF)
	private Sense sense;
	
	// SubcategorizationFrame of this SyntacticBehaviour
	@VarType(type = EVarType.IDREF)
	private SubcategorizationFrame subcategorizationFrame;

	// SubcategorizationFrame of this SyntacticBehaviour
	@VarType(type = EVarType.IDREF)
	private SubcategorizationFrameSet subcategorizationFrameSet;
	
	/**
	 * Returns a {@link SubcategorizationFrameSet} instance that groups different syntactic uses
	 * ("surface alternations") associated with the {@link LexicalEntry} attached to this {@link SyntacticBehaviour}
	 * instance.
	 * 
	 * @return the set of subcategorization frames that groups different syntactic uses of the
	 * lexical entry attached to this syntactic behavior.<br>
	 * If this syntactic behavior does not have a subcategorization frame set set, this method returns null.
	 * 
	 * @see SubcategorizationFrame
	 */
	public SubcategorizationFrameSet getSubcategorizationFrameSet() {
		return subcategorizationFrameSet;
	}

	/**
	 * Sets a {@link SubcategorizationFrameSet} instance that groups different syntactic uses
	 * ("surface alternations") associated with the {@link LexicalEntry} attached to this {@link SyntacticBehaviour}
	 * instance.
	 * 
	 * @param subcategorizationFrameSet the set of subcategorization frames that groups different syntactic uses of the
	 * lexical entry attached to this syntactic behavior to set
	 * 
	 * @see SubcategorizationFrame
	 */
	public void setSubcategorizationFrameSet(SubcategorizationFrameSet subcategorizationFrameSet) {
		this.subcategorizationFrameSet = subcategorizationFrameSet;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	/**
	 * Returns the {@link Sense} instance attached to this {@link SyntacticBehaviour} instance.
	 * 
	 * @return the sense attached to this syntactic behavior or null if the sense is not set
	 */
	public Sense getSense() {
		return sense;
	}

	/**
	 * Sets the {@link Sense} instance attached to this {@link SyntacticBehaviour} instance.
	 * 
	 * @param sense the sense to set
	 */
	public void setSense(Sense sense) {
		this.sense = sense;
	}

	/**
	 * Returns the {@link SubcategorizationFrame} instance which represents a detailed
	 * description of the {@link LexicalEntry} attached to this {@link SyntacticBehaviour} instance.
	 * 
	 * @return the subcategorization frame describing the detailed syntactic behaviour of the
	 * attached lexical entry.<br>If the subcategorization frame is not set for this syntactic
	 * behaviour instance, this method returns null.
	 */
	public SubcategorizationFrame getSubcategorizationFrame() {
		return subcategorizationFrame;
	}

	/**
	 * Sets the {@link SubcategorizationFrame} instance which represents a detailed
	 * description of the {@link LexicalEntry} attached to this {@link SyntacticBehaviour} instance.
	 * 
	 * @param subcategorizationFrame the subcategorization frame describing the detailed syntactic behaviour of the
	 * attached lexical entry to set
	 */
	public void setSubcategorizationFrame(SubcategorizationFrame subcategorizationFrame) {
		this.subcategorizationFrame = subcategorizationFrame;
	}
	
	public String toString(){
		StringBuffer sb = new StringBuffer(512);
		sb.append("SyntacticBehaviour ").append(" id: ").append(id);
		sb.append( "Sense: ").append(sense);
		sb.append(" subcategorizationFrame: ").append(subcategorizationFrame);
		sb.append(" subcategorizationFrameSet: ").append(subcategorizationFrameSet);
		return sb.toString();
	}
	
	public int hashCode(){
		int hashCode = 1;
		return hashCode*31 + this.toString().hashCode();
	}
	
	public boolean equals(Object other) {
	    if (this == other)
	      return true;
	    if (!(other instanceof SyntacticBehaviour))
	      return false;
	    SyntacticBehaviour otherSyntacticBehaviour = (SyntacticBehaviour) other;
	    
	    boolean result=this.toString().equals(otherSyntacticBehaviour.toString());
	    return result;
	  }
	
	

	@Override
	public int compareTo(SyntacticBehaviour o) {
		return this.toString().compareTo(o.toString());
	}
}
