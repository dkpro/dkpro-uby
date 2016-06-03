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

import de.tudarmstadt.ukp.lmf.model.core.Definition;

/**
 * An interface for Uby-LMF classes that are associated to one or more {@link Definition} instances.
 *
 * @author Zijad Maksuti
 *
 */
public interface IHasDefinitions {

	/**
	 * Returns the {@link List} of all {@link Definition} instances representing the
	 * narrative description of this Uby-LMF class instance.
	 * @return the list of all definitions of this sense or an empty list, if the sense does not have
	 * any definitions set
	 */
	List<Definition> getDefinitions();

	/**
	 * Sets the {@link List} of all {@link Definition} instances to this Uby-LMF class instance.
	 * @param definitions the definitions to set
	 */
	void setDefinitions(List<Definition> definitions);

}
