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
package de.tudarmstadt.ukp.lmf.model.semantics;

import java.util.List;

import de.tudarmstadt.ukp.lmf.model.interfaces.IHasID;
import de.tudarmstadt.ukp.lmf.model.miscellaneous.EVarType;
import de.tudarmstadt.ukp.lmf.model.miscellaneous.VarType;

/**
 * This class represents a set of
 * SynSemArgMap instances
 * @author maksuti
 *
 */
public class SynSemCorrespondence implements IHasID {
	
	// Id of this Set
	@VarType(type = EVarType.ATTRIBUTE)
	private String id;

	// List of SynSemArgMaps
	@VarType(type = EVarType.CHILDREN)
	private List<SynSemArgMap> synSemArgMaps;
	
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
	 * @return the synSemArgMaps
	 */
	public List<SynSemArgMap> getSynSemArgMaps() {
		return synSemArgMaps;
	}

	/**
	 * @param synSemArgMaps the synSemArgMaps to set
	 */
	public void setSynSemArgMaps(List<SynSemArgMap> synSemArgMaps) {
		this.synSemArgMaps = synSemArgMaps;
	}

}
