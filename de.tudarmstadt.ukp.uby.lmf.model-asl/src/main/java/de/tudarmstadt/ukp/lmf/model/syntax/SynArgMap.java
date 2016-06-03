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

import de.tudarmstadt.ukp.lmf.model.miscellaneous.EVarType;
import de.tudarmstadt.ukp.lmf.model.miscellaneous.VarType;

/**
 * The SynArgMap is a class representing the relationship that maps various  {@link SyntacticArgument}
 * instances of the same {@link SubcategorizationFrameSet} instance.<br>
 * Therefore, every SynArgMap instance contains exactly two syntactic arguments which
 * are in a relationship. 
 * 
 * @author Silvana Hartmann
 * @author Zijad Maksuti
 *
 */
public class SynArgMap implements Comparable<SynArgMap>{
	
	// first argument in the SynArgMap
	@VarType(type = EVarType.IDREF)
	private SyntacticArgument arg1;

	// second argument in the SynArgMap
	@VarType(type = EVarType.IDREF)
	private SyntacticArgument arg2;

	/**
	 * Sets the first of two {@link SyntacticArgument} instances which are in a relationship
	 * represented by this {@link SynArgMap} instance.
	 * 
	 * @param arg1 the first of two syntactic arguments to set
	 * @see #setArg2(SyntacticArgument)
	 */
	public void setArg1(SyntacticArgument arg1) {
		this.arg1 = arg1;
	}

	/**
	 * Returns the first of two {@link SyntacticArgument} instances which are in a relationship
	 * represented by this {@link SynArgMap} instance.
	 * 
	 * @return the first of two syntactic arguments in a relationship or null if the first argument is not set.<p>
	 * Note that UBY-LMF requires that every SynArgMap instance always has both syntacti arguments set. Absence of
	 * one of these arguments may indicate to incomplete conversion of the original resource. 
	 * @see #getArg2()
	 */
	public SyntacticArgument getArg1() {
		return arg1;
	}

	/**
	 * Sets the second of two {@link SyntacticArgument} instances which are in a relationship
	 * represented by this {@link SynArgMap} instance.
	 * 
	 * @param arg2 the second of two syntactic arguments to set
	 * @see #setArg1(SyntacticArgument)
	 */
	public void setArg2(SyntacticArgument arg2) {
		this.arg2 = arg2;
	}

	/**
	 * Returns the second of two {@link SyntacticArgument} instances which are in a relationship
	 * represented by this {@link SynArgMap} instance.
	 * 
	 * @return the second of two syntactic arguments in a relationship or null if the second argument is not set.<p>
	 * Note that UBY-LMF requires that every SynArgMap instance always has both syntactic arguments set. Absence of
	 * one of these arguments may indicate to incomplete conversion of the original resource. 
	 * @see #getAr1()
	 */
	public SyntacticArgument getArg2() {
		return arg2;
	}
	
	public String toString(){
		StringBuffer sb = new StringBuffer(256);
		sb.append("SynArgMap ").append("arg1:").append(arg1);
		sb.append(" arg2:").append(arg2);
		return sb.toString();
	}

	@Override
	public int compareTo(SynArgMap o) {
		return this.toString().compareTo(o.toString());
	}
	
	public boolean equals(Object other) {
	    if (this == other)
	      return true;
	    if (!(other instanceof SynArgMap))
	      return false;
	    SynArgMap otherSynArgMap = (SynArgMap) other;
	    
	    return this.toString().equals(otherSynArgMap.toString());
	  }
	
	public int hashCode() {
	    int hash = 1;
		hash = hash * 31 + this.toString().hashCode();
		return hash;
	  }
}
