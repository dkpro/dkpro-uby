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
package de.tudarmstadt.ukp.lmf.model.enums;

import de.tudarmstadt.ukp.lmf.model.core.LexicalEntry;

/**
 * Enumeration of different part of speech properties used by 
 * {@link LexicalEntry}. The enum values use a prefix notation to
 * allow for coarse- and fine-grained database queries.
 */
public enum EPartOfSpeech {

	abbreviation,
	abbreviationAcronym,
	abbreviationInitialism,
	adjective,
	adverb,
	adverbPronominal,
	adpositionPreposition,
	adpositionPostposition,
	adpositionCircumposition,
	affix,
	affixPrefix,
	affixSuffix,
	contraction,
	determiner,
	determinerDefinite,
    determinerPossessive,
    determinerIndefinite,
    determinerDemonstrative,
    determinerInterrogative,
    numeral,
    interjection,
    phraseme,
    conjunction,
    conjunctionCoordinating,
    conjunctionSubordinating,
    noun,
    nounCommon,
    nounProper,
    nounProperFirstName,
    nounProperLastName,
    pronoun,
    pronounPersonal,
    pronounPossessive,
    pronounDemonstrative,
    pronounRelative,
    pronounIndefinite,
    pronounPersonalReflexive,
    pronounPersonalIrreflexive,
    pronounInterrogative,
    particle,
    particleNegative,
    particleInfinitive,
    particleComparative,
    particleAnswer,
    symbol,
    verbAuxiliary,
    verbModal,
    verbMain,
    verb
}
