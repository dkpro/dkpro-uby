/*******************************************************************************
 * Copyright 2015
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

import de.tudarmstadt.ukp.lmf.model.meta.SemanticLabel;

/**
 * Interface for Uby-LMF classes containing one or more semantic labels.
 *  
 * @author Zijad Maksuti
 *
 */
public interface IHasSemanticLabels {
	
	/**
	 * Returns the list of all {@link SemanticLabel} instances attached to this
	 * Uby-LMF class instance.
	 * @return the list of all semantic labels attached to this Uby-LMF class instance
	 * or an empty list, if the instance does not have any semantic labels attached
	 */
	public List<SemanticLabel> getSemanticLabels();
	
	/**
	 * Sets the {@link List} of all {@link SemanticLabel} instances attached to
	 * this Uby-LMF class instance instance.
	 * @param semanticLabels the list of all semantic labels to set
	 */
	public void setSemanticLabels(List<SemanticLabel> semanticLabels);
	
}
