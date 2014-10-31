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
package de.tudarmstadt.ukp.lmf.model.enums;

import de.tudarmstadt.ukp.lmf.model.meta.SemanticLabel;

/**
 * Frequently used names for {@link SemanticLabel}s. The label names are not 
 * limited to those specified by this class; the constants focus only on 
 * closed classes of semantic labels.
 */
public class ELabelNameSemantics {

	public static final String SEMANTIC_NOUN_CLASS_TOPONYM = "toponym";
	public static final String SEMANTIC_NOUN_CLASS_ONLY_SINGULAR = "onlySingular";
	public static final String SEMANTIC_NOUN_CLASS_ONLY_PLURAL = "onlyPlural";
	
	public static final String INTERJECTION_SALUTATION = "salutation";
	public static final String INTERJECTION_ONOMATOPOEIA = "onomatopoeia";
	
	public static final String PHRASEME_CLASS_IDIOM = "idiom";
	public static final String PHRASEME_CLASS_COLLOCATION = "collocation";
	public static final String PHRASEME_CLASS_PROVERB = "proverb";
	public static final String PHRASEME_CLASS_MNEMONIC = "mnemonic";
	
	public static final String DISCOURSE_FUNCTION_MODAL_PARTICLE = "modalParticle";
	public static final String DISCOURSE_FUNCTION_FOCUS_PARTICLE = "focusParticle";
	public static final String DISCOURSE_FUNCTION_INTENSIFYING_PARTICLE = "intensifyingParticle";
	
}
