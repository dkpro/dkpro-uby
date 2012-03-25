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
package de.tudarmstadt.ukp.lmf.model.mrd;

import java.util.List;

import de.tudarmstadt.ukp.lmf.model.core.TextRepresentation;
import de.tudarmstadt.ukp.lmf.model.enums.EContextType;
import de.tudarmstadt.ukp.lmf.model.miscellaneous.EVarType;
import de.tudarmstadt.ukp.lmf.model.miscellaneous.VarType;
import de.tudarmstadt.ukp.lmf.model.semantics.MonolingualExternalRef;

public class Context {
	

	// Source of this Context, required for contextType=citation
	// not replaced by MonolingualExternalRef
	@VarType(type = EVarType.ATTRIBUTE)
	private String source;
	
	// Context Type of this Context
	@VarType(type = EVarType.ATTRIBUTE)
	private EContextType contextType;

	// Text Representations of this Context
	@VarType(type = EVarType.CHILDREN)
	private List<TextRepresentation> textRepresentations;
	
	// External reference of this Context
	@VarType(type = EVarType.CHILDREN)
	private List<MonolingualExternalRef> monolingualExternalRefs;

	/**
	 * @return the textRepresentations
	 */
	public List<TextRepresentation> getTextRepresentations() {
		return textRepresentations;
	}

	/**
	 * @param textRepresentations the textRepresentations to set
	 */
	public void setTextRepresentations(List<TextRepresentation> textRepresentations) {
		this.textRepresentations = textRepresentations;
	}

	/**
	 * @return the source
	 */
	public String getSource() {
		return source;
	}

	/**
	 * @param source the source to set
   */
	public void setSource(String source) {
		this.source = source;
	}

	/**
	 * @param externalReferences the externalReferences to set
	 */
	public void setMonolingualExternalRefs(List<MonolingualExternalRef> externalReferences) {
		this.monolingualExternalRefs = externalReferences;
	}

	/**
	 * 
	 * @return the externalReferences
	 */
	public List<MonolingualExternalRef> getMonolingualExternalRefs() {
		return monolingualExternalRefs;
	}

	/**
	 * @return the contextType
	 */
	public EContextType getContextType() {
		return contextType;
	}

	/**
	 * @param contextType the contextType to set
	 */
	public void setContextType(EContextType contextType) {
		this.contextType = contextType;
	}
	
}
