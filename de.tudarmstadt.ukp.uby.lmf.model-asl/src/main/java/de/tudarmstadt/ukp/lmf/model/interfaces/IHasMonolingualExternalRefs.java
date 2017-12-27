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
package de.tudarmstadt.ukp.lmf.model.interfaces;

import java.util.List;

import de.tudarmstadt.ukp.lmf.model.semantics.MonolingualExternalRef;

/**
 * Interface for UBY-LMF classes containing one or more
 * references to an external system, represented by {@link MonolingualExternalRef} instances.
 *
 * @author Zijad Maksuti
 *
 */
public interface IHasMonolingualExternalRefs {

	/**
	 * Returns the {@link List} of all {@link MonolingualExternalRef} instances which
	 * link this UBY-LMF class instance to an external system.
	 * @return the list of all monolingual external references of this UBY-LMF class instance or an empty
	 * list if the instance does not have any monolingual external references set
	 */
	List<MonolingualExternalRef> getMonolingualExternalRefs();

	/**
	 * Sets the {@link List} of all {@link MonolingualExternalRef} instances to this
	 * UBY-LMF class instance.
	 * @param monolingualExternalRefs the monolingual external references to set
	 */
	void setMonolingualExternalRefs(List<MonolingualExternalRef> monolingualExternalRefs);

}
