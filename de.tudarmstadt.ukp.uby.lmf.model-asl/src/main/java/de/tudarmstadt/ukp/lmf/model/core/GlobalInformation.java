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
package de.tudarmstadt.ukp.lmf.model.core;

import de.tudarmstadt.ukp.lmf.model.miscellaneous.EVarType;
import de.tudarmstadt.ukp.lmf.model.miscellaneous.VarType;

/**
 * GlobalInformation is a class representing administrative information and other general attributes
 * of a {@link LexicalResource}.
 * 
 * @author Zijad Maksuti
 */
public class GlobalInformation {
	
	//Label
	@VarType(type = EVarType.ATTRIBUTE)
	private String label;

	/**
	 * Sets label to this {@link GlobalInformation} instance.
	 * @param label the label to set
	 */
	public void setLabel(String label) {
		this.label = label;
	}

	/**
	 * Returns the label of this {@link GlobalInformation} instance.
	 * @return the label of this GlobalInformation instance or null, if the label is not set
	 */
	public String getLabel() {
		return label;
	}
}
