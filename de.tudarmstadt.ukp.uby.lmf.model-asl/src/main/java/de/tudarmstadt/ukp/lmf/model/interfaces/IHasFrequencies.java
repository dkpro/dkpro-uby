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
package de.tudarmstadt.ukp.lmf.model.interfaces;

import java.util.List;

import de.tudarmstadt.ukp.lmf.model.meta.Frequency;

/**
 * An interface for Uby-LMF classes that are associated with one or more {@link Frequency} instances.
 *
 * @author Zijad Maksuti
 *
 */
public interface IHasFrequencies {

	/**
	 * Returns a {@link List} of all {@link Frequency} instances associated with this Uby-LMF class instance.
	 * @return a list of all frequencies of this Uby-LMF class instance or an empty list, if the instance has no frequencies set
	 */
	List<Frequency> getFrequencies();

	/**
	 * Sets the {@link List} of {@link Frequency} instances to this Uby-LMF class instance.
	 * @param frequencies the list of frequencies to set
	 */
	void setFrequencies(List<Frequency> frequencies);

}
