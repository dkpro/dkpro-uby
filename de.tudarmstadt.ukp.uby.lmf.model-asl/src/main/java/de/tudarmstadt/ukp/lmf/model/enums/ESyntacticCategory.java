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
package de.tudarmstadt.ukp.lmf.model.enums;

import de.tudarmstadt.ukp.lmf.model.syntax.SyntacticArgument;

/**
 * Enumeration of the different categories of a {@link SyntacticArgument}
 * instance. A syntactic category is a set of words and/or phrases in a 
 * language which share a significant number of common characteristics.
 */
public enum ESyntacticCategory {
	
	nounPhrase,
	reflexive,
	expletive,
	prepositionalPhrase,
	adverbPhrase,
	adjectivePhrase,
    verbPhrase,
    declarativeClause,
    subordinateClause,
    adverbPhrase_prepositionalPhrase_nounPhrase,
    adverbPhrase_prepositionalPhrase,
    adjectivePhrase_nounPhrase;
    
}
