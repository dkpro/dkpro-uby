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
package de.tudarmstadt.ukp.lmf.model.morphology;

import de.tudarmstadt.ukp.lmf.model.core.LexicalEntry;
import de.tudarmstadt.ukp.lmf.model.enums.EYesNo;
import de.tudarmstadt.ukp.lmf.model.miscellaneous.EVarType;
import de.tudarmstadt.ukp.lmf.model.miscellaneous.VarType;

/**
 * This class represents a single multiword component
 * @author sh
 *
 */
public class Component implements Comparable<Component> {

	// targeted LexicalEntry
	@VarType(type = EVarType.IDREF)
	private LexicalEntry targetLexicalEntry;
	
	// component is head of multiword
	@VarType(type = EVarType.ATTRIBUTE)
	private EYesNo isHead;
	
	// position of component in multiword
	@VarType(type = EVarType.ATTRIBUTE)
	private int position;
	
	// component can be separated
	@VarType(type = EVarType.ATTRIBUTE)
	private EYesNo isBreakBefore;
	
	/**
	 * @param target the target to set
	 */
	public void setTargetLexicalEntry(LexicalEntry targetLexicalEntry) {
		this.targetLexicalEntry = targetLexicalEntry;
	}

	/**
	 * @return the target
	 */
	public LexicalEntry getTargetLexicalEntry() {
		return targetLexicalEntry;
	}

	/**
	 * 
	 * @param isHead the isHead to set
	 */
	public void setIsHead(EYesNo isHead) {
		this.isHead = isHead;
	}

	/**
	 * 
	 * @return isHead
	 */
	public EYesNo getIsHead() {
		return isHead;
	}

	/**
	 * 
	 * @param position the position to set
	 */
	public void setPosition(int position) {
		this.position = position;
	}

	/**
	 * 
	 * @return the position
	 */
	public int getPosition() {
		return position;
	}

	/**
	 * 
	 * @param breakBefore the breakBefore to set
	 */
	public void setIsBreakBefore(EYesNo breakBefore) {
		this.isBreakBefore = breakBefore;
	}

	/**
	 * 
	 * @return the breakBefore
	 */
	public EYesNo getIsBreakBefore() {
		return isBreakBefore;
	}
	

	// be warned: compareTo and hashCode method depend on this!
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
	
	public int hashCode(){
		int hash = 1;
		hash = hash*31 + this.toString().hashCode();
		return hash;
	}
	
	public boolean equals(Object o){
		if (this == o)
		      return true;
		    if (!(o instanceof Component))
		      return false;
		Component c = (Component) o;
		return this.toString().equals(c.toString());
	}

}
