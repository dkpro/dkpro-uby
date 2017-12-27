/*******************************************************************************
 * Copyright 2017
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
package de.tudarmstadt.ukp.lmf.model.abstracts;

import java.util.ArrayList;
import java.util.List;

import de.tudarmstadt.ukp.lmf.model.interfaces.IHasFrequencies;
import de.tudarmstadt.ukp.lmf.model.meta.Frequency;
import de.tudarmstadt.ukp.lmf.model.miscellaneous.EVarType;
import de.tudarmstadt.ukp.lmf.model.miscellaneous.VarType;

/**
 * A parent class for UBY-LMF classes which contain a {@link List} of
 * one or more {@link Frequency} instances.
 * 
 * @author Zijad Maksuti
 *
 */
public class HasFrequencies implements IHasFrequencies {
	
	// Frequency information
	@VarType(type = EVarType.CHILDREN)
	protected List<Frequency> frequencies = new ArrayList<Frequency>();
	
	/**
	 * Sets the {@link List} of {@link Frequency} instances to this UBY-LMF class instance.
	 * @param frequencies the frequencies to set
	 */
	public void setFrequencies(List<Frequency> frequencies) {
		this.frequencies = frequencies;
	}

	/**
	 * Returns a {@link List} of all {@link Frequency} instances of this UBY-LMF class instance.
	 * @return a list of all frequencies of this lexical entry or an empty list if the instance has no frequencies set
	 */
	public List<Frequency> getFrequencies() {
		return frequencies;
	}	

}
