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

import java.util.ArrayList;
import java.util.List;

import de.tudarmstadt.ukp.lmf.model.core.TextRepresentation;
import de.tudarmstadt.ukp.lmf.model.enums.EContextType;
import de.tudarmstadt.ukp.lmf.model.interfaces.IHasTextRepresentations;
import de.tudarmstadt.ukp.lmf.model.miscellaneous.EVarType;
import de.tudarmstadt.ukp.lmf.model.miscellaneous.VarType;
import de.tudarmstadt.ukp.lmf.model.morphology.Lemma;
import de.tudarmstadt.ukp.lmf.model.semantics.MonolingualExternalRef;

/**
 * The Context class represents a text string that provides authentic context
 * for the use of the word form managed by the {@link Lemma}.<br>The  Context class
 * is in a zero to many aggregate association with the {@link Sense} class
 * and may be associated with zero to many {@link TextRepresentation} instances
 * which manage the representation of the translation equivalent in more than one
 * script or orthography.<p>
 * <i>
 * NOTE: The context may use an inflected form of the lemma
 * </i>
 * @author Zijad Maksuti
 *
 */
public class Context implements IHasTextRepresentations {
	

	// Source of this Context, required for contextType=citation
	// not replaced by MonolingualExternalRef
	@VarType(type = EVarType.ATTRIBUTE)
	private String source;
	
	// Context Type of this Context
	@VarType(type = EVarType.ATTRIBUTE)
	private EContextType contextType;

	// Text Representations of this Context
	@VarType(type = EVarType.CHILDREN)
	private List<TextRepresentation> textRepresentations = new ArrayList<TextRepresentation>();
	
	// External reference of this Context
	@VarType(type = EVarType.CHILDREN)
	private List<MonolingualExternalRef> monolingualExternalRefs = new ArrayList<MonolingualExternalRef>();

	/**
	 * Returns the {@link List} of all {@link TextRepresentation} instances associated with
	 * this {@link Context} instance.
	 * 
	 * @return the list of all text representations instance associated with this
	 * context or an empty list, if the context does not have any text representations
	 * associated
	 */
	public List<TextRepresentation> getTextRepresentations() {
		return textRepresentations;
	}

	/**
	 * Sets the {@link List} of all {@link TextRepresentation} instances to this
	 * {@link Context} instance.
	 * @param textRepresentations the list of all text representations to set
	 */
	public void setTextRepresentations(List<TextRepresentation> textRepresentations) {
		this.textRepresentations = textRepresentations;
	}

	/**
	 * Returns the complete citation of the bibliographic information
	 * pertaining to a document or other resource used when creating {@link TextRepresentation}
	 * instances associated with this {@link Context}.
	 * 
	 * @return the source used for creating text representations of this context or null
	 * if the source is not set
	 * 
	 * @see #getTextRepresentations()
	 */
	public String getSource() {
		return source;
	}

	/**
	 * Sets the complete citation of the bibliographic information
	 * pertaining to a document or other resource used when creating {@link TextRepresentation}
	 * instances associated with this {@link Context}.
	 * 
	 * @param source the source used for creating text representations of this context
	 * 
	 * @see #setTextRepresentations()
	 */
	public void setSource(String source) {
		this.source = source;
	}

	/**
	 * Sets the {@link List} of {@link MonolingualExternalRef} instances which represent
	 * the relationship of this {@link Context} instance to an external system.
	 * 
	 * @param externalReferences the list of external references to set
	 * 
	 * @see #setSource(String)
	 */
	public void setMonolingualExternalRefs(List<MonolingualExternalRef> externalReferences) {
		this.monolingualExternalRefs = externalReferences;
	}

	/**
	 * Returns the {@link List} of {@link MonolingualExternalRef} instances which
	 * represent the relationship of this {@link Context} instance to an external system.
	 * 
	 * @return the list of all external references of this context to external system or
	 * an empty list, if the context does not have any references set
	 */
	public List<MonolingualExternalRef> getMonolingualExternalRefs() {
		return monolingualExternalRefs;
	}

	/**
	 * Returns the type of this {@link Context} instance.
	 * 
	 * @return the type of this context or null, if the type is not set
	 * 
	 * @see EContextType
	 */
	public EContextType getContextType() {
		return contextType;
	}

	/**
	 * Sets the type of this {@link Context} instance.
	 * 
	 * @param contextType the type of the context to set
	 * 
	 * @see EContextType
	 */
	public void setContextType(EContextType contextType) {
		this.contextType = contextType;
	}
	
}
