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

import java.util.ArrayList;
import java.util.List;

import de.tudarmstadt.ukp.lmf.model.abstracts.HasFrequencies;
import de.tudarmstadt.ukp.lmf.model.core.LexicalEntry;
import de.tudarmstadt.ukp.lmf.model.interfaces.IHasID;
import de.tudarmstadt.ukp.lmf.model.miscellaneous.EVarType;
import de.tudarmstadt.ukp.lmf.model.miscellaneous.VarType;

/**
 * SubcategorizationFrame  is a class representing one syntactic construction.
 * A SubcategorizationFrame instance is shared by all {@link LexicalEntry} instances that have the same
 * syntactic behaviour in the same language. A  SubcategorizationFrame can inherit
 * relationships and attributes from another more generic SubcategorizationFrame.
 * Therefore, it is possible to integrate a hierarchical structure of SubcategorizationFrame instances.
 *
 * @author Zijad Maksuti
 * @author Judith Eckle-Kohler
 *
 */
public class SubcategorizationFrame extends HasFrequencies implements IHasID, Comparable<SubcategorizationFrame>{
	// Id of this SubcategorizationFrame
	@VarType(type = EVarType.ATTRIBUTE)
	private String id;

	// links to superordinated (more generic) subcat frame
	@VarType(type = EVarType.IDREF)
	private SubcategorizationFrame parentSubcatFrame;

	// Subcategorization label of this SubcategorizationFrame
	@VarType(type = EVarType.ATTRIBUTE)
	private String subcatLabel;

	// LexemeProperty of this SubcategorizationFrame
	@VarType(type = EVarType.CHILD)
	private LexemeProperty lexemeProperty;

	// Syntactic Arguments of this SubcategorizationFrame
	@VarType(type = EVarType.CHILDREN)
	private List<SyntacticArgument> syntacticArguments = new ArrayList<SyntacticArgument>();

	/**
	 *
	 */
	@Override
	public String getId() {
		return id;
	}

	/**
	 *
	 */
	@Override
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * Returns the more generic "parent" {@link SubcategorizationFrame} instance
	 * of this SubcategorizationFrame instance.
	 *
	 * @return more generic "parent" subcategorization frame or null if the
	 * subcategorization frame does not have a parent
	 */
	public SubcategorizationFrame getParentSubcatFrame() {
		return parentSubcatFrame;
	}

	/**
	 * Sets the more generic "parent" {@link SubcategorizationFrame} instance
	 * of this SubcategorizationFrame instance.
	 *
	 * @param parentSubcatFrame more generic "parent" subcategorization frame to set
	 */
	public void setParentSubcatFrame(SubcategorizationFrame parentSubcatFrame) {
		this.parentSubcatFrame = parentSubcatFrame;
	}

	/**
	 * Returns a {@link String} representing the label of this {@link SubcategorizationFrame} instance.
	 *
	 * @return the label of this subcategorization frame or null if the label is not set
	 */
	public String getSubcatLabel() {
		return subcatLabel;
	}

	/**
	 * Sets a {@link String} representing the label of this {@link SubcategorizationFrame} instance.
	 *
	 * @param subcatLabel the label to set
	 */
	public void setSubcatLabel(String subcatLabel) {
		this.subcatLabel = subcatLabel;
	}

	/**
	 * Returns the {@link LexemeProperty} instance attached to this {@link SubcategorizationFrame} instance.
	 *
	 * @return the lexeme property attached to this subcategorization frame or null
	 * if the lexeme property is not set
	 */
	public LexemeProperty getLexemeProperty() {
		return lexemeProperty;
	}

	/**
	 * Sets the {@link LexemeProperty} instance attached to this {@link SubcategorizationFrame} instance.
	 *
	 * @param lexemeProperty the lexeme property attached to this subcategorization frame to set
	 */
	public void setLexemeProperty(LexemeProperty lexemeProperty) {
		this.lexemeProperty = lexemeProperty;
	}

	/**
	 * Returns the {@link List} of all {@link SyntacticArgument} instances, representing
	 * the arguments of this {@link SubcategorizationFrame} instance.
	 *
	 * @return the list of this subcategorization frames arguments or an empty list
	 * if the frame does not have any arguments
	 */
	public List<SyntacticArgument> getSyntacticArguments() {
		return syntacticArguments;
	}

	/**
	 * Sets the {@link List} of all {@link SyntacticArgument} instances, representing
	 * the arguments of this {@link SubcategorizationFrame} instance.
	 *
	 * @param syntacticArguments the list of this subcategorization frames arguments to set
	 */
	public void setSyntacticArguments(List<SyntacticArgument> syntacticArguments) {
		this.syntacticArguments = syntacticArguments;
	}

	@Override
    public String toString(){
		StringBuffer sb = new StringBuffer();
		sb.append("SubcategorizationFrame ");
		sb.append("id:").append(id);
		sb.append(" parentSubcatFrame:").append(parentSubcatFrame);
		sb.append(" subcatLabel:").append(subcatLabel);
		sb.append(" lexemeProperty:").append(lexemeProperty);
		//Collections.sort(syntacticArguments);
		sb.append(" syntacticArguments:").append(syntacticArguments);
		//Collections.sort(frequencies);
		sb.append(" frequencies").append(frequencies);

		return sb.toString();
	}

	@Override
	public int compareTo(SubcategorizationFrame o) {
		return this.toString().compareTo(o.toString());
	}

	@Override
    public boolean equals(Object other) {
	    if (this == other) {
            return true;
        }
	    if (!(other instanceof SubcategorizationFrame)) {
            return false;
        }
	    SubcategorizationFrame otherSubcategorizationFrame = (SubcategorizationFrame) other;

	    boolean result = this.toString().equals(otherSubcategorizationFrame.toString());
	    return result;
	  }

	@Override
    public int hashCode() {
	    int hash = 1;
	    hash = hash * 31 + this.toString().hashCode();
	    return hash;
	  }
}
