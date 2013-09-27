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
package de.tudarmstadt.ukp.lmf.model.morphology;

import de.tudarmstadt.ukp.lmf.model.core.LexicalEntry;
import de.tudarmstadt.ukp.lmf.model.enums.EYesNo;
import de.tudarmstadt.ukp.lmf.model.miscellaneous.AccessType;
import de.tudarmstadt.ukp.lmf.model.miscellaneous.EAccessType;
import de.tudarmstadt.ukp.lmf.model.miscellaneous.EVarType;
import de.tudarmstadt.ukp.lmf.model.miscellaneous.VarType;

/**
 * Component is a class representing a reference to a {@link LexicalEntry}
 * for each lexical component aggregated in a {@link ListOfComponents} class and
 * thus represents a single multiword component.
 * 
 * @author Silvana Hartmann
 *
 */
public class Component implements Comparable<Component> {

	// targeted LexicalEntry
	@VarType(type = EVarType.IDREF)
	private LexicalEntry targetLexicalEntry;

	// component is head of multiword
	@VarType(type = EVarType.ATTRIBUTE)
	@AccessType(type = EAccessType.FIELD)
	private EYesNo isHead;

	// position of component in multiword
	@VarType(type = EVarType.ATTRIBUTE)
	private int position;

	// component can be separated
	@VarType(type = EVarType.ATTRIBUTE)
	@AccessType(type = EAccessType.FIELD)
	private EYesNo isBreakBefore;

	/**
	 * Sets the {@link LexicalEntry} instance referenced by this {@link Component}.
	 * @param target the referenced lexical entry to set
	 */
	public void setTargetLexicalEntry(LexicalEntry targetLexicalEntry) {
		this.targetLexicalEntry = targetLexicalEntry;
	}

	/**
	 * Returns the {@link LexicalEntry} instance referenced by this {@link Component}.
	 * @return the lexical entry referenced by this lexical component or null, if
	 * the referenced lexical entry is not set <p>
	 * <i> Note that Uby-LMF model specifies that a component should always
	 * refer to a lexical entry. Absence of this reference may happen due to an
	 * incomplete conversion of the original resource.</i>
	 */
	public LexicalEntry getTargetLexicalEntry() {
		return targetLexicalEntry;
	}

	/**
	 * Use this method to set the headword property of the {@link Component} instance.<br>
	 * 
	 * @param isHead set to <i>true</i> if the component is the morphological head of a multiword expression,
	 * set to <i>false</i> otherwise
	 */
	public void setHead(boolean isHead) {
		if(isHead)
			this.isHead = EYesNo.yes;
		else
			this.isHead = EYesNo.no;	
	}


	/**
	 * Returns <i>true</i> if the headword property of this {@link Component} instance is set
	 * to <i>true</i> or false if the headword property is set fo <i>false</i>.
	 * @return true if the headword property is set to true or false, if the headword property
	 * is set to false.<br>
	 * If the headword property of the component is not set at all, this method returns null.
	 */
	public Boolean isHead() {
		if(this.isHead == null)
			return null;
		else
			return (isHead.equals(EYesNo.yes)? true : false);
	}

	/**
	 * Sets the position of the multiword component represented by this
	 * {@link Component} instance in its multiword expression.
	 * @param position the position of this multiword component in the multiword expression
	 * @see ListOfComponents
	 */
	public void setPosition(int position) {
		this.position = position;
	}

	/**
	 * Returns the position of the multiword component,
	 * represented by this {@link Component} instance, in its multiword expression.
	 * @return the position of the component in its multiword expression or null
	 * if the position is not set
	 * @see ListOfComponents
	 */
	public int getPosition() {
		return position;
	}

	/**
	 *
	 * @param breakBefore the breakBefore to set
	 * @deprecated
	 * Use {@link Component#setBreakBefore(boolean)} instead
	 */
	@Deprecated
	public void setIsBreakBefore(EYesNo breakBefore) {
		this.isBreakBefore = breakBefore;
	}
	
	/**
	 * Sets the break-before property to this {@link Component instance}.<br>
	 * Break-before property describes the possibility to break a multiword expression, which contains
	 * this multiword component, before the multiword component in order to insert additional constituents.
	 * @param breakBefore set to true, if the component allows insertion of additional constituents before
	 * it, otherwise set to false
	 */
	public void setBreakBefore(boolean breakBefore) {
		if(breakBefore)
			this.isBreakBefore = EYesNo.yes;
		else
			this.isBreakBefore = EYesNo.no;
	}


	/**
	 * Returns the value of break-before property of the multiword component represented
	 * by this {@link Component} instance.<br>
	 * Break-before property describes the possibility to break a multiword expression, which contains
	 * this multiword component, before the multiword component in order to insert additional constituents.
	 * @return true/false if the break-before property of the component is set to true/false.<br>
	 * If the break-before property of this component is not set at all, this method returns null.
	 */
	public Boolean isBreakBefore() {
		if(this.isBreakBefore == null)
			return null;
		else
			return (isBreakBefore.equals(EYesNo.yes)? true : false);
	}

	// be warned: compareTo and hashCode method depend on this!
	@Override
	public String toString(){
		StringBuffer sb = new StringBuffer(256);
		sb.append("Component: ").append(" targetLexicalEntry: ").append(targetLexicalEntry);
		sb.append(" isHead: ").append(isHead).append(" position: ").append(position);
		sb.append(" isBreakBefor: ").append(isBreakBefore);
		return sb.toString();
	}

	@Override
	public int compareTo(Component o) {
		return this.toString().compareTo(o.toString());
	}

	@Override
	public int hashCode(){
		int hash = 1;
		hash = hash*31 + this.toString().hashCode();
		return hash;
	}

	@Override
	public boolean equals(Object o){
		if (this == o) {
			return true;
		}
		    if (!(o instanceof Component)) {
				return false;
			}
		Component c = (Component) o;
		return this.toString().equals(c.toString());
	}

}
