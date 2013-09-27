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
package de.tudarmstadt.ukp.lmf.model.interfaces;

import java.util.List;

import de.tudarmstadt.ukp.lmf.model.core.TextRepresentation;

/**
 * Interface for UBY-LMF classes containing one or more
 * {@link TextRepresentation} instances.
 * 
 * @author Zijad Maksuti
 *
 */
public interface IHasTextRepresentations {
	
	/**
	 * Returns all text representations of this Uby-LMF class instance
	 * @return {@link List} of this Uby-LMF class instances text representations. <br>
	 * If the instance has no text representation, this method returns an empty list
	 * @see TextRepresentation
	 */
	public List<TextRepresentation> getTextRepresentations();

	/**
	 * Sets a {@link List} of text representations to this Uby-LMF class instance instance.
	 * @param textRepresentations the text representations to set
	 * @see TextRepresentation
	 */
	public void setTextRepresentations(List<TextRepresentation> textRepresentations);
	
}
