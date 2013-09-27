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
package de.tudarmstadt.ukp.lmf.model.semantics;

import java.util.ArrayList;
import java.util.List;

import de.tudarmstadt.ukp.lmf.model.interfaces.IHasID;
import de.tudarmstadt.ukp.lmf.model.miscellaneous.EVarType;
import de.tudarmstadt.ukp.lmf.model.miscellaneous.VarType;

/**
 * SynSemCorrespondence is a class representing a set of {@link SynSemArgMap} instances.
 * 
 * @author Zijad Maksuti
 */
public class SynSemCorrespondence implements IHasID {
	
	// Id of this Set
	@VarType(type = EVarType.ATTRIBUTE)
	private String id;

	// List of SynSemArgMaps
	@VarType(type = EVarType.CHILDREN)
	private List<SynSemArgMap> synSemArgMaps = new ArrayList<SynSemArgMap>();
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	/**
	 * Returns a {@link List} of all {@link SynSemArgMap} instances contained in this
	 * {@link SynSemCorrespondence}.
	 * 
	 * @return the list of all syntactic predicate - semantic predicate mappings contained in this
	 * SynSemCorrespondence instance or an empty list if no mappings are set.<p>
	 * 
	 * Note that UBY-LMF requires that every SynSemCorrespondence contains at least one
	 * SynSemArgMap instance. Absence of at least one SynSemArgMap instance may indicate to
	 * incomplete conversion process of the original resource.
	 */
	public List<SynSemArgMap> getSynSemArgMaps() {
		return synSemArgMaps;
	}

	/**
	 * Sets a {@link List} of all {@link SynSemArgMap} instances contained in this
	 * {@link SynSemCorrespondence}.
	 * 
	 * @param synSemArgMaps the list of syntactic argument - semantic argument mappings to set.<p>
	 * 
	 * Note that UBY-LMF requires that every SynSemCorrespondence contains at least one
	 * SynSemArgMap.
	 */
	public void setSynSemArgMaps(List<SynSemArgMap> synSemArgMaps) {
		this.synSemArgMaps = synSemArgMaps;
	}

}
