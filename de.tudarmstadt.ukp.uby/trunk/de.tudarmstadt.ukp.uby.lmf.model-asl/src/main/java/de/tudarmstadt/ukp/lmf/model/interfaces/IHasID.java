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
package de.tudarmstadt.ukp.lmf.model.interfaces;

/**
 * An interface for all LMF-Classes that have a unique identifier.
 * 
 * @author Zijad Maksuti
 *
 */
public interface IHasID {

	
	/**
	 * Returns the unique identifier of the Object
	 * @return the unique identifier of the Object
	 */
	public String getId();
	
	/**
	 * Sets the unique identifier of the Object
	 * @param id The unique identifier to set
	 */
	public void setId(String id);
}
