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

import de.tudarmstadt.ukp.lmf.model.miscellaneous.EVarType;
import de.tudarmstadt.ukp.lmf.model.miscellaneous.VarType;

/**
 * 
 * @author sh
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
	 * 
	 * @param arg1 the arg1 to set
	 */
	public void setArg1(SyntacticArgument arg1) {
		this.arg1 = arg1;
	}

	/**
	 * 
	 * @return the arg1
	 */
	public SyntacticArgument getArg1() {
		return arg1;
	}

	/**
	 * 
	 * @param arg2 the arg2 to set
	 */
	public void setArg2(SyntacticArgument arg2) {
		this.arg2 = arg2;
	}

	/**
	 * 
	 * @return the arg2
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
